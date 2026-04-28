package com.example.pedidos.controller;

import com.example.pedidos.model.Pedido;
import com.example.pedidos.service.PedidoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService s){this.service=s;}

    @GetMapping("/{id}")
    public Pedido get(@PathVariable Long id){
        return service.crear(id);
    }
}
