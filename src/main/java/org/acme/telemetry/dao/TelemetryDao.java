package org.acme.telemetry.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.acme.model.telemetry.Metricas;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class TelemetryDao {

    @Inject
    EntityManager em;

    @Transactional
    public Metricas salvar(Metricas metrica) {
        try {
            if (metrica.getId() == null) {
                em.persist(metrica);
                log.info("New metric persisted for endpoint: {} on date: {}",
                        metrica.getNomeEndpoint(), metrica.getDataReferencia());
            } else {
                metrica = em.merge(metrica);
                log.info("Metric updated for endpoint: {} on date: {}",
                        metrica.getNomeEndpoint(), metrica.getDataReferencia());
            }
            return metrica;
        } catch (Exception e) {
            log.error("Error saving metric for endpoint: {} on date: {}",
                    metrica.getNomeEndpoint(), metrica.getDataReferencia(), e);
            throw e;
        }
    }

    public Optional<Metricas> buscarPorEndpointEData(String nomeEndpoint, LocalDate dataReferencia) {
        try {
            TypedQuery<Metricas> query = em.createQuery(
                    "SELECT m FROM Metricas m WHERE m.nomeEndpoint = :endpoint AND m.dataReferencia = :data",
                    Metricas.class
            );
            query.setParameter("endpoint", nomeEndpoint);
            query.setParameter("data", dataReferencia);

            List<Metricas> resultados = query.getResultList();
            return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.get(0));
        } catch (Exception e) {
            log.error("Error fetching metric for endpoint: {} on date: {}", nomeEndpoint, dataReferencia, e);
            return Optional.empty();
        }
    }

    public List<Metricas> buscarPorData(LocalDate dataReferencia) {
        try {
            TypedQuery<Metricas> query = em.createQuery(
                    "SELECT m FROM Metricas m WHERE m.dataReferencia = :data ORDER BY m.nomeEndpoint",
                    Metricas.class
            );
            query.setParameter("data", dataReferencia);
            return query.getResultList();
        } catch (Exception e) {
            log.error("Error fetching metrics for date: {}", dataReferencia, e);
            return List.of();
        }
    }


}