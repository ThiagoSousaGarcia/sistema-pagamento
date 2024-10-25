package com.exemplo.pagamento;

import com.exemplo.pagamento.exception.CustomExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomExceptionHandlerTest {

    private CustomExceptionHandler customExceptionHandler;

    @BeforeEach
    void setUp() {
        customExceptionHandler = new CustomExceptionHandler();
    }

    @Test
    void deveRetornarErroQuandoRuntimeException() {
        RuntimeException ex = new RuntimeException("Erro de runtime");
        ResponseEntity<String> response = customExceptionHandler.handleRuntimeException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Erro de runtime", response.getBody());
    }

    @Test
    void deveRetornarErroQuandoExceptionGenerica() {
        Exception ex = new Exception("Erro geral");
        ResponseEntity<String> response = customExceptionHandler.handleException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Erro interno do servidor: Erro geral", response.getBody());
    }
}
