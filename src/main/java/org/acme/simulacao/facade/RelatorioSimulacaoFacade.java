package org.acme.simulacao.facade;

import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.simulacao.dao.ProdutoDao;
import org.acme.simulacao.dao.SimulacaoDao;
import org.acme.simulacao.dto.SimulacaoAgregada;
import org.acme.simulacao.dto.SimulacaoDiariaResponse;
import org.acme.simulacao.dto.SimulacaoProdutoDiario;
import org.acme.model.produto.Produto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class RelatorioSimulacaoFacade {

    @Inject
    SimulacaoDao simulacaoDao;

    @Inject
    ProdutoDao produtoDao;

    @CacheResult(cacheName = "relatorios-diarios")
    public SimulacaoDiariaResponse executar(LocalDate data) {

        List<SimulacaoAgregada> agregados = simulacaoDao.getSimulacoesAgregadasPorDia(data);

        if (agregados.isEmpty()) {
            return new SimulacaoDiariaResponse(data.toString(), new ArrayList<>());
        }

        List<Integer> codigosDeProdutos = agregados.stream()
                .map(SimulacaoAgregada::getCoProduto)
                .distinct()
                .toList();

        Map<Integer, Produto> mapaDeProdutos = produtoDao.buscaProdutosPelosCodigos(codigosDeProdutos);

        List<SimulacaoProdutoDiario> simulacoesDiarias = agregados.stream().map(agregado -> {
            Produto produto = mapaDeProdutos.getOrDefault(agregado.getCoProduto(), new Produto());

            return new SimulacaoProdutoDiario(
                    produto.getCoProduto(),
                    produto.getNoProduto(),
                    agregado.getTaxaMediaJuroCalculada().setScale(9, RoundingMode.HALF_UP),
                    agregado.getValorMedioPrestacao(),
                    agregado.getValorTotalDesejado(),
                    agregado.getValorTotalCredito()
            );
        }).collect(Collectors.toList());

        return new SimulacaoDiariaResponse(data.toString(), simulacoesDiarias);
    }
}