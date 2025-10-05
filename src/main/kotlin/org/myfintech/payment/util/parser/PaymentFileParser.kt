/*
 * Copyright (c) 2024 MyFintech Payment Service
 * All rights reserved.
 *
 * This software is proprietary and confidential. Unauthorized copying of this file,
 * via any medium, is strictly prohibited.
 * @author : Dhanuka Ranasinghe
 * @since : Date: 11/07/2025
 */
package org.myfintech.payment.util.parser

import org.myfintech.payment.domain.PaymentDTO
import java.io.InputStream

interface PaymentFileParser {
    @Throws(Exception::class)
    fun parse(inputStream: InputStream): List<PaymentDTO>

    fun supports(filename: String): Boolean
}
