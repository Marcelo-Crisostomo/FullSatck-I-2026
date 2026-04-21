package com.example.ms_pedidos.client;


import com.example.ms_pedidos.dto.ProductoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//Consume la API del microservicio de productos
@FeignClient(name="productosClient", url ="http://localhost:8081" )

public interface ProductoClient {

    //Llamar al endpoint específico GET /api/productos/{id} del otro ms
    @GetMapping("/api/productos/{id}")
    //DTO      Metodo del otro MS
    ProductoDTO obtenerProductoPorId(@PathVariable Long id);
}
