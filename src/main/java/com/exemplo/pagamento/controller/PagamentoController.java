package com.exemplo.pagamento.controller;

import com.exemplo.pagamento.dto.PagamentoRequest;
import com.exemplo.pagamento.service.ProcessamentoPagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {
    @Autowired
    private ProcessamentoPagamentoService pagamentoService;

    @PostMapping
    public ResponseEntity<PagamentoRequest> processarPagamentos(@RequestBody PagamentoRequest pagamentoRequest) {
        PagamentoRequest resposta = pagamentoService.processarPagamentos(pagamentoRequest);
        return ResponseEntity.ok(resposta);
    }
}
