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
import org.myfintech.payment.domain.PaymentDTOListWrapper;
import org.myfintech.payment.exception.FileParsingException;
import org.myfintech.payment.util.jaxb.JAXBContextPool;
import org.springframework.stereotype.Component;

import jakarta.xml.bind.Unmarshaller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * XML payment file parser that uses JAXBContext pooling for improved performance.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class XmlPaymentFileParser implements PaymentFileParser {
    
    private final JAXBContextPool jaxbContextPool;
    
    @Override
    public boolean supports(String filename) {
        return filename != null && filename.toLowerCase().endsWith(".xml");
    }
    
    @Override
    public List<PaymentDTO> parse(InputStream inputStream) throws Exception {
        Unmarshaller unmarshaller = null;
        try {
            // Borrow unmarshaller from pool
            unmarshaller = jaxbContextPool.borrowUnmarshaller(PaymentDTOListWrapper.class);
            
            // Parse XML
            PaymentDTOListWrapper wrapper = (PaymentDTOListWrapper) unmarshaller.unmarshal(inputStream);
            
            if (wrapper == null || wrapper.getPayments() == null) {
                throw new IllegalArgumentException("Invalid XML format: no payments found");
            }
            
            log.debug("Successfully parsed {} payments from XML", wrapper.getPayments().size());
            return wrapper.getPayments();
            
        } catch (Exception e) {
            log.error("Error parsing XML payment file", e);
            throw new FileParsingException("Failed to parse XML payment file", e);
        } finally {
            // Always return unmarshaller to pool
            if (unmarshaller != null) {
                jaxbContextPool.returnUnmarshaller(PaymentDTOListWrapper.class, unmarshaller);
            }
        }
    }
}