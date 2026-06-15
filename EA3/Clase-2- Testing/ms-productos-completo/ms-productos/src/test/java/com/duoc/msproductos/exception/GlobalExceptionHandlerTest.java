package com.duoc.msproductos.exception;

import com.duoc.msproductos.dto.ProductoDTO.ApiResponseDTO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * ============================================================
 * TESTS DEL MANEJADOR DE EXCEPCIONES - GlobalExceptionHandler
 * ============================================================
 *
 * OBJETIVO: Verificar que cada excepción retorna el código HTTP correcto
 * y el formato de respuesta esperado.
 *
 * ¿POR QUÉ TESTEAR EL EXCEPTION HANDLER?
 * - Es parte crítica del contrato de la API
 * - Un 404 que accidentalmente retorne 500 rompe la experiencia del cliente
 * - Los errores de validación deben tener formato consistente
 *
 * ENFOQUE: Instanciamos el Handler directamente y llamamos los métodos.
 * No necesitamos Spring Context para esto → tests ultra rápidos.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests del GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        // Crear un WebRequest simulado para los tests
        webRequest = new ServletWebRequest(new MockHttpServletRequest());
    }

    @Test
    @DisplayName("✅ ProductoNotFoundException → HTTP 404")
    void productoNotFoundException_debeRetornar404() {
        // ARRANGE
        ProductoNotFoundException ex = new ProductoNotFoundException("Producto no encontrado con ID: 5");

        // ACT
        ResponseEntity<ApiResponseDTO<Void>> response =
                exceptionHandler.handleProductoNotFound(ex, webRequest);

        // ASSERT
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getStatusCode()).isEqualTo(404);
        assertThat(response.getBody().getMessage()).contains("5");
    }

    @Test
    @DisplayName("✅ ProductoDuplicadoException → HTTP 409")
    void productoDuplicadoException_debeRetornar409() {
        // ARRANGE
        ProductoDuplicadoException ex = new ProductoDuplicadoException("Ya existe: Laptop XPS");

        // ACT
        ResponseEntity<ApiResponseDTO<Void>> response =
                exceptionHandler.handleProductoDuplicado(ex, webRequest);

        // ASSERT
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getStatusCode()).isEqualTo(409);
        assertThat(response.getBody().getMessage()).contains("Laptop XPS");
    }

    @Test
    @DisplayName("✅ MethodArgumentNotValidException → HTTP 400 con mapa de errores")
    void validationException_debeRetornar400ConDetalleDeErrores() throws Exception {
        // ARRANGE: crear un error de validación simulado
        // Simular que el campo "nombre" falló la validación @NotBlank
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "productoRequestDTO");
        bindingResult.addError(new FieldError(
                "productoRequestDTO",
                "nombre",
                "El nombre no puede estar vacío"));
        bindingResult.addError(new FieldError(
                "productoRequestDTO",
                "precio",
                "El precio es obligatorio"));

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, bindingResult);

        // ACT
        ResponseEntity<ApiResponseDTO<Map<String, String>>> response =
                exceptionHandler.handleValidationErrors(ex);

        // ASSERT
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getData()).containsKey("nombre");
        assertThat(response.getBody().getData()).containsKey("precio");
        assertThat(response.getBody().getData().get("nombre"))
                .isEqualTo("El nombre no puede estar vacío");
    }

    @Test
    @DisplayName("✅ AccessDeniedException → HTTP 403")
    void accessDeniedException_debeRetornar403() {
        // ARRANGE
        AccessDeniedException ex = new AccessDeniedException("Acceso denegado");

        // ACT
        ResponseEntity<ApiResponseDTO<Void>> response =
                exceptionHandler.handleAccessDeniedException(ex);

        // ASSERT
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().getStatusCode()).isEqualTo(403);
        assertThat(response.getBody().getMessage()).containsIgnoringCase("denegado");
    }

    @Test
    @DisplayName("✅ BadCredentialsException → HTTP 401")
    void authenticationException_debeRetornar401() {
        // ARRANGE
        BadCredentialsException ex = new BadCredentialsException("Credenciales inválidas");

        // ACT
        ResponseEntity<ApiResponseDTO<Void>> response =
                exceptionHandler.handleAuthenticationException(ex);

        // ASSERT
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getStatusCode()).isEqualTo(401);
    }

    @Test
    @DisplayName("✅ Exception genérica → HTTP 500 sin exponer detalles")
    void exceptionGenerica_debeRetornar500SinDetalles() {
        // ARRANGE: excepción inesperada con mensaje con datos sensibles
        Exception ex = new RuntimeException("Error de BD: password=secreto123 host=192.168.1.1");

        // ACT
        ResponseEntity<ApiResponseDTO<Void>> response =
                exceptionHandler.handleGenericException(ex, webRequest);

        // ASSERT
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getStatusCode()).isEqualTo(500);
        // CRÍTICO: el mensaje NO debe exponer el error interno
        assertThat(response.getBody().getMessage()).doesNotContain("secreto123");
        assertThat(response.getBody().getMessage()).doesNotContain("192.168.1.1");
        assertThat(response.getBody().getMessage()).containsIgnoringCase("Error interno");
    }
}
