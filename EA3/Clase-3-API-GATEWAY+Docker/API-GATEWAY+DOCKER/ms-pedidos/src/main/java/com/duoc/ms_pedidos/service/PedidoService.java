package com.duoc.ms_pedidos.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.duoc.ms_pedidos.dto.PedidoResponse;
import com.duoc.ms_pedidos.dto.Producto;
import com.duoc.ms_pedidos.model.Pedido;
import com.duoc.ms_pedidos.repository.PedidoRepository;

@Service
public class PedidoService {

    private final PedidoRepository repository;
    private final WebClient productosWebClient;

    public PedidoService(PedidoRepository repository, WebClient productosWebClient) {
        this.repository = repository;
        this.productosWebClient = productosWebClient;
    }

    public List<PedidoResponse> listar() {
        return repository.findAll().stream().map(this::enriquecer).toList();
    }

    public Optional<PedidoResponse> buscarPorId(Long id) {
        return repository.findById(id).map(this::enriquecer);
    }

    public PedidoResponse crear(Long productoId, int cantidad) {
        Pedido creado = repository.save(productoId, cantidad);
        return enriquecer(creado);
    }

    private PedidoResponse enriquecer(Pedido pedido) {
        Producto producto = productosWebClient.get()
                .uri("/api/productos/{id}", pedido.getProductoId())
                .retrieve()
                .bodyToMono(Producto.class)
                .onErrorReturn(new Producto(pedido.getProductoId(), "No disponible", 0))
                .block();
        return new PedidoResponse(pedido.getId(), pedido.getProductoId(), pedido.getCantidad(), producto);
    }
}
