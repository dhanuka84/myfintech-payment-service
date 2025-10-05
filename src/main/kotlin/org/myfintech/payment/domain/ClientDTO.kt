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
import jakarta.validation.constraints.NotNull
import org.myfintech.payment.validator.PaymentValidationConstants

@JvmRecord
data class ClientDTO(
    @JvmField val clientId: @NotNull(message = PaymentValidationConstants.CLIENT_ID_REQUIRED) Long,
    @JvmField val clientName: @NotBlank(message = PaymentValidationConstants.CLIENT_NAME_REQUIRED) String
)
