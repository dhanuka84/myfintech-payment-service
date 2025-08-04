package org.myfintech.payment.util.parser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.myfintech.payment.domain.PaymentDTO;
import org.myfintech.payment.exception.FileParsingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

@Component
public class CsvPaymentFileParser implements PaymentFileParser {

    private static final Logger log = LoggerFactory.getLogger(CsvPaymentFileParser.class);
    private static final int INITIAL_CAPACITY = 10000;

    @Override
    public boolean supports(String filename) {
        return filename != null && filename.toLowerCase().endsWith(".csv");
    }

    @Override
    public List<PaymentDTO> parse(InputStream inputStream) throws FileParsingException {
        List<PaymentDTO> payments = new ArrayList<>(INITIAL_CAPACITY);

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             CSVReader reader = new CSVReader(bufferedReader)) {

            // Skip header
            try {
                reader.readNext();
            } catch (CsvValidationException e) {
                throw new IllegalArgumentException("Invalid CSV header", e);
            }

            String[] row;
            while ((row = reader.readNext()) != null) {
                if (row.length < 4) {
                    log.warn("Skipping invalid row (too few columns): {}", (Object) row);
                    continue;
                }

                try {
                    PaymentDTO dto = new PaymentDTO(
                            row[0].trim(),
                            Double.parseDouble(row[1].trim()),
                            row[2].trim(),
                            row[3].trim()
                    );
                    payments.add(dto);
                } catch (NumberFormatException e) {
                    log.warn("Skipping row due to invalid number format: {}", String.join(",", row), e);
                } catch (Exception e) {
                    log.warn("Skipping row due to unexpected parsing error: {}", String.join(",", row), e);
                }
            }

        } catch (Exception e) {
            log.error("Error while parsing CSV file", e);
            throw new FileParsingException("Failed to parse CSV file", e); // rethrow to let upstream handle if necessary
        }

        return payments;
    }
}
