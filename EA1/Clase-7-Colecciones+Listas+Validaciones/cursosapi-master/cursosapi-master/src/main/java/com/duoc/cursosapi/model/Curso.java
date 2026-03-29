package com.duoc.cursosapi.model;

//Arreglo: Almacenar varios elementos y tiene tamaño fijo. Inmutable
//String[] nombre_arreglo = {"cadena1","cadena2"};

//Coleccion: Permite guardar varios elementos y puede crecer dinamicamente.
//List = son recomendables para endpoints
//List<TipodeDato String> nombre_lista = new ArrayList<>(); //esto es una lista vacia

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class Curso {
    //id del curso
    //Integer no va a se nullo
    //@NotNull= no va  permitir =null
    @NotNull(message = "El id no puede venir nulo")
    private Integer id;

    //Validación: @NotBlank = mensaje no  venga en blanco
    // null, = "" o "  "
    @NotBlank(message = "El nombre del curso no puede estar vacio")
    private String nombre;

    //Nombre docente
    @NotBlank(message="El docente no puede estar vacio")
    private String docente;

    //Cantidad de hora del curso
    @NotNull
    private Integer horas;

    //Constructor vacio
    public Curso(){

    }

    //Constructor con parametros
    public Curso(Integer id, String nombre, String docente, Integer horas){
        this.id=id;
        this.nombre= nombre;
        this.docente=docente;
        this.horas=horas;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDocente() {
        return docente;
    }

    public void setDocente(String docente) {
        this.docente = docente;
    }

    public Integer getHoras() {
        return horas;
    }

    public void setHoras(Integer horas) {
        this.horas = horas;
    }
}
