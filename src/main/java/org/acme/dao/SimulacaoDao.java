package org.acme.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.acme.model.simulacao.Simulacao;

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
}