package com.gindho.chat.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Message extends BaseEntity {
    @Column(nullable = false)
    private Long conversationId;
    @Column(nullable = false)
    private Long expediteurId;
    @Column(nullable = false, length = 50)
    private String role;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;
    @Column
    private LocalDateTime dateEnvoi = LocalDateTime.now();
    @Column(nullable = false)
    private boolean lu = false;
}
