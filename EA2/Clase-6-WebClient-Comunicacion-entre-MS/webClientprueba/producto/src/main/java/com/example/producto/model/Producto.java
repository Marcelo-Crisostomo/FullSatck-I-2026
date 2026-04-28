package com.example.producto.model;
import jakarta.persistence.*;

@Entity
public class Producto {
    @Id @GeneratedValue
    private Long id;
    private String nombre;
    private double precio;

    public Producto(){}
    public Producto(String n,double p){this.nombre=n;this.precio=p;}

    public Long getId(){return id;}
    public String getNombre(){return nombre;}
    public double getPrecio(){return precio;}
}
