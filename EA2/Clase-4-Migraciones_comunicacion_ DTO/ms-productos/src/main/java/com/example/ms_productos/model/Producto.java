package com.example.ms_productos.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// Esta clase representa la entidad Producto en Java
@Entity
public class Producto {

    // Clave primaria del producto
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre del producto
    private String nombre;

    // Precio del producto
    private Double precio;

    // Constructor vacío obligatorio para JPA
    public Producto() {
    }

    // Constructor con datos básicos
    public Producto(String nombre, Double precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }
}
