# Guía: Subir el Microservicio a Render (y por qué es tan fácil)

**Asignatura:** DSY1103 — Desarrollo FullStack
**Microservicio de ejemplo:** `producto-service` (Spring Boot + PostgreSQL)

Esta guía no solo te dice *cómo* desplegar, sino que te explica **por qué** este microservicio sube a Render sin dramas. La idea es que entiendas el truco y puedas aplicarlo para adaptar **tu propio microservicio ya creado**.

---

## 1. La idea en una frase

> En Render, en lugar de configurar la base de datos y el servidor a mano, dejamos un archivo (`render.yaml`) que **describe** toda la infraestructura. Render lo lee y la construye sola.

Eso es todo. El resto de la guía explica cada pieza de esa frase.

---

## 2. ¿Qué es un Blueprint en Render?

Un **Blueprint** es la forma que tiene Render de hacer *"Infraestructura como Código"* (Infrastructure as Code, IaC).

En vez de ir clic por clic creando la base de datos, después el servicio web, después copiar contraseñas de un lado a otro… escribes **un solo archivo** llamado `render.yaml` en la raíz del repositorio. Ese archivo es una **lista de instrucciones** que dice:

- "Quiero una base de datos PostgreSQL llamada así".
- "Quiero un servicio web que se construya con este Dockerfile".
- "Conéctalos entre sí con estas variables".

Cuando en Render eliges **New + → Blueprint** y conectas tu repositorio, Render:

1. Lee el `render.yaml`.
2. Te muestra todo lo que va a crear.
3. Lo crea de una sola vez al presionar **Apply**.

> **Analogía:** un Blueprint es como una receta de cocina. Tú no cocinas plato por plato dándole indicaciones al chef en tiempo real; le entregas la receta completa y él prepara todo en orden. `render.yaml` es esa receta.

---

## 3. ¿Por qué se crea sola la base de datos?

Porque está **declarada** dentro del `render.yaml`. Mira este fragmento del archivo del proyecto:

```yaml
databases:
  - name: bd-productos
    databaseName: productosdb
    user: productosuser
    plan: free
    region: oregon
```

Ese bloque le dice a Render: *"crea una base de datos PostgreSQL gratuita, con este nombre, esta base y este usuario"*. Por eso no tienes que crearla tú a mano: **ya está pedida en la receta**.

### Y lo más importante: la conexión también es automática

El servicio web necesita saber el host, el puerto, el usuario y la contraseña de la base. Lo bonito es que **no copias nada manualmente**. El `render.yaml` lo conecta solo:

```yaml
envVars:
  - key: DB_HOST
    fromDatabase:
      name: bd-productos
      property: host
  - key: DB_USER
    fromDatabase:
      name: bd-productos
      property: user
  - key: DB_PASSWORD
    fromDatabase:
      name: bd-productos
      property: password
```

`fromDatabase` significa: *"toma este dato directamente de la base de datos que acabas de crear"*. Render rellena las variables `DB_HOST`, `DB_USER`, `DB_PASSWORD`, etc., con los valores reales. Cero copiar-y-pegar, cero contraseñas mal escritas.

---

## 4. ¿Por qué este despliegue es tan fácil? (las 3 razones)

### Razón 1 — La aplicación NO tiene datos "quemados" en el código

El microservicio nunca dice "conéctate a `localhost:5432` con la contraseña `1234`". En su lugar, **lee variables de entorno**:

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:productosdb}}
    username: ${DB_USER:postgres}
    password: ${DB_PASSWORD:postgres}
```

La parte `${DB_USER:postgres}` se lee así: *"usa la variable `DB_USER`; si no existe, usa `postgres` por defecto"*. Resultado:

- En **tu computador**, sin definir nada, funciona con los valores por defecto.
- En **Render**, las variables las pone el Blueprint y la app las toma sin cambiar una sola línea de código.

> **Esta es la clave que deben copiar a su MS:** nunca poner usuario/clave/URL fijos en el código. Siempre variables de entorno con un valor por defecto para local.

### Razón 2 — El puerto también es una variable

Render decide en qué puerto corre tu app y te lo entrega en la variable `PORT`. Si tu app usa un puerto fijo, Render no la "ve" y el deploy falla. Por eso:

```yaml
server:
  port: ${PORT:8080}
