package com.exemplo.pagamento.service;

import com.exemplo.pagamento.domain.Cobranca;
import com.exemplo.pagamento.dto.PagamentoRequest;
import com.exemplo.pagamento.domain.Pagamento;
import com.exemplo.pagamento.repository.CobrancaRepository;
import com.exemplo.pagamento.repository.VendedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProcessamentoPagamentoService {
    @Autowired
    private CobrancaRepository cobrancaRepository;

    @Autowired
    private VendedorRepository vendedorRepository;

    @Autowired
    private SQSService sqsService;

    public PagamentoRequest processarPagamentos(PagamentoRequest pagamentoRequest) {
        if (!vendedorRepository.existsByCodigo(pagamentoRequest.getCodigoVendedor())) {
            throw new RuntimeException("Vendedor não encontrado");
        }

        for (Pagamento pagamento : pagamentoRequest.getPagamentos()) {
            Cobranca cobranca = cobrancaRepository.findByCodigo(pagamento.getCodigoCobranca())
                    .orElseThrow(() -> new RuntimeException("Cobrança não encontrada"));

            if (pagamento.getValor() < cobranca.getValor()) {
                pagamento.setStatus("Pagamento Parcial");
                sqsService.enviarMensagem(pagamento.toString(), "Pagamento Parcial");
            } else if (pagamento.getValor() == cobranca.getValor()) {
                pagamento.setStatus("Pagamento Total");
                sqsService.enviarMensagem(pagamento.toString(), "Pagamento Total");
            } else {
                pagamento.setStatus("Pagamento Excedente");
                sqsService.enviarMensagem(pagamento.toString(), "Pagamento Excedente");
            }
        }

        return pagamentoRequest;
    }
}
