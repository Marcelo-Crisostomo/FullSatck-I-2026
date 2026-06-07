# 🎓 Repositorio Oficial - FullStack I (2026)

Bienvenidos al repositorio oficial de la asignatura **FullStack I** impartida en **Duoc UC**, sede **Puerto Montt**. Este espacio está destinado a almacenar todo el material, documentación, ejercicios y proyectos correspondientes a los estudiantes de **3er semestre** durante la versión académica del año **2026**.

---

## 👨‍🏫 Información del Curso

- **Institución:** Duoc UC
- **Sede:** Puerto Montt
- **Asignatura:** FullStack I (Código DSY1103)
- **Semestre del plan de estudios:** 3er Semestre
- **Año Académico:** 2026
- **Docente a cargo:** Marcelo Crisóstomo Carrasco

---

## 🎯 Descripción de la Asignatura

La asignatura de **FullStack I** tiene como objetivo introducir a los estudiantes en el desarrollo de aplicaciones del lado del servidor (**Backend**) mediante la construcción de **APIs REST** y **microservicios** con **Spring Boot**. A lo largo del curso, los alumnos aprenderán a diseñar y exponer servicios web, gestionar la persistencia de datos, validar información, manejar errores de forma profesional y asegurar la comunicación entre distintos componentes de un sistema distribuido.

Este curso sienta las bases necesarias para la construcción de sistemas completos, enfocándose en buenas prácticas de desarrollo, arquitectura de microservicios, documentación de APIs y el uso de herramientas estándares de la industria tecnológica actual.

---

## 💻 Tecnologías y Herramientas a Utilizar

Durante el semestre, estaremos trabajando principalmente con las siguientes tecnologías:

- **Java:** Lenguaje de programación base para el desarrollo del Backend.
- **Spring Boot:** Framework principal para la creación de APIs REST y microservicios.
- **Spring Web (REST):** Para exponer endpoints HTTP (`GET`, `POST`, `PUT`, `PATCH`, `DELETE`) y manejar peticiones/respuestas con `ResponseEntity`.
- **Spring Data JPA:** Para la persistencia y el mapeo objeto-relacional con bases de datos.
- **Bean Validation:** Para la validación de datos de entrada en los servicios.
- **Spring Security:** Para la autenticación y autorización de las APIs.
- **WebClient:** Para la comunicación entre microservicios.
- **Migraciones de Base de Datos:** Versionamiento del esquema de datos.
- **Logging (SLF4J):** Para el registro y monitoreo de la aplicación.
- **Swagger / OpenAPI:** Para la documentación interactiva de las APIs.
- **Maven:** Como gestor de dependencias y construcción del proyecto.
- **Postman:** Para probar y validar los endpoints de las APIs.
- **Git y Control de Versiones:** Uso de repositorios para mantener el historial del código, facilitando el trabajo colaborativo.

---

## 📂 Estructura del Repositorio

El repositorio está organizado en distintas carpetas que reflejan el avance del semestre y las Experiencias de Aprendizaje (EA):

* 📁 **`Info/`**: Contiene la información administrativa del curso (plan de asignatura / syllabus `PA124_DSY1103`).
* 📁 **`EA1/`**: Experiencia de Aprendizaje 1 — **Fundamentos de microservicios y APIs REST con Spring Boot**.
  - Introducción a microservicios (proyecto *PetMontt*).
  - Operaciones CRUD y métodos HTTP `PUT`, `PATCH` y `DELETE`.
  - Pruebas de endpoints con Postman.
  - Colecciones, listas y validaciones.
  - Uso de `ResponseEntity` y manejo de errores siguiendo buenas prácticas.
  - Actividad práctica: Proyecto Biblioteca en Spring Boot.
* 📁 **`EA2/`**: Experiencia de Aprendizaje 2 — **Persistencia, comunicación entre microservicios y seguridad**.
  - Conexión a bases de datos online.
  - Migraciones y versionamiento de base de datos.
  - Uso de DTOs.
  - Logs con SLF4J.
  - Comunicación entre microservicios con WebClient (*ms-productos* / *ms-pedidos*).
  - Spring Security.
* 📁 **`EA3/`**: Experiencia de Aprendizaje 3 — **Documentación de APIs**.
  - Documentación interactiva con Swagger / OpenAPI.

---

## 🚀 Cómo Empezar

1. **Clonar el repositorio:** Para obtener una copia local de este proyecto, ejecuta el siguiente comando en tu terminal:
   ```bash
   git clone https://github.com/Marcelo-Crisostomo/FullSatck-I-2026.git
   ```
2. **Abrir el proyecto:** Se recomienda utilizar **IntelliJ IDEA** (o el editor de tu preferencia) para abrir la carpeta del proyecto correspondiente a cada clase o Experiencia de Aprendizaje.
3. **Ejecutar un microservicio:** Sitúate dentro de la carpeta del proyecto Spring Boot y ejecuta:
   ```bash
   ./mvnw spring-boot:run
   ```
   En Windows puedes usar `mvnw.cmd spring-boot:run`.
4. **Probar la API:** Utiliza **Postman** o la interfaz de **Swagger UI** (cuando esté disponible) para consumir y validar los endpoints.

---

## 🤝 Políticas de Uso y Contribución

Este repositorio es de carácter estrictamente **académico y estudiantil**. Los códigos y proyectos aquí alojados son ejemplos guiados y desarrollos realizados como parte del proceso de aprendizaje.

Se espera que los estudiantes utilicen este material como referencia y apoyo, fomentando el aprendizaje continuo y la honestidad en el desarrollo de sus propias evaluaciones.

---
*Desarrollado con dedicación para los futuros profesionales del área de Informática de Duoc UC. ¡Mucho éxito en el semestre!*
