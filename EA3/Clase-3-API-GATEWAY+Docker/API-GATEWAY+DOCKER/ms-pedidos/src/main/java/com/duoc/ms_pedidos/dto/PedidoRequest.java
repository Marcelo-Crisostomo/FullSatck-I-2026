package com.duoc.ms_pedidos.dto;

public class PedidoRequest {

    private final Long productoId;
    private final int cantidad;

    public PedidoRequest(Long productoId, int cantidad) {
        this.productoId = productoId;
        this.cantidad = cantidad;
    }

    public Long getProductoId() {
        return productoId;
    }

    public int getCantidad() {
        return cantidad;
    }
}
