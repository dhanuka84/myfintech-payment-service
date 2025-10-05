package org.myfintech.payment.service

import org.myfintech.payment.domain.PaymentDTO
import org.myfintech.payment.exception.FileProcessingException
import org.myfintech.payment.util.parser.PaymentFileParser
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class PaymentFileUploadService(private val parsers: List<PaymentFileParser>) {

    fun processFile(file: MultipartFile): List<PaymentDTO> {
        val filename = file.originalFilename
            ?: throw FileProcessingException("File name is missing.", file.name)

        // Find the first parser that supports the file extension
        val parser = parsers.firstOrNull { it.supports(filename) }
            ?: throw IllegalArgumentException("Unsupported file format for: $filename")

        // Use the found parser to parse the file
        return try {
            file.inputStream.use { inputStream ->
                parser.parse(inputStream)
            }
        } catch (e: Exception) {
            // Wrap any parsing exception in a custom, more specific exception
            throw FileProcessingException("Failed to parse file: ${e.message}", e)
        }
    }
}