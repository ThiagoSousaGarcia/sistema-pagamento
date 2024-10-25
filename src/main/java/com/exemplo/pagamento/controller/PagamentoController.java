package com.exemplo.pagamento.controller;

import com.exemplo.pagamento.dto.PagamentoRequest;
import com.exemplo.pagamento.dto.PagamentoResponse;
import com.exemplo.pagamento.service.ProcessamentoPagamentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {

    private final ProcessamentoPagamentoService processamentoPagamentoService;

    public PagamentoController(ProcessamentoPagamentoService processamentoPagamentoService) {
        this.processamentoPagamentoService = processamentoPagamentoService;
    }

    @PostMapping
    public ResponseEntity<PagamentoResponse> processarPagamento(@RequestBody PagamentoRequest pagamentoRequest) {
        PagamentoResponse response = processamentoPagamentoService.processarPagamento(pagamentoRequest);
        return ResponseEntity.ok(response);
    }
}
