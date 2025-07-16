package org.myfintech.payment.service;

import java.util.List;

import org.myfintech.payment.domain.PaymentCreateDTO;
import org.myfintech.payment.domain.PaymentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentServiceFacade {

	void saveAsynch(String trackingNumber, List<PaymentDTO> dto);

	Page<PaymentDTO> findAll(Pageable pageable);

	PaymentDTO findById(Long id);

	PaymentDTO save(PaymentCreateDTO dto);

	PaymentDTO update(Long id, PaymentDTO dto);

	List<PaymentDTO> findPaymentsByContractNumber(String contractNumber);

	void validatePaymentsOrFail(List<PaymentDTO> payments);

}
