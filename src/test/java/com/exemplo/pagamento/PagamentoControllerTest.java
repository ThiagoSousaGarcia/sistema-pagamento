package com.exemplo.pagamento.controller;

import com.exemplo.pagamento.domain.PagamentoRequest;
import com.exemplo.pagamento.service.ProcessamentoPagamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class PagamentoControllerTest {

    @InjectMocks
    private PagamentoController pagamentoController;

    @Mock
    private ProcessamentoPagamentoService processamentoPagamentoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveRetornarOkParaProcessarPagamentos() {
        PagamentoRequest pagamentoRequest = new PagamentoRequest("vendedor1", Collections.emptyList());

        when(processamentoPagamentoService.processarPagamentos(pagamentoRequest)).thenReturn(pagamentoRequest);

        ResponseEntity<PagamentoRequest> response = pagamentoController.processarPagamentos(pagamentoRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pagamentoRequest, response.getBody());
    }

    @Test
    void deveRetornarNotFoundQuandoVendedorNaoEncontrado() {
        PagamentoRequest pagamentoRequest = new PagamentoRequest("vendedor_inexistente", Collections.emptyList());

        when(processamentoPagamentoService.processarPagamentos(pagamentoRequest)).thenThrow(new ResourceNotFoundException("Vendedor não encontrado"));

        ResponseEntity<Object> response = pagamentoController.processarPagamentos(pagamentoRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Vendedor não encontrado", response.getBody());
    }

    @Test
    void deveRetornarNotFoundQuandoCobrancaNaoEncontrada() {
        PagamentoRequest pagamentoRequest = new PagamentoRequest("vendedor1", Collections.singletonList(new Pagamento("cobranca_inexistente", 100)));

        when(processamentoPagamentoService.processarPagamentos(pagamentoRequest)).thenThrow(new ResourceNotFoundException("Cobrança não encontrada"));

        ResponseEntity<Object> response = pagamentoController.processarPagamentos(pagamentoRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Cobrança não encontrada", response.getBody());
    }
}
