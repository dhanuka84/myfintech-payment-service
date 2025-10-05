package org.myfintech.payment.integration

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.myfintech.payment.PaymentApplication
import org.myfintech.payment.config.TestSecurityConfig
import org.myfintech.payment.domain.ClientCreateDTO
import org.myfintech.payment.repository.ClientRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(classes = [PaymentApplication::class, TestSecurityConfig::class])
@AutoConfigureMockMvc
@Transactional
class ClientControllerIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var clientRepository: ClientRepository

    @BeforeEach
    fun setUp() {
        clientRepository.deleteAll()
    }

    @Test
    fun `GET clients returns empty list when no clients exist`() {
        mockMvc.get("/api/v1/clients")
            .andExpect { status { isOk() } }
            .andExpect { jsonPath("$") { isEmpty() } }
    }

    @Test
    fun `POST client creates a new client`() {
        val newClient = ClientCreateDTO("New Client Corp")

        mockMvc.post("/api/v1/clients") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(newClient)
        }.andExpect {
            status { isCreated() }
            jsonPath("$.clientName") { value("New Client Corp") }
            jsonPath("$.clientId") { isNumber() }
        }
    }
}