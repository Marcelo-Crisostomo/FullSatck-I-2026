package com.example.ms_pedidos.client;


import com.example.ms_pedidos.dto.ProductoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Esta interfaz consume la API del microservicio ms-productos
@FeignClient(name = "productosClient", url = "http://localhost:8081")
public interface ProductoClient {

    // Llama al endpoint GET /api/productos/{id} del otro microservicio
    @GetMapping("/api/productos/{id}")
    ProductoDTO obtenerProductoPorId(@PathVariable Long id);
}
