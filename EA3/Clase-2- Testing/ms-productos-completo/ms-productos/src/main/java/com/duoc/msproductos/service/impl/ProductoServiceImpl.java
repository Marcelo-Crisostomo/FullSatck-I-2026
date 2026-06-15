package com.duoc.msproductos.service.impl;

import com.duoc.msproductos.dto.ProductoDTO.*;
import com.duoc.msproductos.exception.ProductoDuplicadoException;
import com.duoc.msproductos.exception.ProductoNotFoundException;
import com.duoc.msproductos.model.Producto;
import com.duoc.msproductos.repository.ProductoRepository;
import com.duoc.msproductos.service.ProductoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del Servicio de Productos.
 *
 * Aquí vive la LÓGICA DE NEGOCIO del microservicio.
 * La capa Service:
 * - NO conoce HttpRequest/HttpResponse (eso es del Controller)
 * - NO conoce SQL/JPQL directamente (eso es del Repository)
 * - SÍ conoce las reglas de negocio (validaciones, transformaciones)
 *
 * @Transactional: asegura que las operaciones sean atómicas.
 * Si algo falla, se hace rollback automático.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    // ================================================
    // MÉTODOS DE LECTURA (@Transactional readOnly=true)
    // ================================================

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> obtenerTodos() {
        log.info("Obteniendo todos los productos activos");
        return productoRepository.findByActivoTrue()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerPorId(Long id) {
        log.info("Buscando producto con ID: {}", id);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(
                        "Producto no encontrado con ID: " + id));
        return mapToResponseDTO(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> obtenerPorCategoria(String categoria) {
        log.info("Buscando productos por categoría: {}", categoria);
        return productoRepository.findByCategoriaIgnoreCaseAndActivoTrue(categoria)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> buscarPorNombre(String nombre) {
        log.info("Buscando productos por nombre: {}", nombre);
        return productoRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> obtenerConStockBajo(int stockMinimo) {
        log.info("Buscando productos con stock <= {}", stockMinimo);
        return productoRepository.findProductosConStockBajo(stockMinimo)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // ================================================
    // MÉTODOS DE ESCRITURA
    // ================================================

    @Override
    public ProductoResponseDTO crear(ProductoRequestDTO request) {
        log.info("Creando producto: {}", request.getNombre());

        // Regla de negocio: no permitir nombres duplicados
        if (productoRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ProductoDuplicadoException(
                    "Ya existe un producto con el nombre: " + request.getNombre());
        }

        Producto producto = mapToEntity(request);
        Producto productoGuardado = productoRepository.save(producto);

        log.info("Producto creado exitosamente con ID: {}", productoGuardado.getId());
        return mapToResponseDTO(productoGuardado);
    }

    @Override
    public ProductoResponseDTO actualizar(Long id, ProductoRequestDTO request) {
        log.info("Actualizando producto con ID: {}", id);

        Producto productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(
                        "Producto no encontrado con ID: " + id));

        // Regla de negocio: si cambia el nombre, verificar que no exista otro con ese nombre
        if (!productoExistente.getNombre().equalsIgnoreCase(request.getNombre())
                && productoRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ProductoDuplicadoException(
                    "Ya existe un producto con el nombre: " + request.getNombre());
        }

        // Actualizar campos
        productoExistente.setNombre(request.getNombre());
        productoExistente.setDescripcion(request.getDescripcion());
        productoExistente.setPrecio(request.getPrecio());
        productoExistente.setStock(request.getStock());
        productoExistente.setCategoria(request.getCategoria());

        Producto productoActualizado = productoRepository.save(productoExistente);
        log.info("Producto actualizado exitosamente: {}", id);
        return mapToResponseDTO(productoActualizado);
    }

    @Override
    public void eliminar(Long id) {
        log.info("Eliminando (desactivando) producto con ID: {}", id);

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(
                        "Producto no encontrado con ID: " + id));

        // Eliminación lógica (soft delete)
        producto.setActivo(false);
        productoRepository.save(producto);
        log.info("Producto desactivado exitosamente: {}", id);
    }

    // ================================================
    // MÉTODOS PRIVADOS DE MAPEO (Entity <-> DTO)
    // ================================================

    /**
     * Convierte una entidad Producto a ProductoResponseDTO.
     * Este método es privado, pero está cubierto indirectamente por los tests.
     */
    private ProductoResponseDTO mapToResponseDTO(Producto producto) {
        return ProductoResponseDTO.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .categoria(producto.getCategoria())
                .activo(producto.getActivo())
                .fechaCreacion(producto.getFechaCreacion())
                .fechaActualizacion(producto.getFechaActualizacion())
                .build();
    }

    /**
     * Convierte un ProductoRequestDTO a entidad Producto.
     */
    private Producto mapToEntity(ProductoRequestDTO request) {
        return Producto.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .stock(request.getStock())
                .categoria(request.getCategoria())
                .activo(true)
                .build();
    }
}
