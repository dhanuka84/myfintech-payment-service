package org.myfintech.payment.service;

import java.util.List;

import org.myfintech.payment.domain.PaymentDTO;
import org.myfintech.payment.exception.FileProcessingException;
import org.myfintech.payment.util.parser.PaymentFileParser;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PaymentFileUploadService {

    private final List<PaymentFileParser> parsers;

    public PaymentFileUploadService(List<PaymentFileParser> parsers) {
        this.parsers = parsers;
    }

    public List<PaymentDTO> processFile(MultipartFile file) {
        String filename = file.getOriginalFilename();

        return parsers.stream()
            .filter(parser -> parser.supports(filename))
            .findFirst()
            .map(parser -> {
                try {
                    return parser.parse(file.getInputStream());
                } catch (Exception e) {
                    throw new FileProcessingException("Failed to parse file: " + e.getMessage(), e);
                }
            })
            .orElseThrow(() -> new IllegalArgumentException("Unsupported file format"));
    }

}
