package com.gindho.notification.model;
import com.gindho.base.BaseEntity; import jakarta.persistence.*; import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name = "notifications") @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Notification extends BaseEntity {
    private Long userId; private String destinataire; private String sujet;
    @Column(columnDefinition = "TEXT") private String contenu;
    @Enumerated(EnumType.STRING) private CanalNotification canal;
    @Enumerated(EnumType.STRING) private StatutNotification statut = StatutNotification.EN_ATTENTE;
    private LocalDateTime dateEnvoi;
    public enum CanalNotification { EMAIL, SMS, WHATSAPP, PUSH }
    public enum StatutNotification { EN_ATTENTE, ENVOYE, ECHEC }
}