package org.myfintech.payment.repository;

import java.util.List;

import org.myfintech.payment.entity.Payment;

public interface PaymentRepository extends BaseRepository<Payment, Long> {
    List<Payment> findPaymentsByContract_ContractNumber(String contractNumber);
    
}
