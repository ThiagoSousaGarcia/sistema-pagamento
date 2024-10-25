package com.exemplo.pagamento;

import com.exemplo.pagamento.domain.Cobranca;
import com.exemplo.pagamento.domain.Pagamento;
import com.exemplo.pagamento.domain.PagamentoRequest;
import com.exemplo.pagamento.domain.Vendedor;
import com.exemplo.pagamento.repository.CobrancaRepository;
import com.exemplo.pagamento.repository.VendedorRepository;
import com.exemplo.pagamento.service.ProcessamentoPagamentoService;
import com.exemplo.pagamento.sqs.SQSService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProcessamentoPagamentoServiceTest {

    @InjectMocks
    private ProcessamentoPagamentoService processamentoPagamentoService;

    @Mock
    private CobrancaRepository cobrancaRepository;

    @Mock
    private VendedorRepository vendedorRepository;

    @Mock
    private SQSService sqsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveProcessarPagamentoTotal() {
        Vendedor vendedor = new Vendedor("codigoVendedor");
        when(vendedorRepository.existsByCodigo("codigoVendedor")).thenReturn(true);
        when(cobrancaRepository.findByCodigo("codigoCobranca"))
                .thenReturn(Optional.of(new Cobranca("codigoCobranca", 100.0)));

        PagamentoRequest pagamentoRequest = new PagamentoRequest("codigoVendedor",
                Collections.singletonList(new Pagamento("codigoCobranca", 100.0)));

        var response = processamentoPagamentoService.processarPagamento(pagamentoRequest);

        assertEquals(Collections.singletonList("Pagamento Total"), response.getStatus());
        verify(sqsService, times(1)).enviarParaFilaSQS(any(Pagamento.class), eq("Pagamento Total"));
    }

    @Test
    void deveProcessarPagamentoParcial() {
        Vendedor vendedor = new Vendedor("codigoVendedor");
        when(vendedorRepository.existsByCodigo("codigoVendedor")).thenReturn(true);
        when(cobrancaRepository.findByCodigo("codigoCobranca"))
                .thenReturn(Optional.of(new Cobranca("codigoCobranca", 100.0)));

        PagamentoRequest pagamentoRequest = new PagamentoRequest("codigoVendedor",
                Collections.singletonList(new Pagamento("codigoCobranca", 50.0)));

        var response = processamentoPagamentoService.processarPagamento(pagamentoRequest);

        assertEquals(Collections.singletonList("Pagamento Parcial"), response.getStatus());
        verify(sqsService, times(1)).enviarParaFilaSQS(any(Pagamento.class), eq("Pagamento Parcial"));
    }

    @Test
    void deveProcessarPagamentoExcedente() {
        Vendedor vendedor = new Vendedor("codigoVendedor");
        when(vendedorRepository.existsByCodigo("codigoVendedor")).thenReturn(true);
        when(cobrancaRepository.findByCodigo("codigoCobranca"))
                .thenReturn(Optional.of(new Cobranca("codigoCobranca", 100.0)));

        PagamentoRequest pagamentoRequest = new PagamentoRequest("codigoVendedor",
                Collections.singletonList(new Pagamento("codigoCobranca", 150.0)));

        var response = processamentoPagamentoService.processarPagamento(pagamentoRequest);

        assertEquals(Collections.singletonList("Pagamento Excedente"), response.getStatus());
        verify(sqsService, times(1)).enviarParaFilaSQS(any(Pagamento.class), eq("Pagamento Excedente"));
    }
}
