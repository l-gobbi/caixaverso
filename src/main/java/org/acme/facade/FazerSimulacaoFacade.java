package org.acme.facade;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import org.acme.dao.ProdutoDao;
import org.acme.dao.SimulacaoDao;
import org.acme.dto.*;
import org.acme.mensageria.SimulacaoEventPublisher;
import org.acme.model.produto.Produto;
import org.acme.model.simulacao.Simulacao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class FazerSimulacaoFacade {

    @Inject
    ProdutoDao produtoDao;
    @Inject
    SimulacaoDao simulacaoDao;
    @Inject
    SimulacaoEventPublisher simulacaoEventPublisher;

    public SimulacaoResponse executar(SimulacaoRequest request) {

        Produto produto = buscarProduto(request);

        List<ResultadoSimulacao> resultados = calcularResultados(request, produto.getPcTaxaJuros());

        Simulacao simulacaoSalva = salvarEPublicar(request, produto, resultados);

        return montarResposta(simulacaoSalva, produto, resultados);
    }

    @Transactional
    protected Simulacao salvarEPublicar(SimulacaoRequest request, Produto produto, List<ResultadoSimulacao> resultados) {

        Simulacao simulacaoSalva = salvarSimulacao(request, produto, resultados);

        SimulacaoResponse responsePayload = montarResposta(simulacaoSalva, produto, resultados);

        simulacaoEventPublisher.publicar(responsePayload.toString());

        return simulacaoSalva;
    }

    private Produto buscarProduto(SimulacaoRequest request) {
        return produtoDao.buscaProdutoPeloValorEPrazo(request.getValorDesejado(), request.getPrazo())
                .orElseThrow(() -> new NotFoundException("Nenhum produto de crédito disponível para os critérios informados."));
    }

    private Simulacao salvarSimulacao(SimulacaoRequest request, Produto produto, List<ResultadoSimulacao> resultados) {
        Simulacao simulacao = new Simulacao();
        simulacao.setValorDesejado(request.getValorDesejado());
        simulacao.setPrazo(request.getPrazo());
        simulacao.setDataSimulacao(LocalDateTime.now());
        simulacao.setTaxaJuros(produto.getPcTaxaJuros());

        Optional<ResultadoSimulacao> price = resultados.stream()
                .filter(r -> "PRICE".equals(r.getTipo()))
                .findFirst();

        if (price.isPresent()) {
            BigDecimal valorTotal = price.get().getParcelas().stream()
                    .map(Parcela::getValorPrestacao)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            simulacao.setValorTotal(valorTotal);
        }

        simulacaoDao.persist(simulacao);
        return simulacao;
    }

    private List<ResultadoSimulacao> calcularResultados(SimulacaoRequest request, BigDecimal taxaJuros) {
        List<ResultadoSimulacao> resultados = new ArrayList<>();
        resultados.add(calcularSac(request, taxaJuros));
        resultados.add(calcularPrice(request, taxaJuros));
        return resultados;
    }

    private ResultadoSimulacao calcularSac(SimulacaoRequest request, BigDecimal taxaJuros) {
        List<Parcela> parcelas = new ArrayList<>();
        BigDecimal saldoDevedor = request.getValorDesejado();
        BigDecimal amortizacao = saldoDevedor.divide(BigDecimal.valueOf(request.getPrazo()), 2, RoundingMode.HALF_UP);

        for (int i = 1; i <= request.getPrazo(); i++) {
            BigDecimal juros = saldoDevedor.multiply(taxaJuros).setScale(2, RoundingMode.HALF_UP);
            BigDecimal prestacao = amortizacao.add(juros);
            parcelas.add(new Parcela(i, amortizacao, juros, prestacao));
            saldoDevedor = saldoDevedor.subtract(amortizacao);
        }

        return new ResultadoSimulacao("SAC", parcelas);
    }

    private ResultadoSimulacao calcularPrice(SimulacaoRequest request, BigDecimal taxaJuros) {
        List<Parcela> parcelas = new ArrayList<>();
        BigDecimal saldoDevedor = request.getValorDesejado();
        BigDecimal taxaMaisUm = taxaJuros.add(BigDecimal.ONE);

        BigDecimal pmt = saldoDevedor.multiply(
                taxaMaisUm.pow(request.getPrazo()).multiply(taxaJuros)
        ).divide(
                taxaMaisUm.pow(request.getPrazo()).subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP
        );

        for (int i = 1; i <= request.getPrazo(); i++) {
            BigDecimal juros = saldoDevedor.multiply(taxaJuros).setScale(2, RoundingMode.HALF_UP);
            BigDecimal amortizacao = pmt.subtract(juros);
            parcelas.add(new Parcela(i, amortizacao, juros, pmt));
            saldoDevedor = saldoDevedor.subtract(amortizacao);
        }
        return new ResultadoSimulacao("PRICE", parcelas);    }

    private SimulacaoResponse montarResposta(Simulacao simulacaoSalva, Produto produto, List<ResultadoSimulacao> resultados) {
        SimulacaoResponse response = new SimulacaoResponse();
        response.setIdSimulacao(simulacaoSalva.getId());
        response.setCodigoProduto(produto.getCoProduto());
        response.setDescricaoProduto(produto.getNoProduto());
        response.setTaxaJuros(produto.getPcTaxaJuros());
        response.setResultadoSimulacao(resultados);
        return response;
    }
}