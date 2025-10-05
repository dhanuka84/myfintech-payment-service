package org.myfintech.payment.api.v1

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.myfintech.payment.domain.PaymentCreateDTO
import org.myfintech.payment.domain.PaymentDTO
import org.myfintech.payment.service.PaymentFileUploadService
import org.myfintech.payment.service.impl.PaymentServiceFacadeImpl
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.multipart.MultipartFile
import java.nio.charset.StandardCharsets
import java.util.List

@ExtendWith(MockitoExtension::class)
class PaymentControllerTest {
    private var mockMvc: MockMvc? = null

    @Mock
    private val paymentService: PaymentServiceFacadeImpl? = null

    @Mock
    private val uploadService: PaymentFileUploadService? = null

    @InjectMocks
    private val paymentController: PaymentController? = null

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController)
            .setCustomArgumentResolvers(PageableHandlerMethodArgumentResolver())
            .build()
    }

    @Test
    @Throws(Exception::class)
    fun shouldReturnAllPayments() {
        // Create test data
        val payments = List.of<PaymentDTO?>(
            PaymentDTO("2024-01-01", 100.0, "incoming", "C123"),
            PaymentDTO("2024-01-02", 200.0, "outgoing", "C456")
        )

        // Create pageable request
        val pageable: Pageable = PageRequest.of(0, 10)


        // Create a Page object with proper metadata
        val pagedPayments: Page<PaymentDTO?> = PageImpl<PaymentDTO?>(payments, pageable, payments.size.toLong())

        // Mock the service call
        Mockito.`when`<Page<PaymentDTO?>?>(paymentService!!.findAll(ArgumentMatchers.any<Pageable?>(Pageable::class.java)))
            .thenReturn(pagedPayments)

        // Perform the request and verify pagination metadata
        mockMvc!!.perform(
            MockMvcRequestBuilders.get("/api/v1/payments")
                .param("page", "0")
                .param("size", "10")
        )
            .andExpect(MockMvcResultMatchers.status().isOk()) // Verify pagination metadata
            .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.number").value(0))
            .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(10))
            .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfElements").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.first").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.last").value(true)) // Verify content
            .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].contractNumber").value("C123"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].amount").value(100.0))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].contractNumber").value("C456"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].amount").value(200.0))
    }


    @Test
    @Throws(Exception::class)
    fun shouldReturnPaymentById() {
        val dto = PaymentDTO("2024-01-01", 150.0, "incoming", "C789")

        Mockito.`when`<PaymentDTO?>(paymentService!!.findById(1L)).thenReturn(dto)

        mockMvc!!.perform(MockMvcRequestBuilders.get("/api/v1/payments/1"))
            .andExpect(MockMvcResultMatchers.status().isOk()) //.andExpect(jsonPath("$.amount").value(150.0))
            .andExpect(MockMvcResultMatchers.jsonPath("$.contractNumber").value("C789"))
    }

    @Test
    @Throws(Exception::class)
    fun shouldCreatePayment() {
        val request = PaymentCreateDTO("2024-01-03", 300.0, "incoming", "C999", "tr-1")
        val result = PaymentDTO("2024-01-03", 300.0, "incoming", "C999")
        val requestJson = """
            {
              "paymentDate": "2024-01-03",
              "amount": 300.0,
              "type": "incoming",
              "contractNumber": "C999",
              "trackingNumber": "tr-1"
            }
        
        """.trimIndent()

        Mockito.`when`<PaymentDTO?>(paymentService!!.save(ArgumentMatchers.any<PaymentCreateDTO?>(PaymentCreateDTO::class.java)))
            .thenReturn(result)

        mockMvc!!.perform(
            MockMvcRequestBuilders.post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(300.0))
            .andExpect(MockMvcResultMatchers.jsonPath("$.contractNumber").value("C999"))
    }

    @Test
    @Throws(Exception::class)
    fun shouldUpdatePayment() {
        val updated = PaymentDTO("2024-01-04", 400.0, "outgoing", "C321")

        val requestJson = """
            {
              "paymentDate": "2024-01-04",
              "amount": 400.0,
              "type": "outgoing",
              "contractNumber": "C321"
            }
        
        """.trimIndent()

        Mockito.`when`<PaymentDTO?>(
            paymentService!!.update(
                ArgumentMatchers.eq(1L),
                ArgumentMatchers.any<PaymentDTO?>(PaymentDTO::class.java)
            )
        ).thenReturn(updated)

        mockMvc!!.perform(
            MockMvcRequestBuilders.put("/api/v1/payments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(400.0))
            .andExpect(MockMvcResultMatchers.jsonPath("$.type").value("outgoing"))
    }

    @Test
    @Throws(Exception::class)
    fun shouldUploadCsvSuccessfully() {
        val csvContent = """
            payment_date,amount,type,contract_number
            2024-01-10,1000.00,incoming,C001
            2024-01-11,500.00,outgoing,C002
        
        """.trimIndent()

        val file = MockMultipartFile(
            "file", "payments.csv", "text/csv", csvContent.toByteArray()
        )

        val mockPayments = List.of<PaymentDTO?>(
            PaymentDTO("2024-01-10", 1000.00, "incoming", "C001"),
            PaymentDTO("2024-01-11", 500.00, "outgoing", "C002")
        )

        Mockito.`when`<MutableList<PaymentDTO?>?>(
            uploadService!!.processFile(
                ArgumentMatchers.any<MultipartFile?>(
                    MultipartFile::class.java
                )
            )
        ).thenReturn(mockPayments)

        mockMvc!!.perform(MockMvcRequestBuilders.multipart("/api/v1/payments/upload/tr-1").file(file))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.count").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Successfully processed payments"))
    }


    @Test
    @Throws(Exception::class)
    fun shouldReturnBadRequestForEmptyUpload() {
        val emptyFile = MockMultipartFile("file", "empty.csv", "text/csv", ByteArray(0))

        mockMvc!!.perform(MockMvcRequestBuilders.multipart("/api/v1/payments/upload/tr-1").file(emptyFile))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Empty file."))
    }

    @Test
    @Throws(Exception::class)
    fun shouldUploadXmlSuccessfully() {
        // Create XML content matching the expected format
        val xmlContent = """
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
            
            """.trimIndent()

        // Create MockMultipartFile with XML content
        val file = MockMultipartFile(
            "file",
            "payments.xml",
            "application/xml",
            xmlContent.toByteArray(StandardCharsets.UTF_8)
        )

        // Create expected payment DTOs
        val mockPayments = List.of<PaymentDTO?>(
            PaymentDTO("2024-01-10", 1000.00, "incoming", "C001"),
            PaymentDTO("2024-01-11", 500.00, "outgoing", "C002")
        )

        // Mock the service behavior
        Mockito.`when`<MutableList<PaymentDTO?>?>(
            uploadService!!.processFile(
                ArgumentMatchers.any<MultipartFile?>(
                    MultipartFile::class.java
                )
            )
        ).thenReturn(mockPayments)

        // Perform the request and verify the response
        mockMvc!!.perform(MockMvcRequestBuilders.multipart("/api/v1/payments/upload/tr-1").file(file))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.count").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Successfully processed payments"))


        // Verify the service was called
        Mockito.verify<PaymentFileUploadService?>(uploadService, Mockito.times(1))
            .processFile(ArgumentMatchers.any<MultipartFile?>(MultipartFile::class.java))
    }

    @Test
    @Throws(Exception::class)
    fun shouldUploadXmlWithComplexDataSuccessfully() {
        // Test with more complex XML including special characters and decimals
        val xmlContent = """
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
            
            """.trimIndent()

        val file = MockMultipartFile(
            "file",
            "complex-payments.xml",
            "text/xml",  // Alternative content type
            xmlContent.toByteArray(StandardCharsets.UTF_8)
        )

        val mockPayments = List.of<PaymentDTO?>(
            PaymentDTO("2024-01-15", 1500.50, "incoming", "C003-A"),
            PaymentDTO("2024-01-16", 2750.99, "outgoing", "C004-B"),
            PaymentDTO("2024-01-17", 500.00, "incoming", "C005-C")
        )

        Mockito.`when`<MutableList<PaymentDTO?>?>(
            uploadService!!.processFile(
                ArgumentMatchers.any<MultipartFile?>(
                    MultipartFile::class.java
                )
            )
        ).thenReturn(mockPayments)

        mockMvc!!.perform(MockMvcRequestBuilders.multipart("/api/v1/payments/upload/tr-2").file(file))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.count").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Successfully processed payments"))
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
    @Throws(Exception::class)
    fun shouldUploadEmptyXmlFile() {
        // Test with empty payments list
        val emptyXmlContent = """
            <?xml version="1.0" encoding="UTF-8"?>
            <payments>
            </payments>
            
            """.trimIndent()

        val file = MockMultipartFile(
            "file",
            "empty-payments.xml",
            "application/xml",
            emptyXmlContent.toByteArray(StandardCharsets.UTF_8)
        )

        // Return empty list for empty XML
        Mockito.`when`<MutableList<PaymentDTO?>?>(
            uploadService!!.processFile(
                ArgumentMatchers.any<MultipartFile?>(
                    MultipartFile::class.java
                )
            )
        ).thenReturn(
            mutableListOf<PaymentDTO?>()
        )

        mockMvc!!.perform(MockMvcRequestBuilders.multipart("/api/v1/payments/upload/tr-4").file(file))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.count").value(0))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Successfully processed payments"))
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
    @Throws(Exception::class)
    fun shouldHandleLargeXmlFile() {
        // Test with many payments
        val xmlBuilder = StringBuilder()
        xmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<payments>\n")

        val largeMockPayments: MutableList<PaymentDTO?> = ArrayList<PaymentDTO?>()
        for (i in 1..100) {
            xmlBuilder.append(
                String.format(
                    """
                <payment>
                    <payment_date>2024-01-%02d</payment_date>
                    <amount>%d.00</amount>
                    <type>%s</type>
                    <contract_number>C%03d</contract_number>
                </payment>
                
                """.trimIndent(), i % 28 + 1, i * 100, if (i % 2 == 0) "incoming" else "outgoing", i
                )
            )

            largeMockPayments.add(
                PaymentDTO(
                    String.format("2024-01-%02d", i % 28 + 1),
                    i * 100.0,
                    if (i % 2 == 0) "incoming" else "outgoing",
                    String.format("C%03d", i)
                )
            )
        }
        xmlBuilder.append("</payments>")

        val file = MockMultipartFile(
            "file",
            "large-payments.xml",
            "application/xml",
            xmlBuilder.toString().toByteArray(StandardCharsets.UTF_8)
        )

        Mockito.`when`<MutableList<PaymentDTO?>?>(
            uploadService!!.processFile(
                ArgumentMatchers.any<MultipartFile?>(
                    MultipartFile::class.java
                )
            )
        ).thenReturn(largeMockPayments)

        mockMvc!!.perform(MockMvcRequestBuilders.multipart("/api/v1/payments/upload/tr-6").file(file))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.count").value(100))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Successfully processed payments"))
    }

    @Test
    @Throws(Exception::class)
    fun shouldValidateXmlContentType() {
        // Test different XML content types
        val xmlContent = """
            <?xml version="1.0" encoding="UTF-8"?>
            <payments>
                <payment>
                    <payment_date>2024-01-10</payment_date>
                    <amount>1000.00</amount>
                    <type>incoming</type>
                    <contract_number>C001</contract_number>
                </payment>
            </payments>
            
            """.trimIndent()

        // Test with different content types that should be accepted
        val validContentTypes = arrayOf<String?>(
            "application/xml",
            "text/xml",
            "application/x-xml"
        )

        for (contentType in validContentTypes) {
            val file = MockMultipartFile(
                "file",
                "payments.xml",
                contentType,
                xmlContent.toByteArray(StandardCharsets.UTF_8)
            )

            val mockPayments = List.of<PaymentDTO?>(
                PaymentDTO("2024-01-10", 1000.00, "incoming", "C001")
            )

            Mockito.`when`<MutableList<PaymentDTO?>?>(
                uploadService!!.processFile(
                    ArgumentMatchers.any<MultipartFile?>(
                        MultipartFile::class.java
                    )
                )
            ).thenReturn(mockPayments)

            mockMvc!!.perform(MockMvcRequestBuilders.multipart("/api/v1/payments/upload/tr-7").file(file))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.count").value(1))
        }
    }
}
