package com.duoc.cursosapi.service;
import com.duoc.cursosapi.model.Curso;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CursoService {
    //lista que va a ser bd
    private List<Curso> listarCursos = new ArrayList<>();

    //Crear un arreglo
    private String[] modalidades = {"Presencial", "Online", "Hibrido"};

    public CursoService(){
        //Cargar algunos datos
        listarCursos.add(new Curso(1, "FullStack I", "Marcelo Crisostomo", 100));
        listarCursos.add(new Curso(2, "Base de Datos", "German Barrientos", 120));
        listarCursos.add(new Curso(3, "IA", "Gio", 150));
    }
    //GET
    public List<Curso> obtenerCursos(){
        return listarCursos;
    }
    //Obtener modalidades
    public String[] obtenerModalidades(){
        return modalidades;
    }

    //Post Guardar
    public Curso guardarCurso(Curso curso){
        listarCursos.add(curso);
        return curso;
    }

}
