package com.duoc.msproductos.service;

import com.duoc.msproductos.dto.ProductoDTO.ProductoRequestDTO;
import com.duoc.msproductos.dto.ProductoDTO.ProductoResponseDTO;

import java.util.List;

/**
 * Interfaz del Servicio de Productos.
 *
 * Define el CONTRATO (qué hace) sin exponer CÓMO lo hace.
 * La implementación concreta está en ProductoServiceImpl.
 *
 * Ventajas de usar interfaces en el Service:
 * 1. Facilita el TESTING con mocks (Mockito puede mockear la interfaz)
 * 2. Permite múltiples implementaciones (ej: cache, versiones)
 * 3. Principio de Inversión de Dependencias (SOLID - D)
 * 4. Desacoplamiento entre capas
 */
public interface ProductoService {

    /**
     * Obtiene todos los productos activos del sistema.
     * @return Lista de productos activos como DTOs
     */
    List<ProductoResponseDTO> obtenerTodos();

    /**
     * Obtiene un producto por su ID.
     * @param id identificador del producto
     * @return Producto encontrado como DTO
     * @throws com.duoc.msproductos.exception.ProductoNotFoundException si no existe
     */
    ProductoResponseDTO obtenerPorId(Long id);

    /**
     * Crea un nuevo producto en el sistema.
     * @param request DTO con los datos del producto a crear
     * @return Producto creado como DTO
     * @throws com.duoc.msproductos.exception.ProductoDuplicadoException si el nombre ya existe
     */
    ProductoResponseDTO crear(ProductoRequestDTO request);

    /**
     * Actualiza un producto existente.
     * @param id identificador del producto a actualizar
     * @param request DTO con los nuevos datos
     * @return Producto actualizado como DTO
     * @throws com.duoc.msproductos.exception.ProductoNotFoundException si no existe
     */
    ProductoResponseDTO actualizar(Long id, ProductoRequestDTO request);

    /**
     * Elimina lógicamente un producto (lo desactiva).
     * No elimina físicamente de la base de datos.
     * @param id identificador del producto a eliminar
     * @throws com.duoc.msproductos.exception.ProductoNotFoundException si no existe
     */
    void eliminar(Long id);

    /**
     * Busca productos por categoría.
     * @param categoria nombre de la categoría
     * @return Lista de productos de esa categoría
     */
    List<ProductoResponseDTO> obtenerPorCategoria(String categoria);

    /**
     * Busca productos por nombre (búsqueda parcial).
     * @param nombre texto a buscar en el nombre
     * @return Lista de productos que coinciden
     */
    List<ProductoResponseDTO> buscarPorNombre(String nombre);

    /**
     * Obtiene productos con stock bajo.
     * @param stockMinimo umbral de stock mínimo
     * @return Lista de productos con stock <= stockMinimo
     */
    List<ProductoResponseDTO> obtenerConStockBajo(int stockMinimo);
}
