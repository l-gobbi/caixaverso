package org.acme.model.telemetry;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "metricas")
public class Metricas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "nome_endpoint", length = 100)
    private String nomeEndpoint;

    @NotNull
    @Column(name = "data_referencia")
    private LocalDate dataReferencia;

    @NotNull
    @PositiveOrZero
    @Column(name = "total_requisicoes")
    private Long totalRequisicoes;

    @NotNull
    @PositiveOrZero
    @Column(name = "requisicoes_sucesso")
    private Long requisicoesSuccesso;

    @NotNull
    @PositiveOrZero
    @Column(name = "requisicoes_erro")
    private Long requisicoesErro;

    @NotNull
    @PositiveOrZero
    @Column(name = "duracao_total_ms", precision = 18, scale = 2)
    private BigDecimal duracaoTotalMs;

    @NotNull
    @PositiveOrZero
    @Column(name = "duracao_minima_ms", precision = 18, scale = 2)
    private BigDecimal duracaoMinimaMs;

    @NotNull
    @PositiveOrZero
    @Column(name = "duracao_maxima_ms", precision = 18, scale = 2)
    private BigDecimal duracaoMaximaMs;

    @NotNull
    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @NotNull
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    public BigDecimal calcularTempoMedio() {
        if (totalRequisicoes == 0) {
            return BigDecimal.ZERO;
        }
        return duracaoTotalMs.divide(BigDecimal.valueOf(totalRequisicoes), 2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal calcularPercentualSucesso() {
        if (totalRequisicoes == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(requisicoesSuccesso)
                .divide(BigDecimal.valueOf(totalRequisicoes), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}