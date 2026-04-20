package com.example.ms_productos.controller;


import com.example.ms_productos.model.Producto;
import com.example.ms_productos.repository.ProductoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    // Inyección del repositorio
    private final ProductoRepository productoRepository;

    // Constructor para recibir el repositorio
    public ProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // Endpoint para listar todos los productos
    @GetMapping
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    // Endpoint para buscar un producto por su id
    @GetMapping("/{id}")
    public Producto obtenerProductoPorId(@PathVariable Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    // Endpoint para guardar un nuevo producto
    @PostMapping
    public Producto guardarProducto(@RequestBody Producto producto) {
        return productoRepository.save(producto);
    }
}