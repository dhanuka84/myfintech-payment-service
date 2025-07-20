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
import jakarta.validation.constraints.NotNull;
import static org.myfintech.payment.validator.PaymentValidationConstants.*;


/**
 * @author : Dhanuka Ranasinghe
 * @since : Date: 05/07/2025
 */
public record ContractDTO(
        @NotNull(message = CONTRACT_ID_REQUIRED) Long contractId,
        @NotNull(message = CLIENT_ID_REQUIRED) Long clientId,
        @NotBlank(message = CONTRACT_NUMBER_REQUIRED) String contractNumber) {}
