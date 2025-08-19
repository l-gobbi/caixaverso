package org.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class SimulacaoRequest {
    private BigDecimal valorDesejado;
    private int prazo;
}
