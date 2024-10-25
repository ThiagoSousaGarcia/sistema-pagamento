package com.exemplo.pagamento;

import com.exemplo.pagamento.controller.PagamentoController;
import com.exemplo.pagamento.domain.PagamentoResponse;
import com.exemplo.pagamento.domain.PagamentoRequest;
import com.exemplo.pagamento.service.ProcessamentoPagamentoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class PagamentoControllerTest {

    @InjectMocks
    private PagamentoController pagamentoController;

    @Mock
    private ProcessamentoPagamentoService processamentoPagamentoService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(pagamentoController).build();
    }

    @Test
    void deveProcessarPagamentoComSucesso() throws Exception {
        PagamentoRequest request = new PagamentoRequest("codigoVendedor", Collections.emptyList());
        PagamentoResponse response = new PagamentoResponse();
        response.setStatus(Collections.singletonList("Pagamento Total"));

        when(processamentoPagamentoService.processarPagamento(request)).thenReturn(response);

        MvcResult result = mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
        ).andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(new ObjectMapper().writeValueAsString(response), result.getResponse().getContentAsString());
    }

    @Test
    void deveRetornarErroQuandoVendedorNaoEncontrado() throws Exception {
        PagamentoRequest request = new PagamentoRequest("codigoInexistente", Collections.emptyList());

        when(processamentoPagamentoService.processarPagamento(request)).thenThrow(new RuntimeException("Vendedor não encontrado."));

        MvcResult result = mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
        ).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertEquals("Vendedor não encontrado.", result.getResponse().getContentAsString());
    }
}
