package org.acme.produto;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/api/v1/produtos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Produtos", description = "Operações para gerenciamento de produtos de empréstimo (CRUD)")
public class ProdutoRest {

    @Inject
    ProdutoRepository produtoRepository;

    @GET
    @Operation(summary = "Listar todos os produtos", description = "Retorna uma lista de todos os produtos de empréstimo cadastrados.")
    @APIResponse(responseCode = "200", description = "Lista de produtos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Produto[].class)))
    public List<Produto> listarTodos() {
        return produtoRepository.listAll();
    }

    @POST
    @Transactional
    @Operation(summary = "Criar um novo produto", description = "Cadastra um novo produto de empréstimo no sistema.")
    @APIResponse(responseCode = "201", description = "Produto criado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Produto.class)))
    public Response criar(Produto produto) {
        produtoRepository.persist(produto);
        return Response.status(Response.Status.CREATED).entity(produto).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar produto por ID", description = "Retorna os detalhes de um produto específico pelo seu ID.")
    @APIResponse(responseCode = "200", description = "Produto encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Produto.class)))
    @APIResponse(responseCode = "404", description = "Produto não encontrado")
    public Response buscarPorId(@PathParam("id") Long id) {
        return produtoRepository.findByIdOptional(id)
                .map(produto -> Response.ok(produto).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Atualizar um produto", description = "Atualiza os dados de um produto de empréstimo existente.")
    @APIResponse(responseCode = "200", description = "Produto atualizado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Produto.class)))
    @APIResponse(responseCode = "404", description = "Produto não encontrado")
    public Response atualizar(@PathParam("id") Long id, Produto produtoAtualizado) {
        return produtoRepository.<Produto>findByIdOptional(id)
                .map(produto -> {
                    produto.nome = produtoAtualizado.nome;
                    produto.taxaJurosAnual = produtoAtualizado.taxaJurosAnual;
                    produto.prazoMaximoMeses = produtoAtualizado.prazoMaximoMeses;
                    return Response.ok(produto).build();
                }).orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Deletar um produto", description = "Remove um produto de empréstimo do sistema pelo seu ID.")
    @APIResponse(responseCode = "204", description = "Produto deletado com sucesso")
    @APIResponse(responseCode = "404", description = "Produto não encontrado")
    public Response deletar(@PathParam("id") Long id) {
        boolean deletado = produtoRepository.deleteById(id);
        if (deletado) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}