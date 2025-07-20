package org.myfintech.payment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.myfintech.payment.domain.PaymentCreateDTO;
import org.myfintech.payment.domain.PaymentDTO;
import org.myfintech.payment.entity.Client;
import org.myfintech.payment.entity.Contract;
import org.myfintech.payment.entity.Payment;
import org.myfintech.payment.entity.PaymentTracking;
import org.myfintech.payment.mapper.ContractMapper;
import org.myfintech.payment.mapper.PaymentMapper;
import org.myfintech.payment.repository.ContractRepository;
import org.myfintech.payment.repository.PaymentRepository;
import org.myfintech.payment.repository.PaymentTrackingRepository;
import org.myfintech.payment.service.impl.ContractServiceImpl;
import org.myfintech.payment.service.impl.PaymentServiceFacadeImpl;
import org.myfintech.payment.service.impl.PaymentServiceImpl;
import org.myfintech.payment.validator.CotractValidator;
import org.myfintech.payment.validator.PaymentValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {

	@Mock
	private PaymentRepository paymentRepository;
	@Mock
	private ContractRepository contractRepository;
	@Mock
	private PaymentTrackingRepository paymentTrackingRepository;
	@Mock
	private ClientService clientService;
	@Mock
	private PaymentValidator validator;
	@Mock
    private CotractValidator contractValidator;

	private final PaymentMapper paymentMapper = Mappers.getMapper(PaymentMapper.class);
	private final ContractMapper contractMapper = Mappers.getMapper(ContractMapper.class);

	private PaymentServiceImpl paymentService;
	private ContractServiceImpl contractService;

	@InjectMocks
	private PaymentServiceFacadeImpl paymentFacade;

	private PaymentCreateDTO paymentCreateDTO;
	private Payment payment;
	private Contract contract;

	@BeforeEach
	void setUp() {
		paymentService = new PaymentServiceImpl(paymentRepository, paymentTrackingRepository, paymentMapper,validator);
		contractService = new ContractServiceImpl(contractRepository, contractMapper, clientService,contractValidator);
		paymentFacade = new PaymentServiceFacadeImpl(paymentService, contractService, paymentMapper);

		OffsetDateTime now = OffsetDateTime.now();
		Client client = new Client(1L, now, now, "Acme");
		contract = new Contract(1L, now, now, client, "12345");

		//payment = new Payment(1L, now, now, LocalDate.of(2024, 1, 30), BigDecimal.valueOf(1000.0), "incoming");
		payment = new Payment();
		payment.setPaymentDate(LocalDate.of(2024, 1, 30));
		payment.setAmount(BigDecimal.valueOf(1000.0));
		payment.setType("incoming");
		payment.setContract(contract);

		paymentCreateDTO = new PaymentCreateDTO("2024-01-30", 1000.0, "incoming", "12345", "tr-1");
	}

	@Test
	void shouldReturnAllPayments() {
		Pageable pageable = PageRequest.of(0, 10);
		when(paymentRepository.findAllWithContract(pageable)).thenReturn(new PageImpl<>(List.of(payment)));

		Page<PaymentDTO> result = paymentFacade.findAll(pageable);

		assertEquals(1, result.getTotalPages());
		assertEquals("12345", result.getContent().get(0).contractNumber());
	}

	@Test
	void shouldReturnPaymentsForTheContract() {
		when(paymentRepository.findPaymentsByContract_ContractNumber("12345")).thenReturn(List.of(payment));
		List<PaymentDTO> result = paymentFacade.findPaymentsByContractNumber("12345");
		assertEquals(1, result.size());
		assertEquals("12345", result.get(0).contractNumber());
	}

	@Test
	void shouldReturnPaymentById() {
		when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
		PaymentDTO result = paymentFacade.findById(1L);
		assertEquals("12345", result.contractNumber());
	}

	@Test
	void shouldSavePayment() {
		PaymentTracking tracking = new PaymentTracking();
		tracking.setId(1L);
		when(paymentTrackingRepository.save(any())).thenReturn(tracking);
		when(contractRepository.findByContractNumber("12345")).thenReturn(Optional.of(contract));
		when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
			Payment saved = invocation.getArgument(0);
			saved.setId(99L);
			return saved;
		});

		PaymentDTO result = paymentFacade.save(paymentCreateDTO);
		assertEquals("12345", result.contractNumber());
		assertEquals(1000.0, result.amount());
	}

	@Test
	void shouldUpdatePaymentSuccessfully() {
		Long id = 1L;
		Payment existing = new Payment();
		existing.setId(id);
		existing.setAmount(BigDecimal.valueOf(500.0));
		existing.setType("MANUAL");
		existing.setPaymentDate(LocalDate.of(2024, 1, 1));
		existing.setContract(contract);

		PaymentDTO updatedDTO = new PaymentDTO("2024-06-01", 200.0, "AUTO", "12345");

		
		//when(contractService.findByContractNumber("12345")).thenReturn(Optional.of(contract));
		when(contractRepository.findByContractNumber("12345")).thenReturn(Optional.of(contract));
		when(paymentRepository.findById(id)).thenReturn(Optional.of(existing));
		when(paymentService.savePayment(existing)).thenReturn(existing);

		PaymentDTO result = paymentFacade.update(id, updatedDTO);

		assertEquals("AUTO", result.type());
		assertEquals(200.0, result.amount());
		assertEquals("12345", result.contractNumber());
	}

}
