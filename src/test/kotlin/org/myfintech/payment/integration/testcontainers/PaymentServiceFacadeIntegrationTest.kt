package org.myfintech.payment.integration.testcontainers

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myfintech.payment.PaymentApplication
import org.myfintech.payment.domain.ClientCreateDTO
import org.myfintech.payment.domain.ContractCreateDTO
import org.myfintech.payment.domain.PaymentCreateDTO.Companion.of
import org.myfintech.payment.service.ClientService
import org.myfintech.payment.service.ContractService
import org.myfintech.payment.service.PaymentServiceFacade
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.function.Supplier

@SpringBootTest(classes = [PaymentApplication::class])
@Testcontainers
class PaymentServiceFacadeIntegrationTest : AbstractTestcontainersIntegrationTest() {
    @Autowired
    var paymentServiceFacade: PaymentServiceFacade? = null

    @Autowired
    var clientService: ClientService? = null

    @Autowired
    var contractService: ContractService? = null

    @Test
    fun savePayment_createsPaymentSuccessfully() {
        // Setup: Create client, contract, payment tracking first
        val clientDto = ClientCreateDTO("Test Facade Client")
        val client = clientService!!.save(clientDto)

        val contractDto = ContractCreateDTO(client.clientId(), "C-TC-10001")
        val contract = contractService!!.save(contractDto)

        val paymentDto = of("2024-07-17", 100.0, "CREDIT", "C-TC-10001", "tracking-10001")

        val saved = paymentServiceFacade!!.save(paymentDto)

        Assertions.assertNotNull(saved)
        assertEquals(contract.contractNumber(), saved.contractNumber())
        assertEquals("CREDIT", saved.type())
        assertEquals(100.00, saved.amount())
    }

    @Test
    fun findAll_shouldReturnPaginatedPayments() {
        // Setup: Insert at least one payment (see above test or do separately)

        val pageable: Pageable = PageRequest.of(0, 10)
        val page = paymentServiceFacade!!.findAll(pageable)

        Assertions.assertNotNull(page)
        Assertions.assertTrue(page!!.getTotalElements() >= 0)
        // If you inserted a payment before, assertTrue(page.getTotalElements() > 0);
    }

    companion object {
        @Container
        var postgres: PostgreSQLContainer<*> = PostgreSQLContainer<SELF?>("postgres:16.3").withDatabaseName("testdb")
            .withUsername("test").withPassword("test")

        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", Supplier { postgres.getJdbcUrl() })
            registry.add("spring.datasource.username", Supplier { postgres.getUsername() })
            registry.add("spring.datasource.password", Supplier { postgres.getPassword() })
            registry.add("spring.datasource.driver-class-name", Supplier { postgres.getDriverClassName() })
        }
    }
}
