package com.exemplo.pagamento.service;

import com.exemplo.pagamento.domain.Cobranca;
import com.exemplo.pagamento.domain.Pagamento;
import com.exemplo.pagamento.dto.PagamentoRequest;
import com.exemplo.pagamento.repository.CobrancaRepository;
import com.exemplo.pagamento.repository.VendedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProcessamentoPagamentoService {

    @Autowired
    private CobrancaRepository cobrancaRepository;

    @Autowired
    private VendedorRepository vendedorRepository;

    @Autowired
    private SQSService sqsService;

    private static final String URL_DA_FILA_PARCIAL = "URL_DA_FILA_PARCIAL";
    private static final String URL_DA_FILA_TOTAL = "URL_DA_FILA_TOTAL";
    private static final String URL_DA_FILA_EXCEDENTE = "URL_DA_FILA_EXCEDENTE";

    public ResponseEntity<PagamentoRequest> processarPagamentos(PagamentoRequest request) {
        if (!vendedorRepository.existsByCodigo(request.getCodigoVendedor())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        for (Pagamento pagamento : request.getPagamentos()) {
            Cobranca cobranca = cobrancaRepository.findByCodigo(pagamento.getCodigoCobranca()).orElse(null);
            if (cobranca == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            double valorCobranca = cobranca.getValor();
            if (pagamento.getValorPago() < valorCobranca) {
                sqsService.enviarMensagem(URL_DA_FILA_PARCIAL, "Pagamento parcial: " + pagamento);
            } else if (pagamento.getValorPago() == valorCobranca) {
                sqsService.enviarMensagem(URL_DA_FILA_TOTAL, "Pagamento total: " + pagamento);
            } else {
                sqsService.enviarMensagem(URL_DA_FILA_EXCEDENTE, "Pagamento excedente: " + pagamento);
            }
        }

        return ResponseEntity.ok(request);
    }
}
