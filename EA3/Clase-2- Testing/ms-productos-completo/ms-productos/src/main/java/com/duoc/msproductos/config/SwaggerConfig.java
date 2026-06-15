package com.duoc.msproductos.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Swagger / OpenAPI 3.0.
 *
 * Genera documentación automática de la API REST accesible en:
 * → http://localhost:8081/swagger-ui.html
 * → http://localhost:8081/api-docs (JSON)
 *
 * Incluye configuración de seguridad para poder probar
 * endpoints protegidos directamente desde Swagger UI.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "basicAuth";

        return new OpenAPI()
            .info(new Info()
                .title("MS Productos API")
                .description("""
                    ## Microservicio de Gestión de Productos
                    
                    API REST para la gestión completa del catálogo de productos.
                    
                    ### Endpoints disponibles:
                    - **GET** `/api/v1/productos` - Listar todos los productos (público)
                    - **GET** `/api/v1/productos/{id}` - Obtener producto por ID (público)
                    - **POST** `/api/v1/productos` - Crear producto (requiere ADMIN)
                    - **PUT** `/api/v1/productos/{id}` - Actualizar producto (requiere ADMIN)
                    - **DELETE** `/api/v1/productos/{id}` - Eliminar producto (requiere ADMIN)
                    
                    ### Credenciales para testing:
                    - **Admin**: admin / admin123
                    - **User**: user / user123
                    
                    ### Proyecto: DSY1106 - Desarrollo Full Stack III
                    """)
                .version("1.0.0")
                .contact(new Contact()
                    .name("Equipo DSY1106")
                    .email("equipo@duoc.cl"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://springdoc.org")))
            // Configurar autenticación Basic para Swagger UI
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
            .components(new Components()
                .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                    .name(securitySchemeName)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("basic")
                    .description("Usar credenciales: admin/admin123 para escritura")));
    }
}
