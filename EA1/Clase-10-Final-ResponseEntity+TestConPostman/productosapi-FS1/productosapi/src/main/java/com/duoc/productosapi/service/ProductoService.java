package com.duoc.productosapi.service;

// Importamos el modelo
import com.duoc.productosapi.model.Producto;

// Importamos el repository
import com.duoc.productosapi.repository.ProductoRepository;

// Importamos Service
import org.springframework.stereotype.Service;

// 1 Importamos Optional y List
import java.util.List;
import java.util.Optional;

// 2 Esta clase contiene la lógica del negocio
@Service
public class ProductoService {

    // 3 Variable para usar el repository
    private final ProductoRepository productoRepository;

    // 4 Constructor
    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // 5 Lista todos los productos
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    // 6 Busca un producto por id
    public Producto buscarPorId(Integer id) {
        Optional<Producto> producto = productoRepository.findById(id);

        //7  Si existe, lo devuelve; si no, devuelve null
        return producto.orElse(null);
    }

    // 7 Guarda un producto
    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    // 8 Verifica si existe un producto
    public boolean existePorId(Integer id) {
        return productoRepository.existsById(id);
    }

    // 9 Elimina un producto por id
    public void eliminarProducto(Integer id) {
        productoRepository.deleteById(id);
    }
}
