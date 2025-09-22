package org.acme.produto;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ProdutoRestTest {

    @Mock
    ProdutoRepository produtoRepository;

    @InjectMocks
    ProdutoRest produtoRest;

    @Test
    void testListarTodos() {
        // Arrange
        Mockito.when(produtoRepository.listAll()).thenReturn(List.of(new Produto()));

        // Act
        List<Produto> result = produtoRest.listarTodos();

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void testBuscarPorIdSucesso() {
        // Arrange
        Produto p = new Produto();
        p.id = 1L;
        Mockito.when(produtoRepository.findByIdOptional(1L)).thenReturn(Optional.of(p));

        // Act
        Response response = produtoRest.buscarPorId(1L);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(p, response.getEntity());
    }

    @Test
    void testBuscarPorIdNaoEncontrado() {
        // Arrange
        Mockito.when(produtoRepository.findByIdOptional(99L)).thenReturn(Optional.empty());

        // Act
        Response response = produtoRest.buscarPorId(99L);

        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }
}