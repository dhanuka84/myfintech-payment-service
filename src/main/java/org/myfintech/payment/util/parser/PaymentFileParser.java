/*
 * Copyright (c) 2024 MyFintech Payment Service
 * All rights reserved.
 *
 * This software is proprietary and confidential. Unauthorized copying of this file,
 * via any medium, is strictly prohibited.
 * @author : Dhanuka Ranasinghe
 * @since : Date: 11/07/2025
 */
package org.myfintech.payment.util.parser;

import java.io.InputStream;
import java.util.List;

import org.myfintech.payment.domain.PaymentDTO;

public interface PaymentFileParser {
    List<PaymentDTO> parse(InputStream inputStream) throws Exception;

    boolean supports(String filename);
}
