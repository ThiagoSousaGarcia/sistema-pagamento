package com.exemplo.pagamento.service;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SQSService {

    private final SqsClient sqsClient;

    @Autowired
    public SQSService(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    public void enviarMensagem(String urlFila, String mensagem) {
        SendMessageRequest requestSQS = SendMessageRequest.builder()
                .queueUrl(urlFila)
                .messageBody(mensagem)
                .build();

        sqsClient.sendMessage(requestSQS);
    }
}
