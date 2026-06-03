package org.vedruna.watchapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción para peticiones inválidas o lógicas incorrectas enviadas por el cliente.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    /**
     * Constructor con mensaje personalizado.
     * 
     * @param message Mensaje detallado del error.
     */
    public BadRequestException(String message) {
        super(message);
    }
}
