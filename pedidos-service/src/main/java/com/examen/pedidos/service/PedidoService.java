package com.examen.pedidos.service;

import com.examen.pedidos.kafka.PedidoProducer;
import com.examen.pedidos.model.Pedido;
import com.examen.pedidos.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoProducer pedidoProducer;

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public List<Pedido> buscarPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    @Transactional
    public Pedido registrarPedido(Pedido pedido) {
        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        log.info("📋 Pedido registrado con ID: {}", pedidoGuardado.getId());

        // Publicar evento en Kafka
        pedidoProducer.publicarPedido(pedidoGuardado);

        return pedidoGuardado;
    }

    @Transactional
    public Pedido actualizarEstado(Long id, Pedido.EstadoPedido nuevoEstado) {
        return pedidoRepository.findById(id).map(p -> {
            p.setEstado(nuevoEstado);
            return pedidoRepository.save(p);
        }).orElseThrow(() -> new RuntimeException("Pedido no encontrado con id: " + id));
    }
}
