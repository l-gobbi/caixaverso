package org.acme.cache;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectSpy;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeast;

@QuarkusTest
public class CacheSchedulerTest {

    @InjectSpy
    CacheInvalidationService cacheInvalidationService;

    @Test
    public void testSchedulerInvalidaOsCaches() {
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            Mockito.verify(cacheInvalidationService, atLeast(1)).clearRelatoriosDiariosCache();
            Mockito.verify(cacheInvalidationService, atLeast(1)).clearProdutosPorValorPrazoCache();
            Mockito.verify(cacheInvalidationService, atLeast(1)).clearProdutosPorTaxasCache();
        });
    }
}