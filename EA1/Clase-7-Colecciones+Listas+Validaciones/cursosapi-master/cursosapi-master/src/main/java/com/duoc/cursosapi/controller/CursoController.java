package com.duoc.cursosapi.controller;

import com.duoc.cursosapi.model.Curso;
import com.duoc.cursosapi.service.CursoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    //instancia del servio
    private final CursoService cursoService;

    //inyectar mediante el constructor
    public CursoController(CursoService cursoService){
        this.cursoService=cursoService;
    }

    //Obtener todos los cursos
    @GetMapping
    public List<Curso> listarCursos(){
        return cursoService.obtenerCursos();
    }
    //Obtener las modalidades
    @GetMapping("/modalidades")
    public String[] listarModalidades(){
        return cursoService.obtenerModalidades();
    }
    //Guardar
    //Debo poner @Valid para que haga la validacion
    @PostMapping
    public Curso agregarCurso(@Valid @RequestBody Curso curso){
        return cursoService.guardarCurso(curso);
    }
}
