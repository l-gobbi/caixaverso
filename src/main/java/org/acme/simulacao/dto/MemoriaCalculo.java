package org.acme.simulacao.dto;

// Usaremos esta classe para a lista detalhada mês a mês
public class MemoriaCalculo {
    public int mes;
    public double saldoDevedorInicial;
    public double juros;
    public double amortizacao;
    public double saldoDevedorFinal;

    public MemoriaCalculo(int mes, double saldoDevedorInicial, double juros, double amortizacao, double saldoDevedorFinal) {
        this.mes = mes;
        this.saldoDevedorInicial = saldoDevedorInicial;
        this.juros = juros;
        this.amortizacao = amortizacao;
        this.saldoDevedorFinal = saldoDevedorFinal;
    }
}