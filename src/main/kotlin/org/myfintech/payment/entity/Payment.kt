/*
 * Copyright (c) 2024 MyFintech Payment Service
 * All rights reserved.
 *
 * This software is proprietary and confidential. Unauthorized copying of this file,
 * via any medium, is strictly prohibited.
 * @author : Dhanuka Ranasinghe
 * @since : Date: 11/07/2025
 */
package org.myfintech.payment.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.LocalDate
import org.myfintech.payment.entity.EntityColumnConstants as Const

@Entity
@Table(name = Const.TABLE_PAYMENT)
class Payment(

    @field:NotNull
    var paymentDate: LocalDate,

    @field:NotNull
    @field:Positive
    @Column(precision = 10, scale = 2)
    var amount: BigDecimal,

    @field:NotBlank
    @field:Size(max = 50)
    var type: String,

    @Column(name = Const.FOREIGN_KEY_CONTRACT_ID, nullable = false)
    var contractId: Long,

    @Column(name = Const.FOREIGN_KEY_PAYMENT_TRACKING_ID, nullable = false)
    var trackingId: Long

) : AbstractEntity<Long>() {

    @Version
    @Column(name = Const.COMMON_VERSION, nullable = false)
    var version: Int? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Const.FOREIGN_KEY_CONTRACT_ID, insertable = false, updatable = false)
    lateinit var contract: Contract

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Const.FOREIGN_KEY_PAYMENT_TRACKING_ID, insertable = false, updatable = false)
    lateinit var paymentTracking: PaymentTracking


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Payment) return false
        if (!super.equals(other)) return false // Compares `id`

        // Compare explicitly included fields
        if (type != other.type) return false
        if (contractId != other.contractId) return false
        if (trackingId != other.trackingId) return false

        return true
    }

    override fun hashCode(): Int {
        // Start with superclass hash code
        var result = super.hashCode()
        // Combine with explicitly included fields
        result = 31 * result + type.hashCode()
        result = 31 * result + contractId.hashCode()
        result = 31 * result + trackingId.hashCode()
        return result
    }
}