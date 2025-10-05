package org.myfintech.payment.service

import org.myfintech.payment.domain.PaymentCreateDTO
import org.myfintech.payment.domain.PaymentDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PaymentServiceFacade {
    fun saveAsynch(trackingNumber: String, dto: List<PaymentDTO>)

    fun findAll(pageable: Pageable): Page<PaymentDTO>

    fun findById(id: Long): PaymentDTO

    fun save(dto: PaymentCreateDTO): PaymentDTO

    fun update(id: Long, dto: PaymentDTO): PaymentDTO

    fun findPaymentsByContractNumber(contractNumber: String): List<PaymentDTO>

    fun validatePaymentsOrFail(payments: List<PaymentDTO>)
}
