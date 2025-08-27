package org.acme.simulacao;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.acme.simulacao.dto.SimulacaoRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasKey;

@QuarkusTest
public class SimulacaoRestTest {

    @Test
    public void testCriarSimulacaoSucesso() {
        SimulacaoRequest request = new SimulacaoRequest();
        request.setValorDesejado(new BigDecimal("20000"));
        request.setPrazo(24);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/api/v1/simulacoes")
                .then()
                .statusCode(201)
                .body("$", hasKey("idSimulacao"))
                .body("codigoProduto", is(1))
                .body("descricaoProduto", is("Crédito Pessoal"));
    }

    @Test
    public void testCriarSimulacaoProdutoNaoEncontrado() {
        SimulacaoRequest request = new SimulacaoRequest();
        request.setValorDesejado(new BigDecimal("99999999"));
        request.setPrazo(1);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/api/v1/simulacoes")
                .then()
                .statusCode(404);
    }

    @Test
    public void testCriarSimulacaoBadRequest() {
        SimulacaoRequest request = new SimulacaoRequest();
        request.setValorDesejado(new BigDecimal("-100"));
        request.setPrazo(1);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/api/v1/simulacoes")
                .then()
                .statusCode(400);
    }

    // (Mantenha os testes que já existem no arquivo e adicione estes)
    @Test
    public void testListarSimulacoes() {
        // Primeiro, crie uma simulação para garantir que a lista não esteja vazia
        SimulacaoRequest request = new SimulacaoRequest();
        request.setValorDesejado(new BigDecimal("15000"));
        request.setPrazo(36);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/api/v1/simulacoes")
                .then()
                .statusCode(201);

        given()
                .when().get("/api/v1/simulacoes")
                .then()
                .statusCode(200)
                .body("pagina", is(1))
                .body("registros[0]", hasKey("idSimulacao"));
    }

    @Test
    public void testGetSimulacoesDiarias() {
        given()
                .queryParam("data", LocalDate.now().toString())
                .when().get("/api/v1/simulacoes/diarias")
                .then()
                .statusCode(200)
                .body("dataReferencia", is(LocalDate.now().toString()));
    }
}