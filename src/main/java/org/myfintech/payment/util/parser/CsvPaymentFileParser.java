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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.myfintech.payment.domain.PaymentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

@Component
public class CsvPaymentFileParser implements PaymentFileParser {

    private static final Logger log = LoggerFactory.getLogger(CsvPaymentFileParser.class);
    private static final int INITIAL_CAPACITY = 10000;  // Estimate; adjust based on typical file size

    @Override
    public boolean supports(String filename) {
        return filename.toLowerCase().endsWith(".csv");
    }

    @Override
    public List<PaymentDTO> parse(InputStream inputStream) throws Exception {
        // Buffer for faster I/O
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        CSVReader reader = new CSVReader(bufferedReader);

        // Skip header
        try {
            reader.readNext();
        } catch (CsvValidationException e) {
            throw new IllegalArgumentException("Invalid CSV header", e);
        }

        // Return a stream for lazy processing (callers can limit/collect as needed)
        return Stream.generate(() -> {
            try {
                String[] row = reader.readNext();
                if (row == null || row.length < 4) {
                    return null;  // End stream on invalid/EOF
                }
                // Trim and parse with local try-catch for resilience
                try {
                    return new PaymentDTO(
                            row[0].trim(),
                            Double.parseDouble(row[1].trim()),
                            row[2].trim(),
                            row[3].trim());
                } catch (NumberFormatException e) {
                    log.warn("Skipping invalid row due to parsing error: {}", String.join(",", row), e);
                    return null;
                }
            } catch (Exception e) {
                log.error("Error reading CSV row", e);
                return null;
            }
         // Stop on null
        }).takeWhile(dto -> dto != null).collect(Collectors.toCollection(() -> new ArrayList<>(INITIAL_CAPACITY))); 
    }
}