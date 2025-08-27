package org.acme.telemetry;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.acme.simulacao.dto.SimulacaoRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class TelemetryRestTest {

    @Inject
    EntityManager em;

    @BeforeEach
    @Transactional
    void setup() {
        em.createQuery("DELETE FROM Metricas").executeUpdate();
    }

    @Test
    void testGetTelemetry_SemData_DeveRetornarMetricasDoDia() {
        SimulacaoRequest request = new SimulacaoRequest();
        request.setValorDesejado(new BigDecimal("10000"));
        request.setPrazo(12);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/api/v1/simulacoes")
                .then()
                .statusCode(201);


        given()
                .when().get("/api/v1/telemetria")
                .then()
                .statusCode(200)
                .body("dataReferencia", is(LocalDate.now().toString()))
                .body("listaEndpoints", is(notNullValue()))
                .body("listaEndpoints.find { it.nomeApi == 'simulacoes.post' }.qtdRequisicoes", is(1));
    }

    @Test
    void testGetTelemetry_ComDataInvalida() {
        given()
                .queryParam("data", "data-invalida")
                .when().get("/api/v1/telemetria")
                .then()
                .statusCode(400);
    }

    @Test
    void testGetTelemetry_SemMetricasParaData() {
        String dataSemMetricas = "2000-01-01";

        given()
                .queryParam("data", dataSemMetricas)
                .when().get("/api/v1/telemetria")
                .then()
                .statusCode(200)
                .body("dataReferencia", is(dataSemMetricas))
                .body("listaEndpoints.size()", is(0));
    }
}