package org.acme.facade;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.acme.dao.ProdutoDao;
import org.acme.dto.SimulacaoRequest;
import org.acme.model.produto.Produto;

import java.util.Optional;

@Slf4j
@ApplicationScoped
public class BuscaProdutoFacade {

    @Inject
    ProdutoDao produtoDao;

    public Produto buscarProduto(SimulacaoRequest request) {
        Optional<Produto> produtoOpt = produtoDao.buscaProdutoPeloValorEPrazo(request.getValorDesejado(), request.getPrazo());

        if (produtoOpt.isEmpty()) {
            log.info("Nenhum produto encontrado para as condições desejadas: Valor = {}, Prazo = {}", request.getValorDesejado(), request.getPrazo());
        }

        return produtoOpt.orElseThrow(() -> {
            log.warn("Nenhum produto encontrado para as condições desejadas: Valor = {}, Prazo = {}", request.getValorDesejado(), request.getPrazo());
            return new NotFoundException("Nenhum produto de crédito disponível para os critérios informados.");
        });
    }

}
