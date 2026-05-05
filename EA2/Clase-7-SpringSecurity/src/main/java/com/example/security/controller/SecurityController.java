package com.example.security.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SecurityController {

    //Endpoint publico = cualquier persona puede acceder
    @GetMapping("/publico")
    public String publico(){
        return " Endpoint público 🙂";
    }

    //Endpoint que requiere autenticacion
    @GetMapping("/privado")
    public String privado(){
        return "Endpoint Privado 🔐 ";
    }

    //Endpoint solo para ADMIN
    @GetMapping("/admin")
    public String admin(){
        return "Endpoint SOLO ADMIN 😎 ";
    }
}
