/*
 * Copyright (c) 2024 MyFintech Payment Service
 * All rights reserved.
 *
 * This software is proprietary and confidential. Unauthorized copying of this file,
 * via any medium, is strictly prohibited.
 * @author : Dhanuka Ranasinghe
 * @since : Date: 11/07/2025
 */
package org.myfintech.payment.domain;

import static org.myfintech.payment.validator.PaymentValidationConstants.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author : Dhanuka Ranasinghe
 * @since : Date: 05/07/2025
 */
public record ClientDTO(
        @NotNull(message = CLIENT_ID_REQUIRED) Long clientId,
        @NotBlank(message = CLIENT_NAME_REQUIRED) String clientName) {}
