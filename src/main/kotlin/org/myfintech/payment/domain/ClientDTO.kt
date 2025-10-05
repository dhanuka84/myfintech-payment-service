package org.myfintech.payment.domain

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.myfintech.payment.validator.PaymentValidationConstants

data class ClientDTO(
    @field:NotNull(message = PaymentValidationConstants.CLIENT_ID_REQUIRED)
    val clientId: Long,

    @field:NotBlank(message = PaymentValidationConstants.CLIENT_NAME_REQUIRED)
    val clientName: String
)