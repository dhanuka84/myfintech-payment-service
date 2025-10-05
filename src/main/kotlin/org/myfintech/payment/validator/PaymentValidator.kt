/*
 * Copyright (c) 2024 MyFintech Payment Service
 * All rights reserved.
 *
 * This software is proprietary and confidential. Unauthorized copying of this file,
 * via any medium, is strictly prohibited.
 * @author : Dhanuka Ranasinghe
 * @since : Date: 11/07/2025
 */
package org.myfintech.payment.validator

import jakarta.validation.ConstraintViolationException
import jakarta.validation.Validator
import org.myfintech.payment.domain.PaymentDTO
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class PaymentValidator(private val validator: Validator) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(PaymentValidator::class.java)
    }

    fun validatePaymentRequest(dto: PaymentDTO) {
        val violations = validator.validate(dto)
        if (violations.isNotEmpty()) {
            log.warn("PaymentDTO validation failed: {}", violations)
            throw ConstraintViolationException("Validation failed for PaymentDTO", violations)
        }
    }
}