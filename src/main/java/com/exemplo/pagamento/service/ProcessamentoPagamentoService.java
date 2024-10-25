package com.exemplo.pagamento.service;

import com.exemplo.pagamento.dto.PagamentoResponse;
import com.exemplo.pagamento.domain.PagamentoRequest;
import com.exemplo.pagamento.domain.Cobranca;
import com.exemplo.pagamento.domain.Pagamento;
import com.exemplo.pagamento.repository.CobrancaRepository;
import com.exemplo.pagamento.repository.VendedorRepository;
import com.exemplo.pagamento.sqs.SQSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProcessamentoPagamentoService {

    @Autowired
    private CobrancaRepository cobrancaRepository;

    @Autowired
    private VendedorRepository vendedorRepository;

    @Autowired
    private SQSService sqsService;

    public PagamentoResponse processarPagamento(PagamentoRequest pagamentoRequest) {
        List<String> listaStatus = new ArrayList<>();

        if (!vendedorRepository.existsByCodigo(pagamentoRequest.getCodigoVendedor())) {
            throw new RuntimeException("Vendedor não encontrado.");
        }

        for (Pagamento pagamento : pagamentoRequest.getPagamentos()) {
            Cobranca cobranca = cobrancaRepository.findByCodigo(pagamento.getCodigoCobranca())
                    .orElseThrow(() -> new RuntimeException("Código da cobrança não encontrado."));

            String status;
            if (pagamento.getValor() < cobranca.getValor()) {
                status = "Pagamento Parcial";
            } else if (pagamento.getValor() == cobranca.getValor()) {
                status = "Pagamento Total";
            } else {
                status = "Pagamento Excedente";
            }

            listaStatus.add(status);
            sqsService.enviarParaFilaSQS(pagamento, status);
        }

        PagamentoResponse response = new PagamentoResponse();
        response.setStatus(listaStatus);
        return response;
    }
}
