package org.acme.simulacao;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.simulacao.dto.SimulacaoRequest;
import org.acme.simulacao.dto.SimulacaoResponse;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/v1/simulacoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Simulação", description = "Operações para simular empréstimos")
public class SimulacaoRest {

    @Inject
    SimulacaoService service;

    @POST
    @Operation(summary = "Realizar simulação de empréstimo", description = "Calcula os detalhes de um empréstimo com base em um produto, valor e prazo.")
    @APIResponse(responseCode = "200", description = "Simulação calculada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimulacaoResponse.class)))
    @APIResponse(responseCode = "400", description = "Dados inválidos para simulação (ex: produto não encontrado ou prazo excedido)")
    public Response simular(SimulacaoRequest request) {
        return service.simular(request)
                .map(simulacao -> Response.ok(simulacao).build())
                .orElse(Response.status(Response.Status.BAD_REQUEST)
                        .entity("Produto não encontrado ou prazo excede o máximo permitido.")
                        .build());
    }
}