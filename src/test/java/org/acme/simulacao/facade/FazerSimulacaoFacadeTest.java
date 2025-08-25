package org.acme.simulacao.facade;

import org.acme.mensageria.SimulacaoEventPublisher;
import org.acme.model.produto.Produto;
import org.acme.simulacao.dao.ProdutoDao;
import org.acme.simulacao.dao.SimulacaoDao;
import org.acme.simulacao.dto.ResultadoSimulacao;
import org.acme.simulacao.dto.SimulacaoRequest;
import org.acme.simulacao.dto.SimulacaoResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FazerSimulacaoFacadeTest {

    @InjectMocks
    FazerSimulacaoFacade facade;

    @Mock
    ProdutoDao produtoDao;

    @Mock
    SimulacaoDao simulacaoDao;

    @Mock
    SimulacaoEventPublisher simulacaoEventPublisher;

    @Test
    void executar_DeveCalcularCorretamente() {
        // Arrange
        SimulacaoRequest request = new SimulacaoRequest();
        request.setValorDesejado(new BigDecimal("10000"));
        request.setPrazo(12);

        Produto produto = new Produto();
        produto.setCoProduto(1);
        produto.setNoProduto("Crédito Pessoal");
        produto.setPcTaxaJuros(new BigDecimal("0.01"));

        when(produtoDao.buscaProdutoPeloValorEPrazo(request.getValorDesejado(), request.getPrazo()))
                .thenReturn(Optional.of(produto));

        // Act
        SimulacaoResponse response = facade.executar(request);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getCodigoProduto());
        assertEquals("Crédito Pessoal", response.getDescricaoProduto());

        Optional<ResultadoSimulacao> price = response.getResultadoSimulacao().stream()
                .filter(r -> "PRICE".equals(r.getTipo()))
                .findFirst();

        Optional<ResultadoSimulacao> sac = response.getResultadoSimulacao().stream()
                .filter(r -> "SAC".equals(r.getTipo()))
                .findFirst();

        assert (price.isPresent());
        assert (sac.isPresent());

        assertEquals(12, price.get().getParcelas().size());
        assertEquals(12, sac.get().getParcelas().size());
    }
}