package com.duoc.ms_pedidos.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.duoc.ms_pedidos.model.Pedido;

@Repository
public class PedidoRepository {

    private final List<Pedido> pedidos = new ArrayList<>(List.of(
            new Pedido(1L, 1L, 2),
            new Pedido(2L, 3L, 1)
    ));

    private final AtomicLong secuencia = new AtomicLong(2);

    public List<Pedido> findAll() {
        return pedidos;
    }

    public Optional<Pedido> findById(Long id) {
        return pedidos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public Pedido save(Long productoId, int cantidad) {
        Pedido pedido = new Pedido(secuencia.incrementAndGet(), productoId, cantidad);
        pedidos.add(pedido);
        return pedido;
    }
}
