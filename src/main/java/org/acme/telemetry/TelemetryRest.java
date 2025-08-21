package org.acme.telemetry;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.acme.telemetry.dto.TelemetryResponse;
import org.acme.telemetry.facade.TelemetryFacade;

@Slf4j
@Path("/api/v1/telemetria")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class TelemetryRest {

    @Inject
    TelemetryFacade telemetryFacade;

    @GET
    public Response getTelemetry() {
        try {
            TelemetryResponse telemetryData = telemetryFacade.getTelemetryData();
            return Response.ok(telemetryData).build();
        } catch (Exception e) {
            log.error("Erro ao buscar dados de telemetria", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar dados de telemetria: " + e.getMessage())
                    .build();
        }
    }
}