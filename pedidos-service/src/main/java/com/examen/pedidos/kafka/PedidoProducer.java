package com.examen.pedidos.kafka;

import com.examen.pedidos.model.Pedido;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class PedidoProducer {

    private static final String TOPIC = "pedidos-topic";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publicarPedido(Pedido pedido) {
        try {
            String mensaje = objectMapper.writeValueAsString(pedido);
            CompletableFuture<SendResult<String, String>> future =
                    kafkaTemplate.send(TOPIC, String.valueOf(pedido.getId()), mensaje);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("✅ Evento publicado en Kafka | Topic: {} | Pedido ID: {} | Offset: {}",
                            TOPIC, pedido.getId(), result.getRecordMetadata().offset());
                } else {
                    log.error("❌ Error al publicar evento en Kafka para Pedido ID: {}", pedido.getId(), ex);
                }
            });
        } catch (JsonProcessingException e) {
            log.error("❌ Error al serializar pedido: {}", e.getMessage());
            throw new RuntimeException("Error al publicar evento de pedido", e);
        }
    }
}
