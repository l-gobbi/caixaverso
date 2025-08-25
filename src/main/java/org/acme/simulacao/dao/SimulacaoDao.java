package org.acme.simulacao.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.acme.simulacao.dto.SimulacaoAgregada;
import org.acme.model.simulacao.Simulacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class SimulacaoDao {

    @Inject
    EntityManager em;

    public void persist(Simulacao simulacao) {
        em.persist(simulacao);
    }

    public List<Simulacao> listAll(int page, int pageSize) {
        TypedQuery<Simulacao> query = em.createQuery("SELECT s FROM Simulacao s ORDER BY s.dataSimulacao DESC", Simulacao.class);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    public long count() {
        return em.createQuery("SELECT COUNT(s) FROM Simulacao s", Long.class).getSingleResult();
    }

    public List<SimulacaoAgregada> getSimulacoesAgregadasPorDia(LocalDate data) {
        String sql = "SELECT " +
                "s.co_produto, " +
                "s.taxa_juros, " +
                "AVG(((s.valor_total - s.valor_desejado) / s.valor_desejado) / (s.prazo / 12.0)), " +
                "AVG(s.valor_total / s.prazo), " +
                "SUM(s.valor_desejado), " +
                "SUM(s.valor_total) " +
                "FROM simulacao s " +
                "WHERE CAST(s.data_simulacao AS DATE) = :data " +
                "GROUP BY s.co_produto, s.taxa_juros";

        Query query = em.createNativeQuery(sql);
        query.setParameter("data", data);

        List<Object[]> results = query.getResultList();
        List<SimulacaoAgregada> agregados = new ArrayList<>();

        for (Object[] result : results) {
            SimulacaoAgregada agregado = new SimulacaoAgregada(
                    (Integer) result[0],
                    (BigDecimal) result[1],
                    (BigDecimal) result[2],
                    (BigDecimal) result[3],
                    (BigDecimal) result[4],
                    (BigDecimal) result[5]
            );
            agregados.add(agregado);
        }

        return agregados;
    }
}