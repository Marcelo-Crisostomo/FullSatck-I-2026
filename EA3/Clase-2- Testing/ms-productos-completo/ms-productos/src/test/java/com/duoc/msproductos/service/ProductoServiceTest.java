package com.duoc.msproductos.service;

import com.duoc.msproductos.dto.ProductoDTO.*;
import com.duoc.msproductos.exception.ProductoDuplicadoException;
import com.duoc.msproductos.exception.ProductoNotFoundException;
import com.duoc.msproductos.model.Producto;
import com.duoc.msproductos.repository.ProductoRepository;
import com.duoc.msproductos.service.impl.ProductoServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ============================================================
 * TESTS UNITARIOS - ProductoServiceImpl
 * ============================================================
 *
 * OBJETIVO: Probar la lógica de negocio del Service de forma AISLADA.
 *
 * PRINCIPIOS:
 * - Usamos @ExtendWith(MockitoExtension.class) → no levanta Spring
 * - Usamos @Mock para simular el Repository (no accedemos a BD real)
 * - Usamos @InjectMocks para inyectar los mocks en el Service
 * - Cada test verifica UNA sola cosa (principio SRP en testing)
 *
 * PATRÓN AAA (Arrange → Act → Assert):
 * - Arrange: preparar datos y configurar mocks
 * - Act: ejecutar el método que se está testeando
 * - Assert: verificar que el resultado es el esperado
 *
 * ¿QUÉ SE TESTEA EN EL SERVICE?
 * ✅ Flujo feliz (happy path): el caso normal funciona
 * ✅ Casos de error: se lanzan las excepciones correctas
 * ✅ Lógica condicional: los if/else del service
 * ✅ Interacciones con dependencias: ¿cuándo llama al Repository?
 * ✅ Transformaciones: ¿convierte bien Entity → DTO?
 */
@ExtendWith(MockitoExtension.class)     // JUnit 5 + Mockito, SIN Spring
@DisplayName("Tests Unitarios - ProductoService")
class ProductoServiceTest {

    // =============================================
    // CONFIGURACIÓN DE MOCKS
    // =============================================

    @Mock
    private ProductoRepository productoRepository;
    // @Mock crea un "doble de prueba" del Repository.
    // NO conecta a ninguna BD. Nosotros controlamos qué retorna.

    @InjectMocks
    private ProductoServiceImpl productoService;
    // @InjectMocks crea la instancia real del Service
    // e inyecta los @Mock en ella automáticamente.

    // =============================================
    // DATOS DE PRUEBA REUTILIZABLES
    // =============================================

    private Producto productoActivo;
    private Producto productoInactivo;
    private ProductoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        // Se ejecuta ANTES de cada test → estado limpio
        productoActivo = Producto.builder()
                .id(1L)
                .nombre("Laptop Dell XPS")
                .descripcion("Laptop profesional 15 pulgadas")
                .precio(new BigDecimal("1299.99"))
                .stock(10)
                .categoria("Electrónica")
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        productoInactivo = Producto.builder()
                .id(2L)
                .nombre("Monitor Viejo")
                .precio(new BigDecimal("200.00"))
                .stock(0)
                .categoria("Electrónica")
                .activo(false)
                .build();

