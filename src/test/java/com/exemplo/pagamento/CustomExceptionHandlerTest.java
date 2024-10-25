package com.exemplo.pagamento.handler;

import com.exemplo.pagamento.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomExceptionHandlerTest {

    private final CustomExceptionHandler exceptionHandler = new CustomExceptionHandler();

    @Test
    void deveRetornarErroQuandoResourceNotFound() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Cobrança não encontrada");

        ResponseEntity<Object> response = exceptionHandler.handleResourceNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Cobrança não encontrada", response.getBody());
    }

    @Test
    void deveRetornarErroGenericoQuandoExcecaoNaoReconhecida() {
        Exception exception = new Exception("Erro inesperado");

        ResponseEntity<Object> response = exceptionHandler.handleGenericException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Erro inesperado", response.getBody());
    }
}
