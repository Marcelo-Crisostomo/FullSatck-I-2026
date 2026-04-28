package com.example.pedidos.model;

public class Pedido {
    private String producto;
    private double precio;

    public Pedido(String p,double pr){this.producto=p;this.precio=pr;}

    public String getProducto(){return producto;}
    public double getPrecio(){return precio;}
}
