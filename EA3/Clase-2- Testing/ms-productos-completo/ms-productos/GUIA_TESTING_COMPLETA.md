# 📘 Guía Completa de Testing en Microservicios con Spring Boot
### DSY1106 - Desarrollo Full Stack III | Duoc UC

---

## 📋 Tabla de Contenidos

1. [¿Qué es el Testing?](#1-qué-es-el-testing)
2. [¿Por qué debemos testear?](#2-por-qué-debemos-testear)
3. [La Pirámide de Testing](#3-la-pirámide-de-testing)
4. [Herramientas utilizadas](#4-herramientas-utilizadas)
5. [Qué se debe testear en un Microservicio](#5-qué-se-debe-testear-en-un-microservicio)
6. [Tests Unitarios con JUnit 5 y Mockito](#6-tests-unitarios-con-junit-5-y-mockito)
7. [Tests de Integración](#7-tests-de-integración)
8. [Cobertura de Código con JaCoCo](#8-cobertura-de-código-con-jacoco)
9. [¿Por qué apuntar al 80% de cobertura?](#9-por-qué-apuntar-al-80-de-cobertura)
10. [Cómo implementar Testing en sus proyectos](#10-cómo-implementar-testing-en-sus-proyectos)
11. [Consideraciones importantes](#11-consideraciones-importantes)
12. [Resumen y checklist final](#12-resumen-y-checklist-final)

---

## 1. ¿Qué es el Testing?

El **testing** (o pruebas de software) es el proceso de verificar que el código hace exactamente lo que se espera que haga, bajo distintas condiciones.

En el contexto de microservicios, testeamos para responder preguntas como:

- ¿El servicio de productos retorna 404 cuando el ID no existe?
- ¿El validador rechaza un precio negativo?
- ¿El endpoint POST realmente crea el registro en la base de datos?
- ¿Un usuario sin rol ADMIN recibe un 403?

### Tipos de Tests

| Tipo | ¿Qué prueba? | Velocidad | Tecnología |
|------|-------------|-----------|------------|
| **Unitario** | Una clase o método en aislamiento | ⚡ Muy rápido | JUnit 5 + Mockito |
| **Integración** | Múltiples componentes juntos | 🐢 Más lento | @SpringBootTest |
| **End-to-End (E2E)** | El sistema completo con UI | 🐌 Lento | Selenium, Cypress |

---

## 2. ¿Por qué debemos testear?

### 2.1 Razones técnicas

**a) Detectar errores temprano (y baratos)**

Corregir un bug en desarrollo cuesta 10 veces menos que corregirlo en producción. Con tests, detectamos los errores en el momento exacto en que los cometemos, no días después.

```
Costo de corregir bugs según la etapa:
Desarrollo     → $1
QA/Testing     → $10
Producción     → $100+
```

**b) Refactorizar con confianza**

Sin tests, cambiar código es arriesgado: ¿cómo sabemos si rompimos algo? Con tests, ejecutamos `mvn test` y obtenemos feedback inmediato.

**c) Documentación viva**

Los tests describen el comportamiento esperado del sistema. Un nuevo desarrollador puede leer los tests y entender exactamente qué hace cada componente.

**d) Prevenir regresiones**

Una *regresión* es cuando un feature que funcionaba deja de funcionar tras un cambio. Los tests automáticos detectan regresiones instantáneamente.

### 2.2 Razones para este proyecto (DSY1106)

La **Evaluación Parcial N°3** exige:
- Informe de Pruebas Unitarias con cobertura de código
- Métricas generadas por herramientas de testing
- Ejemplos de pruebas y sus resultados

La **Evaluación Final** requiere explicar:
- Los resultados de las pruebas unitarias
- Cómo aseguraron la calidad, eficiencia y mantenibilidad

---

## 3. La Pirámide de Testing

La pirámide de testing es la distribución recomendada de tipos de tests:

```
          /\
         /  \
        / E2E \          ← 10% pocos tests, muy lentos
       /--------\
      /Integración\      ← 20% moderados
     /--------------\
    / Tests Unitarios \  ← 70% muchos tests, muy rápidos
   /____________________\
```

### ¿Por qué esta distribución?

**Tests Unitarios (70%):** Son rápidos (milisegundos cada uno), baratos de escribir y muy precisos para detectar exactamente dónde está el problema.

**Tests de Integración (20%):** Más lentos pero verifican que las piezas encajan correctamente. Se usan para flujos críticos.

**E2E (10%):** Los más costosos y lentos. Solo para los flujos de negocio más críticos.

### En nuestro Microservicio de Productos:

```
Unitarios    → ProductoServiceTest     (lógica de negocio)
             → ProductoControllerTest  (endpoints HTTP)
             → ProductoRepositoryTest  (consultas BD)
             → GlobalExceptionHandlerTest (manejo de errores)

Integración  → ProductoIntegrationTest (flujos completos)
```

---

## 4. Herramientas utilizadas

### 4.1 JUnit 5 (Jupiter)

**JUnit 5** es el framework estándar de testing en Java/Spring Boot.

```xml
<!-- Ya viene incluido en spring-boot-starter-test -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

**Anotaciones clave de JUnit 5:**

```java
@Test                    // Marca un método como test
@BeforeEach              // Se ejecuta ANTES de cada @Test
@AfterEach               // Se ejecuta DESPUÉS de cada @Test
@BeforeAll               // Se ejecuta una vez al inicio (static)
@AfterAll                // Se ejecuta una vez al final (static)
@DisplayName("texto")    // Nombre descriptivo para el test
@Nested                  // Agrupa tests relacionados
@ParameterizedTest       // Test con múltiples conjuntos de datos
@ValueSource(ints={1,2}) // Fuente de datos para @ParameterizedTest
@Disabled("razón")       // Deshabilita un test temporalmente
```

### 4.2 Mockito

**Mockito** permite crear "dobles de prueba" (mocks) de las dependencias, para probar clases en aislamiento.

```java
// Crear un mock
@Mock
ProductoRepository productoRepository;

// Configurar comportamiento
when(productoRepository.findById(1L))
    .thenReturn(Optional.of(producto));

// Verificar que se llamó
verify(productoRepository, times(1)).findById(1L);
verify(productoRepository, never()).delete(any());
```

**Tipos de respuestas con Mockito:**

```java
when(mock.metodo()).thenReturn(valor);        // Retorna un valor
when(mock.metodo()).thenThrow(new Excepcion()); // Lanza excepción
doNothing().when(mock).metodoVoid();           // Para métodos void
doThrow(exception).when(mock).metodoVoid();    // Excepción en void
```

**ArgumentMatchers:** Para cuando el valor exacto no importa:

```java
when(repo.save(any(Producto.class))).thenReturn(producto);
when(repo.findById(anyLong())).thenReturn(Optional.empty());
when(repo.existsByNombre(anyString())).thenReturn(false);
```

### 4.3 AssertJ

**AssertJ** provee aserciones fluidas más legibles que las de JUnit nativo:

```java
// JUnit básico (menos legible)
assertEquals("Laptop", producto.getNombre());
assertTrue(lista.size() > 0);

// AssertJ (más expresivo)
assertThat(producto.getNombre()).isEqualTo("Laptop");
assertThat(lista).isNotEmpty().hasSize(2);
assertThat(resultado).isInstanceOf(ProductoResponseDTO.class);

// Verificar excepciones
assertThatThrownBy(() -> service.obtenerPorId(99L))
    .isInstanceOf(ProductoNotFoundException.class)
    .hasMessageContaining("99");
```

### 4.4 MockMvc

**MockMvc** simula el servidor web para testear Controllers HTTP:

```java
mockMvc.perform(get("/api/v1/productos/1"))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.data.nombre").value("Laptop"))
    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
```

### 4.5 JaCoCo

**JaCoCo** (Java Code Coverage) mide qué porcentaje de nuestro código es ejecutado por los tests.

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
</plugin>
```

Reporte generado en: `target/site/jacoco/index.html`

---

## 5. Qué se debe testear en un Microservicio

### 5.1 La regla fundamental

> **Testear comportamiento, no implementación.**

Nos interesa verificar QUÉ hace el código, no CÓMO lo hace internamente.

```java
// ❌ MAL: testea implementación (muy frágil)
@Test
void verificarQueSeUsaFindByActivoTrue() {
    verify(repo).findByActivoTrue(); // Si cambia el método interno, el test falla
}

// ✅ BIEN: testea comportamiento
@Test
void obtenerTodos_debeRetornarSoloProductosActivos() {
    when(repo.findByActivoTrue()).thenReturn(List.of(productoActivo));
    List<ProductoResponseDTO> resultado = service.obtenerTodos();
    assertThat(resultado).allMatch(p -> p.getActivo() == true);
}
```

### 5.2 Qué testear en el SERVICE (lógica de negocio)

El Service es la capa MÁS IMPORTANTE para testear porque contiene las reglas de negocio.

**✅ Lo que SÍ testear:**

```java
// Happy path (flujo normal)
@Test void crear_cuandoDatosValidos_debeRetornarProductoCreado() { ... }

// Validaciones de negocio
@Test void crear_cuandoNombreDuplicado_debeLanzarProductoDuplicadoException() { ... }

// Casos borde (edge cases)
@Test void obtenerTodos_cuandoNoHayProductos_debeRetornarListaVacia() { ... }

// Manejo de excepciones
@Test void obtenerPorId_cuandoIdNoExiste_debeLanzarProductoNotFoundException() { ... }

// Transformaciones correctas (Entity → DTO)
@Test void crear_debeMapearCorrecamenteLosFields() { ... }

// Que NO se llama a métodos que no corresponden
@Test void crear_cuandoDuplicado_nuncaDebeGuardar() {
    verify(repo, never()).save(any()); // Nunca guardar si hay duplicado
}
```

### 5.3 Qué testear en el CONTROLLER

El Controller testea la capa HTTP: códigos de estado, formato JSON, seguridad.

**✅ Lo que SÍ testear:**

```java
// Códigos HTTP correctos
@Test void GET_productoExistente_debeRetornar200() { ... }
@Test void GET_productoInexistente_debeRetornar404() { ... }
@Test void POST_datos_invalidos_debeRetornar400() { ... }
@Test void POST_nombre_duplicado_debeRetornar409() { ... }

// Restricciones de seguridad
@Test void POST_sinAutenticacion_debeRetornar401() { ... }
@Test void POST_sinRolAdmin_debeRetornar403() { ... }
@Test void GET_sinAutenticacion_debeRetornar200() { ... } // Endpoint público

// Validaciones @Valid llegan correctamente
@Test void POST_camposVacios_debeRetornar400ConDetallesDeErrores() { ... }

// Formato de respuesta
@Test void respuesta_debeContenerCampoSuccess() { ... }
@Test void respuesta_debeContenerCampoData() { ... }
```

### 5.4 Qué testear en el REPOSITORY

El Repository testea que las consultas a la BD funcionan correctamente.

**✅ Lo que SÍ testear:**

```java
// Métodos derivados (findBy...)
@Test void findByActivoTrue_debeRetornarSoloActivos() { ... }
@Test void existsByNombre_cuandoExiste_debeRetornarTrue() { ... }

// Consultas @Query personalizadas
@Test void findProductosConStockBajo_debeIgnorarInactivos() { ... }

// Case-insensitivity
@Test void findByCategoria_debeSerCaseInsensitive() { ... }

// Búsqueda parcial
@Test void findByNombreContaining_debeEncontrarCoincidencias() { ... }
```

**❌ Lo que NO testear en Repository:**

```java
// NO testear los métodos heredados de JpaRepository
// Spring Data JPA ya los tiene testeados
@Test void save_debeGuardar() { ... }   // ❌ Innecesario
@Test void findAll_debeRetornarTodo() { ... } // ❌ Innecesario
```

### 5.5 Qué testear en el EXCEPTION HANDLER

```java
// Cada excepción → código HTTP correcto
@Test void productNotFoundException_debeRetornar404() { ... }
@Test void productoDuplicado_debeRetornar409() { ... }
@Test void validationError_debeRetornar400ConCamposDetallados() { ... }
@Test void accessDenied_debeRetornar403() { ... }

// Seguridad: no exponer detalles internos en errores 500
@Test void errorInterno_noDebeExponerStackTrace() { ... }
```

---

## 6. Tests Unitarios con JUnit 5 y Mockito

### 6.1 Anatomía de un test unitario

Todo test bien escrito sigue el patrón **AAA**:

```java
@Test
@DisplayName("✅ Crear producto cuando el nombre no existe")
void crear_cuandoNombreNoExiste_debeRetornarProductoCreado() {
    // ─────────────────────────────────────────────
    // ARRANGE: Preparar los datos y configurar mocks
    // ─────────────────────────────────────────────
    ProductoRequestDTO request = ProductoRequestDTO.builder()
            .nombre("Laptop Dell XPS")
            .precio(new BigDecimal("1299.99"))
            .stock(10)
            .categoria("Electrónica")
            .build();

    when(productoRepository.existsByNombreIgnoreCase("Laptop Dell XPS"))
            .thenReturn(false);  // El nombre NO existe

    when(productoRepository.save(any(Producto.class)))
            .thenReturn(productoEsperado);

    // ─────────────────────────────────────────────
    // ACT: Ejecutar el método bajo prueba
    // ─────────────────────────────────────────────
    ProductoResponseDTO resultado = productoService.crear(request);

    // ─────────────────────────────────────────────
    // ASSERT: Verificar el resultado
    // ─────────────────────────────────────────────
    assertThat(resultado).isNotNull();
    assertThat(resultado.getNombre()).isEqualTo("Laptop Dell XPS");
    assertThat(resultado.getActivo()).isTrue();
    verify(productoRepository, times(1)).save(any(Producto.class));
}
```

### 6.2 Configuración de la clase de test

```java
@ExtendWith(MockitoExtension.class)  // ← Habilita Mockito SIN Spring
@DisplayName("Tests Unitarios - ProductoService")
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;
    // Mockito crea una implementación "falsa" del Repository

    @InjectMocks
    private ProductoServiceImpl productoService;
    // Mockito instancia el Service e inyecta los @Mock automáticamente

    @BeforeEach
    void setUp() {
        // Ejecutado antes de CADA test → estado limpio garantizado
    }
}
```

### 6.3 Tests parametrizados (un test, múltiples datos)

```java
@ParameterizedTest
@ValueSource(ints = {0, -1, -100})  // Tres valores de stock inválidos
@DisplayName("❌ Crear con stock inválido debe fallar")
void crear_conStockInvalido_debeFallar(int stockInvalido) {
    // Este test se ejecuta 3 veces, una por cada valor
    ProductoRequestDTO request = ProductoRequestDTO.builder()
            .nombre("Test")
            .precio(BigDecimal.TEN)
            .stock(stockInvalido)  // ← cambia en cada ejecución
            .categoria("Cat")
            .build();

    // Verificar que la validación Bean Validation rechaza esto
    // (en test real se verifica a nivel de Controller con MockMvc)
}
```

### 6.4 Organizar tests con @Nested

```java
@DisplayName("Tests de ProductoService")
class ProductoServiceTest {

    @Nested
    @DisplayName("obtenerTodos()")
    class ObtenerTodosTests {
        @Test void debeRetornarProductosActivos() { ... }
        @Test void debeRetornarListaVaciaSiNoHay() { ... }
    }

    @Nested
    @DisplayName("crear()")
    class CrearTests {
        @Test void debeCrearCuandoNombreNoExiste() { ... }
        @Test void debeLanzarExcepcionCuandoNombreDuplicado() { ... }
    }
}
```

---

## 7. Tests de Integración

### 7.1 @WebMvcTest (solo capa web)

```java
@WebMvcTest(ProductoController.class)  // Solo carga el Controller y sus dependencias web
class ProductoControllerTest {

    @Autowired
    MockMvc mockMvc;  // Simula HTTP requests

    @MockBean          // Reemplaza el Service real con un mock
    ProductoService productoService;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void postConAdmin_debeRetornar201() throws Exception {
        when(productoService.crear(any())).thenReturn(productoResponse);

        mockMvc.perform(post("/api/v1/productos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.nombre").value("Laptop"));
    }
}
```

### 7.2 @DataJpaTest (solo capa de persistencia)

```java
@DataJpaTest           // Solo carga JPA + H2 en memoria
@ActiveProfiles("test")
class ProductoRepositoryTest {

    @Autowired
    ProductoRepository productoRepository;
    // El Repository REAL (no mock) contra H2 real

    @Test
    void findByActivoTrue_debeRetornarSoloActivos() {
        // Insertar datos directamente en H2
        productoRepository.save(Producto.builder()...activo(true).build());
        productoRepository.save(Producto.builder()...activo(false).build());

        List<Producto> activos = productoRepository.findByActivoTrue();

        assertThat(activos).hasSize(1);
    }
}
```

### 7.3 @SpringBootTest (contexto completo)

```java
@SpringBootTest        // Levanta TODA la aplicación Spring Boot
@AutoConfigureMockMvc  // Configura MockMvc con el contexto completo
@ActiveProfiles("test")
class ProductoIntegrationTest {

    // Prueba el flujo COMPLETO: HTTP → Controller → Service → Repository → H2
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void cicloCRUDCompleto() throws Exception {
        // Crear
        mockMvc.perform(post("/api/v1/productos")...)
               .andExpect(status().isCreated());

        // Verificar que persiste en BD
        assertThat(productoRepository.count()).isEqualTo(1);
    }
}
```

---

## 8. Cobertura de Código con JaCoCo

### 8.1 ¿Qué mide JaCoCo?

JaCoCo mide cuántas líneas/ramas de código son ejecutadas durante los tests.

| Métrica | Qué mide |
|---------|----------|
| **Line Coverage** | % de líneas de código ejecutadas |
| **Branch Coverage** | % de ramas (if/else) cubiertas |
| **Method Coverage** | % de métodos llamados |
| **Class Coverage** | % de clases instanciadas |

### 8.2 Configuración en pom.xml

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <!-- 1. Prepara el agente JaCoCo antes de los tests -->
        <execution>
            <id>prepare-agent</id>
            <goals><goal>prepare-agent</goal></goals>
        </execution>

        <!-- 2. Genera el reporte HTML después de los tests -->
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals><goal>report</goal></goals>
        </execution>

        <!-- 3. FALLA el build si no se alcanza el 80% -->
        <execution>
            <id>jacoco-check</id>
            <goals><goal>check</goal></goals>
            <configuration>
                <rules>
                    <rule>
                        <element>BUNDLE</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.80</minimum>  <!-- 80% líneas -->
                            </limit>
                            <limit>
                                <counter>BRANCH</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.75</minimum>  <!-- 75% ramas -->
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### 8.3 Ejecutar tests y generar reporte

```bash
# Ejecutar tests y generar reporte JaCoCo
mvn clean test

# El reporte se genera en:
# target/site/jacoco/index.html
# Abrirlo en el navegador para ver la cobertura visual
```

### 8.4 Interpretar el reporte

El reporte muestra el código coloreado:

```
🟢 Verde  → Línea cubierta por un test
🔴 Rojo   → Línea NO cubierta (falta test)
🟡 Amarillo → Rama parcialmente cubierta (ej: solo el if, no el else)
```

**Ejemplo de código sin cobertura:**

```java
public ProductoResponseDTO obtenerPorId(Long id) {
    return productoRepository.findById(id)        // 🟢 cubierto
            .orElseThrow(() ->                     // 🟢 cubierto
                new ProductoNotFoundException(     // 🔴 NO cubierto
                    "No encontrado: " + id));      // (falta test de este caso)
}
```

**Solución:** agregar el test del caso negativo.

---

## 9. ¿Por qué apuntar al 80% de cobertura?

### 9.1 El debate

El porcentaje de cobertura es una métrica que genera debate. Veamos por qué **80% es el estándar de la industria**:

**¿Por qué no el 100%?**

- El 100% es costoso de mantener y no siempre posible
- Algunos código es trivial (getters/setters de Lombok, main())
- Más tiempo en tests de cobertura = menos tiempo en lógica real
- Puede llevar a escribir tests malos solo para llegar al número

**¿Por qué no el 60%?**

- Con 60% hay un 40% de código sin testear → bugs potenciales
- Insuficiente para detectar regresiones
- No es aceptable en software de producción

**¿Por qué el 80%?**

```
✅ Cubre la mayoría de rutas críticas del código
✅ Balance costo-beneficio probado en la industria
✅ Estándar adoptado por Google, Amazon, Microsoft
✅ Suficiente para detectar regresiones
✅ Alcanzable sin sacrificar productividad
```

### 9.2 El 80% en la práctica

Con el microservicio de productos:

```
Capa          | Cobertura objetivo | Por qué
--------------|-------------------|--------------------------------
Service       | 90%+              | Contiene lógica de negocio crítica
Controller    | 85%+              | Endpoints públicos deben estar probados
Repository    | 75%+              | Solo los métodos custom (no los de JPA)
Exception     | 95%+              | Manejo de errores es crítico
Config/Main   | Excluir           | Trivial, no aporta valor testear
```

### 9.3 Excluir clases de la cobertura

No todo necesita ser contado. Podemos excluir clases que no aportan valor testear:

```xml
<configuration>
    <excludes>
        <!-- Excluir la clase main -->
        <exclude>com/duoc/msproductos/MsProductosApplication.class</exclude>
        <!-- Excluir DTOs (son solo datos) -->
        <exclude>com/duoc/msproductos/dto/**</exclude>
        <!-- Excluir configuración (testeada indirectamente) -->
        <exclude>com/duoc/msproductos/config/**</exclude>
    </excludes>
</configuration>
```

### 9.4 La cobertura no lo es todo

⚠️ **IMPORTANTE:** 80% de cobertura con tests malos es peor que 60% con tests buenos.

```java
// ❌ Test con 100% de cobertura pero INÚTIL
@Test
void testCrear() {
    service.crear(request);  // Ejecuta el código pero no verifica NADA
    // Sin assertions → el test siempre pasa, incluso con bugs
}

// ✅ Test con buenas assertions (esto es lo que realmente importa)
@Test
void crear_debeRetornarProductoConCamposCorrectos() {
    ProductoResponseDTO resultado = service.crear(request);
    assertThat(resultado.getNombre()).isEqualTo(request.getNombre());
    assertThat(resultado.getPrecio()).isEqualByComparingTo(request.getPrecio());
    assertThat(resultado.getActivo()).isTrue();
}
```

---

## 10. Cómo implementar Testing en sus proyectos

### 10.1 Paso a paso para agregar testing a un microservicio

**Paso 1: Verificar las dependencias en pom.xml**

```xml
<!-- spring-boot-starter-test incluye: JUnit 5, Mockito, AssertJ, MockMvc -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- Para testear endpoints protegidos con Spring Security -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- JaCoCo para cobertura -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
</plugin>
```

**Paso 2: Crear application-test.properties**

```properties
# src/main/resources/application-test.properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false
logging.level.root=WARN
```

**Paso 3: Crear la estructura de directorios de tests**

```
src/
└── test/
    └── java/
        └── com/tuempresa/msnombre/
            ├── service/
            │   └── MiServicioTest.java      ← Tests unitarios del service
            ├── controller/
            │   └── MiControllerTest.java    ← Tests del controller
            ├── repository/
            │   └── MiRepositoryTest.java    ← Tests del repository
            ├── exception/
            │   └── ExceptionHandlerTest.java ← Tests del handler
            └── integration/
                └── MiIntegrationTest.java   ← Tests de integración
```

**Paso 4: Empezar por el Service (TDD o test-después)**

```java
// Estrategia: para cada método del service, crear:
// 1. Un test del happy path (caso normal)
// 2. Un test de cada excepción posible
// 3. Un test de cada condición (if/else)

@ExtendWith(MockitoExtension.class)
class MiServicioTest {

    @Mock MiRepository repository;
    @InjectMocks MiServicioImpl servicio;

    @Test void metodo_casoNormal() { ... }
    @Test void metodo_cuandoNoExiste_debeLanzarExcepcion() { ... }
    @Test void metodo_cuandoDuplicado_debeLanzarExcepcion() { ... }
}
```

**Paso 5: Testear el Controller con MockMvc**

```java
@WebMvcTest(MiController.class)
class MiControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean MiServicio servicio;

    // Para cada endpoint:
    // 1. Test de éxito con usuario autenticado
    // 2. Test sin autenticación (si el endpoint lo requiere)
    // 3. Test con datos inválidos (400)
    // 4. Test cuando el service lanza excepción (404, 409, etc.)
}
```

**Paso 6: Ejecutar y revisar el reporte**

```bash
# Ejecutar todos los tests
mvn clean test

# Ver el reporte de cobertura
open target/site/jacoco/index.html
```

### 10.2 Estrategia por capas para el proyecto del semestre

```java
// Para cada microservicio de tu proyecto, seguir esta secuencia:

// 1. ✅ Tests del Service (prioridad máxima)
//    → Testear toda la lógica de negocio
//    → Mockear el Repository
//    → Objetivo: 90%+ cobertura de la capa Service

// 2. ✅ Tests del Controller (prioridad alta)
//    → Testear códigos HTTP
//    → Testear validaciones @Valid
//    → Testear restricciones de seguridad
//    → MockBean del Service

// 3. ✅ Tests del Repository (prioridad media)
//    → Solo para métodos @Query personalizados
//    → Usar @DataJpaTest con H2

// 4. ✅ Tests del Exception Handler (prioridad media)
//    → Un test por cada @ExceptionHandler
//    → Verificar código HTTP y formato

// 5. ✅ Tests de Integración (prioridad baja)
//    → 2-3 flujos críticos end-to-end
//    → Ciclo CRUD completo
//    → Seguridad end-to-end
```

---

## 11. Consideraciones importantes

### 11.1 ✅ Buenas prácticas

**Nombres descriptivos de tests:**
```java
// ❌ MAL
void test1() { ... }
void testCrear() { ... }

// ✅ BIEN
void crear_cuandoNombreYaExiste_debeLanzarProductoDuplicadoException() { ... }
void obtenerPorId_cuandoIdNoExiste_debeRetornar404() { ... }
```

**Un test verifica UNA sola cosa:**
```java
// ❌ MAL: verifica múltiples cosas (difícil saber qué falló)
@Test
void testCompleto() {
    var resultado = service.crear(request);
    assertThat(resultado.getNombre()).isEqualTo("Laptop");
    assertThat(resultado.getPrecio()).isEqualTo("1299.99");
    assertThat(resultado.getActivo()).isTrue();
    verify(repo).save(any());  // ← esto es diferente
    // Si falla, ¿qué falló?
}

// ✅ BIEN: cada aspecto en su propio test
@Test void crear_debeRetornarNombreCorrecto() { ... }
@Test void crear_debePersistirEnBD() { ... }
```

**Estado limpio entre tests:**
```java
@BeforeEach
void setUp() {
    // Siempre iniciar con datos frescos
    productoRepository.deleteAll(); // En tests de integración
    // O simplemente redefinir los objetos de prueba
}
```

**Tests independientes:**
```java
// ❌ MAL: el test 2 depende del test 1
@Test void test1_crearProducto() { ... }
@Test void test2_actualizarProducto() { ... /* necesita el producto del test 1 */ }

// ✅ BIEN: cada test crea sus propios datos
@Test void actualizar_cuandoExiste_debeActualizarCampos() {
    Producto p = crearProductoParaTest(); // Crea sus propios datos
    ...
}
```

### 11.2 ❌ Errores comunes

**1. Tests sin assertions:**
```java
@Test
void crearProducto() {
    service.crear(request); // Ejecuta pero no verifica nada
    // Este test SIEMPRE pasa, incluso si el método lanza excepción
}
```

**2. Mockear lo que se está testeando:**
```java
// ❌ Si testeas el Service, NO mockees el Service
@Mock ProductoService productoService; // Error: estás testeando esto mismo
@InjectMocks ProductoServiceImpl serviceImpl;
// Mockea las DEPENDENCIAS del service (el Repository), no el service mismo
```

**3. Confiar solo en la cobertura:**
```java
// 100% de cobertura, pero completamente inútil:
@Test
void test() {
    for (int i = 0; i < 1000; i++) {
        try { service.cualquierMetodo(); } catch (Exception e) {}
        // Ejecuta todo el código pero no verifica nada
    }
}
```

**4. Tests que dependen de datos externos:**
```java
// ❌ MAL: depende de una BD real que puede no estar disponible
@Test
void test() {
    // Conecta a PostgreSQL real → falla en CI/CD
}

// ✅ BIEN: usa H2 en memoria o mocks
@DataJpaTest // Usa H2 automáticamente
void test() { ... }
```

### 11.3 Testing con Spring Security

```java
// Simular usuario autenticado sin BD de usuarios
@WithMockUser(username = "admin", roles = {"ADMIN"})
@Test
void postConAdmin() throws Exception { ... }

// Simular usuario SIN autenticación
@Test // Sin @WithMockUser
void sinAutenticacion() throws Exception {
    mockMvc.perform(post("/api/v1/productos")...)
           .andExpect(status().isUnauthorized()); // 401
}

// En requests que modifican datos, agregar CSRF token:
mockMvc.perform(post("/endpoint")
        .with(csrf())  // Obligatorio en Spring Security con CSRF habilitado
        ...
```

### 11.4 Velocidad de los tests

Los tests deben ser rápidos. Si tardan mucho, los desarrolladores los omiten:

```
@ExtendWith(MockitoExtension.class) → ~10ms por test    ✅
@WebMvcTest                         → ~500ms por test   ✅
@DataJpaTest                        → ~1s  por test     ✅
@SpringBootTest                     → ~5s  por test     ⚠️ Usar poco
```

**Estrategia:** usar `@SpringBootTest` solo para los tests de integración más críticos.

---

## 12. Resumen y checklist final

### Checklist para la Evaluación Parcial N°3

Para cada microservicio de su proyecto:

**Tests Unitarios (Service)**
- [ ] Test happy path para cada método público
- [ ] Test de excepción para cada `orElseThrow()`
- [ ] Test de validación de negocio (duplicados, stock, etc.)
- [ ] Test de que el Repository NO es llamado cuando no debe

**Tests Unitarios (Controller)**
- [ ] Test HTTP 200/201 para cada endpoint exitoso
- [ ] Test HTTP 404 cuando el recurso no existe
- [ ] Test HTTP 400 con datos inválidos
- [ ] Test HTTP 401 para endpoints que requieren autenticación
- [ ] Test HTTP 403 para endpoints con restricción de rol
- [ ] Test HTTP 409 para conflictos (duplicados)

**Tests Unitarios (Repository)**
- [ ] Test para cada método `findBy...` personalizado
- [ ] Test para cada consulta `@Query`
- [ ] Test de búsqueda con case-insensitive si aplica

**Tests Unitarios (Exception Handler)**
- [ ] Test que cada excepción retorna el código HTTP correcto
- [ ] Test que errores 500 no exponen información sensible

**Tests de Integración**
- [ ] Test ciclo CRUD completo
- [ ] Test de seguridad end-to-end
- [ ] Test de validaciones end-to-end

**Cobertura JaCoCo**
- [ ] `mvn clean test` ejecuta exitosamente
- [ ] Reporte generado en `target/site/jacoco/index.html`
- [ ] Cobertura total >= 80% de líneas
- [ ] Captura de pantalla del reporte para el informe

### Comandos útiles

```bash
# Ejecutar solo los tests unitarios (excluye integración)
mvn test -Dtest="*Test" -DfailIfNoTests=false

# Ejecutar todos los tests + generar reporte
mvn clean verify

# Ejecutar un test específico
mvn test -Dtest="ProductoServiceTest#crear_cuandoNombreNoExiste"

# Ver reporte JaCoCo
open target/site/jacoco/index.html  # macOS
xdg-open target/site/jacoco/index.html  # Linux
start target/site/jacoco/index.html  # Windows
```

### Estructura final de tests en el proyecto

```
src/test/java/com/duoc/msproductos/
├── service/
│   └── ProductoServiceTest.java         ← 10+ tests
├── controller/
│   └── ProductoControllerTest.java      ← 12+ tests
├── repository/
│   └── ProductoRepositoryTest.java      ← 8+ tests
├── exception/
│   └── GlobalExceptionHandlerTest.java  ← 6+ tests
└── integration/
    └── ProductoIntegrationTest.java     ← 5+ tests
                                         ─────────────
                                         TOTAL: 40+ tests
                                         Cobertura: ~85%
```

---

## Referencias

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/testing.html)
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)
- [AssertJ Documentation](https://assertj.github.io/doc/)
- [Testing Spring Security](https://docs.spring.io/spring-security/reference/servlet/test/index.html)

---

*Guía desarrollada para DSY1106 - Desarrollo Full Stack III | Duoc UC*
*Microservicio de ejemplo: `ms-productos` con Spring Boot 3.2 + JUnit 5 + Mockito + JaCoCo*
