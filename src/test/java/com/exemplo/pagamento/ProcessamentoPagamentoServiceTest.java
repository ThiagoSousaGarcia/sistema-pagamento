package com.exemplo.pagamento.service;

import com.exemplo.pagamento.domain.Cobranca;
import com.exemplo.pagamento.domain.Pagamento;
import com.exemplo.pagamento.domain.PagamentoStatus;
import com.exemplo.pagamento.dto.PagamentoRequest;
import com.exemplo.pagamento.repository.CobrancaRepository;
import com.exemplo.pagamento.repository.VendedorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ProcessamentoPagamentoServiceTest {

    @Mock
    private CobrancaRepository cobrancaRepository;

    @Mock
    private VendedorRepository vendedorRepository;

    @Mock
    private SQSService sqsService;

    @InjectMocks
    private ProcessamentoPagamentoService processamentoPagamentoService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveLancarExcecaoQuandoVendedorNaoExiste() {
        PagamentoRequest pagamentoRequest = new PagamentoRequest("codigo_invalido", Collections.emptyList());

        when(vendedorRepository.existsByCodigo("codigo_invalido")).thenReturn(false);

        assertThrows(RuntimeException.class, () ->
                processamentoPagamentoService.processarPagamentos(pagamentoRequest));
    }

    @Test
    void deveLancarExcecaoQuandoCobrancaNaoExiste() {
        Pagamento pagamento = new Pagamento("codigo_invalido", 100.0);
        PagamentoRequest pagamentoRequest = new PagamentoRequest("codigo_vendedor", Collections.singletonList(pagamento));

        when(vendedorRepository.existsByCodigo("codigo_vendedor")).thenReturn(true);
        when(cobrancaRepository.findByCodigo("codigo_invalido")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                processamentoPagamentoService.processarPagamentos(pagamentoRequest));
    }

    @Test
    void deveProcessarPagamentoParcial() {
        Pagamento pagamento = new Pagamento("codigo_cobranca", 50.0);
        PagamentoRequest pagamentoRequest = new PagamentoRequest("codigo_vendedor", Collections.singletonList(pagamento));

        Cobranca cobranca = new Cobranca("codigo_cobranca", 100.0);

        when(vendedorRepository.existsByCodigo("codigo_vendedor")).thenReturn(true);
        when(cobrancaRepository.findByCodigo("codigo_cobranca")).thenReturn(Optional.of(cobranca));

        processamentoPagamentoService.processarPagamentos(pagamentoRequest);

        assertEquals(PagamentoStatus.PARCIAL, pagamento.getStatus());
        verify(sqsService).enviarMensagem(pagamento.toString(), PagamentoStatus.PARCIAL);
    }

    @Test
    void deveProcessarPagamentoTotal() {
        Pagamento pagamento = new Pagamento("codigo_cobranca", 100.0);
        PagamentoRequest pagamentoRequest = new PagamentoRequest("codigo_vendedor", Collections.singletonList(pagamento));

        Cobranca cobranca = new Cobranca("codigo_cobranca", 100.0);

        when(vendedorRepository.existsByCodigo("codigo_vendedor")).thenReturn(true);
        when(cobrancaRepository.findByCodigo("codigo_cobranca")).thenReturn(Optional.of(cobranca));

        processamentoPagamentoService.processarPagamentos(pagamentoRequest);

        assertEquals(PagamentoStatus.TOTAL, pagamento.getStatus());
        verify(sqsService).enviarMensagem(pagamento.toString(), PagamentoStatus.TOTAL);
    }

    @Test
    void deveProcessarPagamentoExcedente() {
        Pagamento pagamento = new Pagamento("codigo_cobranca", 150.0);
        PagamentoRequest pagamentoRequest = new PagamentoRequest("codigo_vendedor", Collections.singletonList(pagamento));

        Cobranca cobranca = new Cobranca("codigo_cobranca", 100.0);

        when(vendedorRepository.existsByCodigo("codigo_vendedor")).thenReturn(true);
        when(cobrancaRepository.findByCodigo("codigo_cobranca")).thenReturn(Optional.of(cobranca));

        processamentoPagamentoService.processarPagamentos(pagamentoRequest);

        assertEquals(PagamentoStatus.EXCEDENTE, pagamento.getStatus());
        verify(sqsService).enviarMensagem(pagamento.toString(), PagamentoStatus.EXCEDENTE);
    }
}
