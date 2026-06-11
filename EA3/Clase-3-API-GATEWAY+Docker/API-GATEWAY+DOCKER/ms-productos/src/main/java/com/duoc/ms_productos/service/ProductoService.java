package com.duoc.ms_productos.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.duoc.ms_productos.model.Producto;
import com.duoc.ms_productos.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    public List<Producto> listar() {
        return repository.findAll();
    }

    public Optional<Producto> buscarPorId(Long id) {
        return repository.findById(id);
    }
}
