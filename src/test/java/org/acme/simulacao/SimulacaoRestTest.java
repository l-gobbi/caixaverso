package org.acme.simulacao;

import jakarta.ws.rs.core.Response;
import org.acme.simulacao.dto.SimulacaoRequest;
import org.acme.simulacao.dto.SimulacaoResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class SimulacaoRestTest {

    @Mock
    SimulacaoService simulacaoService;

    @InjectMocks
    SimulacaoRest simulacaoRest;

    @Test
    void testSimularComSucesso() {
        // Arrange
        SimulacaoRequest request = new SimulacaoRequest();
        SimulacaoResponse simulacaoResponse = new SimulacaoResponse();
        Mockito.when(simulacaoService.simular(any(SimulacaoRequest.class)))
                .thenReturn(Optional.of(simulacaoResponse));

        // Act
        Response response = simulacaoRest.simular(request);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(simulacaoResponse, response.getEntity());
    }

    @Test
    void testSimularComFalha() {
        // Arrange
        SimulacaoRequest request = new SimulacaoRequest();
        // O serviço retorna um Optional vazio em caso de falha (produto não encontrado, etc.)
        Mockito.when(simulacaoService.simular(any(SimulacaoRequest.class)))
                .thenReturn(Optional.empty());

        // Act
        Response response = simulacaoRest.simular(request);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }
}