package org.myfintech.payment.integration.testcontainers

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
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(classes = [PaymentApplication::class])
@Testcontainers
class ClientServiceIntegrationTest : AbstractTestcontainersIntegrationTest() {

    @Autowired
    private lateinit var clientService: ClientService

    @Test
    fun `saveClient creates client successfully`() {
        val dto = ClientCreateDTO("Testcontainers User")
        val saved = clientService.save(dto)

        assertNotNull(saved)
        assertNotNull(saved.clientId)
        assertEquals("Testcontainers User", saved.clientName)
    }

    companion object {
        @Container
        val postgres = PostgreSQLContainer<Nothing>("postgres:16.3").apply {
            withDatabaseName("testdb")
            withUsername("test")
            withPassword("test")
        }

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }
    }
}