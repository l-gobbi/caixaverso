package org.acme.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Produto {
    private int coProduto;
    private String noProduto;
    private BigDecimal pcTaxaJuros;
    private int nuMinimoMeses;
    private Integer nuMaximoMeses;
    private BigDecimal vrMinimo;
    private BigDecimal vrMaximo;
}