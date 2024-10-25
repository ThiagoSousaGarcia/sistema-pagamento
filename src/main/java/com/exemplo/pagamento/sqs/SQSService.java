package com.exemplo.pagamento.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.exemplo.pagamento.domain.Pagamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SQSService {

    @Autowired
    private AmazonSQS amazonSQS;

    // URLs das filas SQS
    private static final String QUEUE_URL_PARCIAL = "url_parcial";
    private static final String QUEUE_URL_TOTAL = "url_total";
    private static final String QUEUE_URL_EXCEDENTE = "url_excedente";

    public void enviarParaFilaSQS(Pagamento pagamento, String status) {
        String queueUrl;
        switch (status) {
            case "Pagamento Parcial":
                queueUrl = QUEUE_URL_PARCIAL;
                break;
            case "Pagamento Total":
                queueUrl = QUEUE_URL_TOTAL;
                break;
            case "Pagamento Excedente":
                queueUrl = QUEUE_URL_EXCEDENTE;
                break;
            default:
                throw new IllegalArgumentException("Status desconhecido: " + status);
        }

        SendMessageRequest sendMsgRequest = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody("Pagamento: " + pagamento.getCodigoCobranca() + ", Status: " + status);
        amazonSQS.sendMessage(sendMsgRequest);
    }
}
