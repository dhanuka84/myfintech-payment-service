package org.myfintech.payment.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.myfintech.payment.domain.PaymentCreateDTO;
import org.myfintech.payment.domain.PaymentDTO;
import org.myfintech.payment.entity.Client;
import org.myfintech.payment.entity.Contract;
import org.myfintech.payment.entity.Payment;
import org.myfintech.payment.entity.projection.ContractWithClientProjection;
import org.myfintech.payment.mapper.PaymentMapper;
import org.myfintech.payment.service.ContractService;
import org.myfintech.payment.service.PaymentService;
import org.myfintech.payment.service.PaymentServiceFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentServiceFacadeImpl implements PaymentServiceFacade {

	private final PaymentService paymentService;
	private final ContractService contractService;
	private final PaymentMapper mapper;

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public PaymentServiceFacadeImpl(PaymentService paymentService, ContractService contractService, PaymentMapper mapper) {
		super();
		this.paymentService = paymentService;
		this.contractService = contractService;
		this.mapper = mapper;
	}

	@Async
	@Override
	public void saveAsynch(String trackingNumber, List<PaymentDTO> validPayments) {

		// Step 1: Group DTOs by contract number
		Set<String> contractNumbers = validPayments.stream().map(PaymentDTO::contractNumber)
				.collect(Collectors.toSet());

		// Step 2: Fetch contracts using projection in a read-only tx
		List<ContractWithClientProjection> projections = contractService.findAllByContractNumbers(contractNumbers);
		Map<String, Contract> contractsByNumber = projections.stream().map(this::mapToContractEntity)
				.collect(Collectors.toMap(Contract::getContractNumber, Function.identity()));

		// Step 3: Map DTOs to Payment entities
		List<Payment> paymentEntities = filterOutValidEntities(validPayments, contractsByNumber);

		// Step 4: Delegate all database write operations to a single, cohesive service method.
        // This call joins the existing transaction started by this `saveAsynch` method.
        paymentService.saveTrackedPayments(trackingNumber, paymentEntities);
	}

	private Contract mapToContractEntity(ContractWithClientProjection projection) {
		Client client = new Client();
		client.setId(projection.getClientId());
		client.setClientName(projection.getClientName());

		Contract contract = new Contract();
		contract.setId(projection.getId());
		contract.setContractNumber(projection.getContractNumber());
		contract.setClient(client);

		return contract;
	}

	private List<Payment> filterOutValidEntities(List<PaymentDTO> validPayments, Map<String, Contract> contractsByNumber) {

		return validPayments.stream().map(dto -> {
			Contract contract = contractsByNumber.get(dto.contractNumber());
			if (contract == null) {
				log.error("Contract not found for number: {}", dto.contractNumber());
				throw new IllegalArgumentException("Contract not found: " + dto.contractNumber());
			}
			return mapper.toEntity(dto, contract);
		}).collect(Collectors.toList());
	}

	@Override
	public Page<PaymentDTO> findAll(Pageable pageable) {
	    return paymentService.findAll(pageable);
	}


	@Override
	public PaymentDTO findById(Long id) {
		return mapper.toDTO(paymentService.findById(id));
	}

	@Override
	public PaymentDTO save(PaymentCreateDTO dto) {
		Optional<Contract> contract = contractService.findByContractNumber(dto.contractNumber());
		Payment entity = mapper.toEntity(dto, contract.get());
		return mapper.toDTO(paymentService.savePayment(entity, dto.trackingNumber()));
	}

	@Override
	public PaymentDTO update(Long id, PaymentDTO dto) {
		//external validation
		paymentService.validate(dto);
		LocalDate paymentDate = LocalDate.parse(dto.paymentDate(), DATE_FORMATTER);
		Payment payment = paymentService.findById(id);

		// Step 1: Validate and fetch contract
		contractService.findByContractNumber(dto.contractNumber())
				.orElseThrow(() -> new IllegalArgumentException("Contract not found: " + dto.contractNumber()));

		// Step 3: Apply updates
		payment.setPaymentDate(paymentDate);
		payment.setAmount(BigDecimal.valueOf(dto.amount()));
		payment.setType(dto.type());

		// Step 4: Need to call save — because not TX
		return mapper.toDTO(paymentService.savePayment(payment));
	}

	@Override
	public List<PaymentDTO> findPaymentsByContractNumber(String contractNumber) {
		return paymentService.findPaymentsByContractNumber(contractNumber);
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public void validatePaymentsOrFail(List<PaymentDTO> payments) {
		payments.forEach(paymentService::validate);
		
	}

}
