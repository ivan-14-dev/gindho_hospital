package com.gindho.kafka;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Base64;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(exclude = "payload")
public class BaseEvent {
    private String eventId = UUID.randomUUID().toString();
    private String eventType;
    private String source;
    private LocalDateTime timestamp = LocalDateTime.now();
    private Object payload;

    public String toBase64Payload() {
        if (payload == null) return null;
        return Base64.getEncoder().encodeToString(payload.toString().getBytes());
    }
}
