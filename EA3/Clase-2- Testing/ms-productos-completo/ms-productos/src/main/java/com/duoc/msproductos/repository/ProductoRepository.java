package com.duoc.msproductos.repository;

import com.duoc.msproductos.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository del Microservicio de Productos.
 *
 * Spring Data JPA genera automáticamente la implementación de estos métodos.
 * Patrones de nomenclatura:
 * - findBy{Campo}            → SELECT ... WHERE campo = ?
 * - findBy{Campo}Contains    → SELECT ... WHERE campo LIKE %?%
 * - findBy{Campo}And{Campo2} → SELECT ... WHERE campo = ? AND campo2 = ?
 * - @Query                   → JPQL personalizado
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /**
     * Busca todos los productos activos.
     * Equivale a: SELECT * FROM productos WHERE activo = true
     */
    List<Producto> findByActivoTrue();

    /**
     * Busca productos por categoría (case-insensitive).
     */
    List<Producto> findByCategoriaIgnoreCase(String categoria);

    /**
     * Busca productos por nombre (búsqueda parcial, case-insensitive).
     */
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Busca productos por categoría y que estén activos.
     */
    List<Producto> findByCategoriaIgnoreCaseAndActivoTrue(String categoria);

    /**
     * Busca productos con precio menor o igual al indicado.
     */
    List<Producto> findByPrecioLessThanEqual(BigDecimal precioMaximo);

    /**
     * Verifica si existe un producto con el mismo nombre (para evitar duplicados).
     */
    boolean existsByNombreIgnoreCase(String nombre);

    /**
     * Busca un producto por nombre exacto (para validaciones).
     */
    Optional<Producto> findByNombreIgnoreCase(String nombre);

    /**
     * Consulta JPQL personalizada: productos con stock bajo.
     * @param stockMinimo umbral mínimo de stock
     */
    @Query("SELECT p FROM Producto p WHERE p.stock <= :stockMinimo AND p.activo = true")
    List<Producto> findProductosConStockBajo(@Param("stockMinimo") int stockMinimo);

    /**
     * Consulta JPQL: cuenta productos por categoría.
     */
    @Query("SELECT p.categoria, COUNT(p) FROM Producto p WHERE p.activo = true GROUP BY p.categoria")
    List<Object[]> countByCategoria();
}
