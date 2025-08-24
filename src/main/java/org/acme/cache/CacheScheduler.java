package org.acme.cache;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class CacheScheduler {

    @Inject
    CacheInvalidationService cacheInvalidationService;

    @Scheduled(cron = "{cache.cleanup.cron}")
    void cleanCaches() {
        log.info("Iniciando a limpeza agendada dos caches...");

        cacheInvalidationService.clearRelatoriosDiariosCache();
        cacheInvalidationService.clearProdutosPorValorPrazoCache();
        cacheInvalidationService.clearProdutosPorTaxasCache();

        log.info("Limpeza agendada dos caches concluída com sucesso.");
    }
}