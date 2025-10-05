package org.myfintech.payment.util.parser

import com.opencsv.CSVReader
import com.opencsv.exceptions.CsvValidationException
import org.myfintech.payment.domain.PaymentDTO
import org.myfintech.payment.exception.FileParsingException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.InputStream
import java.io.InputStreamReader

@Component
class CsvPaymentFileParser : PaymentFileParser {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(CsvPaymentFileParser::class.java)
    }

    override fun supports(filename: String): Boolean {
        return filename.endsWith(".csv", ignoreCase = true)
    }

    override fun parse(inputStream: InputStream): List<PaymentDTO> {
        val payments = mutableListOf<PaymentDTO>()

        try {
            InputStreamReader(inputStream).use { isr ->
                CSVReader(isr).use { reader ->
                    // Skip header row
                    try {
                        reader.readNext()
                    } catch (e: CsvValidationException) {
                        throw IllegalArgumentException("Invalid or missing CSV header", e)
                    }

                    // Process rows sequentially
                    for (row in reader) {
                        if (row.size < 4) {
                            log.warn("Skipping invalid row (too few columns): {}", row.joinToString(","))
                            continue
                        }

                        val amount = row[1]?.trim()?.toDoubleOrNull()
                        if (amount == null) {
                            log.warn("Skipping row due to invalid number format: {}", row.joinToString(","))
                            continue
                        }

                        try {
                            val dto = PaymentDTO(
                                paymentDate = row[0].trim(),
                                amount = amount,
                                type = row[2].trim(),
                                contractNumber = row[3].trim()
                            )
                            payments.add(dto)
                        } catch (e: Exception) {
                            log.warn("Skipping row due to unexpected parsing error: {}", row.joinToString(","), e)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            log.error("Error while parsing CSV file", e)
            // Re-throw as a custom exception for the global handler
            throw FileParsingException("Failed to parse CSV file", e)
        }

        return payments
    }
}