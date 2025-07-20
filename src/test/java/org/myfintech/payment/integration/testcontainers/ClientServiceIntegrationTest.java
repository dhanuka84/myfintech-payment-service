package org.myfintech.payment.integration.testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.myfintech.payment.PaymentApplication;
import org.myfintech.payment.domain.ClientCreateDTO;
import org.myfintech.payment.domain.ClientDTO;
import org.myfintech.payment.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = PaymentApplication.class)
@Testcontainers
public class ClientServiceIntegrationTest extends AbstractTestcontainersIntegrationTest{

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.3")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
        // If using Liquibase
        // registry.add("spring.liquibase.enabled", () -> true);
    }

    @Autowired
    ClientService clientService;

    @Test
    void saveClient_createsClientSuccessfully() {
        ClientCreateDTO dto = new ClientCreateDTO("Testcontainers User");
        ClientDTO saved = clientService.save(dto);

        assertNotNull(saved);
        assertNotNull(saved.clientId());
        assertEquals("Testcontainers User", saved.clientName());
    }
}
