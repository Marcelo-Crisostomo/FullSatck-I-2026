package com.duoc.petmontt.service;

import com.duoc.petmontt.model.Mascota;
import com.duoc.petmontt.repository.MascotaRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MascotaService {
    //instancia del repositorio
    public final MascotaRepository repo;

    //Constructor para poder inyectar el repo
    public MascotaService(MascotaRepository repo){
        this.repo = repo;


    }

    //Obtener todas las mascotas
    public List<Mascota> obtenerTodas(){
        return repo.findAll();
    }

    //Buscar una mascota por su ID
    public Optional<Mascota> obtenerPorId(Long id){
        return repo.findById(id);
    }

    //Guardar mascota
    public Mascota guardar( Mascota mascota){
        return repo.save(mascota);
    }

}
