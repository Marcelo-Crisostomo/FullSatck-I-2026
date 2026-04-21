package com.example.ms_pedidos.controller;

import com.example.ms_pedidos.service.PedidoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/crear/{idProducto}")
    public String crearPedido(@PathVariable Long idProducto,
                              @RequestParam String cliente) {

        return pedidoService.crearPedido(idProducto, cliente);
    }
}