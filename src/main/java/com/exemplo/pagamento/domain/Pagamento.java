package com.exemplo.pagamento.domain;

public class Pagamento {
    private String codigoCobranca;
    private double valor;

    public Pagamento() {}

    public Pagamento(String codigoCobranca, double valor) {
        this.codigoCobranca = codigoCobranca;
        this.valor = valor;
    }

    public String getCodigoCobranca() {
        return codigoCobranca;
    }

    public void setCodigoCobranca(String codigoCobranca) {
        this.codigoCobranca = codigoCobranca;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}