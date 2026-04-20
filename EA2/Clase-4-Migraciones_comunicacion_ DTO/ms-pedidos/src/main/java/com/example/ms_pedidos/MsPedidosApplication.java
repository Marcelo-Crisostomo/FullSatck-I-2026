package com.example.ms_pedidos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsPedidosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsPedidosApplication.class, args);
	}

}
