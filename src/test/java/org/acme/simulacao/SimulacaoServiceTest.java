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
public class SimulacaoServiceTest {

    @Mock
    ProdutoRepository produtoRepository; // Cria um mock do repository

    @InjectMocks
    SimulacaoService simulacaoService; // Cria uma instância do serviço e injeta os mocks

    @Test
    void testSimularSucesso() {
        // 1. Arrange
        Produto produtoMock = new Produto();
        produtoMock.id = 1L;
        produtoMock.taxaJurosAnual = 18.0;
        produtoMock.prazoMaximoMeses = 24;

        Mockito.when(produtoRepository.findByIdOptional(1L)).thenReturn(Optional.of(produtoMock));

        SimulacaoRequest request = new SimulacaoRequest();
        request.idProduto = 1L;
        request.valorSolicitado = 10000.0;
        request.prazoMeses = 12;

        // 2. Act
        Optional<SimulacaoResponse> response = simulacaoService.simular(request);

        // 3. Assert
        assertTrue(response.isPresent());
        assertEquals(1L, response.get().produto.id);
    }
}