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

import jakarta.validation.constraints.NotBlank;
import static org.myfintech.payment.validator.PaymentValidationConstants.*;

public record ClientCreateDTO(@NotBlank(message = CLIENT_NAME_REQUIRED) String clientName) {}
