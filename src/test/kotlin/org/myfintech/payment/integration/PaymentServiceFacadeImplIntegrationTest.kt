package org.myfintech.payment.integration

import org.awaitility.Awaitility
import org.awaitility.core.ThrowingRunnable
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.myfintech.payment.PaymentApplication
import org.myfintech.payment.config.TestSecurityConfig
import org.myfintech.payment.domain.ClientCreateDTO
import org.myfintech.payment.domain.ContractCreateDTO
import org.myfintech.payment.domain.PaymentCreateDTO
import org.myfintech.payment.domain.PaymentDTO
import org.myfintech.payment.domain.PaymentDTO.Companion.of
import org.myfintech.payment.entity.Payment
import org.myfintech.payment.entity.PaymentTracking
import org.myfintech.payment.repository.ClientRepository
import org.myfintech.payment.repository.ContractRepository
import org.myfintech.payment.repository.PaymentRepository
import org.myfintech.payment.repository.PaymentTrackingRepository
import org.myfintech.payment.service.ClientService
import org.myfintech.payment.service.ContractService
import org.myfintech.payment.service.PaymentServiceFacade
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.test.annotation.DirtiesContext
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.util.List
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

@SpringBootTest(classes = [PaymentApplication::class, TestSecurityConfig::class])
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PaymentServiceFacadeImplIntegrationTest : AbstractIntegrationTest() {
    @Autowired
    private val paymentServiceFacade: PaymentServiceFacade? = null

    @Autowired
    private val clientService: ClientService? = null // To setup test data

    @Autowired
    private val contractService: ContractService? = null // To setup test data

    @Autowired
    private val paymentRepository: PaymentRepository? = null // To verify results directly

    @Autowired
    private val paymentTrackingRepository: PaymentTrackingRepository? = null // To verify tracking directly

    @Autowired
    private val clientRepository: ClientRepository? = null

    @Autowired
    private val contractRepository: ContractRepository? = null

    @BeforeEach
    @Transactional
    fun setUp() {
        // Clear all data before each test to ensure a clean slate
        paymentRepository!!.deleteAll()
        paymentTrackingRepository!!.deleteAll()
        contractRepository!!.deleteAll()
        clientRepository!!.deleteAll()
    }

    @Test
    @Transactional
    fun findAll_shouldReturnPaginatedPayments() {
        // Arrange: Create test data
        val clientCreateDTO = ClientCreateDTO("Test Client")
        val client = clientService!!.save(clientCreateDTO)

        val contractCreateDTO = ContractCreateDTO(client.clientId(), "C-PAGINATION-01")
        val contract = contractService!!.save(contractCreateDTO)
        assertEquals("C-PAGINATION-01", contract.contractNumber())
        val contractEntity = contractService.findByContractNumber("C-PAGINATION-01")!!.get()

        val tracking = paymentTrackingRepository!!.save<PaymentTracking>(PaymentTracking("tracking-1"))

        // Save payments directly to bypass facade's save method complexities for this test
        val p1 = Payment()
        p1.setPaymentDate(LocalDate.now())
        p1.setAmount(BigDecimal.valueOf(100.0))
        p1.setType("CREDIT")
        p1.setContractId(contract.clientId())
        p1.setContract(contractEntity) // Set entity reference for mapping
        p1.setTrackingId(tracking.id) // Default value before tracking is set
        paymentRepository!!.save<Payment?>(p1)

        val p2 = Payment()
        p2.setPaymentDate(LocalDate.now().minusDays(1))
        p2.setAmount(BigDecimal.valueOf(200.0))
        p2.setType("DEBIT")
        p2.setContractId(contract.contractId())
        p2.setContract(contractEntity)
        p2.setTrackingId(tracking.id)
        paymentRepository.save<Payment?>(p2)

        val pageable: Pageable = PageRequest.of(0, 10)

        // Act
        val result = paymentServiceFacade!!.findAll(pageable)

        // Assert
        Assertions.assertNotNull(result)
        Assertions.assertEquals(2, result!!.getTotalElements())
        Assertions.assertEquals(1, result.getTotalPages())
        Assertions.assertEquals(0, result.getNumber())
        Assertions.assertEquals(2, result.getContent().size)
        result.getContent().forEach(Consumer { x: PaymentDTO? -> Assertions.assertNotNull(x.paymentDate()) })
        assertEquals("C-PAGINATION-01", result.getContent().getFirst().contractNumber())
    }

    @Test
    @Transactional
    fun findById_shouldReturnPaymentDTO() {
        // Arrange
        val clientCreateDTO = ClientCreateDTO("FindById Client")
        val client = clientService!!.save(clientCreateDTO)

        val contractCreateDTO = ContractCreateDTO(client.clientId(), "C-FIND-BY-ID")
        val contract = contractService!!.save(contractCreateDTO)

        val createDTO = PaymentCreateDTO("2024-07-15", 500.0, "CREDIT", contract.contractNumber(), "TR-001")
        val savedPayment = paymentServiceFacade!!.save(createDTO)

        // Act
        val found: PaymentDTO? = paymentServiceFacade.findPaymentsByContractNumber("C-FIND-BY-ID").getFirst()

        // Assert
        Assertions.assertNotNull(found)
        assertEquals(savedPayment.paymentDate(), found.paymentDate())
        assertEquals(createDTO.paymentDate(), found.paymentDate())
        assertEquals(createDTO.amount(), found.amount())
        assertEquals(createDTO.type(), found.type())
        assertEquals(createDTO.contractNumber(), found.contractNumber())
    }

    @Test
    @Transactional
    fun save_shouldCreatePaymentAndTracking() {
        // Arrange
        val clientCreateDTO = ClientCreateDTO("Save Client")
        val client = clientService!!.save(clientCreateDTO)

        val contractCreateDTO = ContractCreateDTO(client.clientId(), "C-SAVE-01")
        contractService!!.save(contractCreateDTO) // Save the contract so it exists

        val createDTO = PaymentCreateDTO("2024-07-16", 750.0, "DEBIT", "C-SAVE-01", "TR-SAVE-01")

        // Act
        val saved = paymentServiceFacade!!.save(createDTO)

        // Assert
        Assertions.assertNotNull(saved)
        assertEquals(createDTO.paymentDate(), saved.paymentDate())
        assertEquals(createDTO.amount(), saved.amount())
        assertEquals(createDTO.type(), saved.type())
        assertEquals(createDTO.contractNumber(), saved.contractNumber())

        // Verify that tracking record and payment exist in DB
        Assertions.assertNotNull(paymentRepository!!.findPaymentsByContract_ContractNumber("C-SAVE-01").getFirst())
        Assertions.assertTrue(paymentTrackingRepository!!.findByTrackingNumber("TR-SAVE-01")!!.isPresent())
    }

    @Test
    @Transactional
    fun update_shouldModifyPayment() {
        // Arrange
        val clientCreateDTO = ClientCreateDTO("Update Client")
        val client = clientService!!.save(clientCreateDTO)

        val contractCreateDTO = ContractCreateDTO(client.clientId(), "C-UPDATE-01")
        contractService!!.save(contractCreateDTO)

        val initialCreateDTO = PaymentCreateDTO("2024-07-17", 100.0, "CREDIT", "C-UPDATE-01", "TR-UPDATE-01")
        val saved = paymentServiceFacade!!.save(initialCreateDTO)

        val updateDTO = of("2024-07-18", 150.0, "DEBIT", "C-UPDATE-01")
        val payment: Payment = paymentRepository!!.findPaymentsByContract_ContractNumber("C-UPDATE-01").getFirst()

        // Act
        val updated = paymentServiceFacade.update(payment.id, updateDTO)

        // Assert
        Assertions.assertNotNull(updated)
        assertEquals("2024-07-18", updated.paymentDate())
        assertEquals(150.0, updated.amount())
        assertEquals("DEBIT", updated.type())
    }

    @Test
    @Transactional
    fun findPaymentsByContractNumber_shouldReturnCorrectPayments() {
        // Arrange
        val clientCreateDTO = ClientCreateDTO("Contract Search Client")
        val client = clientService!!.save(clientCreateDTO)

        val contractCreateDTO1 = ContractCreateDTO(client.clientId(), "C-SEARCH-01")
        val contract1 = contractService!!.save(contractCreateDTO1)

        val contractCreateDTO2 = ContractCreateDTO(client.clientId(), "C-SEARCH-02")
        val contract2 = contractService.save(contractCreateDTO2)

        paymentServiceFacade!!.save(PaymentCreateDTO("2024-07-19", 100.0, "CREDIT", "C-SEARCH-01", "TR-SEARCH-01-A"))
        paymentServiceFacade.save(PaymentCreateDTO("2024-07-20", 200.0, "DEBIT", "C-SEARCH-01", "TR-SEARCH-01-B"))
        paymentServiceFacade.save(PaymentCreateDTO("2024-07-21", 300.0, "CREDIT", "C-SEARCH-02", "TR-SEARCH-02-A"))

        // Act
        val paymentsForContract1 = paymentServiceFacade.findPaymentsByContractNumber("C-SEARCH-01")
        val paymentsForContract2 = paymentServiceFacade.findPaymentsByContractNumber("C-SEARCH-02")
        val paymentsForNonExistentContract = paymentServiceFacade.findPaymentsByContractNumber("C-NON-EXISTENT")

        // Assert
        Assertions.assertEquals(2, paymentsForContract1!!.size)
        Assertions.assertTrue(
            paymentsForContract1.stream().allMatch { p: PaymentDTO? -> p.contractNumber().equals("C-SEARCH-01") })

        Assertions.assertEquals(1, paymentsForContract2!!.size)
        Assertions.assertTrue(
            paymentsForContract2.stream().allMatch { p: PaymentDTO? -> p.contractNumber().equals("C-SEARCH-02") })

        Assertions.assertTrue(paymentsForNonExistentContract!!.isEmpty())
    }

    @Test
    fun saveAsynch_shouldProcessPaymentsAsyncAndSaveTracking() {
        // Arrange
        val clientCreateDTO = ClientCreateDTO("Async Client")
        val client = clientService!!.save(clientCreateDTO)

        val contractCreateDTO1 = ContractCreateDTO(client.clientId(), "C-ASYNC-01")
        contractService!!.save(contractCreateDTO1)

        val contractCreateDTO2 = ContractCreateDTO(client.clientId(), "C-ASYNC-02")
        contractService.save(contractCreateDTO2)

        val paymentsToSave = List.of<PaymentDTO?>(
            of("2024-07-22", 10.0, "CREDIT", "C-ASYNC-01"),
            of("2024-07-23", 20.0, "DEBIT", "C-ASYNC-02")
        )
        val trackingNumber = "TR-ASYNC-001"

        // Act
        paymentServiceFacade!!.saveAsynch(trackingNumber, paymentsToSave)

        // Assert: Use Awaitility to wait for the asynchronous operation to complete
        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(ThrowingRunnable {
            Assertions.assertFalse(paymentRepository!!.findAll().isEmpty())
            Assertions.assertEquals(2, paymentRepository.count())
            Assertions.assertTrue(paymentTrackingRepository!!.findByTrackingNumber(trackingNumber)!!.isPresent())
            Assertions.assertEquals(1L, paymentTrackingRepository.findByTrackingNumber(trackingNumber)!!.get().id)
            // Verify payments are linked to the tracking ID
            paymentRepository.findAll()
                .forEach(Consumer { payment: Payment? -> Assertions.assertNotNull(payment.getTrackingId()) }
                )
        })
    }

    @Test
    @Transactional
    fun validatePaymentsOrFail_shouldNotThrowExceptionForValidPayments() {
        // Arrange (no data needed in DB for validation, only DTOs)
        val validPayments = List.of<PaymentDTO?>(
            of("2024-01-01", 100.0, "CREDIT", "C-VALID-01"),
            of("2024-01-02", 200.0, "DEBIT", "C-VALID-02")
        )

        // Act & Assert (should not throw any exception)
        Assertions.assertDoesNotThrow(Executable { paymentServiceFacade!!.validatePaymentsOrFail(validPayments) })
    }

    @Test
    @Transactional
    fun validatePaymentsOrFail_shouldThrowExceptionForInvalidPayments() {
        // Arrange
        val invalidPayments = List.of<PaymentDTO?>(
            of("2024-01-01", 100.0, "CREDIT", "C-VALID-01"),
            of("INVALID_DATE", 200.0, "DEBIT", "C-INVALID-02") // Invalid date format
        )

        // Act & Assert
        // Expect an IllegalArgumentException (or similar, depending on exact validation error handling)
        Assertions.assertThrows<RuntimeException?>(
            RuntimeException::class.java,
            Executable { paymentServiceFacade!!.validatePaymentsOrFail(invalidPayments) })
    }
}