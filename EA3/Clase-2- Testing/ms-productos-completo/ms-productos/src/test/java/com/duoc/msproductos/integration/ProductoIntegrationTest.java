package com.duoc.msproductos.integration;

import com.duoc.msproductos.dto.ProductoDTO.ProductoRequestDTO;
import com.duoc.msproductos.model.Producto;
import com.duoc.msproductos.repository.ProductoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ============================================================
 * TEST DE INTEGRACIÓN - Flujo completo del Microservicio
 * ============================================================
 *
 * OBJETIVO: Probar el flujo COMPLETO del request:
 * HTTP Request → Controller → Service → Repository → H2 → Response
 *
 * DIFERENCIA CON TESTS UNITARIOS:
 * - @SpringBootTest carga el contexto completo de Spring
 * - Usa BD H2 en memoria real (no mocks)
 * - Prueba que todas las capas trabajan juntas correctamente
 * - Más lento que los tests unitarios (por eso se usan menos)
 *
 * CUÁNDO USAR:
 * ✅ Flujos críticos end-to-end
 * ✅ Verificar integración entre capas
 * ✅ Smoke tests básicos
 *
 * CUÁNDO NO USAR:
 * ❌ Para cada caso borde → usar tests unitarios (más rápidos)
 * ❌ Para lógica de negocio detallada → usar ProductoServiceTest
 *
 * PROPORCIÓN RECOMENDADA (Pirámide de Testing):
 * 70% unitarios | 20% integración | 10% E2E
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Tests de Integración - Flujo completo del MS")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductoRepository productoRepository;

    private ProductoRequestDTO productoValido;

    @BeforeEach
    void setUp() {
        productoRepository.deleteAll();

        productoValido = ProductoRequestDTO.builder()
                .nombre("Laptop Dell XPS Integration")
                .descripcion("Test de integración")
                .precio(new BigDecimal("1299.99"))
                .stock(10)
                .categoria("Electrónica")
                .build();
    }

    // =============================================
    // TEST 1: Crear y luego listar
    // =============================================

    @Test
    @Order(1)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("✅ INTEGRACIÓN: Crear producto y verificar que aparece en el listado")
    void crearProductoYVerificarEnListado() throws Exception {
        // PASO 1: Crear el producto
        mockMvc.perform(post("/api/v1/productos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productoValido)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.nombre").value("Laptop Dell XPS Integration"))
                .andExpect(jsonPath("$.data.activo").value(true));

        // PASO 2: Verificar que aparece en el listado
        mockMvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].nombre").value("Laptop Dell XPS Integration"));
    }

    // =============================================
    // TEST 2: Ciclo completo CRUD
    // =============================================

    @Test
    @Order(2)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("✅ INTEGRACIÓN: Ciclo completo CRUD (crear → leer → actualizar → eliminar)")
    void cicloCRUDCompleto() throws Exception {
        // CREAR
        String responseJson = mockMvc.perform(post("/api/v1/productos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productoValido)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Extraer el ID del producto creado
        Long id = objectMapper.readTree(responseJson)
                .path("data").path("id").asLong();

        // LEER por ID
        mockMvc.perform(get("/api/v1/productos/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id));

        // ACTUALIZAR
        ProductoRequestDTO update = ProductoRequestDTO.builder()
                .nombre("Laptop Dell XPS ACTUALIZADA")
                .descripcion("Descripción actualizada")
                .precio(new BigDecimal("999.99"))
                .stock(20)
                .categoria("Electrónica Premium")
                .build();

        mockMvc.perform(put("/api/v1/productos/" + id)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nombre").value("Laptop Dell XPS ACTUALIZADA"))
                .andExpect(jsonPath("$.data.precio").value(999.99));

        // ELIMINAR (soft delete)
        mockMvc.perform(delete("/api/v1/productos/" + id).with(csrf()))
                .andExpect(status().isOk());

        // VERIFICAR que ya no aparece en el listado (pero sí en BD)
        mockMvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)));  // Eliminado lógicamente

        // Verificar que sigue en BD pero inactivo
        Producto enBD = productoRepository.findById(id).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(enBD.getActivo()).isFalse();
    }

    // =============================================
    // TEST 3: Validaciones end-to-end
    // =============================================

    @Test
    @Order(3)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("❌ INTEGRACIÓN: Validación de campos requeridos llega hasta la respuesta")
    void validacionCamposRequeridos() throws Exception {
        // Request con campos inválidos
        String requestInvalido = """
                {
                    "nombre": "",
                    "precio": -100,
                    "stock": -1,
                    "categoria": ""
                }
                """;

        mockMvc.perform(post("/api/v1/productos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data.nombre").exists())
                .andExpect(jsonPath("$.data.precio").exists());
    }

    // =============================================
    // TEST 4: Seguridad end-to-end
    // =============================================

    @Test
    @Order(4)
    @DisplayName("❌ INTEGRACIÓN: POST sin autenticación retorna 401")
    void sinAutenticacionRetorna401() throws Exception {
        mockMvc.perform(post("/api/v1/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productoValido)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(5)
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("❌ INTEGRACIÓN: POST con rol USER retorna 403")
    void conRolUserRetorna403() throws Exception {
        mockMvc.perform(post("/api/v1/productos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productoValido)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(6)
    @DisplayName("✅ INTEGRACIÓN: GET sin autenticación retorna 200 (público)")
    void sinAutenticacionGetRetorna200() throws Exception {
        mockMvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk());
    }
}
