package org.myfintech.payment.api.v1

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.myfintech.payment.domain.ClientCreateDTO
import org.myfintech.payment.domain.ClientDTO
import org.myfintech.payment.service.ClientService
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockitoExtension::class)
class ClientControllerTest {

    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var clientService: ClientService

    @InjectMocks
    private lateinit var clientController: ClientController

    private val objectMapper = ObjectMapper()

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(clientController).build()
    }

    @Test
    fun `should return list of clients`() {
        whenever(clientService.findAll()).thenReturn(listOf(ClientDTO(1L, "Acme")))

        mockMvc.perform(get("/api/v1/clients"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].clientId").value(1L))
            .andExpect(jsonPath("$[0].clientName").value("Acme"))
    }

    @Test
    fun `should return client by id`() {
        whenever(clientService.findById(1L)).thenReturn(ClientDTO(1L, "Acme"))

        mockMvc.perform(get("/api/v1/clients/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.clientId").value(1L))
            .andExpect(jsonPath("$.clientName").value("Acme"))
    }

    @Test
    fun `should create client`() {
        val input = ClientCreateDTO("NewClient")
        val saved = ClientDTO(2L, "NewClient")
        whenever(clientService.save(input)).thenReturn(saved)

        mockMvc.perform(post("/api/v1/clients")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.clientId").value(2L))
            .andExpect(jsonPath("$.clientName").value("NewClient"))
    }

    @Test
    fun `should update client`() {
        val update = ClientDTO(1L, "UpdatedClient")
        whenever(clientService.update(1L, update)).thenReturn(update)

        mockMvc.perform(put("/api/v1/clients/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(update)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.clientId").value(1L))
            .andExpect(jsonPath("$.clientName").value("UpdatedClient"))
    }
}