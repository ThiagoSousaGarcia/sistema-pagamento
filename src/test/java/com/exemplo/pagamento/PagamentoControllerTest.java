package com.exemplo.pagamento.controller;

import com.exemplo.pagamento.dto.PagamentoRequest;
import com.exemplo.pagamento.domain.Pagamento;
import com.exemplo.pagamento.service.ProcessamentoPagamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PagamentoControllerTest {

    @InjectMocks
    private PagamentoController pagamentoController;

    @Mock
    private ProcessamentoPagamentoService pagamentoService;

    private PagamentoRequest pagamentoRequest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        List<Pagamento> pagamentos = new ArrayList<>();
        pagamentos.add(new Pagamento("cobranca123", 100.0));

        pagamentoRequest = new PagamentoRequest("vendedor123", pagamentos);
    }

    @Test
    public void testProcessarPagamentos() {
        PagamentoRequest respostaEsperada = new PagamentoRequest("vendedor123", new ArrayList<>());
        when(pagamentoService.processarPagamentos(pagamentoRequest)).thenReturn(respostaEsperada);

        ResponseEntity<PagamentoRequest> response = pagamentoController.processarPagamentos(pagamentoRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(respostaEsperada, response.getBody());
    }
}
