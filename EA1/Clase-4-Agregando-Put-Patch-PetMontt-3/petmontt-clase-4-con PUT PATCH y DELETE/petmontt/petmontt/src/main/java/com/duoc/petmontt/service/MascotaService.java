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
    //Arrow function = Funcion anonima que se ejecuta de maneta automatica
    //Actualizar PUT
    public Optional<Mascota> actualizarCompleta(Long id, Mascota mascotaActualizada){
        return repo.findById(id).map(mascota ->{
            mascota.setNombre(mascotaActualizada.getNombre());
            mascota.setEspecie(mascotaActualizada.getEspecie());
            mascota.setEdad(mascotaActualizada.getEdad());
            //una ves tenemos los datos actualizados los guardamos
            return repo.save(mascota);
        });

    }

    //PATCH actualizar un dato especifico
    public Optional<Mascota> actualizarParcial(Long id, Mascota mascotaParcial){
        return repo.findById(id).map(mascota ->{

            // Si el nombre no es nulo, se actualiza Nombre
            if (mascotaParcial.getNombre() !=null){
                mascota.setNombre(mascotaParcial.getNombre());
            }

            //Especie
            if (mascotaParcial.getEspecie() !=null){
                mascota.setEspecie(mascotaParcial.getEspecie());
            }

            //Edad
            if (mascotaParcial.getEdad() !=null){
                mascota.setEdad(mascotaParcial.getEdad());
            }
            return repo.save(mascota);

        });
    }
    //ELiminar o DELETE
    public boolean eliminar(Long id){
        if (repo.existsById(id)){
            repo.deleteById(id);
            return true;
        }
        return false;
    }
}
