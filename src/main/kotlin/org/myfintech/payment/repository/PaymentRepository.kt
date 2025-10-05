package org.myfintech.payment.repository

import org.myfintech.payment.entity.Payment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query

interface PaymentRepository : BaseRepository<Payment, Long> {
    fun findPaymentsByContract_ContractNumber(contractNumber: String): List<Payment>

    @Query(value = "SELECT p FROM Payment p JOIN FETCH p.contract", countQuery = "SELECT COUNT(p) FROM Payment p")
    fun findAllWithContract(pageable: Pageable): Page<Payment>
}
