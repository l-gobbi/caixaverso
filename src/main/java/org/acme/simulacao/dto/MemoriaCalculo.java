package org.acme.simulacao.dto;

public record MemoriaCalculo(int mes, double saldoDevedorInicial, double juros, double amortizacao,
                             double saldoDevedorFinal) {
}