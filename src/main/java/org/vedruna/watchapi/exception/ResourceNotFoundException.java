package org.vedruna.watchapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción para cuando un recurso solicitado no se encuentra en el sistema.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    
    /**
     * Constructor con mensaje personalizado.
     * 
     * @param message Mensaje detallado del error.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
