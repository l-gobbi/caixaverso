package org.acme.simulacao.facade;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.simulacao.dao.SimulacaoDao;
import org.acme.simulacao.dto.PaginatedSimulacaoResponse;
import org.acme.simulacao.dto.SimulacaoDetalheResponse;
import org.acme.model.simulacao.Simulacao;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ListaSimulacaoFacade {

    @Inject
    SimulacaoDao simulacaoDao;

    public PaginatedSimulacaoResponse executar(int pagina, int qtdRegistrosPagina) {
        List<Simulacao> simulacoes = simulacaoDao.listAll(pagina - 1, qtdRegistrosPagina);
        long totalRegistros = simulacaoDao.count();

        List<SimulacaoDetalheResponse> registros = simulacoes.stream()
                .map(s -> new SimulacaoDetalheResponse(
                        s.getId(),
                        s.getValorDesejado(),
                        s.getPrazo(),
                        s.getValorTotal()))
                .collect(Collectors.toList());

        return new PaginatedSimulacaoResponse(pagina, totalRegistros, qtdRegistrosPagina, registros);
    }
}