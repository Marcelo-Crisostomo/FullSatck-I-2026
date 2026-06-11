package com.duoc.ms_pedidos.model;

public class Pedido {

    private final Long id;
    private final Long productoId;
    private final int cantidad;

    public Pedido(Long id, Long productoId, int cantidad) {
        this.id = id;
        this.productoId = productoId;
        this.cantidad = cantidad;
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
}
