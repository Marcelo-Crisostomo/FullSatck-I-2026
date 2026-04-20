package com.example.ms_pedidos.controller;


import com.example.ms_pedidos.client.ProductoClient;
import com.example.ms_pedidos.dto.ProductoDTO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    // Inyectamos el cliente Feign
    private final ProductoClient productoClient;

    public PedidoController(ProductoClient productoClient) {
        this.productoClient = productoClient;
    }

    // Este endpoint simula la creación de un pedido
    // consultando antes la información del producto
    @GetMapping("/crear/{idProducto}")
    public String crearPedido(@PathVariable Long idProducto) {

        // Consultamos el microservicio de productos
        ProductoDTO producto = productoClient.obtenerProductoPorId(idProducto);

        // Si no existe el producto, devolvemos mensaje simple
        if (producto == null) {
            return "No se pudo crear el pedido porque el producto no existe.";
        }

        // Construimos una respuesta sencilla para la clase
        return "Pedido creado correctamente. Producto: "
                + producto.getNombre()
                + " | Precio: $" + producto.getPrecio();
    }
}
