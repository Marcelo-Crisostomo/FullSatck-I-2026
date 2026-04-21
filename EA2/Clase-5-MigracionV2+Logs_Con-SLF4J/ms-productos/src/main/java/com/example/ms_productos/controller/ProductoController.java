package com.example.ms_productos.controller;


import com.example.ms_productos.model.Producto;
import com.example.ms_productos.repository.ProductoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    //Inyeccion del repo

    private final ProductoRepository productoRepository;

    //Constructor para usar el repo
    public ProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @GetMapping
    public List<Producto> listarProductos(){
        return productoRepository.findAll();
    }

    @GetMapping("/{id}")
    public Producto obtenerProductoPorId(@PathVariable Long id){
          return productoRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Producto guardarProducto(@RequestBody Producto producto){
        return productoRepository.save(producto);
    }

}
