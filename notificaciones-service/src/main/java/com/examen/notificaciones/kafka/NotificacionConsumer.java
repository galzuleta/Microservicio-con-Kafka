package com.examen.notificaciones.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificacionConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "pedidos-topic", groupId = "notificaciones-group")
    public void consumirPedido(ConsumerRecord<String, String> record) {
        try {
            log.info("═══════════════════════════════════════════════");
            log.info("🔔 [notificaciones-service] Evento recibido de Kafka");
            log.info("   Topic    : {}", record.topic());
            log.info("   Partition: {}", record.partition());
            log.info("   Offset   : {}", record.offset());

            Map<?, ?> pedido = objectMapper.readValue(record.value(), Map.class);

            Long pedidoId    = Long.valueOf(pedido.get("id").toString());
            Long clienteId   = Long.valueOf(pedido.get("clienteId").toString());
            Long productoId  = Long.valueOf(pedido.get("productoId").toString());
            int  cantidad    = Integer.parseInt(pedido.get("cantidad").toString());
            String estado    = pedido.get("estado").toString();

            log.info("═══════════════════════════════════════════════");
            log.info("✅ Pedido recibido correctamente");
            log.info("   Pedido ID  : {}", pedidoId);
            log.info("   Cliente ID : {}", clienteId);
            log.info("   Producto ID: {}", productoId);
            log.info("   Cantidad   : {}", cantidad);
            log.info("   Estado     : {}", estado);
            log.info("═══════════════════════════════════════════════");

        } catch (Exception e) {
            log.error("❌ Error al procesar notificación: {}", e.getMessage());
        }
    }
}
