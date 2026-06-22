package com.gindho.round.model;
import com.gindho.base.BaseEntity; import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="checklists_ronde") @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ChecklistRonde extends BaseEntity {
    private Long rondeId; private String element; private boolean effectue;
    private Long validePar; private String observations;
}