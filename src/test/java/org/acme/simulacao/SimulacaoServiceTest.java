package org.acme.simulacao;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.simulacao.dto.SimulacaoRequest;
import org.acme.simulacao.dto.SimulacaoResponse;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class SimulacaoServiceTest {

    @Inject
    SimulacaoService service;

    @Test
    @Transactional // Garante que a transação de teste seja iniciada
    public void testSimularSucesso() {
        // 1. Cenário
        SimulacaoRequest request = new SimulacaoRequest();
        request.idProduto = 1L; // Usamos o produto real do import.sql
        request.valorSolicitado = 10000.00;
        request.prazoMeses = 12;

        // 2. Execução (sem mocks!)
        Optional<SimulacaoResponse> responseOpt = service.simular(request);

        // 3. Verificação
        assertTrue(responseOpt.isPresent());
        SimulacaoResponse response = responseOpt.get();

        assertEquals(10000.00, response.valorSolicitado);
        assertEquals(12, response.prazoMeses);
        assertEquals(18.0, response.produto.taxaJurosAnual);

        // Verificando os cálculos
        assertEquals(0.01388843, response.taxaJurosEfetivaMensal, 0.000001);
        assertEquals(910.46, response.parcelaMensal, 0.01);
        assertEquals(910.46 * 12, response.valorTotalComJuros, 0.01);
        assertEquals(12, response.memoriaCalculo.size());
        assertEquals(10000.00, response.memoriaCalculo.get(0).saldoDevedorInicial, 0.01);
        assertEquals(138.88, response.memoriaCalculo.get(0).juros, 0.01);
    }

    @Test
    @Transactional
    public void testSimularProdutoNaoEncontrado() {
        SimulacaoRequest request = new SimulacaoRequest();
        request.idProduto = 99L; // ID inexistente

        Optional<SimulacaoResponse> responseOpt = service.simular(request);

        assertTrue(responseOpt.isEmpty());
    }

    @Test
    @Transactional
    public void testSimularPrazoExcedido() {
        SimulacaoRequest request = new SimulacaoRequest();
        request.idProduto = 1L;
        request.prazoMeses = 30; // Prazo maior que o permitido (24)

        Optional<SimulacaoResponse> responseOpt = service.simular(request);

        assertTrue(responseOpt.isEmpty());
    }
}