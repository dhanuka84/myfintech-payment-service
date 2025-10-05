package org.myfintech.payment.integration

import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.myfintech.payment.PaymentApplication
import org.myfintech.payment.config.TestSecurityConfig
import org.myfintech.payment.domain.ClientCreateDTO
import org.myfintech.payment.domain.ClientDTO
import org.myfintech.payment.repository.ClientRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(
    classes = [PaymentApplication::class, TestSecurityConfig::class],
    webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Ensures clean state between tests
class ClientControllerIntegrationTest : AbstractIntegrationTest() {
    @Autowired
    private val mockMvc: MockMvc? = null

    @Autowired
    private val objectMapper: ObjectMapper? = null // For converting objects to JSON

    @Autowired
    private val clientRepository: ClientRepository? = null // To verify direct DB state if needed

    @BeforeEach
    @Transactional
    fun setUp() {
        // Clear data before each test to ensure test isolation
        clientRepository!!.deleteAll()
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllClients_shouldReturnEmptyList_whenNoClientsExist() {
        mockMvc!!.perform(
            MockMvcRequestBuilders.get("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty())
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllClients_shouldReturnClients() {
        // Arrange: Create a client directly via repository (simulating persistence)
        val newClient1 = ClientCreateDTO("Client A")
        val savedClient1 = createClientViaPost(newClient1)

        mockMvc!!.perform(
            MockMvcRequestBuilders.get("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath<MutableCollection<*>?>("$", Matchers.hasSize<Any?>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].clientName").value("Client A"))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getClientById_shouldReturnClient() {
        // Arrange
        val newClient = ClientCreateDTO("Test Client")
        val savedClient = createClientViaPost(newClient)

        // Act & Assert
        mockMvc!!.perform(
            get("/api/v1/clients/{id}", savedClient.clientId())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.clientId").value(savedClient.clientId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.clientName").value("Test Client"))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getClientById_shouldReturn404_whenClientNotFound() {
        mockMvc!!.perform(
            MockMvcRequestBuilders.get("/api/v1/clients/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Resource Not Found"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("client not found: 999"))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createClient_shouldReturnCreatedClient() {
        val newClient = ClientCreateDTO("New Client Corp")

        mockMvc!!.perform(
            MockMvcRequestBuilders.post("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper!!.writeValueAsString(newClient))
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.clientId").isNumber())
            .andExpect(MockMvcResultMatchers.jsonPath("$.clientName").value("New Client Corp"))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createClient_shouldReturn400_whenClientNameMissing() {
        val invalidClientJson = "{\"clientName\": \"\"}" // Missing clientName

        mockMvc!!.perform(
            MockMvcRequestBuilders.post("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidClientJson)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Validation Failed"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.validationErrors.fieldErrors[0].field").value("clientName"))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.validationErrors.fieldErrors[0].message")
                    .value("Client name is required")
            )
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun updateClient_shouldReturnUpdatedClient() {
        // Arrange: Create a client first
        val initialClient = ClientCreateDTO("Old Name")
        val savedClient = createClientViaPost(initialClient)

        // Act: Update the client
        val updateDTO = ClientDTO(savedClient.clientId(), "Updated Client Name")
        mockMvc!!.perform(
            put("/api/v1/clients/{id}", savedClient.clientId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper!!.writeValueAsString(updateDTO))
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.clientId").value(savedClient.clientId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.clientName").value("Updated Client Name"))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun updateClient_shouldReturn404_whenClientToUpdateNotFound() {
        val updateDTO = ClientDTO(999L, "Non Existent Client")
        mockMvc!!.perform(
            MockMvcRequestBuilders.put("/api/v1/clients/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper!!.writeValueAsString(updateDTO))
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Resource Not Found"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("client not found: 999"))
    }

    // Helper method to create a client via POST request
    @Throws(Exception::class)
    private fun createClientViaPost(clientCreateDTO: ClientCreateDTO?): ClientDTO {
        val responseContent = mockMvc!!.perform(
            MockMvcRequestBuilders.post("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper!!.writeValueAsString(clientCreateDTO))
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andReturn().getResponse().getContentAsString()
        return objectMapper.readValue<ClientDTO>(responseContent, ClientDTO::class.java)
    }
}