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

import org.myfintech.payment.domain.ContractCreateDTO
import org.myfintech.payment.domain.ContractDTO
import org.springframework.stereotype.Component

@Component
class CotractValidator {
    //external validators
    fun validateUpdateRequest(id: Long?, contractDTO: ContractDTO?) {
        return
    }

    fun validateCreate(contractDTO: ContractCreateDTO?) {
        return
    }
}
