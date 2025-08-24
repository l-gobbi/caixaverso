package org.acme.cache;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Slf4j
@Path("/api/v1/cache")
@ApplicationScoped
@Tag(name = "Cache", description = "Endpoints para gerenciamento de cache")
public class CacheRest {

    @Inject
    org.acme.cache.CacheInvalidationService cacheInvalidationService;

    @POST
    @Path("/clear")
    @Produces(MediaType.TEXT_PLAIN)
    public Response clearAllCaches() {
        log.info("Recebida requisição para limpar todos os caches...");

        cacheInvalidationService.clearRelatoriosDiariosCache();
        cacheInvalidationService.clearProdutosPorValorPrazoCache();
        cacheInvalidationService.clearProdutosPorTaxasCache();

        log.info("Todos os caches foram invalidados com sucesso.");
        return Response.ok("Todos os caches foram limpos com sucesso.").build();
    }
}