package com.example.pedidos.service;

import com.example.pedidos.dto.ProductoDTO;
import com.example.pedidos.model.Pedido;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class PedidoService {

    private final WebClient client = WebClient.create("http://localhost:8081");

    public Pedido crear(Long id){
        ProductoDTO p = client.get()
                .uri("/productos/"+id)
                .retrieve()
                .bodyToMono(ProductoDTO.class)
                .block(); // simple para clase

        return new Pedido(p.nombre,p.precio);
    }
}
