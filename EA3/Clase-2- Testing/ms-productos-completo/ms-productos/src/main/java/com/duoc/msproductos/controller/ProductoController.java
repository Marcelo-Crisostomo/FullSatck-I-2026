package com.duoc.msproductos.controller;

import com.duoc.msproductos.dto.ProductoDTO.*;
import com.duoc.msproductos.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST del Microservicio de Productos.
 *
 * Responsabilidades de la capa Controller:
 * 1. Recibir y validar la request HTTP
 * 2. Delegar la lógica al Service
 * 3. Construir y retornar la response HTTP apropiada
 *
 * El Controller NO debe contener lógica de negocio.
 * Toda la lógica va en el Service.
 *
 * Acceso en: http://localhost:8081/api/v1/productos
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "API de gestión del catálogo de productos")
public class ProductoController {

    private final ProductoService productoService;

    // =============================================
    // GET - Operaciones de Lectura (Públicas)
    // =============================================

    @Operation(
        summary = "Listar todos los productos",
        description = "Retorna todos los productos activos en el catálogo. Acceso público."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<ProductoResponseDTO>>> listarTodos() {
        log.debug("GET /api/v1/productos");
        List<ProductoResponseDTO> productos = productoService.obtenerTodos();
        return ResponseEntity.ok(ApiResponseDTO.ok(
                "Productos obtenidos exitosamente", productos));
    }

    @Operation(
        summary = "Obtener producto por ID",
        description = "Retorna un producto específico por su identificador único."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ProductoResponseDTO>> obtenerPorId(
            @Parameter(description = "ID del producto", example = "1")
            @PathVariable Long id) {
        log.debug("GET /api/v1/productos/{}", id);
        ProductoResponseDTO producto = productoService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponseDTO.ok("Producto encontrado", producto));
    }

    @Operation(summary = "Buscar por categoría", description = "Filtra productos por categoría.")
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<ApiResponseDTO<List<ProductoResponseDTO>>> obtenerPorCategoria(
            @Parameter(description = "Nombre de la categoría", example = "Electrónica")
            @PathVariable String categoria) {
        List<ProductoResponseDTO> productos = productoService.obtenerPorCategoria(categoria);
        return ResponseEntity.ok(ApiResponseDTO.ok(
                "Productos de categoría '" + categoria + "'", productos));
    }

    @Operation(summary = "Buscar por nombre", description = "Búsqueda parcial de productos por nombre.")
    @GetMapping("/buscar")
    public ResponseEntity<ApiResponseDTO<List<ProductoResponseDTO>>> buscarPorNombre(
            @Parameter(description = "Texto a buscar en el nombre", example = "laptop")
            @RequestParam String nombre) {
        List<ProductoResponseDTO> productos = productoService.buscarPorNombre(nombre);
        return ResponseEntity.ok(ApiResponseDTO.ok(
                "Resultados de búsqueda para: " + nombre, productos));
    }

    @Operation(summary = "Productos con stock bajo", description = "Retorna productos con stock igual o inferior al umbral.")
    @GetMapping("/stock-bajo")
    public ResponseEntity<ApiResponseDTO<List<ProductoResponseDTO>>> stockBajo(
            @Parameter(description = "Umbral mínimo de stock", example = "5")
            @RequestParam(defaultValue = "5") int umbral) {
        List<ProductoResponseDTO> productos = productoService.obtenerConStockBajo(umbral);
        return ResponseEntity.ok(ApiResponseDTO.ok(
                "Productos con stock <= " + umbral, productos));
    }

    // =============================================
    // POST - Crear (requiere ADMIN)
    // =============================================

    @Operation(
        summary = "Crear nuevo producto",
        description = "Crea un nuevo producto en el catálogo. **Requiere rol ADMIN.**",
        security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de administrador"),
        @ApiResponse(responseCode = "409", description = "Nombre de producto ya existe")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<ProductoResponseDTO>> crear(
            @Valid @RequestBody ProductoRequestDTO request) {
        log.debug("POST /api/v1/productos - nombre: {}", request.getNombre());
        ProductoResponseDTO productoCreado = productoService.crear(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDTO.created("Producto creado exitosamente", productoCreado));
    }

    // =============================================
    // PUT - Actualizar (requiere ADMIN)
    // =============================================

    @Operation(
        summary = "Actualizar producto",
        description = "Actualiza todos los campos de un producto existente. **Requiere rol ADMIN.**",
        security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "409", description = "Nombre de producto ya existe")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<ProductoResponseDTO>> actualizar(
            @Parameter(description = "ID del producto a actualizar", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequestDTO request) {
        log.debug("PUT /api/v1/productos/{}", id);
        ProductoResponseDTO productoActualizado = productoService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponseDTO.ok(
                "Producto actualizado exitosamente", productoActualizado));
    }

    // =============================================
    // DELETE - Eliminar lógico (requiere ADMIN)
    // =============================================

    @Operation(
        summary = "Eliminar producto",
        description = "Desactiva un producto del catálogo (eliminación lógica). **Requiere rol ADMIN.**",
        security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Void>> eliminar(
            @Parameter(description = "ID del producto a eliminar", example = "1")
            @PathVariable Long id) {
        log.debug("DELETE /api/v1/productos/{}", id);
        productoService.eliminar(id);
        return ResponseEntity.ok(ApiResponseDTO.<Void>builder()
                .success(true)
                .message("Producto eliminado exitosamente")
                .statusCode(200)
                .build());
    }
}
