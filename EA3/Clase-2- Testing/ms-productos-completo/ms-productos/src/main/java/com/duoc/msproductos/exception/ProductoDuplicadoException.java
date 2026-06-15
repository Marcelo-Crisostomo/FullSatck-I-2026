package com.duoc.msproductos.exception;

/**
 * Excepción lanzada cuando se intenta crear un producto con un nombre ya existente.
 * Mapeada a HTTP 409 Conflict por GlobalExceptionHandler.
 */
public class ProductoDuplicadoException extends RuntimeException {
    public ProductoDuplicadoException(String message) {
        super(message);
    }
}
