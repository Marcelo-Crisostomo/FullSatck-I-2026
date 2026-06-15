package com.duoc.msproductos.controller;

import com.duoc.msproductos.dto.ProductoDTO.*;
import com.duoc.msproductos.exception.ProductoDuplicadoException;
import com.duoc.msproductos.exception.ProductoNotFoundException;
import com.duoc.msproductos.service.ProductoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ============================================================
 * TESTS DEL CONTROLLER - ProductoController
 * ============================================================
 *
 * OBJETIVO: Probar que el Controller:
 * 1. Mapea correctamente las URLs a los métodos
 * 2. Retorna los códigos HTTP correctos (200, 201, 400, 404, etc.)
 * 3. Deserializa correctamente el JSON de entrada (@RequestBody)
 * 4. Serializa correctamente el JSON de salida (@ResponseBody)
 * 5. Aplica correctamente las restricciones de seguridad
 * 6. Valida los datos de entrada (@Valid)
 *
 * HERRAMIENTAS:
 * - @WebMvcTest: carga SOLO la capa web (Controller + Security + MVC)
 *   NO carga la BD, los servicios reales, etc. → más rápido
 * - @MockBean: reemplaza el Service real con un mock de Mockito
 * - MockMvc: cliente HTTP simulado para hacer requests en los tests
 * - @WithMockUser: simula usuarios autenticados sin credenciales reales
 *
 * IMPORTANTE: NO estamos testeando la lógica de negocio aquí.
 * La lógica se testea en ProductoServiceTest.
 */
