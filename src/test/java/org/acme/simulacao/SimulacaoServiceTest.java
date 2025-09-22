package org.acme.simulacao;

import org.acme.produto.Produto;
import org.acme.produto.ProdutoRepository;
import org.acme.simulacao.dto.SimulacaoRequest;
import org.acme.simulacao.dto.SimulacaoResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SimulacaoServiceTest {

    @Mock
    ProdutoRepository produtoRepository;

    @InjectMocks
    SimulacaoService simulacaoService;

    @Test
    void testSimularSucesso() {
        // Arrange
        Produto produtoMock = new Produto();
        produtoMock.id = 1L;
        produtoMock.taxaJurosAnual = 18.0;
        produtoMock.prazoMaximoMeses = 24;

        Mockito.when(produtoRepository.findByIdOptional(1L)).thenReturn(Optional.of(produtoMock));

        SimulacaoRequest request = new SimulacaoRequest();
        request.idProduto = 1L;
        request.valorSolicitado = 10000.0;
        request.prazoMeses = 12;

        // Act
        Optional<SimulacaoResponse> response = simulacaoService.simular(request);

        // Assert
        assertTrue(response.isPresent());
        assertEquals(1L, response.get().produto.id);
        assertEquals(910.46, response.get().parcelaMensal, 0.01);
    }

    @Test
    void testSimularProdutoNaoEncontrado() {
        // Arrange
        Mockito.when(produtoRepository.findByIdOptional(99L)).thenReturn(Optional.empty());

        SimulacaoRequest request = new SimulacaoRequest();
        request.idProduto = 99L;
        request.valorSolicitado = 10000.0;
        request.prazoMeses = 12;

        // Act
        Optional<SimulacaoResponse> response = simulacaoService.simular(request);

        // Assert
        assertTrue(response.isEmpty());
    }

    @Test
    void testSimularPrazoExcedido() {
        // Arrange
        Produto produtoMock = new Produto();
        produtoMock.id = 1L;
        produtoMock.prazoMaximoMeses = 24; // Prazo máximo de 24 meses

        Mockito.when(produtoRepository.findByIdOptional(1L)).thenReturn(Optional.of(produtoMock));

        SimulacaoRequest request = new SimulacaoRequest();
        request.idProduto = 1L;
        request.valorSolicitado = 10000.0;
        request.prazoMeses = 30; // Solicita um prazo de 30 meses

        // Act
        Optional<SimulacaoResponse> response = simulacaoService.simular(request);

        // Assert
        assertTrue(response.isEmpty());
    }
}