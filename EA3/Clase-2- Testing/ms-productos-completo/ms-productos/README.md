# 🚀 ms-productos — Microservicio de Productos
### DSY1106 - Desarrollo Full Stack III | Duoc UC

## Estructura del proyecto

```
ms-productos/
├── pom.xml                          ← Dependencias + JaCoCo config
├── GUIA_TESTING_COMPLETA.md         ← Guía completa de testing
├── src/
│   ├── main/java/com/duoc/msproductos/
│   │   ├── MsProductosApplication.java    ← Clase principal
│   │   ├── config/
│   │   │   ├── SecurityConfig.java        ← Spring Security
│   │   │   └── SwaggerConfig.java         ← OpenAPI / Swagger UI
│   │   ├── controller/
│   │   │   └── ProductoController.java    ← REST Controller
│   │   ├── dto/
│   │   │   └── ProductoDTO.java           ← Request/Response DTOs
│   │   ├── exception/
│   │   │   ├── ProductoNotFoundException.java
│   │   │   ├── ProductoDuplicadoException.java
│   │   │   └── GlobalExceptionHandler.java ← @RestControllerAdvice
│   │   ├── model/
│   │   │   └── Producto.java              ← Entidad JPA
│   │   ├── repository/
│   │   │   └── ProductoRepository.java    ← Spring Data JPA
│   │   └── service/
│   │       ├── ProductoService.java        ← Interfaz
│   │       └── impl/
│   │           └── ProductoServiceImpl.java ← Implementación
│   ├── main/resources/
│   │   ├── application.properties
│   │   └── application-test.properties
│   └── test/java/com/duoc/msproductos/
│       ├── service/ProductoServiceTest.java       ← JUnit + Mockito
│       ├── controller/ProductoControllerTest.java ← WebMvcTest
│       ├── repository/ProductoRepositoryTest.java ← DataJpaTest
│       ├── exception/GlobalExceptionHandlerTest.java
│       └── integration/ProductoIntegrationTest.java ← SpringBootTest
```

## Ejecutar

```bash
# Ejecutar la aplicación
mvn spring-boot:run

# Swagger UI
http://localhost:8081/swagger-ui.html

# Ejecutar tests + generar cobertura JaCoCo
mvn clean test

# Ver reporte de cobertura
open target/site/jacoco/index.html
```

## Credenciales
- **Admin**: admin / admin123 (GET + POST + PUT + DELETE)
- **User**: user / user123 (solo GET)
