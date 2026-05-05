package com.example.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SecurityApplication {

	//Autenticación= Quien eres?
	//Autorización= que puedes hacer
	//Proteccion de endpoints = datos sensibles
	//???
	//Evitar accesos no autorizados , Proteger la API
	//Controlar roles= Admin , user
	//API Rest
	//Microservicios
	//Sistema login
	//No ==datos sensibles expuestos publicamente

	public static void main(String[] args) {
		SpringApplication.run(SecurityApplication.class, args);
	}

}
