package org.acme.simulacao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.acme.dto.SimulacaoRequest;

@Slf4j
@Path("/simulacao")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class SimulacaoRest {

    @POST
    public Response buscarSimulaco(SimulacaoRequest request) {
        log.info("Request: {}", request);
        try {
            return Response.ok(request).build();
        } catch (Exception e) {
            log.error("Erro ao simular: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar resultado da simulação: " + e.getMessage())
                    .build();
        }
    }
}
