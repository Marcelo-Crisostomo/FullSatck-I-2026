package com.duoc.petmontt.controller;
import com.duoc.petmontt.model.Mascota;
import com.duoc.petmontt.service.MascotaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//Rest comunicar mediante protocolo http
@RestController
@RequestMapping("/api/1/mascotas")
public class MascotaController {

    //Inyectar el servicio Para consumirlo
    private final MascotaService mascotaService;
    //Constructor
    public MascotaController(MascotaService mascotaService){
        this.mascotaService = mascotaService;
    }

    //Get obtener todas las mascotas
    @GetMapping
    public List<Mascota> listarMascotas(){
        return mascotaService.obtenerTodas();
    }
    //Obtener mascota por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Mascota> buscarPorId(@PathVariable Long id){
        return mascotaService.obtenerPorId(id)
                .map(ResponseEntity::ok)  //devolver un estado 200 respuesta exitosa
                .orElse(ResponseEntity.notFound().build()); //Si no está responde 400 no existe
    }
    //Post Guardar mascotas
    @PostMapping
    public Mascota guardarMascota(@RequestBody Mascota mascota){
        return mascotaService.guardar(mascota);
    }

    //PUT actualizar all
    //http://localhost/api/mascotas/3
    @PutMapping("/{id}")
    public ResponseEntity<Mascota> actualizarCompleta(@PathVariable Long id, @RequestBody Mascota mascota){
        return mascotaService.actualizarCompleta(id, mascota)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    //PATCH actualizar un dato especifico
    @PatchMapping("/{id}")
    public ResponseEntity<Mascota> actualizarParcial(@PathVariable Long id, @RequestBody Mascota mascota){
        return mascotaService.actualizarParcial(id, mascota)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMascota(@PathVariable Long id){
        boolean eliminada = mascotaService.eliminar(id);

        if (eliminada){
            return ResponseEntity.noContent().build();
        }

        //si la mascota no existe
        return ResponseEntity.notFound().build();
    }


}
