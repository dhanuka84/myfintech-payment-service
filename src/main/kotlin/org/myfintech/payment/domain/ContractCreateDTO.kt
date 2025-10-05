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

import jakarta.validation.constraints.NotNull
import org.myfintech.payment.validator.CommonValidations.ValidContractNumber

/**
 * @author : Dhanuka Ranasinghe
 * @since : Date: 05/07/2025
 */
data class ContractCreateDTO(
    val clientId: @NotNull(message = "clientId is required") Long,
    @field:ValidContractNumber @param:ValidContractNumber val contractNumber: String
)
