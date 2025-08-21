package org.acme.simulacao.facade;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.simulacao.dao.ProdutoDao;
import org.acme.simulacao.dao.SimulacaoDao;
import org.acme.simulacao.dto.SimulacaoAgregada;
import org.acme.simulacao.dto.SimulacaoDiariaResponse;
import org.acme.simulacao.dto.SimulacaoProdutoDiario;
import org.acme.model.produto.Produto;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class RelatorioSimulacaoFacade {

    @Inject
    SimulacaoDao simulacaoDao;

    @Inject
    ProdutoDao produtoDao;

    public SimulacaoDiariaResponse executar(LocalDate data) {
        // 1. Busca os dados agregados do banco de simulações
        List<SimulacaoAgregada> agregados = simulacaoDao.getSimulacoesAgregadasPorDia(data);
        List<SimulacaoProdutoDiario> simulacoesDiarias = new ArrayList<>();

        // 2. Para cada resultado agregado, busca as informações do produto no outro banco
        for (SimulacaoAgregada agregado : agregados) {
            Produto produto = produtoDao.buscaProdutoPelaTaxa(agregado.getTaxaJuros())
                    .orElse(new Produto()); // Caso o produto não seja encontrado

            simulacoesDiarias.add(new SimulacaoProdutoDiario(
                    produto.getCoProduto(),
                    produto.getNoProduto(),
                    agregado.getTaxaMediaJuroCalculada().setScale(9, RoundingMode.HALF_UP),
                    agregado.getValorMedioPrestacao(),
                    agregado.getValorTotalDesejado(),
                    agregado.getValorTotalCredito()
            ));
        }

        return new SimulacaoDiariaResponse(data.toString(), simulacoesDiarias);
    }
}