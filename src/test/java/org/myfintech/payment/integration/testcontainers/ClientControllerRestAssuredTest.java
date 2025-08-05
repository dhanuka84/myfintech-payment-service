package org.myfintech.payment.integration.testcontainers;

import static io.restassured.RestAssured.given;

import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.MountableFile;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.restassured.RestAssured;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClientControllerRestAssuredTest extends AbstractTestcontainersIntegrationTest {

	@LocalServerPort
	int port;

	static final KeycloakContainer keycloak = new KeycloakContainer("quay.io/keycloak/keycloak:26.3")
			.withExposedPorts(8080)
			.withEnv("KEYCLOAK_ADMIN", "admin")
			.withEnv("KEYCLOAK_ADMIN_PASSWORD", "admin")
			.withCopyFileToContainer(MountableFile.forClasspathResource("realms/myrealm-realm.json"),
					"/opt/keycloak/data/import/myrealm-realm.json")
			.withCustomCommand("start-dev --import-realm")
			.waitingFor(Wait.forHttp("/realms/myrealm/.well-known/openid-configuration").forStatusCode(200));

	static {
		keycloak.start();
	}

	@BeforeAll
	static void setupBaseUri() {
		RestAssured.baseURI = "http://localhost";
	}

	@BeforeEach
	void configurePort() {
		RestAssured.port = port;
	}

	@DynamicPropertySource
	static void registerKeycloakProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
				() -> keycloak.getAuthServerUrl() + "/realms/myrealm");
		registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
				() -> keycloak.getAuthServerUrl() + "/realms/myrealm/protocol/openid-connect/certs");
	}

	private String getAccessToken() {
		RestTemplate restTemplate = new RestTemplate();
		String tokenUrl = keycloak.getAuthServerUrl() + "/realms/myrealm/protocol/openid-connect/token";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		String body = "grant_type=password&client_id=demo-client&username=testuser&password=password";

		HttpEntity<String> request = new HttpEntity<>(body, headers);
		ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, Map.class);

		return (String) response.getBody().get("access_token");
	}

	@Test
	void shouldAccessClientsEndpointWithValidToken() {
	    String token = getAccessToken();
	    given()
	        .port(port)
	        .auth().oauth2(token)
	    .when()
	        .get("/api/v1/clients")
	    .then()
	        .statusCode(200);
	}

	@Test
	void shouldRejectAccessToClientsEndpointWithoutToken() {
	    given()
	        .port(port)
	    .when()
	        .get("/api/v1/clients")
	    .then()
	        .statusCode(401);
	}

	@Test
	void shouldRejectAccessWithInvalidToken() {
	    given()
	        .port(port)
	        .auth().oauth2("invalid.token")
	    .when()
	        .get("/api/v1/clients")
	    .then()
	        .statusCode(401);
	}

}
