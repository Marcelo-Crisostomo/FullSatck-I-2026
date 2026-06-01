package com.example.swagger.service;

import com.example.swagger.model.Equipo;
import com.example.swagger.repository.EquipoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipoService {

    private final EquipoRepository repository;

    public EquipoService(EquipoRepository repository) {
        this.repository = repository;
    }

    public List<Equipo> listar() {
        return repository.obtenerTodos();
    }

    public void guardar(Equipo equipo) {
        repository.guardar(equipo);
    }
}
