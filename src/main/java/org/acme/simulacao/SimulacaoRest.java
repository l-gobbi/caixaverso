package org.acme.simulacao;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
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


import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Slf4j
@Path("/api/v1/simulacoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
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
    @Timed(value = "endpoint.simulacoes.post.tempo", description = "Mede o tempo de resposta do endpoint de simulação.", percentiles = 0.0)
    public Response criarSimulacao(SimulacaoRequest request) {
        Response response;

        try {
            log.info("Request recebido para fazer simulação: {}", request);

            SimulacaoResponse simulacaoResponse = fazerSimulacaoFacade.executar(request);

            log.info("Simulação processada com sucesso: {}", simulacaoResponse);
            response = Response.ok(simulacaoResponse).build();
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
    @Timed(value = "endpoint.simulacoes.get.tempo", description = "Mede o tempo de resposta do endpoint de listagem.", percentiles = 0.0)
    public Response listarSimulacoes(
            @QueryParam("pagina") @DefaultValue("1") int pagina,
            @QueryParam("qtdRegistrosPagina") @DefaultValue("10") int qtdRegistrosPagina) {
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
