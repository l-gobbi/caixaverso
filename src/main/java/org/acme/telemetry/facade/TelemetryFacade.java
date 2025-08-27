package org.acme.telemetry.facade;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.model.telemetry.Metricas;
import org.acme.telemetry.dao.TelemetryDao;
import org.acme.telemetry.dto.EndpointStats;
import org.acme.telemetry.dto.TelemetryResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@ApplicationScoped
public class TelemetryFacade {

    @Inject
    TelemetryDao telemetryDao;


    public TelemetryResponse getTelemetryData(LocalDate data) {
        List<Metricas> metricasDoDia = telemetryDao.buscarPorData(data);


        List<EndpointStats> statsList = metricasDoDia.stream()
                .map(this::converterMetricaParaStats)
                .collect(Collectors.toList());

        return new TelemetryResponse(data.toString(), statsList);
    }

    @Transactional
    public void registrarRequisicao(String nomeEndpoint, double duracaoMs, int statusCode) {
        LocalDate hoje = LocalDate.now();
        boolean isSuccess = statusCode >= 200 && statusCode < 400;
        BigDecimal duracaoDecimal = BigDecimal.valueOf(duracaoMs);

        Optional<Metricas> metricaOpt = telemetryDao.buscarPorEndpointEData(nomeEndpoint, hoje);

        Metricas metrica;
        if (metricaOpt.isPresent()) {
            metrica = metricaOpt.get();
            metrica.setTotalRequisicoes(metrica.getTotalRequisicoes() + 1);
            metrica.setRequisicoesSuccesso(metrica.getRequisicoesSuccesso() + (isSuccess ? 1 : 0));
            metrica.setRequisicoesErro(metrica.getRequisicoesErro() + (isSuccess ? 0 : 1));
            metrica.setDuracaoTotalMs(metrica.getDuracaoTotalMs().add(duracaoDecimal));
            metrica.setDuracaoMinimaMs(metrica.getDuracaoMinimaMs().min(duracaoDecimal));
            metrica.setDuracaoMaximaMs(metrica.getDuracaoMaximaMs().max(duracaoDecimal));
        } else {
            metrica = Metricas.builder()
                    .nomeEndpoint(nomeEndpoint)
                    .dataReferencia(hoje)
                    .totalRequisicoes(1L)
                    .requisicoesSuccesso(isSuccess ? 1L : 0L)
                    .requisicoesErro(isSuccess ? 0L : 1L)
                    .duracaoTotalMs(duracaoDecimal)
                    .duracaoMinimaMs(duracaoDecimal)
                    .duracaoMaximaMs(duracaoDecimal)
                    .build();
        }
        telemetryDao.salvar(metrica);
    }

    private EndpointStats converterMetricaParaStats(Metricas metrica) {
        double percentualSucesso = metrica.calcularPercentualSucesso().doubleValue() / 100.0;

        return new EndpointStats(
                metrica.getNomeEndpoint(),
                metrica.getTotalRequisicoes(),
                metrica.calcularTempoMedio().doubleValue(),
                metrica.getDuracaoMinimaMs().doubleValue(),
                metrica.getDuracaoMaximaMs().doubleValue(),
                percentualSucesso
        );
    }

}