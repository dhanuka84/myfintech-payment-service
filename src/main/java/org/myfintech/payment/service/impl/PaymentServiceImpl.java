package org.myfintech.payment.service.impl;

import java.util.List;

import org.myfintech.payment.domain.PaymentDTO;
import org.myfintech.payment.entity.Payment;
import org.myfintech.payment.entity.PaymentTracking;
import org.myfintech.payment.exception.Http404NotFoundException;
import org.myfintech.payment.mapper.PaymentMapper;
import org.myfintech.payment.repository.PaymentRepository;
import org.myfintech.payment.repository.PaymentTrackingRepository;
import org.myfintech.payment.service.PaymentService;
import org.myfintech.payment.validator.PaymentValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;
	private final PaymentTrackingRepository trackingRepository;
	private final PaymentMapper mapper;
	private final PaymentValidator validator;

	public PaymentServiceImpl(PaymentRepository repository, PaymentTrackingRepository trackingRepository,
			PaymentMapper mapper, PaymentValidator validator) {
		this.paymentRepository = repository;
		this.trackingRepository = trackingRepository;
		this.mapper = mapper;
		this.validator = validator;
	}

	@Override
	public Page<PaymentDTO> findAll(Pageable pageable) {
		return paymentRepository.findAllWithContract(pageable).map(mapper::toDTO); 
	}



	@Override
	@Transactional(readOnly = true)
	public Payment findById(Long id) {
		return paymentRepository.findById(id)
				.orElseThrow(() -> new Http404NotFoundException("Payment not found with id: " + id));
	}

	@Override
	@Transactional(readOnly = true)
	public List<PaymentDTO> findPaymentsByContractNumber(String contractNumber) {
		return paymentRepository.findPaymentsByContract_ContractNumber(contractNumber).stream().map(mapper::toDTO).toList();
	}

	@Override
	@Transactional
	public List<Payment> savePayments(List<Payment> paymentEntities) {
		// Save all Payment entities
		return paymentRepository.saveAll(paymentEntities);

	}
	
	@Override
	@Transactional
	public Payment savePayment(Payment payement) {
		return paymentRepository.save(payement);

	}

	/**
	 * This new method encapsulates the business rule of saving a tracking record
	 * along with its associated payments. It is designed to be called from within
	 * an existing transaction.
	 *
	 * @param trackingNumber  The unique tracking number for this batch.
	 * @param paymentEntities The list of fully-formed Payment entities to save.
	 */
	@Transactional // Default propagation is REQUIRED, so it joins the caller's transaction.
	public void saveTrackedPayments(String trackingNumber, List<Payment> paymentEntities) {
		// Step 1: Save the master tracking record.
		PaymentTracking paymentTracking = trackingRepository.save(new PaymentTracking(trackingNumber));

		// Step 2: Associate the newly saved tracking record with each payment entity.
		// This assumes you have a relationship from Payment to PaymentTracking.
		paymentEntities.forEach(payment -> payment.setTrackingId(paymentTracking.getId()));

		// Step 3: Persist all the payment entities in a batch.
		// Both this save and the tracking save above will be committed or rolled back
		// together.
		paymentRepository.saveAll(paymentEntities);

		log.info("Successfully saved tracking record {} and {} of payments", trackingNumber , paymentEntities.size());
	}

	@Override
	@Transactional
	public Payment savePayment(Payment payment, String trackingNumber) {
		PaymentTracking tracking = trackingRepository.save(new PaymentTracking(trackingNumber));
		payment.setTrackingId(tracking.getId());
		return paymentRepository.save(payment);
	}
	
	@Transactional(readOnly = true)
	public void validate(PaymentDTO dto) {
		validator.validatePaymentRequest(dto);
	}
}
