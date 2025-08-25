package org.acme.cache;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import org.acme.simulacao.dao.SimulacaoDao;
import org.acme.simulacao.dto.SimulacaoAgregada;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@QuarkusTest
@TestProfile(CacheFlowTest.DisableSchedulerProfile.class)
public class CacheFlowTest {

    @InjectMock
    SimulacaoDao simulacaoDao;

    public static class DisableSchedulerProfile implements QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of("quarkus.scheduler.enabled", "false");
        }
    }

    @BeforeEach
    public void setup() {
        Mockito.reset(simulacaoDao);
    }

    @Test
    public void testCacheFuncionaEInvalidaCorretamente() {
        LocalDate data = LocalDate.now();
        String dataStr = data.toString();

        List<SimulacaoAgregada> resultadoSimulado = Collections.singletonList(
                new SimulacaoAgregada(1, BigDecimal.valueOf(0.01), BigDecimal.valueOf(0.012), BigDecimal.valueOf(150.0), BigDecimal.valueOf(10000.0), BigDecimal.valueOf(12000.0)))
                ;
        when(simulacaoDao.getSimulacoesAgregadasPorDia(any(LocalDate.class))).thenReturn(resultadoSimulado);

        given()
                .queryParam("data", dataStr)
                .when().get("/api/v1/simulacoes/diarias")
                .then()
                .statusCode(200);

        Mockito.verify(simulacaoDao, times(1)).getSimulacoesAgregadasPorDia(data);

        given()
                .queryParam("data", dataStr)
                .when().get("/api/v1/simulacoes/diarias")
                .then()
                .statusCode(200);

        Mockito.verify(simulacaoDao, times(1)).getSimulacoesAgregadasPorDia(data);

        given()
                .when().post("/api/v1/cache/clear")
                .then()
                .statusCode(200);

        given()
                .queryParam("data", dataStr)
                .when().get("/api/v1/simulacoes/diarias")
                .then()
                .statusCode(200);

        Mockito.verify(simulacaoDao, times(2)).getSimulacoesAgregadasPorDia(data);
    }
}