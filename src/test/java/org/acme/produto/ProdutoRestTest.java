package org.acme.produto;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class ProdutoRestTest {

    @Inject
    EntityManager em;

    @BeforeEach
    @Transactional
    public void setup() {

        Produto.deleteAll();

        em.createNativeQuery("ALTER TABLE Produto ALTER COLUMN id RESTART WITH 1").executeUpdate();

        Produto p1 = new Produto();
        p1.nome = "Empréstimo Pessoal";
        p1.taxaJurosAnual = 18.0;
        p1.prazoMaximoMeses = 24;
        p1.persist();

        Produto p2 = new Produto();
        p2.nome = "Crédito Consignado";
        p2.taxaJurosAnual = 12.0;
        p2.prazoMaximoMeses = 48;
        p2.persist();
    }

    @Test
    public void testListarTodos() {
        given()
                .when().get("/api/v1/produtos")
                .then()
                .statusCode(200)
                .body("size()", is(2))
                .body("id", hasItems(1, 2))
                .body("nome", hasItems("Empréstimo Pessoal", "Crédito Consignado"));
    }

    @Test
    public void testBuscarPorIdSucesso() {
        given()
                .pathParam("id", 1)
                .when().get("/api/v1/produtos/{id}")
                .then()
                .statusCode(200)
                .body("id", is(1))
                // E esta também!
                .body("nome", is("Empréstimo Pessoal"));
    }

    @Test
    public void testBuscarPorIdNaoEncontrado() {
        given()
                .pathParam("id", 999)
                .when().get("/api/v1/produtos/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    public void testCriarProduto() {
        String novoProdutoJson = """
            {
                "nome": "Crédito Imobiliário",
                "taxaJurosAnual": 9.5,
                "prazoMaximoMeses": 360
            }
            """;

        // Remove o produto recém-criado para não afetar outros testes
        // (Apesar do setup() já fazer isso, é uma boa prática limpar o que se cria)
        var response = given()
                .contentType(ContentType.JSON)
                .body(novoProdutoJson)
                .when().post("/api/v1/produtos")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("nome", is("Crédito Imobiliário"))
                .extract().body().as(Produto.class);

        // Limpeza opcional, mas recomendada.
        given().pathParam("id", response.id).when().delete("/api/v1/produtos/{id}").then().statusCode(204);
    }

    @Test
    public void testAtualizarProduto() {
        String produtoAtualizadoJson = """
            {
                "nome": "Empréstimo Pessoal v2",
                "taxaJurosAnual": 17.0,
                "prazoMaximoMeses": 30
            }
            """;

        given()
                .pathParam("id", 1)
                .contentType(ContentType.JSON)
                .body(produtoAtualizadoJson)
                .when().put("/api/v1/produtos/{id}")
                .then()
                .statusCode(200)
                .body("nome", is("Empréstimo Pessoal v2"))
                .body("taxaJurosAnual", is(17.0f));
    }

    @Test
    public void testDeletarProduto() {
        given()
                .pathParam("id", 2)
                .when().delete("/api/v1/produtos/{id}")
                .then()
                .statusCode(204);
    }
}