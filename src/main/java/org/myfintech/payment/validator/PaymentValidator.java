/*
 * Copyright (c) 2024 MyFintech Payment Service
 * All rights reserved.
 *
 * This software is proprietary and confidential. Unauthorized copying of this file,
 * via any medium, is strictly prohibited.
 * @author : Dhanuka Ranasinghe
 * @since : Date: 11/07/2025
 */
package org.myfintech.payment.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.myfintech.payment.domain.PaymentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Component
@Slf4j
public class PaymentValidator {

    private final Validator validator;

    @Autowired
    public PaymentValidator(Validator validator) {
        this.validator = validator;
    }

    public void validatePaymentRequest(PaymentDTO dto) {
        Set<ConstraintViolation<PaymentDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            log.warn("PaymentDTO validation failed: {}", violations);
            throw new ConstraintViolationException("Validation failed for PaymentDTO", violations);
        }
    }
}
