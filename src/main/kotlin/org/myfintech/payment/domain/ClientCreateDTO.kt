package org.myfintech.payment.domain

import jakarta.validation.constraints.NotBlank
import org.myfintech.payment.validator.PaymentValidationConstants

data class ClientCreateDTO(
    @field:NotBlank(message = PaymentValidationConstants.CLIENT_NAME_REQUIRED)
    val clientName: String
)