package org.acme.model.produto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PRODUTO")
public class Produto {

    @Id
    @Column(name = "CO_PRODUTO")
    private int coProduto;

    @Column(name = "NO_PRODUTO")
    private String noProduto;

    @Column(name = "PC_TAXA_JUROS")
    private BigDecimal pcTaxaJuros;

    @Column(name = "NU_MINIMO_MESES")
    private int nuMinimoMeses;

    @Column(name = "NU_MAXIMO_MESES")
    private Integer nuMaximoMeses;

    @Column(name = "VR_MINIMO")
    private BigDecimal vrMinimo;

    @Column(name = "VR_MAXIMO")
    private BigDecimal vrMaximo;
}