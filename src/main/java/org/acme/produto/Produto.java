package org.acme.produto;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Produto extends PanacheEntity {

    public String nome;
    public double taxaJurosAnual;
    public int prazoMaximoMeses;

}