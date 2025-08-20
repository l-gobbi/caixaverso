package org.acme.facade;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.dto.EndpointStats;
import org.acme.dto.TelemetryResponse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class TelemetryFacade {

    @Inject
    MeterRegistry registry;

    public TelemetryResponse getTelemetryData() {
        List<EndpointStats> statsList = new ArrayList<>();

        statsList.add(createStatsForEndpoint("fazerSimulacao"));
        statsList.add(createStatsForEndpoint("listarSimulacoes"));
        statsList.add(createStatsForEndpoint("health"));

        return new TelemetryResponse(LocalDate.now().toString(), statsList);
    }

    private EndpointStats createStatsForEndpoint(String apiName) {
        // Busca o Timer pela métrica que definimos na anotação @Timed
        Timer timer = registry.find("endpoint." + apiName + ".tempo").timer();

        // ** LÓGICA CORRIGIDA **
        // Busca o contador de SUCESSO pela nossa tag customizada
        Counter successRequests = registry.find("endpoint." + apiName + ".requisicoes").tag("outcome", "SUCCESS").counter();

        // Busca o contador de ERRO pela nossa tag customizada
        Counter errorRequests = registry.find("endpoint." + apiName + ".requisicoes").tag("outcome", "ERROR").counter();

        long successCount = (successRequests != null) ? (long) successRequests.count() : 0;
        long errorCount = (errorRequests != null) ? (long) errorRequests.count() : 0;
        long totalCount = successCount + errorCount;

        double successPercentage = (totalCount > 0) ? ((double) successCount / totalCount) : 0.0;

        double avg = 0;
        double min = 0;
        double max = 0;

        if (timer != null) {
            avg = timer.mean(TimeUnit.MILLISECONDS);
            max = timer.max(TimeUnit.MILLISECONDS);
        }

        return new EndpointStats(
                apiName,
                totalCount,
                avg,
                min,
                max,
                successPercentage
        );
    }
}