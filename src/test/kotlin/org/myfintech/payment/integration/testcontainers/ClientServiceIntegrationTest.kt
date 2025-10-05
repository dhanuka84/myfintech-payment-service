package org.myfintech.payment.integration.testcontainers

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myfintech.payment.PaymentApplication
import org.myfintech.payment.domain.ClientCreateDTO
import org.myfintech.payment.service.ClientService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.function.Supplier

@SpringBootTest(classes = [PaymentApplication::class])
@Testcontainers
class ClientServiceIntegrationTest : AbstractTestcontainersIntegrationTest() {
    @Autowired
    var clientService: ClientService? = null

    @Test
    fun saveClient_createsClientSuccessfully() {
        val dto = ClientCreateDTO("Testcontainers User")
        val saved = clientService!!.save(dto)

        Assertions.assertNotNull(saved)
        Assertions.assertNotNull(saved.clientId())
        assertEquals("Testcontainers User", saved.clientName())
    }

    companion object {
        @Container
        var postgres: PostgreSQLContainer<*> = PostgreSQLContainer<SELF?>("postgres:16.3")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")

        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", Supplier { postgres.getJdbcUrl() })
            registry.add("spring.datasource.username", Supplier { postgres.getUsername() })
            registry.add("spring.datasource.password", Supplier { postgres.getPassword() })
            registry.add("spring.datasource.driver-class-name", Supplier { postgres.getDriverClassName() })
            // If using Liquibase
            // registry.add("spring.liquibase.enabled", () -> true);
        }
    }
}
