package org.acme.model.simulacao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "simulacao")
@Getter
@Setter
public class Simulacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal valorDesejado;
    private int prazo;
    @Column(precision = 10, scale = 9)
    private BigDecimal taxaJuros;
    private BigDecimal valorTotal;
    private LocalDateTime dataSimulacao;
}