package org.acme.produto;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/v1/produtos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProdutoRest {

    @Inject
    ProdutoRepository produtoRepository;

    @GET
    public List<Produto> listarTodos() {
        return produtoRepository.listAll();
    }

    @POST
    @Transactional
    public Response criar(Produto produto) {
        produtoRepository.persist(produto);
        return Response.status(Response.Status.CREATED).entity(produto).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        return produtoRepository.findByIdOptional(id)
                .map(produto -> Response.ok(produto).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @PUT
    @Path("/{id}")
    @Transactional
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
    public Response deletar(@PathParam("id") Long id) {
        boolean deletado = produtoRepository.deleteById(id);
        if (deletado) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}