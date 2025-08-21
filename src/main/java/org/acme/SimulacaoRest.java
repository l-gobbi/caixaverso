package org.acme;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.acme.dto.*;
import org.acme.facade.*;


import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Slf4j
@Path("/")
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
    @Path("/fazerSimulacao")
    @Timed(value = "endpoint.fazerSimulacao.tempo", description = "Mede o tempo de resposta do endpoint de simulação.", percentiles = 0.0)
    public Response fazerSimulacao(SimulacaoRequest request) {
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
        registry.counter("endpoint.fazerSimulacao.requisicoes", "outcome", outcome).increment();
        return response;
    }

    @GET
    @Path("/listarSimulacoes")
    @Timed(value = "endpoint.listarSimulacoes.tempo", description = "Mede o tempo de resposta do endpoint de listagem.", percentiles = 0.0)
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
        registry.counter("endpoint.listarSimulacoes.requisicoes", "outcome", outcome).increment();

        return response;
    }

    @GET
    @Path("/relatorio/simulacoes-diarias")
    @Timed(value = "endpoint.relatorio.simulacoesdiarias.tempo", description = "Mede o tempo de resposta do endpoint de relatório de simulações diárias.", percentiles = 0.0)
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
        registry.counter("endpoint.relatorio.simulacoesdiarias.requisicoes", "outcome", outcome).increment();
        return response;
    }

    @GET
    @Path("/health")
    @Produces(MediaType.TEXT_PLAIN)
    @Timed(value = "endpoint.health.tempo", description = "Mede o tempo de resposta do health check.", percentiles = 0.0)
    public Response healthCheck() {
        Response response;
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(5)) {
                log.info("Conexão com o banco de dados bem-sucedida!");
                response = Response.ok("SUCESSO: Conexão com o banco de dados estabelecida!").build();
            } else {
                log.error("A conexão com o banco de dados não é válida.");
                response = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("FALHA: A conexão com o banco de dados não é válida.")
                        .build();
            }
        } catch (Exception e) {
            log.error("Erro ao tentar conectar com o banco de dados: {}", e.getMessage());
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("FALHA: Não foi possível conectar ao banco de dados. Erro: " + e.getMessage())
                    .build();
        }
        String outcome = response.getStatus() >= 200 && response.getStatus() < 300 ? "SUCCESS" : "ERROR";
        registry.counter("endpoint.health.requisicoes", "outcome", outcome).increment();

        return response;
    }
}
