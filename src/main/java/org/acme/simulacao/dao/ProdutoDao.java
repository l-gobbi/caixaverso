package org.acme.simulacao.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.Query;
import org.acme.model.produto.Produto;

import java.math.BigDecimal;
import java.util.Optional;

@ApplicationScoped
public class ProdutoDao {

    @Inject
    @PersistenceUnit(unitName = "consulta")
    EntityManager em;

    public Optional<Produto> buscaProdutoPeloValorEPrazo(BigDecimal valor, int prazo) {

        String sql = "SELECT * FROM PRODUTO p " +
                "WHERE ? >= p.VR_MINIMO " +
                "AND (? <= p.VR_MAXIMO OR p.VR_MAXIMO IS NULL) " +
                "AND ? >= p.NU_MINIMO_MESES " +
                "AND (? <= p.NU_MAXIMO_MESES OR p.NU_MAXIMO_MESES IS NULL)";

        Query query = em.createNativeQuery(sql, Produto.class);

        query.setParameter(1, valor);
        query.setParameter(2, valor);
        query.setParameter(3, prazo);
        query.setParameter(4, prazo);

        return query.getResultStream()
                .findFirst()
                .map(result -> (Produto) result);
    }

    public Optional<Produto> buscaProdutoPelaTaxa(BigDecimal taxa) {
        String sql = "SELECT * FROM PRODUTO p WHERE p.PC_TAXA_JUROS = ?";
        Query query = em.createNativeQuery(sql, Produto.class);
        query.setParameter(1, taxa);
        return query.getResultStream()
                .findFirst()
                .map(result -> (Produto) result);
    }
}
