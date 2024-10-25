package com.exemplo.pagamento.dto;

import java.util.List;
import com.exemplo.pagamento.domain.Pagamento;


public class PagamentoRequest {
    private String codigoVendedor;
    private List<Pagamento> pagamentos;

    public PagamentoRequest() {}

    public PagamentoRequest(String codigoVendedor, List<Pagamento> pagamentos) {
        this.codigoVendedor = codigoVendedor;
        this.pagamentos = pagamentos;
    }

    public String getCodigoVendedor() {
        return codigoVendedor;
    }

    public void setCodigoVendedor(String codigoVendedor) {
        this.codigoVendedor = codigoVendedor;
    }

    public List<Pagamento> getPagamentos() {
        return pagamentos;
    }

    public void setPagamentos(List<Pagamento> pagamentos) {
        this.pagamentos = pagamentos;
    }
}
