package com.example.swagger.repository;

import com.example.swagger.model.Equipo;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class EquipoRepository {

    private final List<Equipo> equipos = new ArrayList<>();

    public List<Equipo> obtenerTodos() {
        return equipos;
    }

    public void guardar(Equipo equipo) {
        equipos.add(equipo);
    }
}