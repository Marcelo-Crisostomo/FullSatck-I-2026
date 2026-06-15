package com.duoc.msproductos.repository;

import com.duoc.msproductos.model.Producto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * ============================================================
 * TESTS DEL REPOSITORY - ProductoRepository
 * ============================================================
 *
 * OBJETIVO: Probar que las consultas a la BD funcionan correctamente.
 *
 * HERRAMIENTAS:
 * - @DataJpaTest: carga SOLO la capa JPA (Repository + H2 en memoria)
 *   NO carga controllers, services, security, etc. → muy rápido
 * - @ActiveProfiles("test"): usa application-test.properties
 * - H2 Database: BD en memoria que se crea/destruye por cada test
 *
 * ¿QUÉ SE TESTEA EN EL REPOSITORY?
 * ✅ Métodos derivados de Spring Data JPA (findBy..., existsBy...)
 * ✅ Consultas @Query personalizadas (JPQL)
 * ✅ Integridad de datos (constraints de la entidad)
 * ✅ Métodos básicos CRUD (findById, save, delete)
 *
 * ¿QUÉ NO SE TESTEA?
 * ❌ Los métodos heredados de JpaRepository (findAll, save, delete)
 *    Spring Data JPA los tiene testeados; no duplicamos eso.
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests del Repository - ProductoRepository")
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository productoRepository;

    // =============================================
    // DATOS DE PRUEBA
    // =============================================

    private Producto laptop;
    private Producto monitor;
    private Producto tecladoInactivo;

    @BeforeEach
    void setUp() {
        // Limpiar antes de cada test para garantizar estado limpio
        productoRepository.deleteAll();

        laptop = productoRepository.save(Producto.builder()
                .nombre("Laptop Dell XPS 15")
                .descripcion("Laptop profesional")
                .precio(new BigDecimal("1299.99"))
                .stock(10)
                .categoria("Electrónica")
                .activo(true)
                .build());

        monitor = productoRepository.save(Producto.builder()
                .nombre("Monitor Samsung 27")
                .descripcion("Monitor 4K")
                .precio(new BigDecimal("599.99"))
                .stock(3)
                .categoria("Electrónica")
                .activo(true)
                .build());

        tecladoInactivo = productoRepository.save(Producto.builder()
                .nombre("Teclado Mecánico")
                .descripcion("Teclado gaming")
                .precio(new BigDecimal("150.00"))
                .stock(0)
                .categoria("Periféricos")
                .activo(false)  // ← Inactivo
                .build());
    }

    // =============================================
    // TESTS: findByActivoTrue()
    // =============================================

    @Nested
    @DisplayName("findByActivoTrue()")
    class FindByActivoTrueTests {

        @Test
        @DisplayName("✅ Debe retornar solo productos activos")
        void debeRetornarSoloProductosActivos() {
            List<Producto> activos = productoRepository.findByActivoTrue();

            assertThat(activos).hasSize(2);
            assertThat(activos).allMatch(p -> p.getActivo().equals(true));
            assertThat(activos).extracting(Producto::getNombre)
                    .containsExactlyInAnyOrder("Laptop Dell XPS 15", "Monitor Samsung 27");
        }

        @Test
        @DisplayName("✅ No debe incluir productos inactivos")
        void noDebeIncluirProductosInactivos() {
            List<Producto> activos = productoRepository.findByActivoTrue();

            assertThat(activos)
                    .extracting(Producto::getNombre)
                    .doesNotContain("Teclado Mecánico");
        }
    }

    // =============================================
    // TESTS: findByCategoriaIgnoreCase()
    // =============================================

    @Nested
    @DisplayName("findByCategoriaIgnoreCase()")
    class FindByCategoriaTests {

        @Test
        @DisplayName("✅ Debe encontrar productos por categoría exacta")
        void debeEncontrarPorCategoriaExacta() {
            List<Producto> electronicos = productoRepository
                    .findByCategoriaIgnoreCase("Electrónica");

            // Incluye activos e inactivos (busca solo por categoría)
            assertThat(electronicos).hasSize(2);
        }

        @Test
        @DisplayName("✅ Búsqueda de categoría es case-insensitive")
        void busquedaEsCaseInsensitive() {
            List<Producto> electronicos1 = productoRepository
                    .findByCategoriaIgnoreCase("electrónica");
            List<Producto> electronicos2 = productoRepository
                    .findByCategoriaIgnoreCase("ELECTRÓNICA");

            assertThat(electronicos1).hasSize(2);
            assertThat(electronicos2).hasSize(2);
        }

        @Test
        @DisplayName("✅ Debe retornar lista vacía para categoría inexistente")
        void debeRetornarVacioParaCategoriaInexistente() {
            List<Producto> resultado = productoRepository
                    .findByCategoriaIgnoreCase("CategoríaQueNoExiste");

            assertThat(resultado).isEmpty();
        }
    }

    // =============================================
    // TESTS: findByNombreContainingIgnoreCase()
    // =============================================

    @Nested
    @DisplayName("findByNombreContainingIgnoreCase()")
    class FindByNombreTests {

        @Test
        @DisplayName("✅ Debe encontrar productos con búsqueda parcial")
        void debeEncontrarPorBusquedaParcial() {
            List<Producto> resultado = productoRepository
                    .findByNombreContainingIgnoreCase("laptop");

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getNombre()).contains("Laptop");
        }

        @Test
        @DisplayName("✅ Debe encontrar múltiples productos con término común")
        void debeEncontrarMultiplesConTerminoComun() {
            // "Samsung" aparece en el monitor
            List<Producto> resultado = productoRepository
                    .findByNombreContainingIgnoreCase("samsung");

            assertThat(resultado).hasSize(1);
        }

        @Test
        @DisplayName("✅ Debe retornar vacío si no hay coincidencias")
        void debeRetornarVacioCuandoNoHayCoincidencias() {
            List<Producto> resultado = productoRepository
                    .findByNombreContainingIgnoreCase("TERMINO_QUE_NO_EXISTE");

            assertThat(resultado).isEmpty();
        }
    }

    // =============================================
    // TESTS: existsByNombreIgnoreCase()
    // =============================================

    @Nested
    @DisplayName("existsByNombreIgnoreCase()")
    class ExistsByNombreTests {

        @Test
        @DisplayName("✅ Debe retornar true cuando el nombre existe")
        void debeRetornarTrueCuandoExiste() {
            boolean existe = productoRepository.existsByNombreIgnoreCase("Laptop Dell XPS 15");
            assertThat(existe).isTrue();
        }

        @Test
        @DisplayName("✅ Debe retornar false cuando el nombre no existe")
        void debeRetornarFalseCuandoNoExiste() {
            boolean existe = productoRepository.existsByNombreIgnoreCase("Producto Inexistente");
            assertThat(existe).isFalse();
        }

        @Test
        @DisplayName("✅ Debe ser case-insensitive")
        void debeSerCaseInsensitive() {
            boolean existeMayus = productoRepository.existsByNombreIgnoreCase("LAPTOP DELL XPS 15");
            boolean existeMinus = productoRepository.existsByNombreIgnoreCase("laptop dell xps 15");

            assertThat(existeMayus).isTrue();
            assertThat(existeMinus).isTrue();
        }
    }

    // =============================================
    // TESTS: findProductosConStockBajo() - @Query personalizada
    // =============================================

    @Nested
    @DisplayName("findProductosConStockBajo() - @Query personalizada")
    class StockBajoTests {

        @Test
        @DisplayName("✅ Debe retornar productos activos con stock bajo")
        void debeRetornarProductosConStockBajo() {
            // El monitor tiene stock=3, la laptop tiene stock=10
            List<Producto> stockBajo = productoRepository.findProductosConStockBajo(5);

            assertThat(stockBajo).hasSize(1);
            assertThat(stockBajo.get(0).getNombre()).isEqualTo("Monitor Samsung 27");
        }

        @Test
        @DisplayName("✅ No debe incluir productos inactivos aunque tengan stock bajo")
        void noDebeIncluirInactivosAunConStockBajo() {
            // El teclado inactivo tiene stock=0, pero no debe aparecer
            List<Producto> stockBajo = productoRepository.findProductosConStockBajo(10);

            assertThat(stockBajo)
                    .extracting(Producto::getNombre)
                    .doesNotContain("Teclado Mecánico");
        }

        @Test
        @DisplayName("✅ Debe retornar vacío cuando todos tienen stock suficiente")
        void debeRetornarVacioCuandoTodosTienenStock() {
            List<Producto> stockBajo = productoRepository.findProductosConStockBajo(0);

            // Con umbral 0, ningún activo tiene stock <= 0 (monitor tiene 3, laptop 10)
            assertThat(stockBajo).isEmpty();
        }
    }

    // =============================================
    // TESTS: findByCategoriaIgnoreCaseAndActivoTrue()
    // =============================================

    @Test
    @DisplayName("✅ findByCategoriaAndActivo: debe filtrar por categoría Y estado activo")
    void debeFiltrarPorCategoriaYActivo() {
        // Electrónica tiene: laptop (activo) + monitor (activo) = 2
        // Pero si buscamos Periféricos (donde está el teclado inactivo) → debe dar 0
        List<Producto> perifericos = productoRepository
                .findByCategoriaIgnoreCaseAndActivoTrue("Periféricos");

        assertThat(perifericos).isEmpty();  // El teclado está inactivo
    }
}
