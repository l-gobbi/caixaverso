package org.acme.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.model.Produto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProdutoDao {
    private final List<Produto> produtos = new ArrayList<>();

    public ProdutoDao() {
        produtos.add(new Produto(1, "Produto 1", new BigDecimal("0.017900000"), 0, 24, new BigDecimal("200.00"), new BigDecimal("10000.00")));
        produtos.add(new Produto(2, "Produto 2", new BigDecimal("0.017500000"), 25, 48, new BigDecimal("10001.00"), new BigDecimal("100000.00")));
        produtos.add(new Produto(3, "Produto 3", new BigDecimal("0.018200000"), 49, 96, new BigDecimal("100000.01"), new BigDecimal("1000000.00")));
        produtos.add(new Produto(4, "Produto 4", new BigDecimal("0.015100000"), 96, null, new BigDecimal("1000000.01"), null));
    }

    public Optional<Produto> buscaProdutoPeloValorEPrazo(BigDecimal valor, int prazo) {
        return produtos.stream()
                .filter(p -> valor.compareTo(p.getVrMinimo()) >= 0 &&
                        (p.getVrMaximo() == null || valor.compareTo(p.getVrMaximo()) <= 0) &&
                        prazo >= p.getNuMinimoMeses() &&
                        (p.getNuMaximoMeses() == null || prazo <= p.getNuMaximoMeses()))
                .findFirst();
    }
}
