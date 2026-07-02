package com.gindho.chat.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Conversation extends BaseEntity {
    @Column(nullable = false)
    private Long patientId;
    @Column(nullable = false)
    private Long medecinId;
    @Column(nullable = false)
    private String sujet;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatutConversation statut = StatutConversation.ACTIVE;
    @Column
    private LocalDateTime derniereActivite;
}
