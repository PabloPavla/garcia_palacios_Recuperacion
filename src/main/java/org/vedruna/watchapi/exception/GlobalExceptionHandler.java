package org.vedruna.watchapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador de asesoramiento REST global que intercepta todas las excepciones 
 * lanzadas por los controladores y devuelve un objeto de tipo ProblemDetail (RFC 7807).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Captura la excepción de recurso no encontrado (HTTP 404).
     * 
     * @param ex La excepción capturada.
     * @return El objeto ProblemDetail con los detalles del error.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Recurso no encontrado");
        problem.setType(URI.create("https://api.watchapi.es/errors/not-found"));
        return problem;
    }

    /**
     * Captura la excepción de petición incorrecta (HTTP 400).
     * 
     * @param ex La excepción capturada.
     * @return El objeto ProblemDetail con los detalles del error.
     */
    @ExceptionHandler(BadRequestException.class)
    public ProblemDetail handleBadRequest(BadRequestException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Petición incorrecta");
        problem.setType(URI.create("https://api.watchapi.es/errors/bad-request"));
        return problem;
    }

    /**
     * Captura los errores de validación de argumentos/DTOs anotados con @Valid (HTTP 400).
     * 
     * @param ex La excepción capturada.
     * @return El objeto ProblemDetail detallando los campos erróneos en 'invalid_params'.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Error de validación en los campos");
        problem.setTitle("Validación fallida");
        problem.setType(URI.create("https://api.watchapi.es/errors/validation-error"));

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        problem.setProperty("invalid_params", errors);
        return problem;
    }

    /**
     * Captura los errores de credenciales inválidas en el login (HTTP 401).
     * 
     * @param ex La excepción de BadCredentialsException.
     * @return El objeto ProblemDetail correspondiente.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Nombre de usuario o contraseña incorrectos");
        problem.setTitle("Credenciales inválidas");
        problem.setType(URI.create("https://api.watchapi.es/errors/unauthorized"));
        return problem;
    }

    /**
     * Captura de forma global cualquier otra excepción no controlada (HTTP 500).
     * 
     * @param ex La excepción genérica.
     * @return El objeto ProblemDetail correspondiente.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGlobalException(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problem.setTitle("Error interno del servidor");
        problem.setType(URI.create("https://api.watchapi.es/errors/internal-server-error"));
        return problem;
    }
}
