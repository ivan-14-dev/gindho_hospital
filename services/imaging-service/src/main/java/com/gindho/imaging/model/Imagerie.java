package com.gindho.imaging.model;
import com.gindho.base.BaseEntity; import jakarta.persistence.*; import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name = "imagerie") @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Imagerie extends BaseEntity {
    private Long patientId; private Long medecinId;
    private String typeExamen; // RADIOGRAPHIE, IRM, SCANNER, ECHOGRAPHIE, MAMMOGRAPHIE
    private LocalDateTime datePrescription; private LocalDateTime dateRealisation;
    @Column(columnDefinition = "TEXT") private String compteRendu;
    private String lienDicom; private boolean dicomDisponible;
    @Enumerated(EnumType.STRING) private StatutImagerie statut = StatutImagerie.PRESCRIT;
    public enum StatutImagerie { PRESCRIT, PROGRAMME, REALISE, VALIDE }
}