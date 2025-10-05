package org.myfintech.payment.api.v1

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.myfintech.payment.domain.ContractCreateDTO
import org.myfintech.payment.domain.ContractDTO
import org.myfintech.payment.service.ContractService
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.List

@ExtendWith(MockitoExtension::class)
class ContractControllerTest {
    private var mockMvc: MockMvc? = null

    @Mock
    private val contractService: ContractService? = null

    @InjectMocks
    private val contractController: ContractController? = null

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(contractController).build()
    }

    @Test
    @Throws(Exception::class)
    fun shouldReturnListOfContracts() {
        Mockito.`when`<MutableList<ContractDTO?>?>(contractService!!.findAll()).thenReturn(
            List.of<ContractDTO?>(ContractDTO(1L, 1L, "12345"))
        )

        mockMvc!!.perform(MockMvcRequestBuilders.get("/api/v1/contracts"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].contractId").value(1L))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].clientId").value(1L))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].contractNumber").value("12345"))
    }

    @Test
    @Throws(Exception::class)
    fun shouldReturnContractById() {
        Mockito.`when`<ContractDTO?>(contractService!!.findById(1L)).thenReturn(
            ContractDTO(1L, 1L, "12345")
        )

        mockMvc!!.perform(MockMvcRequestBuilders.get("/api/v1/contracts/1"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.contractId").value(1L))
            .andExpect(MockMvcResultMatchers.jsonPath("$.clientId").value(1L))
            .andExpect(MockMvcResultMatchers.jsonPath("$.contractNumber").value("12345"))
    }

    @Test
    @Throws(Exception::class)
    fun shouldCreateContract() {
        val requestJson = "{\"clientId\":1,\"contractNumber\":\"12345\"}"

        val requestDTO = ContractCreateDTO(1L, "12345")
        val responseDTO = ContractDTO(1L, 1L, "12345")

        Mockito.`when`<ContractDTO?>(contractService!!.save(requestDTO)).thenReturn(responseDTO)

        mockMvc!!.perform(
            MockMvcRequestBuilders.post("/api/v1/contracts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.contractId").value(1L))
            .andExpect(MockMvcResultMatchers.jsonPath("$.clientId").value(1L))
            .andExpect(MockMvcResultMatchers.jsonPath("$.contractNumber").value("12345"))
    }

    @Test
    @Throws(Exception::class)
    fun shouldUpdateContract() {
        val requestJson = "{\"contractId\":1,\"clientId\":2,\"contractNumber\":\"54321\"}"

        val requestDTO = ContractDTO(1L, 2L, "54321")
        val responseDTO = ContractDTO(1L, 2L, "54321")

        Mockito.`when`<ContractDTO?>(contractService!!.update(1L, requestDTO)).thenReturn(responseDTO)

        mockMvc!!.perform(
            MockMvcRequestBuilders.put("/api/v1/contracts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.contractId").value(1L))
            .andExpect(MockMvcResultMatchers.jsonPath("$.clientId").value(2L))
            .andExpect(MockMvcResultMatchers.jsonPath("$.contractNumber").value("54321"))
    }
}
