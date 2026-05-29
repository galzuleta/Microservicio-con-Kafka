package com.examen.productos.kafka;

import com.examen.productos.service.ProductoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockConsumer {

    private final ProductoService productoService;
    private final ObjectMapper objectMapper;

    /**
     * BONUS: Consume pedidos-topic para descontar stock automáticamente
     */
    @KafkaListener(topics = "pedidos-topic", groupId = "productos-group")
    public void consumirPedido(String mensaje) {
        try {
            log.info("📦 [productos-service] Evento recibido para descontar stock: {}", mensaje);
            Map<?, ?> pedido = objectMapper.readValue(mensaje, Map.class);
            Long productoId = Long.valueOf(pedido.get("productoId").toString());
            int cantidad = Integer.parseInt(pedido.get("cantidad").toString());

            productoService.descontarStock(productoId, cantidad);
        } catch (Exception e) {
            log.error("❌ Error al descontar stock: {}", e.getMessage());
        }
    }
}
