package com.duoc.msproductos.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTOs (Data Transfer Objects) del Microservicio de Productos.
 *
 * Los DTOs desacoplan la capa de presentación de la capa de dominio:
 * - ProductoRequestDTO: datos que llegan desde el cliente (POST/PUT)
 * - ProductoResponseDTO: datos que se envían al cliente (GET/respuestas)
 *
 * Ventajas:
 * 1. Evitar exponer la entidad directamente (seguridad)
 * 2. Control total sobre qué campos se reciben/envían
 * 3. Independencia entre contratos de API y modelo de datos
 */
public class ProductoDTO {

    // =============================================
    // DTO de REQUEST (entrada desde el cliente)
    // =============================================
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductoRequestDTO {

        @NotBlank(message = "El nombre no puede estar vacío")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        private String nombre;

        @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
        private String descripcion;

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
        @Digits(integer = 10, fraction = 2, message = "El precio debe tener máximo 2 decimales")
        private BigDecimal precio;

        @NotNull(message = "El stock es obligatorio")
        @Min(value = 0, message = "El stock no puede ser negativo")
        private Integer stock;

        @NotBlank(message = "La categoría no puede estar vacía")
        private String categoria;
    }

    // =============================================
    // DTO de RESPONSE (salida hacia el cliente)
    // =============================================
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductoResponseDTO {
        private Long id;
        private String nombre;
        private String descripcion;
        private BigDecimal precio;
        private Integer stock;
        private String categoria;
        private Boolean activo;
        private LocalDateTime fechaCreacion;
        private LocalDateTime fechaActualizacion;
    }

    // =============================================
    // DTO de RESPUESTA API genérica
    // =============================================
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApiResponseDTO<T> {
        private boolean success;
        private String message;
        private T data;
        private int statusCode;

        public static <T> ApiResponseDTO<T> ok(String message, T data) {
            return ApiResponseDTO.<T>builder()
                    .success(true)
                    .message(message)
                    .data(data)
                    .statusCode(200)
                    .build();
        }

        public static <T> ApiResponseDTO<T> created(String message, T data) {
            return ApiResponseDTO.<T>builder()
                    .success(true)
                    .message(message)
                    .data(data)
                    .statusCode(201)
                    .build();
        }

        public static <T> ApiResponseDTO<T> error(String message, int statusCode) {
            return ApiResponseDTO.<T>builder()
                    .success(false)
                    .message(message)
                    .data(null)
                    .statusCode(statusCode)
                    .build();
        }
    }
}
