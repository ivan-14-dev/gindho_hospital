package com.gindho.round.model;
import com.gindho.base.BaseEntity; import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="participants_ronde") @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ParticipantRonde extends BaseEntity {
    private Long rondeId; private Long employeId; private String role; private boolean present;
}