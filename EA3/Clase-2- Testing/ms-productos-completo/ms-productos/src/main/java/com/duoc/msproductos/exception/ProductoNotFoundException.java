package com.duoc.msproductos.exception;

/**
 * Excepción lanzada cuando un Producto no se encuentra en la base de datos.
 * Mapeada a HTTP 404 Not Found por GlobalExceptionHandler.
 */
public class ProductoNotFoundException extends RuntimeException {
    public ProductoNotFoundException(String message) {
        super(message);
    }
}
