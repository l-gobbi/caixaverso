package org.acme.telemetry.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EndpointStats {
    private String nomeApi;
    private long qtdRequisicoes;
    private double tempoMedio;
    private double tempoMinimo;
    private double tempoMaximo;
    private double percentualSucesso;
}