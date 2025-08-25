package org.acme.simulacao.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimulacaoAgregada {
    private int coProduto;
    private BigDecimal taxaJuros;
    private BigDecimal taxaMediaJuroCalculada;
    private BigDecimal valorMedioPrestacao;
    private BigDecimal valorTotalDesejado;
    private BigDecimal valorTotalCredito;
}