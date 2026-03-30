package com.duoc.productosapi.model;

// Importaciones de JPA
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

// @Entity le dice a Spring que esta clase será una tabla
@Entity
public class Producto {

    // @Id marca la clave primaria
    @Id
    private Integer id;

    // Nombre del producto
    private String nombre;

    // Precio del producto
    private Double precio;

    // Constructor vacío obligatorio para JPA
    public Producto() {
    }

    // Constructor con parámetros
    public Producto(Integer id, String nombre, Double precio) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
    }

    // Getter del id
    public Integer getId() {
        return id;
    }

    // Setter del id
    public void setId(Integer id) {
        this.id = id;
    }

    // Getter del nombre
    public String getNombre() {
        return nombre;
    }

    // Setter del nombre
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Getter del precio
    public Double getPrecio() {
        return precio;
    }

    // Setter del precio
    public void setPrecio(Double precio) {
        this.precio = precio;
    }
}