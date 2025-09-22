package org.acme.simulacao;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.simulacao.dto.SimulacaoRequest;

@Path("/api/v1/simulacoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SimulacaoRest {

    @Inject
    SimulacaoService service;

    @POST
    public Response simular(SimulacaoRequest request) {
        return service.simular(request)
                .map(simulacao -> Response.ok(simulacao).build())
                .orElse(Response.status(Response.Status.BAD_REQUEST)
                        .entity("Produto não encontrado ou prazo excede o máximo permitido.")
                        .build());
    }
}