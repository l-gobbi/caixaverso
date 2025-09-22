package org.acme.produto;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ProdutoRestTest {

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

    @Test
    void testCriarProduto() {
        // Arrange
        Produto produto = new Produto();
        produto.nome = "Teste";

        // Act
        Response response = produtoRest.criar(produto);

        // Assert
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        Mockito.verify(produtoRepository, Mockito.times(1)).persist(any(Produto.class));
    }

    @Test
    void testAtualizarProdutoSucesso() {
        // Arrange
        Produto produtoExistente = new Produto();
        produtoExistente.id = 1L;
        produtoExistente.nome = "Original";

        Produto produtoAtualizado = new Produto();
        produtoAtualizado.nome = "Atualizado";

        Mockito.when(produtoRepository.findByIdOptional(1L)).thenReturn(Optional.of(produtoExistente));

        // Act
        Response response = produtoRest.atualizar(1L, produtoAtualizado);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Produto produtoRetornado = (Produto) response.getEntity();
        assertEquals("Atualizado", produtoRetornado.nome);
    }

    @Test
    void testAtualizarProdutoNaoEncontrado() {
        // Arrange
        Produto produtoAtualizado = new Produto();
        produtoAtualizado.nome = "Atualizado";
        Mockito.when(produtoRepository.findByIdOptional(anyLong())).thenReturn(Optional.empty());

        // Act
        Response response = produtoRest.atualizar(99L, produtoAtualizado);

        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }


    @Test
    void testDeletarProdutoSucesso() {
        // Arrange
        Mockito.when(produtoRepository.deleteById(1L)).thenReturn(true);

        // Act
        Response response = produtoRest.deletar(1L);

        // Assert
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    void testDeletarProdutoNaoEncontrado() {
        // Arrange
        Mockito.when(produtoRepository.deleteById(99L)).thenReturn(false);

        // Act
        Response response = produtoRest.deletar(99L);

        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }
}