package org.acme.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.acme.dto.SimulacaoAgregada;
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
                "s.taxajuros, " +
                "AVG(((s.valortotal - s.valordesejado) / s.valordesejado) / (s.prazo / 12.0)), " +
                "AVG(s.valortotal / s.prazo), " +
                "SUM(s.valordesejado), " +
                "SUM(s.valortotal) " +
                "FROM simulacao s " +
                "WHERE CAST(s.datasimulacao AS DATE) = :data " +
                "GROUP BY s.taxajuros";

        Query query = em.createNativeQuery(sql);
        query.setParameter("data", data);

        List<Object[]> results = query.getResultList();
        List<SimulacaoAgregada> agregados = new ArrayList<>();

        for (Object[] result : results) {
            SimulacaoAgregada agregado = new SimulacaoAgregada(
                    (BigDecimal) result[0],
                    (BigDecimal) result[1],
                    (BigDecimal) result[2],
                    (BigDecimal) result[3],
                    (BigDecimal) result[4]
            );
            agregados.add(agregado);
        }

        return agregados;
    }
}