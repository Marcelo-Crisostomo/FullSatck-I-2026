package com.duoc.petmontt.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
//Es la clase que será una tabla en la bd
@Entity
//Código repetitivo con Lombok
@Data
//Constructor vacio
@NoArgsConstructor
//Constructor con argumentos
@AllArgsConstructor
public class Mascota {
    //PK de mi tabla
    @Id
    //id autoincremental
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String especie;
    private Integer edad;
}
