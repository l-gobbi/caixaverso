package org.acme.health;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class DatabaseHealthCheckTest {

    @Test
    public void testReadinessEndpoint() {
        given()
                .when().get("/q/health/ready")
                .then()
                .statusCode(200)
                .body("status", is("UP"))
                .body("checks[0].name", is("Conexões com Bancos de Dados"))
                .body("checks[0].status", is("UP"))
                .body("checks[0].data.'simulacoes-db'", is("OK"))
                .body("checks[0].data.'produtos-db'", is("OK"));
    }
}