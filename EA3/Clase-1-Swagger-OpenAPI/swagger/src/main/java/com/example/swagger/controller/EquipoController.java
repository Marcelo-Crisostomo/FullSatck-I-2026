package com.example.swagger.controller;

import com.example.swagger.model.Equipo;
import com.example.swagger.service.EquipoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipos")
//Manera general en controlador
@Tag(
        name = "Equipos",
        description = "Operaciones relacionadas con el inventario tecnológico"
)
public class EquipoController {

    private final EquipoService service;

    public EquipoController(EquipoService service) {
        this.service = service;
    }

    @Operation(
            summary = "Obtiene todos los equipos del inventario",
            description = "Retorna la lista completa de equipos registrados"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Consulta exitosa"),
            @ApiResponse(responseCode = "500",
                    description = "Error interno"
            )
    })
    @GetMapping
    public List<Equipo> listar() {
        return service.listar();
    }


    @Operation(
            summary = "Registro de un equipo",
            description = "Permite agregar un nuevo activo al inventario"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "Equipo creado"),
            @ApiResponse(responseCode = "400",
                    description = "Datos inválidos"
            )
    })

    @PostMapping
    public void guardar(@RequestBody Equipo equipo) {
        service.guardar(equipo);
    }
}
