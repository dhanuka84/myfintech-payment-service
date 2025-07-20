package org.myfintech.payment.service;

import java.util.List;

import org.myfintech.payment.domain.PaymentDTO;
import org.myfintech.payment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
	Page<PaymentDTO> findAll(Pageable pageable);

	Payment findById(Long id);

	List<PaymentDTO> findPaymentsByContractNumber(String contractNumber);

	List<Payment> savePayments(List<Payment> paymentEntities);

	Payment savePayment(Payment payment, String trackingNumber);

	public void saveTrackedPayments(String trackingNumber, List<Payment> paymentEntities);
	
	public void validate(PaymentDTO dto);

	Payment savePayment(Payment payement);
}
