package com.gindho.scheduling.model;
import com.gindho.base.BaseEntity; import jakarta.persistence.*; import lombok.*;
import java.time.LocalDate;
@Entity @Table(name="absences") @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Absence extends BaseEntity {
    private Long employeId; private LocalDate dateDebut; private LocalDate dateFin;
    private String motif; @Enumerated(EnumType.STRING) private TypeAbsence typeAbsence;
    private Long approuvePar; private boolean valide;
}