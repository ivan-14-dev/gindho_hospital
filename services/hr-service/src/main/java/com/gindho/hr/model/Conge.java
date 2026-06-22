package com.gindho.hr.model;
import com.gindho.base.BaseEntity; import jakarta.persistence.*; import lombok.*;
import java.time.LocalDate;
@Entity @Table(name = "conges") @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Conge extends BaseEntity {
    private Long employeId; private LocalDate dateDebut; private LocalDate dateFin;
    @Enumerated(EnumType.STRING) private TypeConge type;
    @Enumerated(EnumType.STRING) private StatutConge statut = StatutConge.EN_ATTENTE;
    private String motif;
    public enum TypeConge { ANNUEL, MALADIE, FORMATION, EXCEPTIONNEL }
    public enum StatutConge { EN_ATTENTE, APPROUVE, REFUSE }
}