package com.example.ms_pedidos.service;

import com.example.ms_pedidos.client.ProductoClient;
import com.example.ms_pedidos.dto.ProductoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PedidoService {

    // Logger profesional
    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);

    private final ProductoClient productoClient;

    public PedidoService(ProductoClient productoClient) {
        this.productoClient = productoClient;
    }

    public String crearPedido(Long idProducto, String cliente) {

        // Log de inicio de proceso
        log.info("Intentando crear pedido para cliente: {}", cliente);

        // Log antes de consumir otro microservicio
        log.info("Consultando producto con id: {}", idProducto);

        ProductoDTO producto = productoClient.obtenerProductoPorId(idProducto);

        // Validación con log de error
        if (producto == null) {
            log.error("Producto no encontrado con id: {}", idProducto);
            return "Error: producto no existe";
        }

        // Log de éxito
        log.info("Producto encontrado: {} - ${}", producto.getNombre(), producto.getPrecio());

        return "Pedido creado para cliente " + cliente +
                " | Producto: " + producto.getNombre();
    }
}