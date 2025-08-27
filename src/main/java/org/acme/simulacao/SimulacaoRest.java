package org.acme.simulacao;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.acme.simulacao.dto.PaginatedSimulacaoResponse;
import org.acme.simulacao.dto.SimulacaoDiariaResponse;
import org.acme.simulacao.dto.SimulacaoRequest;
import org.acme.simulacao.dto.SimulacaoResponse;
import org.acme.simulacao.facade.FazerSimulacaoFacade;
import org.acme.simulacao.facade.ListaSimulacaoFacade;
import org.acme.simulacao.facade.RelatorioSimulacaoFacade;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Slf4j
@Path("/api/v1/simulacoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
@Tag(name = "Simulações de Crédito", description = "Endpoints para criar e consultar simulações")
public class SimulacaoRest {

    @Inject
    MeterRegistry registry;

    @Inject
    @io.quarkus.agroal.DataSource("consulta")
    DataSource dataSource;

    @Inject
    FazerSimulacaoFacade fazerSimulacaoFacade;

    @Inject
    ListaSimulacaoFacade listaSimulacaoFacade;

    @Inject
    RelatorioSimulacaoFacade relatorioSimulacaoFacade;

    @POST
    @Operation(summary = "Cria uma nova simulação", description = "Calcula e armazena uma simulação de crédito com base no valor e prazo desejados.")
    @APIResponse(responseCode = "201", description = "Simulação criada com sucesso", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = SimulacaoResponse.class)))
    @APIResponse(responseCode = "404", description = "Nenhum produto de crédito encontrado para os critérios informados")
    @APIResponse(responseCode = "400", description = "Dados de entrada inválidos (ex: valor negativo)")
    @APIResponse(responseCode = "500", description = "Erro interno no servidor")
    @Timed(value = "endpoint.simulacoes.post.tempo", description = "Mede o tempo de resposta do endpoint de simulação.", percentiles = 0.0)
    public Response criarSimulacao(@Valid SimulacaoRequest request) {
        Response response;

        try {
            log.info("Request recebido para fazer simulação: {}", request);

            SimulacaoResponse simulacaoResponse = fazerSimulacaoFacade.executar(request);

            log.info("Simulação processada com sucesso: {}", simulacaoResponse);
            response = Response.status(Response.Status.CREATED).entity(simulacaoResponse).build();
        } catch (NotFoundException e) {
            log.error("Tentativa de simulação para produto inexistente: {}", e.getMessage());
            response = Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
        catch (Exception e) {
            log.error("Erro inesperado ao fazer simulação: {}", e.getMessage(), e);
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Ocorreu um erro inesperado ao processar a simulação.").build();

        }
        String outcome = response.getStatus() >= 200 && response.getStatus() < 300 ? "SUCCESS" : "ERROR";
        registry.counter("endpoint.simulacoes.post.requisicoes", "outcome", outcome).increment();
        return response;
    }

    @GET
    @Operation(summary = "Lista todas as simulações", description = "Retorna uma lista paginada de todas as simulações de crédito já realizadas.")
    @APIResponse(responseCode = "200", description = "Lista de simulações recuperada com sucesso", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = PaginatedSimulacaoResponse.class)))
    @APIResponse(responseCode = "500", description = "Erro interno no servidor")
    @Timed(value = "endpoint.simulacoes.get.tempo", description = "Mede o tempo de resposta do endpoint de listagem.", percentiles = 0.0)
    public Response listarSimulacoes(
            @QueryParam("pagina") @DefaultValue("1") @Min(1) int pagina,
            @QueryParam("qtdRegistrosPagina") @DefaultValue("10") @Min(1) @Max(100) int qtdRegistrosPagina) {
        Response response;
        try {
            PaginatedSimulacaoResponse paginatedResponse = listaSimulacaoFacade.executar(pagina, qtdRegistrosPagina);
            response = Response.ok(paginatedResponse).build();
        } catch (Exception e) {
            log.error("Erro ao listar simulações: {}", e.getMessage());
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao listar simulações: " + e.getMessage())
                    .build();
        }
        String outcome = response.getStatus() >= 200 && response.getStatus() < 300 ? "SUCCESS" : "ERROR";
        registry.counter("endpoint.simulacoes.get.requisicoes", "outcome", outcome).increment();

        return response;
    }

    @GET
    @Path("/diarias")
    @Operation(summary = "Relatório de simulações diárias", description = "Retorna dados agregados de todas as simulações realizadas em uma data específica.")
    @APIResponse(responseCode = "200", description = "Relatório gerado com sucesso", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = SimulacaoDiariaResponse.class)))
    @APIResponse(responseCode = "400", description = "Formato de data inválido. Use AAAA-MM-DD.")
    @APIResponse(responseCode = "500", description = "Erro interno no servidor")
    @Timed(value = "endpoint.simulacoes.diarias.get.tempo", description = "Mede o tempo de resposta do endpoint de relatório de simulações diárias.", percentiles = 0.0)
    public Response getSimulacoesDiarias(@QueryParam("data") String dataStr) {
        Response response;
        try {
            LocalDate data;
            if (dataStr == null || dataStr.trim().isEmpty()) {
                data = LocalDate.now();
            } else {
                data = LocalDate.parse(dataStr);
            }
            SimulacaoDiariaResponse simulacaoDiariaResponse = relatorioSimulacaoFacade.executar(data);
            response = Response.ok(simulacaoDiariaResponse).build();
        } catch (DateTimeParseException e) {
            response = Response.status(Response.Status.BAD_REQUEST)
                    .entity("Formato de data inválido. Use o formato AAAA-MM-DD.")
                    .build();
        } catch (Exception e) {
            log.error("Erro ao buscar relatório de simulações diárias: {}", e.getMessage());
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar relatório de simulações diárias: " + e.getMessage())
                    .build();
        }
        String outcome = response.getStatus() >= 200 && response.getStatus() < 300 ? "SUCCESS" : "ERROR";
        registry.counter("endpoint.simulacoes.diarias.get.requisicoes", "outcome", outcome).increment();
        return response;
    }
}
