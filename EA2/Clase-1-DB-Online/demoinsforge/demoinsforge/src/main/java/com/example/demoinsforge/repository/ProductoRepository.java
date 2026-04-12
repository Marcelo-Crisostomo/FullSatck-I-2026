package com.example.demoinsforge.repository;

import com.example.demoinsforge.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByActivo(Boolean activo);

    List<Producto> findByNombreContaining(String nombre);
}
