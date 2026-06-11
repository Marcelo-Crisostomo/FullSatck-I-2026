package com.duoc.ms_pedidos.dto;

public class PedidoResponse {

    private final Long id;
    private final Long productoId;
    private final int cantidad;
    private final Producto producto;

    public PedidoResponse(Long id, Long productoId, int cantidad, Producto producto) {
        this.id = id;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.producto = producto;
    }

    public Long getId() {
        return id;
    }

    public Long getProductoId() {
        return productoId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public Producto getProducto() {
        return producto;
    }
}
