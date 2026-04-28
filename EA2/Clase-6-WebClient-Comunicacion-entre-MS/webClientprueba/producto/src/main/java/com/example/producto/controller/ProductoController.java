package com.example.producto.controller;

import com.example.producto.model.Producto;
import com.example.producto.repository.ProductoRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoRepository repo;

    public ProductoController(ProductoRepository r){this.repo=r;}

    @PostMapping
    public Producto crear(){
        return repo.save(new Producto("Teclado",20000));
    }

    @GetMapping("/{id}")
    public Producto get(@PathVariable Long id){
        return repo.findById(id).orElse(null);
    }
}