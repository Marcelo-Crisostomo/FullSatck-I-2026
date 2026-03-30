package com.duoc.productosapi.controller;


// Importamos el modelo
import com.duoc.productosapi.model.Producto;

// Importamos el service
import com.duoc.productosapi.service.ProductoService;

// Importamos clases HTTP
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

// Importamos anotaciones REST
import org.springframework.web.bind.annotation.*;

// Importamos List
import java.util.List;

// Este controlador recibe las peticiones HTTP
@RestController

// Ruta base del controlador
@RequestMapping("/api/productos")
public class ProductoController {

    // Variable para usar el service
    private final ProductoService productoService;

    // Constructor
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // GET: listar todos los productos
    @GetMapping
    public ResponseEntity<?> listarProductos() {
        try {
            List<Producto> productos = productoService.listarProductos();
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al listar productos");
        }
    }

    // GET: buscar un producto por id
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        try {
            Producto producto = productoService.buscarPorId(id);

            if (producto == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Producto no encontrado");
            }

            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al buscar el producto");
        }
    }

    // POST: guardar producto
    @PostMapping
    public ResponseEntity<?> guardarProducto(@RequestBody Producto producto) {
        try {
            if (producto.getId() == null || producto.getNombre() == null || producto.getPrecio() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Todos los campos son obligatorios");
            }

            if (productoService.existePorId(producto.getId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Ya existe un producto con ese id");
            }

            Producto nuevoProducto = productoService.guardarProducto(producto);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(nuevoProducto);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar el producto");
        }
    }

    // DELETE: eliminar producto
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Integer id) {
        try {
            if (!productoService.existePorId(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Producto no encontrado");
            }

            productoService.eliminarProducto(id);

            return ResponseEntity.ok("Producto eliminado correctamente");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el producto");
        }
    }
}
