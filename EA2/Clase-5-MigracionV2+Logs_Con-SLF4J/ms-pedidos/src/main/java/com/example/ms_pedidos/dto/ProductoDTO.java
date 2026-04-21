package com.example.ms_pedidos.dto;
//DTO solo contiene los datos que el ms de pedidos necesita para trabajar
public class ProductoDTO {
    private Long id;
    private String nombre;
    private Double precio;

    public ProductoDTO() {}

    public ProductoDTO(Long id, String nombre, Double precio) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }
}
