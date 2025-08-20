package org.acme.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimulacaoDiariaResponse {
    private String dataReferencia;
    private List<SimulacaoProdutoDiario> simulacoes;
}