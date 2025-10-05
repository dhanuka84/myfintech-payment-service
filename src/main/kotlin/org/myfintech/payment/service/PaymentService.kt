package org.myfintech.payment.service

import org.myfintech.payment.domain.PaymentDTO
import org.myfintech.payment.entity.Payment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PaymentService {
    fun findAll(pageable: Pageable): Page<PaymentDTO>

    fun findById(id: Long): Payment

    fun findPaymentsByContractNumber(contractNumber: String): List<PaymentDTO>

    fun savePayments(paymentEntities: List<Payment>): List<Payment>

    fun savePayment(payment: Payment, trackingNumber: String): Payment

    fun saveTrackedPayments(trackingNumber: String, paymentEntities: List<Payment>)

    fun validate(dto: PaymentDTO)

    fun savePayment(payement: Payment): Payment
}
