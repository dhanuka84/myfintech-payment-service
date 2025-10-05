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
import jakarta.xml.bind.annotation.XmlElement
import org.myfintech.payment.validator.PaymentValidationConstants



data class PaymentCreateDTO(
     @field:XmlElement(name = "payment_date") @param:XmlElement(
        name = "payment_date"
    ) val paymentDate: @NotBlank(message = PaymentValidationConstants.PAYMENT_DATE_REQUIRED) @Pattern(
        regexp = PaymentValidationConstants.DATE_YYYY_MM_DD_PATTERN,
        message = PaymentValidationConstants.PAYMENT_DATE_FORMAT
    ) String,
     @field:XmlElement @param:XmlElement val amount: @Positive(message = PaymentValidationConstants.AMOUNT_POSITIVE) Double,
     @field:XmlElement @param:XmlElement val type: @NotBlank(message = PaymentValidationConstants.TYPE_REQUIRED) String,
     @field:XmlElement(name = "contract_number") @param:XmlElement(
        name = "contract_number"
    ) val contractNumber: @NotBlank(message = PaymentValidationConstants.CONTRACT_NUMBER_REQUIRED) String,  // derived property

    val trackingNumber: @NotBlank(message = PaymentValidationConstants.TRACKING_NUMBER_REQUIRED) String
) {
    companion object {
        @JvmStatic
        fun of(
            paymentDate: String, amount: Double, type: String, contractNumber: String,
            trackingNumber: String
        ): PaymentCreateDTO {
            return PaymentCreateDTO(paymentDate, amount, type, contractNumber, trackingNumber)
        }
    }
}
