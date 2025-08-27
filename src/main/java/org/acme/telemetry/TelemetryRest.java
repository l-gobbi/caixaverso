package org.acme.telemetry;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.acme.telemetry.dto.TelemetryResponse;
import org.acme.telemetry.facade.TelemetryFacade;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Slf4j
@Path("/api/v1/telemetria")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
@Tag(name = "Telemetria", description = "Endpoints para monitoramento e métricas da aplicação")
public class TelemetryRest {

    @Inject
    TelemetryFacade telemetryFacade;

    @GET
    @Operation(summary = "Obtém dados de telemetria", description = "Retorna métricas de performance dos principais endpoints da aplicação, como quantidade de requisições e tempos de resposta.")
    @APIResponse(responseCode = "200", description = "Dados de telemetria recuperados com sucesso", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = TelemetryResponse.class)))
    @APIResponse(responseCode = "400", description = "Formato de data inválido. Use AAAA-MM-DD.")
    @APIResponse(responseCode = "500", description = "Erro ao buscar dados de telemetria")
    public Response getTelemetry(@QueryParam("data") String dataStr) {
        try {
            LocalDate data;
            if (dataStr == null || dataStr.trim().isEmpty()) {
                data = LocalDate.now();
            } else {
                data = LocalDate.parse(dataStr);
            }
            TelemetryResponse telemetryData = telemetryFacade.getTelemetryData(data);
            return Response.ok(telemetryData).build();
        } catch (DateTimeParseException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Formato de data inválido. Use o formato AAAA-MM-DD.")
                    .build();
        } catch (Exception e) {
            log.error("Erro ao buscar dados de telemetria", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar dados de telemetria: " + e.getMessage())
                    .build();
        }
    }
}