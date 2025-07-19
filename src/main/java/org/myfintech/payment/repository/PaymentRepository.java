package org.myfintech.payment.repository;

import java.util.List;

import org.myfintech.payment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends BaseRepository<Payment, Long> {

	List<Payment> findPaymentsByContract_ContractNumber(String contractNumber);

	@Query(value = "SELECT p FROM Payment p JOIN FETCH p.contract", countQuery = "SELECT COUNT(p) FROM Payment p")
	Page<Payment> findAllWithContract(Pageable pageable);

}
