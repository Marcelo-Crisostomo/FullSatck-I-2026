package com.duoc.msproductos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Spring Security para el Microservicio de Productos.
 *
 * Política de seguridad:
 * - Endpoints públicos (GET): lectura libre
 * - Endpoints de escritura (POST/PUT/DELETE): requieren autenticación
 * - Swagger UI / H2 Console: acceso libre (desarrollo)
 * - Autenticación: HTTP Basic (para simplicidad en desarrollo)
 *   En producción se usaría JWT con OAuth2
 *
 * Roles:
 * - ROLE_USER: solo lectura
 * - ROLE_ADMIN: lectura y escritura
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configura las reglas de seguridad HTTP.
     * Orden importa: las reglas más específicas van primero.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitar CSRF (en microservicios REST con sesiones stateless no aplica)
            .csrf(AbstractHttpConfigurer::disable)

            // Política de sesión: STATELESS (cada request es independiente)
            // En microservicios se usa JWT, no sesiones de servidor
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Reglas de autorización
            .authorizeHttpRequests(auth -> auth
                // Swagger UI - acceso libre para desarrollo
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/api-docs/**",
                    "/v3/api-docs/**"
                ).permitAll()

                // H2 Console - solo en desarrollo
                .requestMatchers("/h2-console/**").permitAll()

                // Actuator health check - acceso libre
                .requestMatchers("/actuator/health").permitAll()

                // GET (lectura): público
                .requestMatchers(HttpMethod.GET, "/api/v1/productos/**").permitAll()

                // POST, PUT, DELETE: requieren autenticación y rol ADMIN
                .requestMatchers(HttpMethod.POST, "/api/v1/productos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/productos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/productos/**").hasRole("ADMIN")

                // Cualquier otra solicitud requiere autenticación
                .anyRequest().authenticated()
            )

            // Habilitar HTTP Basic Authentication
            .httpBasic(basic -> {})

            // Necesario para H2 Console (usa frames)
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    /**
     * Usuarios en memoria para desarrollo/testing.
     * En producción: usar base de datos o servicio de identidad.
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .roles("ADMIN", "USER")
                .build();

        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("user123"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }

    /**
     * Encoder de contraseñas BCrypt (estándar de la industria).
     * BCrypt aplica un salt aleatorio y múltiples rondas de hash.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