        requestDTO = ProductoRequestDTO.builder()
                .nombre("Laptop Dell XPS")
                .descripcion("Laptop profesional 15 pulgadas")
                .precio(new BigDecimal("1299.99"))
                .stock(10)
                .categoria("Electrónica")
                .build();
    }

    // =============================================
    // TESTS: obtenerTodos()
    // =============================================

    @Nested
    @DisplayName("obtenerTodos()")
    class ObtenerTodosTests {

        @Test
        @DisplayName("✅ Debe retornar lista de productos activos")
        void debeRetornarProductosActivos() {
            // ARRANGE: configurar el mock para que retorne nuestra lista
            when(productoRepository.findByActivoTrue())
                    .thenReturn(Arrays.asList(productoActivo));

            // ACT: ejecutar el método a testear
            List<ProductoResponseDTO> resultado = productoService.obtenerTodos();

            // ASSERT: verificar el resultado
            assertThat(resultado).isNotNull();
            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getNombre()).isEqualTo("Laptop Dell XPS");
            assertThat(resultado.get(0).getActivo()).isTrue();

            // VERIFY: confirmar que se llamó al Repository exactamente 1 vez
            verify(productoRepository, times(1)).findByActivoTrue();
        }

        @Test
        @DisplayName("✅ Debe retornar lista vacía si no hay productos activos")
        void debeRetornarListaVaciaSiNoHayProductos() {
            // ARRANGE
            when(productoRepository.findByActivoTrue()).thenReturn(List.of());

            // ACT
            List<ProductoResponseDTO> resultado = productoService.obtenerTodos();

            // ASSERT
            assertThat(resultado).isEmpty();
            verify(productoRepository, times(1)).findByActivoTrue();
        }
    }

    // =============================================
    // TESTS: obtenerPorId()
    // =============================================

    @Nested
    @DisplayName("obtenerPorId()")
    class ObtenerPorIdTests {

        @Test
        @DisplayName("✅ Debe retornar producto cuando el ID existe")
        void debeRetornarProductoCuandoExiste() {
            // ARRANGE
            when(productoRepository.findById(1L))
                    .thenReturn(Optional.of(productoActivo));

            // ACT
            ProductoResponseDTO resultado = productoService.obtenerPorId(1L);

            // ASSERT
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getNombre()).isEqualTo("Laptop Dell XPS");
            assertThat(resultado.getPrecio()).isEqualByComparingTo("1299.99");
        }

        @Test
        @DisplayName("❌ Debe lanzar ProductoNotFoundException cuando el ID no existe")
        void debeLanzarExcepcionCuandoIdNoExiste() {
            // ARRANGE: el repositorio retorna vacío
            when(productoRepository.findById(99L))
                    .thenReturn(Optional.empty());

            // ACT & ASSERT: verificar que se lanza la excepción correcta
            assertThatThrownBy(() -> productoService.obtenerPorId(99L))
                    .isInstanceOf(ProductoNotFoundException.class)
                    .hasMessageContaining("99");  // El mensaje debe incluir el ID

            // Verificar que sí se intentó buscar
            verify(productoRepository, times(1)).findById(99L);
        }
    }

    // =============================================
    // TESTS: crear()
    // =============================================

    @Nested
    @DisplayName("crear()")
    class CrearTests {

        @Test
        @DisplayName("✅ Debe crear y retornar producto cuando el nombre no existe")
        void debeCrearProductoCuandoNombreNoExiste() {
            // ARRANGE
            when(productoRepository.existsByNombreIgnoreCase("Laptop Dell XPS"))
                    .thenReturn(false);  // El nombre NO existe → puede crearse
            when(productoRepository.save(any(Producto.class)))
                    .thenReturn(productoActivo);

            // ACT
            ProductoResponseDTO resultado = productoService.crear(requestDTO);

            // ASSERT
            assertThat(resultado).isNotNull();
            assertThat(resultado.getNombre()).isEqualTo("Laptop Dell XPS");
            assertThat(resultado.getActivo()).isTrue();

            // VERIFY: verificar que se llamó a save()
            verify(productoRepository, times(1)).save(any(Producto.class));
        }

        @Test
        @DisplayName("❌ Debe lanzar ProductoDuplicadoException cuando el nombre ya existe")
        void debeLanzarExcepcionCuandoNombreDuplicado() {
            // ARRANGE: simular que el nombre YA existe
            when(productoRepository.existsByNombreIgnoreCase("Laptop Dell XPS"))
                    .thenReturn(true);

            // ACT & ASSERT
            assertThatThrownBy(() -> productoService.crear(requestDTO))
                    .isInstanceOf(ProductoDuplicadoException.class)
                    .hasMessageContaining("Laptop Dell XPS");

            // VERIFY: NUNCA debe llamarse a save() si hay duplicado
            verify(productoRepository, never()).save(any(Producto.class));
        }

        @Test
        @DisplayName("✅ Debe crear producto con activo=true por defecto")
        void debeCrearProductoConActivoTrue() {
            // ARRANGE
            when(productoRepository.existsByNombreIgnoreCase(anyString())).thenReturn(false);
            when(productoRepository.save(any(Producto.class))).thenReturn(productoActivo);

            // ACT
            ProductoResponseDTO resultado = productoService.crear(requestDTO);

            // ASSERT: el producto recién creado debe estar activo
            assertThat(resultado.getActivo()).isTrue();
        }
    }

    // =============================================
    // TESTS: actualizar()
    // =============================================

    @Nested
    @DisplayName("actualizar()")
    class ActualizarTests {

        @Test
        @DisplayName("✅ Debe actualizar producto cuando existe y nombre no cambia")
        void debeActualizarProductoCuandoExiste() {
            // ARRANGE
            when(productoRepository.findById(1L)).thenReturn(Optional.of(productoActivo));
            when(productoRepository.save(any(Producto.class))).thenReturn(productoActivo);

            // ACT (mismo nombre → no valida duplicado)
            ProductoResponseDTO resultado = productoService.actualizar(1L, requestDTO);

            // ASSERT
            assertThat(resultado).isNotNull();
            verify(productoRepository, times(1)).save(any(Producto.class));
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando producto a actualizar no existe")
        void debeLanzarExcepcionCuandoProductoNoExiste() {
            // ARRANGE
            when(productoRepository.findById(99L)).thenReturn(Optional.empty());

            // ACT & ASSERT
            assertThatThrownBy(() -> productoService.actualizar(99L, requestDTO))
                    .isInstanceOf(ProductoNotFoundException.class);

            verify(productoRepository, never()).save(any(Producto.class));
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando nuevo nombre pertenece a otro producto")
        void debeLanzarExcepcionCuandoNuevoNombreEsDuplicado() {
            // ARRANGE: el producto a editar tiene nombre "Monitor Viejo"
            Producto productoOtro = Producto.builder().id(3L).nombre("Monitor Viejo")
                    .precio(BigDecimal.TEN).stock(5).categoria("Office")
                    .activo(true).build();

            when(productoRepository.findById(3L)).thenReturn(Optional.of(productoOtro));
            // Intentamos cambiar el nombre a uno que ya existe
            when(productoRepository.existsByNombreIgnoreCase("Laptop Dell XPS"))
                    .thenReturn(true);

            // ACT & ASSERT
            assertThatThrownBy(() -> productoService.actualizar(3L, requestDTO))
                    .isInstanceOf(ProductoDuplicadoException.class);
        }
    }

    // =============================================
    // TESTS: eliminar()
    // =============================================

    @Nested
    @DisplayName("eliminar()")
    class EliminarTests {

        @Test
        @DisplayName("✅ Debe desactivar producto cuando existe (soft delete)")
        void debeDesactivarProductoCuandoExiste() {
            // ARRANGE
            when(productoRepository.findById(1L)).thenReturn(Optional.of(productoActivo));
            when(productoRepository.save(any(Producto.class))).thenReturn(productoActivo);

            // ACT
            productoService.eliminar(1L);

            // ASSERT: verificar que se llamó a save (no delete físico)
            verify(productoRepository, times(1)).save(any(Producto.class));
            // El producto debe haberse marcado como inactivo
            assertThat(productoActivo.getActivo()).isFalse();
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando producto a eliminar no existe")
        void debeLanzarExcepcionCuandoProductoNoExiste() {
            // ARRANGE
            when(productoRepository.findById(99L)).thenReturn(Optional.empty());

            // ACT & ASSERT
            assertThatThrownBy(() -> productoService.eliminar(99L))
                    .isInstanceOf(ProductoNotFoundException.class)
                    .hasMessageContaining("99");

            // Verificar que NO se llama a save ni delete
            verify(productoRepository, never()).save(any(Producto.class));
            verify(productoRepository, never()).deleteById(anyLong());
        }
    }

    // =============================================
    // TESTS: obtenerPorCategoria()
    // =============================================

    @Nested
    @DisplayName("obtenerPorCategoria()")
    class ObtenerPorCategoriaTests {

        @Test
        @DisplayName("✅ Debe retornar productos de la categoría indicada")
        void debeRetornarProductosDeLaCategoria() {
            // ARRANGE
            when(productoRepository.findByCategoriaIgnoreCaseAndActivoTrue("Electrónica"))
                    .thenReturn(List.of(productoActivo));

            // ACT
            List<ProductoResponseDTO> resultado = productoService.obtenerPorCategoria("Electrónica");

            // ASSERT
            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getCategoria()).isEqualTo("Electrónica");
        }

        @Test
        @DisplayName("✅ Búsqueda de categoría es case-insensitive")
        void busquedaCategoriaEsCaseInsensitive() {
            // ARRANGE
            when(productoRepository.findByCategoriaIgnoreCaseAndActivoTrue("electronica"))
                    .thenReturn(List.of(productoActivo));

            // ACT
            List<ProductoResponseDTO> resultado = productoService.obtenerPorCategoria("electronica");

            // ASSERT
            assertThat(resultado).isNotEmpty();
            verify(productoRepository).findByCategoriaIgnoreCaseAndActivoTrue("electronica");
        }
    }
}
