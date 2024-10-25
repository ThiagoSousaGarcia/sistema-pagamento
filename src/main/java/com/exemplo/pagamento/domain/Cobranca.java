package com.exemplo.pagamento.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Cobranca {
    @Id
    private String codigo;
    private double valor;

    public Cobranca() {}

    public Cobranca(String codigo, double valor) {
        this.codigo = codigo;
        this.valor = valor;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
