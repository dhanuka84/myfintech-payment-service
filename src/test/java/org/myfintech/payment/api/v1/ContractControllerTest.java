package org.myfintech.payment.api.v1;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.myfintech.payment.domain.ContractCreateDTO;
import org.myfintech.payment.domain.ContractDTO;
import org.myfintech.payment.service.ContractService;
import org.myfintech.payment.validator.CotractValidator;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class ContractControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ContractService contractService;

    @InjectMocks
    private ContractController contractController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(contractController).build();
    }

    @Test
    void shouldReturnListOfContracts() throws Exception {
        when(contractService.findAll()).thenReturn(
            List.of(new ContractDTO(1L, 1L, "12345"))
        );

        mockMvc.perform(get("/api/v1/contracts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].contractId").value(1L))
                .andExpect(jsonPath("$[0].clientId").value(1L))
                .andExpect(jsonPath("$[0].contractNumber").value("12345"));
    }

    @Test
    void shouldReturnContractById() throws Exception {
        when(contractService.findById(1L)).thenReturn(
            new ContractDTO(1L, 1L, "12345")
        );

        mockMvc.perform(get("/api/v1/contracts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contractId").value(1L))
                .andExpect(jsonPath("$.clientId").value(1L))
                .andExpect(jsonPath("$.contractNumber").value("12345"));
    }

    @Test
    void shouldCreateContract() throws Exception {
        String requestJson = "{\"clientId\":1,\"contractNumber\":\"12345\"}";

        ContractCreateDTO requestDTO = new ContractCreateDTO( 1L, "12345");
        ContractDTO responseDTO = new ContractDTO(1L, 1L, "12345");

        when(contractService.save(requestDTO)).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/contracts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.contractId").value(1L))
                .andExpect(jsonPath("$.clientId").value(1L))
                .andExpect(jsonPath("$.contractNumber").value("12345"));
    }

    @Test
    void shouldUpdateContract() throws Exception {
        String requestJson = "{\"contractId\":1,\"clientId\":2,\"contractNumber\":\"54321\"}";

        ContractDTO requestDTO = new ContractDTO(1L, 2L, "54321");
        ContractDTO responseDTO = new ContractDTO(1L, 2L, "54321");

        when(contractService.update(1L, requestDTO)).thenReturn(responseDTO);

        mockMvc.perform(put("/api/v1/contracts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contractId").value(1L))
                .andExpect(jsonPath("$.clientId").value(2L))
                .andExpect(jsonPath("$.contractNumber").value("54321"));
    }
}
