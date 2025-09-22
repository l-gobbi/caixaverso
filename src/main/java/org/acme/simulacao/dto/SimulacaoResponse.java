package org.acme.simulacao.dto;

import org.acme.produto.Produto;
import java.util.List;

public class SimulacaoResponse {
    public Produto produto;
    public double valorSolicitado;
    public int prazoMeses;
    public double taxaJurosEfetivaMensal;
    public double valorTotalComJuros;
    public double parcelaMensal;
    public List<MemoriaCalculo> memoriaCalculo;
}