package org.acme.facade;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.dao.SimulacaoDao;
import org.acme.dto.Parcela;
import org.acme.dto.ResultadoSimulacao;
import org.acme.dto.SimulacaoRequest;
import org.acme.model.Produto;
import org.acme.model.simulacao.Simulacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SalvarSimulacaoFacade {

    @Inject
    SimulacaoDao simulacaoDao;

    @Transactional
    public void executar(SimulacaoRequest request, Produto produto, List<ResultadoSimulacao> resultados) {
        Simulacao simulacao = new Simulacao();
        simulacao.setValorDesejado(request.getValorDesejado());
        simulacao.setPrazo(request.getPrazo());
        simulacao.setDataSimulacao(LocalDateTime.now());
        simulacao.setTaxaJuros(produto.getPcTaxaJuros());

        // Lógica para calcular o valor total a partir das parcelas
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
    }
}