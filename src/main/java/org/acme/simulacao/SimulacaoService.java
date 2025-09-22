package org.acme.simulacao;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.produto.Produto;
import org.acme.simulacao.dto.MemoriaCalculo;
import org.acme.simulacao.dto.SimulacaoRequest;
import org.acme.simulacao.dto.SimulacaoResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SimulacaoService {

    public Optional<SimulacaoResponse> simular(SimulacaoRequest request) {
        Optional<Produto> produtoOpt = Produto.findByIdOptional(request.idProduto);

        if (produtoOpt.isEmpty()) {
            return Optional.empty(); // Produto não encontrado
        }

        Produto produto = produtoOpt.get();

        if (request.prazoMeses > produto.prazoMaximoMeses) {
            // Poderíamos lançar uma exceção de negócio aqui
            return Optional.empty();
        }

        // 1. Calcular taxa de juros efetiva mensal
        double taxaAnualDecimal = produto.taxaJurosAnual / 100.0;
        double taxaMensal = Math.pow(1 + taxaAnualDecimal, 1.0 / 12.0) - 1;

        // 2. Calcular Parcela (Sistema Price)
        double pmt = request.valorSolicitado * (taxaMensal * Math.pow(1 + taxaMensal, request.prazoMeses)) / (Math.pow(1 + taxaMensal, request.prazoMeses) - 1);

        // 3. Gerar memória de cálculo
        List<MemoriaCalculo> memoria = new ArrayList<>();
        double saldoDevedor = request.valorSolicitado;
        for (int i = 1; i <= request.prazoMeses; i++) {
            double juros = saldoDevedor * taxaMensal;
            double amortizacao = pmt - juros;
            double saldoDevedorInicial = saldoDevedor;
            saldoDevedor -= amortizacao;
            memoria.add(new MemoriaCalculo(i, saldoDevedorInicial, juros, amortizacao, saldoDevedor));
        }

        // 4. Montar a resposta
        SimulacaoResponse response = new SimulacaoResponse();
        response.produto = produto;
        response.valorSolicitado = request.valorSolicitado;
        response.prazoMeses = request.prazoMeses;
        response.taxaJurosEfetivaMensal = taxaMensal;
        response.parcelaMensal = pmt;
        response.valorTotalComJuros = pmt * request.prazoMeses;
        response.memoriaCalculo = memoria;

        return Optional.of(response);
    }
}