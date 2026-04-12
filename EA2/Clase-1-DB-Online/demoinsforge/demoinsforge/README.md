

# 📦 Proyecto Spring Boot + PostgreSQL (InsForge)

## 📌 Descripción

Este proyecto es una aplicación backend desarrollada con **Spring Boot**, que implementa una arquitectura en capas (`controller`, `service`, `repository`, `model`) utilizando **ORM (JPA/Hibernate)** para conectarse a una base de datos **PostgreSQL alojada en InsForge**.

El objetivo es demostrar cómo construir una API REST profesional, validada y documentada, siguiendo buenas prácticas de desarrollo backend.

---

## 🧱 Arquitectura del proyecto

El proyecto está estructurado de la siguiente manera:

```text
src/main/java/com/ejemplo/demoinsforge
│
├── controller     → Expone los endpoints REST
├── service        → Contiene la lógica de negocio
├── repository     → Acceso a datos (Spring Data JPA)
├── model          → Entidades (mappeo ORM)
└── DemoinsforgeApplication.java
```

---

## 🚀 Tecnologías utilizadas

* Java 17
* Spring Boot
* Spring Web
* Spring Data JPA (Hibernate)
* PostgreSQL (InsForge)
* Lombok
* Spring Validation
* Swagger (OpenAPI)
* DevTools

---

## 🗄️ Base de datos

El proyecto utiliza **PostgreSQL provisto por InsForge**.

### Tabla utilizada

```sql
CREATE TABLE productos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    precio NUMERIC(10,2) NOT NULL,
    stock INTEGER NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT true
);
```

---

## ⚙️ Configuración

Archivo: `application.properties`

```properties
spring.application.name=demoinsforge
server.port=8080

# Conexion PostgreSQL (InsForge)
spring.datasource.url=jdbc:postgresql://HOST:5432/DB?sslmode=require
spring.datasource.username=USUARIO
spring.datasource.password=PASSWORD
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# Pool de conexiones
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.connection-timeout=20000

# Actuator
management.endpoints.web.exposure.include=*
```

---

## ▶️ Ejecución del proyecto

### 1. Clonar repositorio

```bash
git clone https://github.com/tu-repo/demoinsforge.git
cd demoinsforge
```

### 2. Configurar credenciales

Editar `application.properties` con los datos de InsForge.

### 3. Ejecutar

```bash
./mvnw spring-boot:run
```

---

## 📡 Endpoints disponibles

Base URL:

```text
http://localhost:8080/api/productos
```

### 🔹 Listar todos

```http
GET /
```

### 🔹 Listar activos

```http
GET /activos
```

### 🔹 Buscar por nombre

```http
GET /buscar?nombre=texto
```

### 🔹 Crear producto

```http
POST /
Content-Type: application/json
```

```json
{
  "nombre": "Mouse Gamer",
  "precio": 19990,
  "stock": 10,
  "activo": true
}
```

---

## 📘 Documentación Swagger

Disponible en:

```text
http://localhost:8080/swagger-ui.html
```

Permite probar los endpoints directamente desde el navegador.

---

## 📊 Actuator (Monitoreo)

Endpoints disponibles:

```text
http://localhost:8080/actuator/health
http://localhost:8080/actuator/info
```

---

## ✅ Validaciones

El proyecto utiliza **Spring Validation** para validar los datos de entrada.

Ejemplo:

```java
@NotBlank
@Size(min = 3, max = 100)
private String nombre;
```

---

## 🧠 Conceptos clave aplicados

* Arquitectura en capas
* ORM con JPA/Hibernate
* Conexión segura a PostgreSQL (SSL)
* Validación de datos en backend
* Documentación automática con Swagger
* Separación de responsabilidades

---

## ⚠️ Consideraciones importantes

* InsForge utiliza PostgreSQL como motor principal.
* La conexión se realiza directamente a la base de datos (no vía PostgREST).
* Se recomienda mantener `ddl-auto=none` en entornos productivos.

---

## 🧪 Pruebas recomendadas

* Usar Postman o Swagger
* Probar inserciones inválidas (validaciones)
* Ver logs SQL en consola (`show-sql=true`)

---

## 📌 Mejoras futuras

* Implementar DTOs
* Manejo global de errores (`@RestControllerAdvice`)
* Seguridad con Spring Security
* Testing con JUnit y Mockito
* Dockerización del proyecto

---

## 👨‍🏫 Uso académico

Este proyecto está diseñado como base para:

* clases de Spring Boot
* introducción a microservicios
* evaluaciones de backend
* integración con frontend

---
