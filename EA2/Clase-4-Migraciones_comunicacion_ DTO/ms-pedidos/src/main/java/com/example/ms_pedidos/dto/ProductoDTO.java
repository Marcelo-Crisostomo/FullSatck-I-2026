package com.example.ms_pedidos.dto;



// Este DTO solo contiene los datos que el microservicio
// de pedidos necesita del microservicio de productos
public class ProductoDTO {

    private Long id;
    private String nombre;
    private Double precio;

    public ProductoDTO() {
    }

    public ProductoDTO(Long id, String nombre, Double precio) {
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
