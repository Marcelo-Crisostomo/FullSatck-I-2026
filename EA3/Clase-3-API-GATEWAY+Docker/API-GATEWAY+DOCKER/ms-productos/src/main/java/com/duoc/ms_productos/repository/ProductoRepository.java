package com.duoc.ms_productos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.duoc.ms_productos.model.Producto;

@Repository
public class ProductoRepository {

    private final List<Producto> productos = List.of(
            new Producto(1L, "Teclado mecanico", 25990),
            new Producto(2L, "Mouse inalambrico", 12990),
            new Producto(3L, "Monitor 24 pulgadas", 89990)
    );

    public List<Producto> findAll() {
        return productos;
    }

    public Optional<Producto> findById(Long id) {
        return productos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }
}
