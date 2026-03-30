package com.duoc.productosapi.repository;

// Importamos el modelo Producto
import com.duoc.productosapi.model.Producto;

// Importamos JpaRepository
import org.springframework.data.jpa.repository.JpaRepository;

// Esta interfaz hereda métodos listos como findAll, findById, save y deleteById
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
}