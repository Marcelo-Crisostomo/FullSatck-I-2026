package com.example.security.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    //Configuracion de seguridad principal

    @Bean
    //SecurityFilterChain= define que se va a proteger y como se va a proteger
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        //lambda = anonima y que se ejecuta automaticamente
        http
                .csrf(csrf -> csrf.disable()) //Desactivar csrf = no vamos a usar formularios

                .authorizeHttpRequests(auth -> auth
                        //Endpoint: público que permite cualquier acceso
                        .requestMatchers("/api/publico").permitAll()

                        //Endpoint protegido = solo para admin
                        .requestMatchers("/api/admin").hasRole("ADMIN")

                        //Cualquier tipo de endpoint requiere autenticacion
                        .anyRequest().authenticated()

                )
                //Activar la autenticacion basica
                //Forma simple de aplicar pruebas test
                .httpBasic(Customizer.withDefaults());
                    //Construir y retornar la peticion de configuracion de seguridad
                    return http.build();
    }
                    //Bean que defina los usuarios del sistema
                    @Bean
                    public UserDetailsService users(){

                        //Usuario normal
                        UserDetails user = User.withUsername("user")//nombre del usuario
                                .password("{noop}1234")//{noop}=no encriptar
                                .roles("USER")//Rol asignado
                                .build();//Construir el objeto = usuario

                        //Usuario administrador
                        UserDetails admin = User.withUsername("admin")//nombre del usuario
                                .password("{noop}1234")//{noop}=no encriptar
                                .roles("ADMIN")//Rol asignado
                                .build();//Construir el objeto = usuario

                        //Almacenar estos usuarios en memoria
                        return new InMemoryUserDetailsManager(user,admin);






    }

}
