package org.acme.telemetry;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.time.LocalDate;

@QuarkusTest
public class TelemetryRestTest {

    @Test
    void testGetTelemetry() {
        given()
                .when().get("/api/v1/telemetria")
                .then()
                .statusCode(200)
                .body("dataReferencia", is(LocalDate.now().toString()))
                .body("listaEndpoints", is(notNullValue()))
                .body("listaEndpoints[0].nomeApi", is("simulacoes.post"))
                .body("listaEndpoints[1].nomeApi", is("simulacoes.get"))
                .body("listaEndpoints[2].nomeApi", is("simulacoes.diarias.get"));
    }
}