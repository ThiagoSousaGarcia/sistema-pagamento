package com.exemplo.pagamento.service;

import com.exemplo.pagamento.domain.Cobranca;
import com.exemplo.pagamento.domain.Pagamento;
import com.exemplo.pagamento.domain.PagamentoRequest;
import com.exemplo.pagamento.exception.ResourceNotFoundException;
import com.exemplo.pagamento.repository.CobrancaRepository;
import com.exemplo.pagamento.repository.VendedorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProcessamentoPagamentoServiceTest {

    @InjectMocks
    private ProcessamentoPagamentoService processamentoPagamentoService;

    @Mock
    private VendedorRepository vendedorRepository;

    @Mock
    private CobrancaRepository cobrancaRepository;

    @Mock
    private SqsService sqsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveProcessarPagamentosComSucesso() {
        String codigoVendedor = "vendedor1";
        Pagamento pagamento = new Pagamento("cobranca1", 100);
        PagamentoRequest pagamentoRequest = new PagamentoRequest(codigoVendedor, Collections.singletonList(pagamento));

        when(vendedorRepository.existsByCodigo(codigoVendedor)).thenReturn(true);
        when(cobrancaRepository.findByCodigo("cobranca1")).thenReturn(Optional.of(new Cobranca("cobranca1", 100)));

        PagamentoRequest resultado = processamentoPagamentoService.processarPagamentos(pagamentoRequest);

        assertEquals("Pagamento Total", resultado.getPagamentos().get(0).getStatus());
        verify(sqsService, times(1)).enviarMensagem(anyString(), eq("Pagamento Total"));
    }

    @Test
    void deveRetornarErroQuandoVendedorNaoEncontrado() {
        String codigoVendedor = "vendedor1";
        Pagamento pagamento = new Pagamento("cobranca1", 100);
        PagamentoRequest pagamentoRequest = new PagamentoRequest(codigoVendedor, Collections.singletonList(pagamento));

        when(vendedorRepository.existsByCodigo(codigoVendedor)).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            processamentoPagamentoService.processarPagamentos(pagamentoRequest);
        });

        assertEquals("Vendedor não encontrado", exception.getMessage());
    }

    @Test
    void deveRetornarErroQuandoCobrancaNaoEncontrada() {
        String codigoVendedor = "vendedor1";
        Pagamento pagamento = new Pagamento("cobranca1", 100);
        PagamentoRequest pagamentoRequest = new PagamentoRequest(codigoVendedor, Collections.singletonList(pagamento));

        when(vendedorRepository.existsByCodigo(codigoVendedor)).thenReturn(true);
        when(cobrancaRepository.findByCodigo("cobranca1")).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            processamentoPagamentoService.processarPagamentos(pagamentoRequest);
        });

        assertEquals("Cobrança não encontrada", exception.getMessage());
    }

    @Test
    void deveIdentificarPagamentoParcial() {
        String codigoVendedor = "vendedor1";
        Pagamento pagamento = new Pagamento("cobranca1", 50);
        PagamentoRequest pagamentoRequest = new PagamentoRequest(codigoVendedor, Collections.singletonList(pagamento));

        when(vendedorRepository.existsByCodigo(codigoVendedor)).thenReturn(true);
        when(cobrancaRepository.findByCodigo("cobranca1")).thenReturn(Optional.of(new Cobranca("cobranca1", 100)));

        PagamentoRequest resultado = processamentoPagamentoService.processarPagamentos(pagamentoRequest);

        assertEquals("Pagamento Parcial", resultado.getPagamentos().get(0).getStatus());
        verify(sqsService, times(1)).enviarMensagem(anyString(), eq("Pagamento Parcial"));
    }

    @Test
    void deveIdentificarPagamentoExcedente() {
        String codigoVendedor = "vendedor1";
        Pagamento pagamento = new Pagamento("cobranca1", 150);
        PagamentoRequest pagamentoRequest = new PagamentoRequest(codigoVendedor, Collections.singletonList(pagamento));

        when(vendedorRepository.existsByCodigo(codigoVendedor)).thenReturn(true);
        when(cobrancaRepository.findByCodigo("cobranca1")).thenReturn(Optional.of(new Cobranca("cobranca1", 100)));

        PagamentoRequest resultado = processamentoPagamentoService.processarPagamentos(pagamentoRequest);

        assertEquals("Pagamento Excedente", resultado.getPagamentos().get(0).getStatus());
        verify(sqsService, times(1)).enviarMensagem(anyString(), eq("Pagamento Excedente"));
    }
}
