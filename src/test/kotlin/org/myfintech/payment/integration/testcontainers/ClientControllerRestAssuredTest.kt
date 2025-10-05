package org.myfintech.payment.integration.testcontainers

import dasniko.testcontainers.keycloak.KeycloakContainer
import io.restassured.RestAssured
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.web.client.RestTemplate
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.MountableFile
import java.util.function.Supplier

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ClientControllerRestAssuredTest : AbstractTestcontainersIntegrationTest() {
    @LocalServerPort
    var port: Int = 0

    @BeforeEach
    fun configurePort() {
        RestAssured.port = port
    }

    private val accessToken: String?
        get() {
            val restTemplate = RestTemplate()
            val tokenUrl: String =
                keycloak.getAuthServerUrl() + "/realms/myrealm/protocol/openid-connect/token"

            val headers = HttpHeaders()
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED)
            val body = "grant_type=password&client_id=demo-client&username=testuser&password=password"

            val request =
                HttpEntity<String?>(body, headers)
            val response =
                restTemplate.exchange<MutableMap<*, *>?>(
                    tokenUrl,
                    HttpMethod.POST,
                    request,
                    MutableMap::class.java
                )

            return response.getBody()!!.get("access_token") as String?
        }

    @Test
    fun shouldAccessClientsEndpointWithValidToken() {
        val token = this.accessToken
        RestAssured.given()
            .port(port)
            .auth().oauth2(token)
            .`when`()
            .get("/api/v1/clients")
            .then()
            .statusCode(200)
    }

    @Test
    fun shouldRejectAccessToClientsEndpointWithoutToken() {
        RestAssured.given()
            .port(port)
            .`when`()
            .get("/api/v1/clients")
            .then()
            .statusCode(401)
    }

    @Test
    fun shouldRejectAccessWithInvalidToken() {
        RestAssured.given()
            .port(port)
            .auth().oauth2("invalid.token")
            .`when`()
            .get("/api/v1/clients")
            .then()
            .statusCode(401)
    }

    companion object {
        val keycloak: KeycloakContainer = KeycloakContainer("quay.io/keycloak/keycloak:26.3")
            .withExposedPorts(8080)
            .withEnv("KEYCLOAK_ADMIN", "admin")
            .withEnv("KEYCLOAK_ADMIN_PASSWORD", "admin")
            .withCopyFileToContainer(
                MountableFile.forClasspathResource("realms/myrealm-realm.json"),
                "/opt/keycloak/data/import/myrealm-realm.json"
            )
            .withCustomCommand("start-dev --import-realm")
            .waitingFor(Wait.forHttp("/realms/myrealm/.well-known/openid-configuration").forStatusCode(200))

        init {
            keycloak.start()
        }

        @BeforeAll
        fun setupBaseUri() {
            RestAssured.baseURI = "http://localhost"
        }

        @DynamicPropertySource
        fun registerKeycloakProperties(registry: DynamicPropertyRegistry) {
            registry.add(
                "spring.security.oauth2.resourceserver.jwt.issuer-uri",
                Supplier { keycloak.getAuthServerUrl() + "/realms/myrealm" })
            registry.add(
                "spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
                Supplier { keycloak.getAuthServerUrl() + "/realms/myrealm/protocol/openid-connect/certs" })
        }
    }
}
