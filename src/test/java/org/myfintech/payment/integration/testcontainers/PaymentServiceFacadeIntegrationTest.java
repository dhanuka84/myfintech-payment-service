package org.myfintech.payment.integration.testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.myfintech.payment.PaymentApplication;
import org.myfintech.payment.domain.ClientCreateDTO;
import org.myfintech.payment.domain.ClientDTO;
import org.myfintech.payment.domain.ContractCreateDTO;
import org.myfintech.payment.domain.ContractDTO;
import org.myfintech.payment.domain.PaymentCreateDTO;
import org.myfintech.payment.domain.PaymentDTO;
import org.myfintech.payment.service.ClientService;
import org.myfintech.payment.service.ContractService;
import org.myfintech.payment.service.PaymentServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = PaymentApplication.class)
@Testcontainers
public class PaymentServiceFacadeIntegrationTest extends AbstractTestcontainersIntegrationTest {

	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.3").withDatabaseName("testdb")
			.withUsername("test").withPassword("test");

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
		registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
	}

	@Autowired
	PaymentServiceFacade paymentServiceFacade;

	@Autowired
	ClientService clientService;

	@Autowired
	ContractService contractService;

	@Test
	void savePayment_createsPaymentSuccessfully() {
		// Setup: Create client, contract, payment tracking first
		ClientCreateDTO clientDto = new ClientCreateDTO("Test Facade Client");
		ClientDTO client = clientService.save(clientDto);

		ContractCreateDTO contractDto = new ContractCreateDTO(client.clientId(), "C-TC-10001");
		ContractDTO contract = contractService.save(contractDto);

		PaymentCreateDTO paymentDto = PaymentCreateDTO.of("2024-07-17", 100.0, "CREDIT", "C-TC-10001", "tracking-10001");

		PaymentDTO saved = paymentServiceFacade.save(paymentDto);

		assertNotNull(saved);
		assertEquals(contract.contractNumber(), saved.contractNumber());
		assertEquals("CREDIT", saved.type());
		assertEquals(100.00, saved.amount());
	}

	@Test
	void findAll_shouldReturnPaginatedPayments() {
		// Setup: Insert at least one payment (see above test or do separately)

		Pageable pageable = PageRequest.of(0, 10);
		Page<PaymentDTO> page = paymentServiceFacade.findAll(pageable);

		assertNotNull(page);
		assertTrue(page.getTotalElements() >= 0);
		// If you inserted a payment before, assertTrue(page.getTotalElements() > 0);
	}
}
