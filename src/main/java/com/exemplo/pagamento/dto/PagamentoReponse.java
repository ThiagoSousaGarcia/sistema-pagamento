package com.exemplo.pagamento.dto;

import java.util.ArrayList;
import java.util.List;

public class PagamentoResponse {
    private List<String> status = new ArrayList<>();

    public List<String> getStatus() {
        return status;
    }

    public void setStatus(List<String> status) {
        this.status = status;
    }
}
