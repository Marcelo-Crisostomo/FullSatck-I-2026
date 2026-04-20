package com.example.ms_productos.repository;


import com.example.ms_productos.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository nos entrega CRUD listo:
// save, findAll, findById, deleteById, etc.
public interface ProductoRepository extends JpaRepository<Producto, Long> {
}