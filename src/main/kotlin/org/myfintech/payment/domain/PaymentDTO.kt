/*
 * Copyright (c) 2024 MyFintech Payment Service
 * All rights reserved.
 *
 * This software is proprietary and confidential. Unauthorized copying of this file,
 * via any medium, is strictly prohibited.
 * @author : Dhanuka Ranasinghe
 * @since : Date: 11/07/2025
 */
package org.myfintech.payment.domain

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlElement
import jakarta.xml.bind.annotation.XmlRootElement
import org.myfintech.payment.validator.PaymentValidationConstants

@XmlRootElement(name = "payment")
@XmlAccessorType(XmlAccessType.FIELD)
@JvmRecord
data class PaymentDTO(
    @JvmField @field:XmlElement(name = "payment_date") @param:XmlElement(
        name = "payment_date"
    ) val paymentDate: @NotBlank(message = PaymentValidationConstants.PAYMENT_DATE_REQUIRED) @Pattern(
        regexp = PaymentValidationConstants.DATE_YYYY_MM_DD_PATTERN,
        message = PaymentValidationConstants.PAYMENT_DATE_FORMAT
    ) String,
    @JvmField @field:XmlElement @param:XmlElement val amount: @Positive(message = PaymentValidationConstants.AMOUNT_POSITIVE) Double,
    @JvmField @field:XmlElement @param:XmlElement val type: @NotBlank(message = PaymentValidationConstants.TYPE_REQUIRED) String,
    @JvmField @field:XmlElement(name = "contract_number") @param:XmlElement(
        name = "contract_number"
    ) val contractNumber: @NotBlank(message = PaymentValidationConstants.CONTRACT_NUMBER_REQUIRED) String

) {
    companion object {
        @JvmStatic
        fun of(paymentDate: String, amount: Double, type: String, contractNumber: String): PaymentDTO {
            return PaymentDTO(paymentDate, amount, type, contractNumber)
        }
    }
}
