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
import org.myfintech.payment.domain.ClientCreateDTO;
import org.myfintech.payment.domain.ClientDTO;
import org.myfintech.payment.service.ClientService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class ClientControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private ClientController clientController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(clientController).build();
    }

    @Test
    void shouldReturnListOfClients() throws Exception {
        when(clientService.findAll()).thenReturn(List.of(new ClientDTO(1L, "Acme")));

        mockMvc.perform(get("/api/v1/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clientId").value(1L))
                .andExpect(jsonPath("$[0].clientName").value("Acme"));
    }

    @Test
    void shouldReturnClientById() throws Exception {
        when(clientService.findById(1L)).thenReturn(new ClientDTO(1L, "Acme"));

        mockMvc.perform(get("/api/v1/clients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value(1L))
                .andExpect(jsonPath("$.clientName").value("Acme"));
    }

    @Test
    void shouldCreateClient() throws Exception {
    	ClientCreateDTO input = new ClientCreateDTO("NewClient");
        ClientDTO saved = new ClientDTO(2L, "NewClient");

        when(clientService.save(input)).thenReturn(saved);

        mockMvc.perform(post("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clientId").value(2L))
                .andExpect(jsonPath("$.clientName").value("NewClient"));
    }

    @Test
    void shouldUpdateClient() throws Exception {
        ClientDTO update = new ClientDTO(1L, "UpdatedClient");
        ClientDTO updated = new ClientDTO(1L, "UpdatedClient");

        when(clientService.update(1L, update)).thenReturn(updated);

        mockMvc.perform(put("/api/v1/clients/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value(1L))
                .andExpect(jsonPath("$.clientName").value("UpdatedClient"));
    }
} 
