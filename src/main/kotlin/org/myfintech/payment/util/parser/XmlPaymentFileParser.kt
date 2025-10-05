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

import jakarta.xml.bind.Unmarshaller
import org.myfintech.payment.domain.PaymentDTO
import org.myfintech.payment.domain.PaymentDTOListWrapper
import org.myfintech.payment.exception.FileParsingException
import org.myfintech.payment.util.jaxb.JAXBContextPool
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.InputStream

/**
 * XML payment file parser that uses JAXBContext pooling for improved performance.
 */
@Component
class XmlPaymentFileParser(
    private val jaxbContextPool: JAXBContextPool
) : PaymentFileParser {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(XmlPaymentFileParser::class.java)
    }

    override fun supports(filename: String): Boolean {
        return filename.endsWith(".xml", ignoreCase = true)
    }

    override fun parse(inputStream: InputStream): List<PaymentDTO> {
        var unmarshaller: Unmarshaller? = null
        try {
            // Borrow unmarshaller from the pool
            unmarshaller = jaxbContextPool.borrowUnmarshaller(PaymentDTOListWrapper::class.java)

            // Parse XML and perform a safe cast
            val wrapper = unmarshaller.unmarshal(inputStream) as? PaymentDTOListWrapper

            // Use Kotlin's requireNotNull for a concise null check and clear error message
            requireNotNull(wrapper?.payments) { "Invalid XML format: no payments found" }

            log.debug("Successfully parsed {} payments from XML", wrapper.payments.size)
            return wrapper.payments

        } catch (e: Exception) {
            log.error("Error parsing XML payment file", e)
            throw FileParsingException("Failed to parse XML payment file", e)
        } finally {
            // Always return the unmarshaller to the pool if it was borrowed
            unmarshaller?.let {
                jaxbContextPool.returnUnmarshaller(PaymentDTOListWrapper::class.java, it)
            }
        }
    }
}