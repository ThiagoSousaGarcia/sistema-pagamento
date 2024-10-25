package com.exemplo.pagamento.service;

import com.exemplo.pagamento.domain.Cobranca;
import com.exemplo.pagamento.dto.PagamentoRequest;
import com.exemplo.pagamento.domain.Pagamento;
import com.exemplo.pagamento.repository.CobrancaRepository;
import com.exemplo.pagamento.repository.VendedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.exemplo.pagamento.domain.PagamentoStatus;

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
                pagamento.setStatus(PagamentoStatus.PARCIAL);
                sqsService.enviarMensagem(pagamento.toString(), PagamentoStatus.PARCIAL);
            } else if (pagamento.getValor() == cobranca.getValor()) {
                pagamento.setStatus(PagamentoStatus.TOTAL);
                sqsService.enviarMensagem(pagamento.toString(), PagamentoStatus.TOTAL);
            } else {
                pagamento.setStatus(PagamentoStatus.EXCEDENTE);
                sqsService.enviarMensagem(pagamento.toString(), PagamentoStatus.EXCEDENTE);
            }
        }

        return pagamentoRequest;
    }
}
