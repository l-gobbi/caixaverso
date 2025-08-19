package org.acme.facade;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.acme.dao.ProdutoDao;
import org.acme.dto.Parcela;
import org.acme.dto.ResultadoSimulacao;
import org.acme.dto.SimulacaoRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@ApplicationScoped
public class CalculaSimulacaoFacade {

    @Inject
    ProdutoDao produtoDao;

    public List<ResultadoSimulacao> calcular(SimulacaoRequest request, BigDecimal taxaJuros) {

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
        return new ResultadoSimulacao("PRICE", parcelas);
    }
}
