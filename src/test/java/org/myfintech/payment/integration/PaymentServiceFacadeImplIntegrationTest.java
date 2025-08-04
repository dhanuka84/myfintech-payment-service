package org.myfintech.payment.integration;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.myfintech.payment.PaymentApplication;
import org.myfintech.payment.config.TestSecurityConfig;
import org.myfintech.payment.domain.ClientCreateDTO;
import org.myfintech.payment.domain.ClientDTO;
import org.myfintech.payment.domain.ContractCreateDTO;
import org.myfintech.payment.domain.ContractDTO;
import org.myfintech.payment.domain.PaymentCreateDTO;
import org.myfintech.payment.domain.PaymentDTO;
import org.myfintech.payment.entity.Contract;
import org.myfintech.payment.entity.Payment;
import org.myfintech.payment.entity.PaymentTracking;
import org.myfintech.payment.repository.ClientRepository;
import org.myfintech.payment.repository.ContractRepository;
import org.myfintech.payment.repository.PaymentRepository;
import org.myfintech.payment.repository.PaymentTrackingRepository;
import org.myfintech.payment.service.ClientService;
import org.myfintech.payment.service.ContractService;
import org.myfintech.payment.service.PaymentServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = { PaymentApplication.class, TestSecurityConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PaymentServiceFacadeImplIntegrationTest  extends AbstractIntegrationTest{

    @Autowired
    private PaymentServiceFacade paymentServiceFacade;

    @Autowired
    private ClientService clientService; // To setup test data
    @Autowired
    private ContractService contractService; // To setup test data

    @Autowired
    private PaymentRepository paymentRepository; // To verify results directly
    @Autowired
    private PaymentTrackingRepository paymentTrackingRepository; // To verify tracking directly
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ContractRepository contractRepository;

    @BeforeEach
    @Transactional
    void setUp() {
        // Clear all data before each test to ensure a clean slate
        paymentRepository.deleteAll();
        paymentTrackingRepository.deleteAll();
        contractRepository.deleteAll();
        clientRepository.deleteAll();
    }

    @Test
    @Transactional
    void findAll_shouldReturnPaginatedPayments() {
        // Arrange: Create test data
        ClientCreateDTO clientCreateDTO = new ClientCreateDTO("Test Client");
        ClientDTO client = clientService.save(clientCreateDTO);

        ContractCreateDTO contractCreateDTO = new ContractCreateDTO(client.clientId(), "C-PAGINATION-01");
        ContractDTO contract = contractService.save(contractCreateDTO);
        assertEquals("C-PAGINATION-01", contract.contractNumber());
        Contract contractEntity = contractService.findByContractNumber("C-PAGINATION-01").get();
        
        PaymentTracking tracking = paymentTrackingRepository.save(new PaymentTracking("tracking-1"));

        // Save payments directly to bypass facade's save method complexities for this test
        Payment p1 = new Payment();
        p1.setPaymentDate(LocalDate.now());
        p1.setAmount(java.math.BigDecimal.valueOf(100.0));
        p1.setType("CREDIT");
        p1.setContractId(contract.clientId());
        p1.setContract(contractEntity); // Set entity reference for mapping
        p1.setTrackingId(tracking.getId()); // Default value before tracking is set
        paymentRepository.save(p1);

        Payment p2 = new Payment();
        p2.setPaymentDate(LocalDate.now().minusDays(1));
        p2.setAmount(java.math.BigDecimal.valueOf(200.0));
        p2.setType("DEBIT");
        p2.setContractId(contract.contractId());
        p2.setContract(contractEntity);
        p2.setTrackingId(tracking.getId());
        paymentRepository.save(p2);

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<PaymentDTO> result = paymentServiceFacade.findAll(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(0, result.getNumber());
        assertEquals(2, result.getContent().size());
        result.getContent().forEach(x-> assertNotNull(x.paymentDate()));
        assertEquals("C-PAGINATION-01", result.getContent().getFirst().contractNumber());
    }

    @Test
    @Transactional
    void findById_shouldReturnPaymentDTO() {
        // Arrange
        ClientCreateDTO clientCreateDTO = new ClientCreateDTO("FindById Client");
        ClientDTO client = clientService.save(clientCreateDTO);

        ContractCreateDTO contractCreateDTO = new ContractCreateDTO(client.clientId(), "C-FIND-BY-ID");
        ContractDTO contract = contractService.save(contractCreateDTO);

        PaymentCreateDTO createDTO = new PaymentCreateDTO("2024-07-15", 500.0, "CREDIT", contract.contractNumber(), "TR-001");
        PaymentDTO savedPayment = paymentServiceFacade.save(createDTO);

        // Act
        PaymentDTO found = paymentServiceFacade.findPaymentsByContractNumber("C-FIND-BY-ID").getFirst();

        // Assert
        assertNotNull(found);
        assertEquals(savedPayment.paymentDate(), found.paymentDate());
        assertEquals(createDTO.paymentDate(), found.paymentDate());
        assertEquals(createDTO.amount(), found.amount());
        assertEquals(createDTO.type(), found.type());
        assertEquals(createDTO.contractNumber(), found.contractNumber());
    }

    @Test
    @Transactional
    void save_shouldCreatePaymentAndTracking() {
        // Arrange
        ClientCreateDTO clientCreateDTO = new ClientCreateDTO("Save Client");
        ClientDTO client = clientService.save(clientCreateDTO);

        ContractCreateDTO contractCreateDTO = new ContractCreateDTO(client.clientId(), "C-SAVE-01");
        contractService.save(contractCreateDTO); // Save the contract so it exists

        PaymentCreateDTO createDTO = new PaymentCreateDTO("2024-07-16", 750.0, "DEBIT", "C-SAVE-01", "TR-SAVE-01");

        // Act
        PaymentDTO saved = paymentServiceFacade.save(createDTO);

        // Assert
        assertNotNull(saved);
        assertEquals(createDTO.paymentDate(), saved.paymentDate());
        assertEquals(createDTO.amount(), saved.amount());
        assertEquals(createDTO.type(), saved.type());
        assertEquals(createDTO.contractNumber(), saved.contractNumber());

        // Verify that tracking record and payment exist in DB
        assertNotNull(paymentRepository.findPaymentsByContract_ContractNumber("C-SAVE-01").getFirst());
        assertTrue(paymentTrackingRepository.findByTrackingNumber("TR-SAVE-01").isPresent());
    }

    @Test
    @Transactional
    void update_shouldModifyPayment() {
        // Arrange
        ClientCreateDTO clientCreateDTO = new ClientCreateDTO("Update Client");
        ClientDTO client = clientService.save(clientCreateDTO);

        ContractCreateDTO contractCreateDTO = new ContractCreateDTO(client.clientId(), "C-UPDATE-01");
        contractService.save(contractCreateDTO);

        PaymentCreateDTO initialCreateDTO = new PaymentCreateDTO("2024-07-17", 100.0, "CREDIT", "C-UPDATE-01", "TR-UPDATE-01");
        PaymentDTO saved = paymentServiceFacade.save(initialCreateDTO);

        PaymentDTO updateDTO = PaymentDTO.of("2024-07-18", 150.0, "DEBIT", "C-UPDATE-01");
        Payment payment = paymentRepository.findPaymentsByContract_ContractNumber("C-UPDATE-01").getFirst();

        // Act
        PaymentDTO updated = paymentServiceFacade.update(payment.getId(), updateDTO);

        // Assert
        assertNotNull(updated);
        assertEquals("2024-07-18", updated.paymentDate());
        assertEquals(150.0, updated.amount());
        assertEquals("DEBIT", updated.type());
    }

    @Test
    @Transactional
    void findPaymentsByContractNumber_shouldReturnCorrectPayments() {
        // Arrange
        ClientCreateDTO clientCreateDTO = new ClientCreateDTO("Contract Search Client");
        ClientDTO client = clientService.save(clientCreateDTO);

        ContractCreateDTO contractCreateDTO1 = new ContractCreateDTO(client.clientId(), "C-SEARCH-01");
        ContractDTO contract1 = contractService.save(contractCreateDTO1);

        ContractCreateDTO contractCreateDTO2 = new ContractCreateDTO(client.clientId(), "C-SEARCH-02");
        ContractDTO contract2 = contractService.save(contractCreateDTO2);

        paymentServiceFacade.save(new PaymentCreateDTO("2024-07-19", 100.0, "CREDIT", "C-SEARCH-01", "TR-SEARCH-01-A"));
        paymentServiceFacade.save(new PaymentCreateDTO("2024-07-20", 200.0, "DEBIT", "C-SEARCH-01", "TR-SEARCH-01-B"));
        paymentServiceFacade.save(new PaymentCreateDTO("2024-07-21", 300.0, "CREDIT", "C-SEARCH-02", "TR-SEARCH-02-A"));

        // Act
        List<PaymentDTO> paymentsForContract1 = paymentServiceFacade.findPaymentsByContractNumber("C-SEARCH-01");
        List<PaymentDTO> paymentsForContract2 = paymentServiceFacade.findPaymentsByContractNumber("C-SEARCH-02");
        List<PaymentDTO> paymentsForNonExistentContract = paymentServiceFacade.findPaymentsByContractNumber("C-NON-EXISTENT");

        // Assert
        assertEquals(2, paymentsForContract1.size());
        assertTrue(paymentsForContract1.stream().allMatch(p -> p.contractNumber().equals("C-SEARCH-01")));

        assertEquals(1, paymentsForContract2.size());
        assertTrue(paymentsForContract2.stream().allMatch(p -> p.contractNumber().equals("C-SEARCH-02")));

        assertTrue(paymentsForNonExistentContract.isEmpty());
    }

    @Test
    void saveAsynch_shouldProcessPaymentsAsyncAndSaveTracking() {
        // Arrange
        ClientCreateDTO clientCreateDTO = new ClientCreateDTO("Async Client");
        ClientDTO client = clientService.save(clientCreateDTO);

        ContractCreateDTO contractCreateDTO1 = new ContractCreateDTO(client.clientId(), "C-ASYNC-01");
        contractService.save(contractCreateDTO1);

        ContractCreateDTO contractCreateDTO2 = new ContractCreateDTO(client.clientId(), "C-ASYNC-02");
        contractService.save(contractCreateDTO2);

        List<PaymentDTO> paymentsToSave = List.of(
                PaymentDTO.of("2024-07-22", 10.0, "CREDIT", "C-ASYNC-01"),
                PaymentDTO.of("2024-07-23", 20.0, "DEBIT", "C-ASYNC-02")
        );
        String trackingNumber = "TR-ASYNC-001";

        // Act
        paymentServiceFacade.saveAsynch(trackingNumber, paymentsToSave);

        // Assert: Use Awaitility to wait for the asynchronous operation to complete
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            assertFalse(paymentRepository.findAll().isEmpty());
            assertEquals(2, paymentRepository.count());
            assertTrue(paymentTrackingRepository.findByTrackingNumber(trackingNumber).isPresent());
            assertEquals(1L, paymentTrackingRepository.findByTrackingNumber(trackingNumber).get().getId());
            // Verify payments are linked to the tracking ID
            paymentRepository.findAll().forEach(payment ->
                    assertNotNull(payment.getTrackingId())
            );
        });
    }

    @Test
    @Transactional
    void validatePaymentsOrFail_shouldNotThrowExceptionForValidPayments() {
        // Arrange (no data needed in DB for validation, only DTOs)
        List<PaymentDTO> validPayments = List.of(
                PaymentDTO.of("2024-01-01", 100.0, "CREDIT", "C-VALID-01"),
                PaymentDTO.of("2024-01-02", 200.0, "DEBIT", "C-VALID-02")
        );

        // Act & Assert (should not throw any exception)
        assertDoesNotThrow(() -> paymentServiceFacade.validatePaymentsOrFail(validPayments));
    }

    @Test
    @Transactional
    void validatePaymentsOrFail_shouldThrowExceptionForInvalidPayments() {
        // Arrange
        List<PaymentDTO> invalidPayments = List.of(
                PaymentDTO.of("2024-01-01", 100.0, "CREDIT", "C-VALID-01"),
                PaymentDTO.of("INVALID_DATE", 200.0, "DEBIT", "C-INVALID-02") // Invalid date format
        );

        // Act & Assert
        // Expect an IllegalArgumentException (or similar, depending on exact validation error handling)
        assertThrows(RuntimeException.class, () -> paymentServiceFacade.validatePaymentsOrFail(invalidPayments));
    }
}