```

*"Usa el puerto que me dé Render; en local, el 8080"*. Simple, pero es de los errores más comunes cuando no se considera.

### Razón 3 — El Dockerfile elimina las sorpresas de "en mi máquina sí funcionaba"

El proyecto incluye un `Dockerfile` que **construye y ejecuta** la app dentro de un contenedor con la versión correcta de Java y Maven:

```dockerfile
FROM maven:3.9.9-eclipse-temurin-21 AS build   # compila con Maven + Java 21
...
FROM eclipse-temurin:21-jre                     # ejecuta con Java liviano
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Render solo ve el Dockerfile y dice "ah, sé exactamente cómo construir esto". No importa qué versión de Java tenga el servidor: el contenedor trae la suya. **El mismo contenedor que se construye, es el que corre.** Eso elimina casi todos los errores de entorno.

---

## 5. ¿Qué tiene de especial este MS para subir sin problemas? (checklist para adaptar el tuyo)

Cuando adapten **su propio microservicio ya creado**, revisen que cumpla estos puntos. Son justo lo que hace que este suba sin pelear:

| # | Característica | Por qué importa en Render |
|---|---------------|---------------------------|
| 1 | **Conexión a BD por variables de entorno** (no datos fijos en el código) | Render inyecta las credenciales; sin esto, la app intenta conectarse a una base local que no existe en la nube |
| 2 | **Puerto leído desde `${PORT}`** | Render asigna el puerto; un puerto fijo hace fallar el deploy |
| 3 | **`ddl-auto: update`** (no `create-drop`) | Conserva los datos entre reinicios; `create-drop` los borraría cada vez |
| 4 | **PostgreSQL como base de producción** (no H2 en memoria) | H2 en memoria pierde todo al reiniciar; no sirve para demostrar persistencia |
| 5 | **`Dockerfile` que compila y ejecuta** | Render construye con la versión correcta de Java/Maven, sin sorpresas |
| 6 | **`render.yaml` con la base y las variables declaradas** | Permite el despliegue automático y autoconectado con un clic |
| 7 | **Sin seed que duplique datos en producción** (`sql.init.mode: never`) | Evita insertar los mismos datos de ejemplo en cada arranque |
| 8 | **Driver de PostgreSQL en el `pom.xml`** | Sin el driver, la app no sabe "hablar" con PostgreSQL |

> Si su MS marca estas 8 casillas, subirá a Render igual de fácil que este.

---

## 6. Pasos para desplegarlo (resumen práctico)

### Paso 1 — Subir el proyecto a GitHub
```bash
git add .
git commit -m "chore: configura despliegue en Render (Dockerfile + render.yaml)"
git push origin main
```

### Paso 2 — Crear todo con el Blueprint
1. En Render: **New +** → **Blueprint**.
2. Conecta tu cuenta de GitHub y elige el repositorio.
3. Render detecta `render.yaml` y muestra la base `bd-productos` y el servicio `producto-service`.
4. Presiona **Apply** y espera el build.

### Paso 3 — Probar
Render te da una URL (`https://producto-service-xxxx.onrender.com`):

- Swagger: `.../swagger-ui.html`
- Listar: `GET .../api/productos`
- Crear: `POST .../api/productos`

```json
{
  "nombre": "Notebook Lenovo",
  "descripcion": "Core i5, 8GB RAM",
  "precio": 599990,
  "stock": 10,
  "categoria": "Computadores"
}
```

### Paso 4 — Demostrar la persistencia
Crea un producto, reinicia el servicio desde Render y vuelve a hacer GET. **El producto sigue ahí** porque vive en PostgreSQL, no en memoria. Eso es exactamente lo que se evalúa.

---

## 7. Detalles a tener en cuenta

- **Plan Free:** el servicio "se duerme" tras un rato sin uso; la primera petición después puede tardar unos segundos en despertar. Es normal.
- **Región:** mantener la base de datos y el servicio web en la **misma región** (aquí, Oregon) hace la conexión más rápida.
- **Migrar tu MS:** si tu microservicio hoy usa H2, no lo borres. Déjalo como **perfil local** y agrega PostgreSQL como configuración por defecto. Así sigues desarrollando cómodo en tu PC y despliegas en la nube sin tocar el código.

---

> **Resumen:** Render despliega esto "solo" porque toda la infraestructura está **descrita en `render.yaml`** (Blueprint), la base de datos se **declara y se autoconecta** mediante `fromDatabase`, y la app está hecha para **leer su configuración del entorno** (variables y puerto) y correr dentro de un **contenedor Docker** reproducible. Repliquen esos tres pilares en su MS y tendrán el mismo despliegue de un clic.
