package com.duoc.ms_pedidos.dto;

public class Producto {

    private final Long id;
    private final String nombre;
    private final int precio;

    public Producto(Long id, String nombre, int precio) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPrecio() {
        return precio;
    }
}
