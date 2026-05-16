package com.example.gamesapi;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import org.springframework.data.mapping.PropertyReferenceException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Parametro de ordenacao invalido (ex: sort=string quando o campo nao existe)
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<Map<String, Object>> handlePropertyReference(PropertyReferenceException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", 400);
        body.put("error", "Parametro de ordenacao invalido");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // Recurso nao encontrado (ex: findById().orElseThrow())
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoSuchElementException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", 404);
        body.put("error", "Nao encontrado");
        body.put("message", "O recurso solicitado nao foi encontrado.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // Erros de validacao do @Valid no @RequestBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", 400);
        body.put("error", "Erro de validacao");

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe ->
                fieldErrors.put(fe.getField(), fe.getDefaultMessage()));
        body.put("campos", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // Erros de validacao do @Validated nos @PathVariable / @RequestParam
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraint(ConstraintViolationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", 400);
        body.put("error", "Erro de validacao");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // Excecoes lancadas manualmente com ResponseStatusException
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", ex.getStatusCode().value());
        body.put("error", ex.getReason());
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }

    // JSON invalido ou enum inexistente no body (400)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", 400);
        body.put("error", "Requisicao invalida");
        body.put("message", "O corpo da requisicao esta malformado ou contem valores invalidos. Verifique o JSON enviado.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // Metodo HTTP nao suportado (405)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", 405);
        body.put("error", "Metodo nao permitido");
        body.put("message", "O metodo " + ex.getMethod() + " nao e suportado neste endpoint. Metodos suportados: " + String.join(", ", ex.getSupportedMethods() != null ? ex.getSupportedMethods() : new String[]{}));
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(body);
    }

    // Parametro de query obrigatorio ausente (400)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParam(MissingServletRequestParameterException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", 400);
        body.put("error", "Parametro obrigatorio ausente");
        body.put("message", "O parametro '" + ex.getParameterName() + "' e obrigatorio.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // Tipo de argumento invalido (ex: id=abc ao inves de numero)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", 400);
        body.put("error", "Tipo de parametro invalido");
        body.put("message", "O parametro '" + ex.getName() + "' recebeu o valor '" + ex.getValue() + "' que nao e do tipo esperado.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // Argumento invalido (ex: PropertyReferenceException encapsulada pelo Spring)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", 400);
        body.put("error", "Argumento invalido");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // Excecao generica (fallback) - 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", 500);
        body.put("error", "Erro interno do servidor");
        body.put("message", ex.getMessage());
        body.put("exception", ex.getClass().getName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
