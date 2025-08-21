package org.acme.simulacao.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoSimulacao {
    private String tipo;
    private List<Parcela> parcelas;
}