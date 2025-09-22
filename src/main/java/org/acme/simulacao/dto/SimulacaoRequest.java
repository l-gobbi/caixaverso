package org.acme.simulacao.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public class SimulacaoRequest {

    @NotNull
    @Schema(description = "ID do produto de empréstimo a ser simulado", example = "1")
    public Long idProduto;

    @Min(value = 1, message = "O valor solicitado deve ser maior que zero")
    @Schema(description = "Valor total do empréstimo solicitado", example = "10000.00")
    public double valorSolicitado;

    @Min(value = 1, message = "O prazo deve ser de no mínimo 1 mês")
    @Schema(description = "Número de meses para pagar o empréstimo", example = "12")
    public int prazoMeses;
}