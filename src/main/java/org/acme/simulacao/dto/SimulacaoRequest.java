package org.acme.simulacao.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class SimulacaoRequest {
    @NotNull(message = "O valor desejado não pode ser nulo")
    @Positive(message = "O valor desejado deve ser maior que zero")
    private BigDecimal valorDesejado;
    @Min(value = 1, message = "O prazo mínimo é de 1 mês")
    private int prazo;
}
