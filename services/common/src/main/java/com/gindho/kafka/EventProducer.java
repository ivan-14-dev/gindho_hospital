package com.gindho.kafka;
import lombok.RequiredArgsConstructor; import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate; import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component; import java.util.concurrent.CompletableFuture;
@Slf4j @Component @RequiredArgsConstructor
public class EventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    public void publish(String topic, BaseEvent event) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, event.getEventId(), event);
        future.whenComplete((r, ex) -> { if (ex == null) log.debug("Published to {} id={}", topic, event.getEventId());
            else log.error("Failed publish to {} id={}", topic, event.getEventId(), ex); });
    }
}