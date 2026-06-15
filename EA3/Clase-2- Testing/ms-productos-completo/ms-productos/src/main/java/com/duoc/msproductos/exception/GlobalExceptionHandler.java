package com.duoc.msproductos.exception;

import com.duoc.msproductos.dto.ProductoDTO.ApiResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Manejador Global de Excepciones (Global Exception Handler).
 *
 * @RestControllerAdvice intercepta TODAS las excepciones lanzadas
 * en cualquier Controller del microservicio y las convierte en
 * respuestas HTTP con formato consistente.
 *
 * Patrón: en lugar de manejar excepciones en cada Controller (try/catch),
 * las manejamos en un solo lugar → código más limpio y DRY.
 *
 * Mapa de excepciones a HTTP:
 * - ProductoNotFoundException     → 404 Not Found
 * - ProductoDuplicadoException    → 409 Conflict
 * - MethodArgumentNotValidException → 400 Bad Request (validaciones @Valid)
 * - AuthenticationException       → 401 Unauthorized
 * - AccessDeniedException         → 403 Forbidden
 * - Exception                     → 500 Internal Server Error
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja el caso cuando un producto no existe.
     * Retorna HTTP 404.
     */
    @ExceptionHandler(ProductoNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleProductoNotFound(
            ProductoNotFoundException ex, WebRequest request) {
        log.warn("Producto no encontrado: {} | Request: {}", ex.getMessage(), request.getDescription(false));
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDTO.error(ex.getMessage(), 404));
    }

    /**
     * Maneja el caso cuando se intenta crear un producto duplicado.
     * Retorna HTTP 409.
     */
    @ExceptionHandler(ProductoDuplicadoException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleProductoDuplicado(
            ProductoDuplicadoException ex, WebRequest request) {
        log.warn("Producto duplicado: {} | Request: {}", ex.getMessage(), request.getDescription(false));
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponseDTO.error(ex.getMessage(), 409));
    }

    /**
     * Maneja errores de validación de Bean Validation (@Valid).
     * Se activa cuando un @RequestBody falla las validaciones (@NotBlank, @Min, etc.).
     * Retorna HTTP 400 con detalle de cada campo que falló.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String campo = ((FieldError) error).getField();
            String mensaje = error.getDefaultMessage();
            errores.put(campo, mensaje);
        });
        log.warn("Errores de validación: {}", errores);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.<Map<String, String>>builder()
                        .success(false)
                        .message("Errores de validación en los datos enviados")
                        .data(errores)
                        .statusCode(400)
                        .build());
    }

    /**
     * Maneja errores de autenticación (credenciales inválidas, token expirado).
     * Retorna HTTP 401.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleAuthenticationException(
            AuthenticationException ex) {
        log.warn("Error de autenticación: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponseDTO.error("No autorizado: " + ex.getMessage(), 401));
    }

    /**
     * Maneja errores de autorización (usuario autenticado pero sin permisos).
     * Retorna HTTP 403.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleAccessDeniedException(
            AccessDeniedException ex) {
        log.warn("Acceso denegado: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponseDTO.error("Acceso denegado: no tiene permisos para esta operación", 403));
    }

    /**
     * Captura cualquier excepción no manejada específicamente.
     * Retorna HTTP 500. NUNCA exponer el stack trace al cliente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleGenericException(
            Exception ex, WebRequest request) {
        log.error("Error interno no controlado: {} | Request: {}",
                ex.getMessage(), request.getDescription(false), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error(
                        "Error interno del servidor. Por favor contacte al administrador.", 500));
    }
}
