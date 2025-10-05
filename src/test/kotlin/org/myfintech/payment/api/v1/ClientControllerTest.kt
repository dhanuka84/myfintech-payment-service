package org.myfintech.payment.api.v1

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.myfintech.payment.domain.ClientCreateDTO
import org.myfintech.payment.domain.ClientDTO
import org.myfintech.payment.service.ClientService
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.List

@ExtendWith(MockitoExtension::class)
class ClientControllerTest {
    private var mockMvc: MockMvc? = null

    @Mock
    private val clientService: ClientService? = null

    @InjectMocks
    private val clientController: ClientController? = null

    private val objectMapper = ObjectMapper()

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(clientController).build()
    }

    @Test
    @Throws(Exception::class)
    fun shouldReturnListOfClients() {
        Mockito.`when`<MutableList<ClientDTO?>?>(clientService!!.findAll())
            .thenReturn(List.of<ClientDTO?>(ClientDTO(1L, "Acme")))

        mockMvc!!.perform(MockMvcRequestBuilders.get("/api/v1/clients"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].clientId").value(1L))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].clientName").value("Acme"))
    }

    @Test
    @Throws(Exception::class)
    fun shouldReturnClientById() {
        Mockito.`when`<ClientDTO?>(clientService!!.findById(1L)).thenReturn(ClientDTO(1L, "Acme"))

        mockMvc!!.perform(MockMvcRequestBuilders.get("/api/v1/clients/1"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.clientId").value(1L))
            .andExpect(MockMvcResultMatchers.jsonPath("$.clientName").value("Acme"))
    }

    @Test
    @Throws(Exception::class)
    fun shouldCreateClient() {
        val input = ClientCreateDTO("NewClient")
        val saved = ClientDTO(2L, "NewClient")

        Mockito.`when`<ClientDTO?>(clientService!!.save(input)).thenReturn(saved)

        mockMvc!!.perform(
            MockMvcRequestBuilders.post("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input))
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.clientId").value(2L))
            .andExpect(MockMvcResultMatchers.jsonPath("$.clientName").value("NewClient"))
    }

    @Test
    @Throws(Exception::class)
    fun shouldUpdateClient() {
        val update = ClientDTO(1L, "UpdatedClient")
        val updated = ClientDTO(1L, "UpdatedClient")

        Mockito.`when`<ClientDTO?>(clientService!!.update(1L, update)).thenReturn(updated)

        mockMvc!!.perform(
            MockMvcRequestBuilders.put("/api/v1/clients/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update))
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.clientId").value(1L))
            .andExpect(MockMvcResultMatchers.jsonPath("$.clientName").value("UpdatedClient"))
    }
}
