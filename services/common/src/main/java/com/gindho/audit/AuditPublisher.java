package com.gindho.audit;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gindho.kafka.BaseEvent; import com.gindho.kafka.EventProducer;
import lombok.RequiredArgsConstructor; import lombok.extern.slf4j.Slf4j; import org.springframework.stereotype.Component;
@Slf4j @Component @RequiredArgsConstructor
public class AuditPublisher {
    private final EventProducer eventProducer; private final ObjectMapper objectMapper;
    public void publish(AuditEvent e) {
        try { eventProducer.publish("audit", BaseEvent.builder().eventType("AuditLog").source(e.getServiceName()).payload(objectMapper.valueToTree(e)).build()); }
        catch (Exception ex) { log.error("Audit publish failed: {}", ex.getMessage()); }
    }
}