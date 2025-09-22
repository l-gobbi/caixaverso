package org.acme.simulacao.dto;

import org.acme.produto.Produto;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

public class SimulacaoResponse {
    @Schema(description = "Dados do produto utilizado na simulação")
    public Produto produto;

    @Schema(description = "Valor solicitado na simulação", defaultValue = "10000.00")
    public double valorSolicitado;

    @Schema(description = "Prazo em meses da simulação", defaultValue = "12")
    public int prazoMeses;

    @Schema(description = "Taxa de juros efetiva calculada para o período mensal", defaultValue = "0.01388843")
    public double taxaJurosEfetivaMensal;

    @Schema(description = "Valor total a ser pago ao final do empréstimo (principal + juros)", defaultValue = "10910.46")
    public double valorTotalComJuros;

    @Schema(description = "Valor fixo da parcela mensal (Sistema Price)", defaultValue = "910.46")
    public double parcelaMensal;

    @Schema(description = "Detalhamento mês a mês da evolução do saldo devedor")
    public List<MemoriaCalculo> memoriaCalculo;
}