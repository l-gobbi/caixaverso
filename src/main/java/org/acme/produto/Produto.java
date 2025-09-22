package org.acme.produto;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Entity
public class Produto extends PanacheEntity {

    @Schema(description = "Nome do produto de empréstimo", defaultValue = "Crédito Pessoal")
    public String nome;
    @Schema(description = "Taxa de juros anual do produto em porcentagem", defaultValue = "18.0")
    public double taxaJurosAnual;
    @Schema(description = "Prazo máximo de pagamento em meses", defaultValue = "24")
    public int prazoMaximoMeses;

}