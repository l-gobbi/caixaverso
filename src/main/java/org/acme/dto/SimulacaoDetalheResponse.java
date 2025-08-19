package org.acme.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class SimulacaoDetalheResponse {
    private Long idSimulacao;
    private BigDecimal valorDesejado;
    private int prazo;
    private BigDecimal valorTotalParcelas;
}