package com.exemplo.pagamento.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.exemplo.pagamento.domain.PagamentoStatus;


@Service
public class SQSService {
    @Autowired
    private AmazonSQS sqs;

    private static final String FILA_PARCIAL = "url_da_fila_parcial";
    private static final String FILA_TOTAL = "url_da_fila_total";
    private static final String FILA_EXCEDENTE = "url_da_fila_excedente";

    public void enviarMensagem(String mensagem, PagamentoStatus status) {
        String filaUrl;

        switch (status) {
            case PARCIAL:
                filaUrl = FILA_PARCIAL;
                break;
            case TOTAL:
                filaUrl = FILA_TOTAL;
                break;
            case EXCEDENTE:
                filaUrl = FILA_EXCEDENTE;
                break;
            default:
                throw new IllegalArgumentException("Status de pagamento inválido.");
        }

        SendMessageRequest sendMessageRequest = new SendMessageRequest(filaUrl, mensagem);
        sqs.sendMessage(sendMessageRequest);
    }
}
