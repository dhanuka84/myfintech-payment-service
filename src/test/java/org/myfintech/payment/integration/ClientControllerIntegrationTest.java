package org.myfintech.payment.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.myfintech.payment.PaymentApplication;
import org.myfintech.payment.config.TestSecurityConfig;
import org.myfintech.payment.domain.ClientCreateDTO;
import org.myfintech.payment.domain.ClientDTO;
import org.myfintech.payment.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(classes = { PaymentApplication.class, TestSecurityConfig.class }, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Ensures clean state between tests
public class ClientControllerIntegrationTest extends AbstractIntegrationTest{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // For converting objects to JSON

    @Autowired
    private ClientRepository clientRepository; // To verify direct DB state if needed

    @BeforeEach
    @Transactional // Ensure setup runs in a transaction and rolls back
    void setUp() {
        // Clear data before each test to ensure test isolation
        clientRepository.deleteAll();
    }

    @Test
    @Transactional // Ensures the test runs in a transaction and rolls back
    void getAllClients_shouldReturnEmptyList_whenNoClientsExist() throws Exception {
        mockMvc.perform(get("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @Transactional
    void getAllClients_shouldReturnClients() throws Exception {
        // Arrange: Create a client directly via repository (simulating persistence)
        ClientCreateDTO newClient1 = new ClientCreateDTO("Client A");
        ClientDTO savedClient1 = createClientViaPost(newClient1);

        mockMvc.perform(get("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].clientName").value("Client A"));
    }

    @Test
    @Transactional
    void getClientById_shouldReturnClient() throws Exception {
        // Arrange
        ClientCreateDTO newClient = new ClientCreateDTO("Test Client");
        ClientDTO savedClient = createClientViaPost(newClient);

        // Act & Assert
        mockMvc.perform(get("/api/v1/clients/{id}", savedClient.clientId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value(savedClient.clientId()))
                .andExpect(jsonPath("$.clientName").value("Test Client"));
    }

    @Test
    @Transactional
    void getClientById_shouldReturn404_whenClientNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/clients/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value("client not found: 999"));
    }

    @Test
    @Transactional
    void createClient_shouldReturnCreatedClient() throws Exception {
        ClientCreateDTO newClient = new ClientCreateDTO("New Client Corp");

        mockMvc.perform(post("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newClient)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clientId").isNumber())
                .andExpect(jsonPath("$.clientName").value("New Client Corp"));
    }

    @Test
    @Transactional
    void createClient_shouldReturn400_whenClientNameMissing() throws Exception {
        String invalidClientJson = "{\"clientName\": \"\"}"; // Missing clientName

        mockMvc.perform(post("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidClientJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.validationErrors.fieldErrors[0].field").value("clientName"))
                .andExpect(jsonPath("$.validationErrors.fieldErrors[0].message").value("Client name is required"));
    }

    @Test
    @Transactional
    void updateClient_shouldReturnUpdatedClient() throws Exception {
        // Arrange: Create a client first
        ClientCreateDTO initialClient = new ClientCreateDTO("Old Name");
        ClientDTO savedClient = createClientViaPost(initialClient);

        // Act: Update the client
        ClientDTO updateDTO = new ClientDTO(savedClient.clientId(), "Updated Client Name");
        mockMvc.perform(put("/api/v1/clients/{id}", savedClient.clientId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value(savedClient.clientId()))
                .andExpect(jsonPath("$.clientName").value("Updated Client Name"));
    }

    @Test
    @Transactional
    void updateClient_shouldReturn404_whenClientToUpdateNotFound() throws Exception {
        ClientDTO updateDTO = new ClientDTO(999L, "Non Existent Client");
        mockMvc.perform(put("/api/v1/clients/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value("client not found: 999"));
    }

    // Helper method to create a client via POST request
    private ClientDTO createClientViaPost(ClientCreateDTO clientCreateDTO) throws Exception {
        String responseContent = mockMvc.perform(post("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clientCreateDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(responseContent, ClientDTO.class);
    }
}