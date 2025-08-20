package org.acme.simulacao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.acme.dto.ResultadoSimulacao;
import org.acme.dto.SimulacaoRequest;
import org.acme.dto.SimulacaoResponse;
import org.acme.facade.BuscaProdutoFacade;
import org.acme.facade.CalculaSimulacaoFacade;
import org.acme.facade.SalvarSimulacaoFacade;
import org.acme.model.produto.Produto;


import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

@Slf4j
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class SimulacaoRest {

    @Inject
    @io.quarkus.agroal.DataSource("consulta")
    DataSource dataSource;

    @Inject
    CalculaSimulacaoFacade calculaSimulacaoFacade;

    @Inject
    BuscaProdutoFacade buscaProdutoFacade;

    @Inject
    SalvarSimulacaoFacade salvarSimulacaoFacade;

    @POST
    @Path("/fazerSimulacao")
    public Response fazerSimulacao(SimulacaoRequest request) {
        log.info("Request: {}", request);
        Produto produto = buscaProdutoFacade.buscarProduto(request);
        List<ResultadoSimulacao> resultados = calculaSimulacaoFacade.calcular(request, produto.getPcTaxaJuros());
        salvarSimulacaoFacade.executar(request, produto, resultados);

        try {
            SimulacaoResponse response = new SimulacaoResponse();
            response.setCodigoProduto(produto.getCoProduto());
            response.setDescricaoProduto(produto.getNoProduto());
            response.setTaxaJuros(produto.getPcTaxaJuros());
            response.setResultadoSimulacao(calculaSimulacaoFacade.calcular(request, produto.getPcTaxaJuros()));
            log.info("Response: {}", response);
            return Response.ok(response).build();
        } catch (NotFoundException e) {
            log.error("Simulação não encontrada: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        }
        catch (Exception e) {
            log.error("Erro ao simular: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar resultado da simulação: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/health")
    @Produces(MediaType.TEXT_PLAIN)
    public Response healthCheck() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(5)) {
                log.info("Conexão com o banco de dados bem-sucedida!");
                return Response.ok("SUCESSO: Conexão com o banco de dados estabelecida!").build();
            } else {
                log.error("A conexão com o banco de dados não é válida.");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("FALHA: A conexão com o banco de dados não é válida.")
                        .build();
            }
        } catch (Exception e) {
            log.error("Erro ao tentar conectar com o banco de dados: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("FALHA: Não foi possível conectar ao banco de dados. Erro: " + e.getMessage())
                    .build();
        }
    }
}
