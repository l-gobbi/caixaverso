package org.acme.telemetry.facade;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.telemetry.dto.EndpointStats;
import org.acme.telemetry.dto.TelemetryResponse;

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

        statsList.add(createStatsForEndpoint("simulacoes.post"));
        statsList.add(createStatsForEndpoint("simulacoes.get"));
        statsList.add(createStatsForEndpoint("simulacoes.diarias.get"));

        return new TelemetryResponse(LocalDate.now().toString(), statsList);
    }

    private EndpointStats createStatsForEndpoint(String apiName) {

        Timer timer = registry.find("endpoint." + apiName + ".tempo").timer();

        Counter successRequests = registry.find("endpoint." + apiName + ".requisicoes").tag("outcome", "SUCCESS").counter();

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

            ValueAtPercentile[] percentiles = timer.takeSnapshot().percentileValues();
            if (percentiles.length > 0 && percentiles[0].percentile() == 0.0) {
                min = percentiles[0].value(TimeUnit.MILLISECONDS);
            }
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