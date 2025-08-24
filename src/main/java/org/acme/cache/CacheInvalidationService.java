package org.acme.cache;

import io.quarkus.cache.CacheInvalidateAll;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CacheInvalidationService {

    @CacheInvalidateAll(cacheName = "relatorios-diarios")
    public void clearRelatoriosDiariosCache() {}

    @CacheInvalidateAll(cacheName = "produtos-por-valor-prazo")
    public void clearProdutosPorValorPrazoCache() {}

    @CacheInvalidateAll(cacheName = "produtos-por-taxas")
    public void clearProdutosPorTaxasCache() {}
}