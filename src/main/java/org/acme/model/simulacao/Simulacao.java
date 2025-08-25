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
    @Column(name = "CO_PRODUTO", nullable = false)
    private int coProduto;
    @Column(name = "VALOR_DESEJADO")
    private BigDecimal valorDesejado;
    @Column(name = "PRAZO")
    private int prazo;
    @Column(name = "TAXA_JUROS", precision = 10, scale = 9)
    private BigDecimal taxaJuros;
    @Column(name = "VALOR_TOTAL")
    private BigDecimal valorTotal;
    @Column(name = "DATA_SIMULACAO")
    private LocalDateTime dataSimulacao;
}