@WebMvcTest(ProductoController.class)
@DisplayName("Tests del Controller - ProductoController")
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    // MockMvc simula el servidor web y nos permite hacer HTTP requests en los tests

    @MockBean
    private ProductoService productoService;
    // @MockBean registra un mock de Mockito en el contexto de Spring
    // Reemplaza el bean real de ProductoService

    @Autowired
    private ObjectMapper objectMapper;
    // ObjectMapper: convierte objetos Java a JSON y viceversa

    // =============================================
    // DATOS DE PRUEBA
    // =============================================

    private ProductoResponseDTO productoResponse;
    private ProductoRequestDTO productoRequest;

    @BeforeEach
    void setUp() {
        productoResponse = ProductoResponseDTO.builder()
                .id(1L)
                .nombre("Laptop Dell XPS")
                .descripcion("Laptop profesional 15 pulgadas")
                .precio(new BigDecimal("1299.99"))
                .stock(10)
                .categoria("Electrónica")
                .activo(true)
                .build();

        productoRequest = ProductoRequestDTO.builder()
                .nombre("Laptop Dell XPS")
                .descripcion("Laptop profesional 15 pulgadas")
                .precio(new BigDecimal("1299.99"))
                .stock(10)
                .categoria("Electrónica")
                .build();
    }

    // =============================================
    // TESTS: GET /api/v1/productos
    // =============================================

    @Nested
    @DisplayName("GET /api/v1/productos")
    class GetTodosTests {

        @Test
        @DisplayName("✅ Debe retornar 200 con lista de productos (acceso público)")
        void debeRetornar200ConListaProductos() throws Exception {
            // ARRANGE
            when(productoService.obtenerTodos()).thenReturn(List.of(productoResponse));

            // ACT & ASSERT
            mockMvc.perform(get("/api/v1/productos")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())  // Imprime request/response en consola
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].nombre").value("Laptop Dell XPS"))
                    .andExpect(jsonPath("$.data[0].precio").value(1299.99));
        }

        @Test
        @DisplayName("✅ Debe retornar 200 con lista vacía cuando no hay productos")
        void debeRetornar200ConListaVacia() throws Exception {
            when(productoService.obtenerTodos()).thenReturn(List.of());

            mockMvc.perform(get("/api/v1/productos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isEmpty());
        }
    }

    // =============================================
    // TESTS: GET /api/v1/productos/{id}
    // =============================================

    @Nested
    @DisplayName("GET /api/v1/productos/{id}")
    class GetPorIdTests {

        @Test
        @DisplayName("✅ Debe retornar 200 con producto existente")
        void debeRetornar200CuandoExiste() throws Exception {
            when(productoService.obtenerPorId(1L)).thenReturn(productoResponse);

            mockMvc.perform(get("/api/v1/productos/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.nombre").value("Laptop Dell XPS"));
        }

        @Test
        @DisplayName("❌ Debe retornar 404 cuando el producto no existe")
        void debeRetornar404CuandoNoExiste() throws Exception {
            // ARRANGE: simular que el Service lanza la excepción
            when(productoService.obtenerPorId(99L))
                    .thenThrow(new ProductoNotFoundException("Producto no encontrado con ID: 99"));

            // ACT & ASSERT
            mockMvc.perform(get("/api/v1/productos/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value(containsString("99")));
        }
    }

    // =============================================
    // TESTS: POST /api/v1/productos
    // =============================================

    @Nested
    @DisplayName("POST /api/v1/productos")
    class PostCrearTests {

        @Test
        @DisplayName("✅ Debe retornar 201 al crear producto con ROLE_ADMIN")
        @WithMockUser(username = "admin", roles = {"ADMIN"})
        // @WithMockUser simula un usuario con el rol ADMIN sin autenticación real
        void debeRetornar201AlCrearConAdmin() throws Exception {
            // ARRANGE
            when(productoService.crear(any(ProductoRequestDTO.class)))
                    .thenReturn(productoResponse);

            // ACT & ASSERT
            mockMvc.perform(post("/api/v1/productos")
                    .with(csrf())  // Spring Security requiere CSRF token (en tests se agrega con .with(csrf()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productoRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.statusCode").value(201))
                    .andExpect(jsonPath("$.data.nombre").value("Laptop Dell XPS"));
        }

        @Test
        @DisplayName("❌ Debe retornar 403 cuando usuario no tiene rol ADMIN")
        @WithMockUser(username = "user", roles = {"USER"})
        void debeRetornar403SinRolAdmin() throws Exception {
            mockMvc.perform(post("/api/v1/productos")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productoRequest)))
                    .andExpect(status().isForbidden());

            // VERIFY: el service NUNCA debe ser llamado
            verify(productoService, never()).crear(any());
        }

        @Test
        @DisplayName("❌ Debe retornar 401 cuando no está autenticado")
        void debeRetornar401SinAutenticacion() throws Exception {
            mockMvc.perform(post("/api/v1/productos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productoRequest)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("❌ Debe retornar 400 cuando los datos son inválidos")
        @WithMockUser(username = "admin", roles = {"ADMIN"})
        void debeRetornar400CuandoDatosInvalidos() throws Exception {
            // ARRANGE: request con nombre vacío y precio negativo
            ProductoRequestDTO requestInvalido = ProductoRequestDTO.builder()
                    .nombre("")           // ❌ nombre vacío
                    .precio(new BigDecimal("-10"))  // ❌ precio negativo
                    .stock(-5)            // ❌ stock negativo
                    .categoria("")        // ❌ categoría vacía
                    .build();

            mockMvc.perform(post("/api/v1/productos")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestInvalido)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.data").isMap()); // Los errores de campo vienen en $.data
        }

        @Test
        @DisplayName("❌ Debe retornar 409 cuando el nombre ya existe")
        @WithMockUser(username = "admin", roles = {"ADMIN"})
        void debeRetornar409CuandoNombreDuplicado() throws Exception {
            // ARRANGE
            when(productoService.crear(any(ProductoRequestDTO.class)))
                    .thenThrow(new ProductoDuplicadoException(
                            "Ya existe un producto con el nombre: Laptop Dell XPS"));

            mockMvc.perform(post("/api/v1/productos")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productoRequest)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    // =============================================
    // TESTS: PUT /api/v1/productos/{id}
    // =============================================

    @Nested
    @DisplayName("PUT /api/v1/productos/{id}")
    class PutActualizarTests {

        @Test
        @DisplayName("✅ Debe retornar 200 al actualizar producto con ROLE_ADMIN")
        @WithMockUser(username = "admin", roles = {"ADMIN"})
        void debeRetornar200AlActualizar() throws Exception {
            when(productoService.actualizar(eq(1L), any(ProductoRequestDTO.class)))
                    .thenReturn(productoResponse);

            mockMvc.perform(put("/api/v1/productos/1")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productoRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("❌ Debe retornar 404 al actualizar producto inexistente")
        @WithMockUser(username = "admin", roles = {"ADMIN"})
        void debeRetornar404AlActualizarProductoInexistente() throws Exception {
            when(productoService.actualizar(eq(99L), any()))
                    .thenThrow(new ProductoNotFoundException("Producto no encontrado con ID: 99"));

            mockMvc.perform(put("/api/v1/productos/99")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productoRequest)))
                    .andExpect(status().isNotFound());
        }
    }

    // =============================================
    // TESTS: DELETE /api/v1/productos/{id}
    // =============================================

    @Nested
    @DisplayName("DELETE /api/v1/productos/{id}")
    class DeleteTests {

        @Test
        @DisplayName("✅ Debe retornar 200 al eliminar producto existente con ROLE_ADMIN")
        @WithMockUser(username = "admin", roles = {"ADMIN"})
        void debeRetornar200AlEliminar() throws Exception {
            doNothing().when(productoService).eliminar(1L);

            mockMvc.perform(delete("/api/v1/productos/1")
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value(containsString("eliminado")));
        }

        @Test
        @DisplayName("❌ Debe retornar 404 al eliminar producto inexistente")
        @WithMockUser(username = "admin", roles = {"ADMIN"})
        void debeRetornar404AlEliminarInexistente() throws Exception {
            doThrow(new ProductoNotFoundException("Producto no encontrado con ID: 99"))
                    .when(productoService).eliminar(99L);

            mockMvc.perform(delete("/api/v1/productos/99")
                    .with(csrf()))
                    .andExpect(status().isNotFound());
        }
    }
}
