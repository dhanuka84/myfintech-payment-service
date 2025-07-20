package org.myfintech.payment.api.v1;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.myfintech.payment.domain.PaymentCreateDTO;
import org.myfintech.payment.domain.PaymentDTO;
import org.myfintech.payment.service.PaymentFileUploadService;
import org.myfintech.payment.service.impl.PaymentServiceFacadeImpl;
import org.myfintech.payment.validator.PaymentValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class PaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentServiceFacadeImpl paymentService;
    
    @Mock
    private PaymentFileUploadService uploadService;

    @InjectMocks
    private PaymentController paymentController;

    @BeforeEach
    void setUp() {
    	mockMvc = MockMvcBuilders.standaloneSetup(paymentController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void shouldReturnAllPayments() throws Exception {
        // Create test data
        List<PaymentDTO> payments = List.of(
                new PaymentDTO("2024-01-01", 100.0, "incoming", "C123"),
                new PaymentDTO("2024-01-02", 200.0, "outgoing", "C456")
        );

        // Create pageable request
        Pageable pageable = PageRequest.of(0, 10);
        
        // Create a Page object with proper metadata
        Page<PaymentDTO> pagedPayments = new PageImpl<>(payments, pageable, payments.size());

        // Mock the service call
        when(paymentService.findAll(any(Pageable.class))).thenReturn(pagedPayments);

        // Perform the request and verify pagination metadata
        mockMvc.perform(get("/api/v1/payments")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                // Verify pagination metadata
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.numberOfElements").value(2))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true))
                // Verify content
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].contractNumber").value("C123"))
                .andExpect(jsonPath("$.content[0].amount").value(100.0))
                .andExpect(jsonPath("$.content[1].contractNumber").value("C456"))
                .andExpect(jsonPath("$.content[1].amount").value(200.0));
    }



    @Test
    void shouldReturnPaymentById() throws Exception {
        PaymentDTO dto = new PaymentDTO("2024-01-01", 150.0, "incoming", "C789");

        when(paymentService.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/payments/1"))
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$.amount").value(150.0))
                .andExpect(jsonPath("$.contractNumber").value("C789"));
    }

    @Test
    void shouldCreatePayment() throws Exception {
    	PaymentCreateDTO request = new PaymentCreateDTO("2024-01-03", 300.0, "incoming", "C999","tr-1");
    	PaymentDTO result = new PaymentDTO("2024-01-03", 300.0, "incoming", "C999");
        String requestJson = """
            {
              "paymentDate": "2024-01-03",
              "amount": 300.0,
              "type": "incoming",
              "contractNumber": "C999",
              "trackingNumber": "tr-1"
            }
        """;

        when(paymentService.save(any(PaymentCreateDTO.class))).thenReturn(result);

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(300.0))
                .andExpect(jsonPath("$.contractNumber").value("C999"));
    }

    @Test
    void shouldUpdatePayment() throws Exception {
        PaymentDTO updated = new PaymentDTO("2024-01-04", 400.0, "outgoing", "C321");

        String requestJson = """
            {
              "paymentDate": "2024-01-04",
              "amount": 400.0,
              "type": "outgoing",
              "contractNumber": "C321"
            }
        """;

        when(paymentService.update(eq(1L), any(PaymentDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/payments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(400.0))
                .andExpect(jsonPath("$.type").value("outgoing"));
    }

    @Test
    void shouldUploadCsvSuccessfully() throws Exception {
        String csvContent = """
            payment_date,amount,type,contract_number
            2024-01-10,1000.00,incoming,C001
            2024-01-11,500.00,outgoing,C002
        """;

        MockMultipartFile file = new MockMultipartFile(
                "file", "payments.csv", "text/csv", csvContent.getBytes()
        );

        List<PaymentDTO> mockPayments = List.of(
                new PaymentDTO("2024-01-10", 1000.00, "incoming", "C001"),
                new PaymentDTO("2024-01-11", 500.00, "outgoing", "C002")
        );

        when(uploadService.processFile(any(MultipartFile.class))).thenReturn(mockPayments);

        mockMvc.perform(multipart("/api/v1/payments/upload/tr-1").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.message").value("Successfully processed payments"));
    }



    @Test
    void shouldReturnBadRequestForEmptyUpload() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.csv", "text/csv", new byte[0]);

        mockMvc.perform(multipart("/api/v1/payments/upload/tr-1").file(emptyFile))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Empty file."));
    }
    
    @Test
    void shouldUploadXmlSuccessfully() throws Exception {
        // Create XML content matching the expected format
        String xmlContent = """
            <?xml version="1.0" encoding="UTF-8"?>
            <payments>
                <payment>
                    <payment_date>2024-01-10</payment_date>
                    <amount>1000.00</amount>
                    <type>incoming</type>
                    <contract_number>C001</contract_number>
                </payment>
                <payment>
                    <payment_date>2024-01-11</payment_date>
                    <amount>500.00</amount>
                    <type>outgoing</type>
                    <contract_number>C002</contract_number>
                </payment>
            </payments>
            """;

        // Create MockMultipartFile with XML content
        MockMultipartFile file = new MockMultipartFile(
                "file", 
                "payments.xml", 
                "application/xml", 
                xmlContent.getBytes(StandardCharsets.UTF_8)
        );

        // Create expected payment DTOs
        List<PaymentDTO> mockPayments = List.of(
                new PaymentDTO("2024-01-10", 1000.00, "incoming", "C001"),
                new PaymentDTO("2024-01-11", 500.00, "outgoing", "C002")
        );

        // Mock the service behavior
        when(uploadService.processFile(any(MultipartFile.class))).thenReturn(mockPayments);

        // Perform the request and verify the response
        mockMvc.perform(multipart("/api/v1/payments/upload/tr-1").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.message").value("Successfully processed payments"));
        
        // Verify the service was called
        verify(uploadService, times(1)).processFile(any(MultipartFile.class));
    }

    @Test
    void shouldUploadXmlWithComplexDataSuccessfully() throws Exception {
        // Test with more complex XML including special characters and decimals
        String xmlContent = """
            <?xml version="1.0" encoding="UTF-8"?>
            <payments>
                <payment>
                    <payment_date>2024-01-15</payment_date>
                    <amount>1500.50</amount>
                    <type>incoming</type>
                    <contract_number>C003-A</contract_number>
                </payment>
                <payment>
                    <payment_date>2024-01-16</payment_date>
                    <amount>2750.99</amount>
                    <type>outgoing</type>
                    <contract_number>C004-B</contract_number>
                </payment>
                <payment>
                    <payment_date>2024-01-17</payment_date>
                    <amount>500.00</amount>
                    <type>incoming</type>
                    <contract_number>C005-C</contract_number>
                </payment>
            </payments>
            """;

        MockMultipartFile file = new MockMultipartFile(
                "file", 
                "complex-payments.xml", 
                "text/xml", // Alternative content type
                xmlContent.getBytes(StandardCharsets.UTF_8)
        );

        List<PaymentDTO> mockPayments = List.of(
                new PaymentDTO("2024-01-15", 1500.50, "incoming", "C003-A"),
                new PaymentDTO("2024-01-16", 2750.99, "outgoing", "C004-B"),
                new PaymentDTO("2024-01-17", 500.00, "incoming", "C005-C")
        );

        when(uploadService.processFile(any(MultipartFile.class))).thenReturn(mockPayments);

        mockMvc.perform(multipart("/api/v1/payments/upload/tr-2").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(3))
                .andExpect(jsonPath("$.message").value("Successfully processed payments"));
    }

	/*
	 * @Test void shouldRejectInvalidXmlFormat() throws Exception { // Test with
	 * malformed XML String invalidXmlContent = """ <?xml version="1.0"
	 * encoding="UTF-8"?> <payments> <payment>
	 * <payment_date>2024-01-10</payment_date> <amount>not-a-number</amount>
	 * <type>incoming</type> <!-- Missing closing tag --> </payment> </payments>
	 * """;
	 * 
	 * MockMultipartFile file = new MockMultipartFile( "file",
	 * "invalid-payments.xml", "application/xml",
	 * invalidXmlContent.getBytes(StandardCharsets.UTF_8) );
	 * 
	 * // Mock service to throw exception for invalid XML
	 * when(uploadService.processFile(any(MultipartFile.class))) .thenThrow(new
	 * RuntimeException("Invalid XML format"));
	 * 
	 * mockMvc.perform(multipart("/api/v1/payments/upload/tr-3").file(file))
	 * .andExpect(status().isInternalServerError()); }
	 */

    @Test
    void shouldUploadEmptyXmlFile() throws Exception {
        // Test with empty payments list
        String emptyXmlContent = """
            <?xml version="1.0" encoding="UTF-8"?>
            <payments>
            </payments>
            """;

        MockMultipartFile file = new MockMultipartFile(
                "file", 
                "empty-payments.xml", 
                "application/xml", 
                emptyXmlContent.getBytes(StandardCharsets.UTF_8)
        );

        // Return empty list for empty XML
        when(uploadService.processFile(any(MultipartFile.class))).thenReturn(List.of());

        mockMvc.perform(multipart("/api/v1/payments/upload/tr-4").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(0))
                .andExpect(jsonPath("$.message").value("Successfully processed payments"));
    }

	/*
	 * @Test void shouldRejectNonXmlFileWithXmlExtension() throws Exception { //
	 * Test with non-XML content but .xml extension String nonXmlContent =
	 * "This is not XML content";
	 * 
	 * MockMultipartFile file = new MockMultipartFile( "file", "fake.xml",
	 * "application/xml", nonXmlContent.getBytes() );
	 * 
	 * when(uploadService.processFile(any(MultipartFile.class))) .thenThrow(new
	 * RuntimeException("Failed to parse XML payment file"));
	 * 
	 * mockMvc.perform(multipart("/api/v1/payments/upload/tr-5").file(file))
	 * .andExpect(status().isInternalServerError()); }
	 */

    @Test
    void shouldHandleLargeXmlFile() throws Exception {
        // Test with many payments
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<payments>\n");
        
        List<PaymentDTO> largeMockPayments = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            xmlBuilder.append(String.format("""
                <payment>
                    <payment_date>2024-01-%02d</payment_date>
                    <amount>%d.00</amount>
                    <type>%s</type>
                    <contract_number>C%03d</contract_number>
                </payment>
                """, i % 28 + 1, i * 100, i % 2 == 0 ? "incoming" : "outgoing", i));
            
            largeMockPayments.add(new PaymentDTO(
                String.format("2024-01-%02d", i % 28 + 1),
                i * 100.0,
                i % 2 == 0 ? "incoming" : "outgoing",
                String.format("C%03d", i)
            ));
        }
        xmlBuilder.append("</payments>");

        MockMultipartFile file = new MockMultipartFile(
                "file", 
                "large-payments.xml", 
                "application/xml", 
                xmlBuilder.toString().getBytes(StandardCharsets.UTF_8)
        );

        when(uploadService.processFile(any(MultipartFile.class))).thenReturn(largeMockPayments);

        mockMvc.perform(multipart("/api/v1/payments/upload/tr-6").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(100))
                .andExpect(jsonPath("$.message").value("Successfully processed payments"));
    }

    @Test
    void shouldValidateXmlContentType() throws Exception {
        // Test different XML content types
        String xmlContent = """
            <?xml version="1.0" encoding="UTF-8"?>
            <payments>
                <payment>
                    <payment_date>2024-01-10</payment_date>
                    <amount>1000.00</amount>
                    <type>incoming</type>
                    <contract_number>C001</contract_number>
                </payment>
            </payments>
            """;

        // Test with different content types that should be accepted
        String[] validContentTypes = {
            "application/xml",
            "text/xml",
            "application/x-xml"
        };

        for (String contentType : validContentTypes) {
            MockMultipartFile file = new MockMultipartFile(
                    "file", 
                    "payments.xml", 
                    contentType, 
                    xmlContent.getBytes(StandardCharsets.UTF_8)
            );

            List<PaymentDTO> mockPayments = List.of(
                    new PaymentDTO("2024-01-10", 1000.00, "incoming", "C001")
            );

            when(uploadService.processFile(any(MultipartFile.class))).thenReturn(mockPayments);

            mockMvc.perform(multipart("/api/v1/payments/upload/tr-7").file(file))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.count").value(1));
        }
    }
}
