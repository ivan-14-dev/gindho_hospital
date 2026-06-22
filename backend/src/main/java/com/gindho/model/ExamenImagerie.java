package com.gindho.model;
import jakarta.persistence.*; import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "examens_imagerie")
@Data @Builder @EqualsAndHashCode(callSuper=true) @NoArgsConstructor @AllArgsConstructor
public class ExamenImagerie extends BaseEntity {
    @Column(nullable=false, length=100) private String typeExamen; // RADIOGRAPHIE, SCANNER, IRM, ECHOGRAPHIE
    @Column(nullable=false) private LocalDateTime dateExamen;
    @Column(length=2000) private String compteRendu;
    @Column(length=500) private String fichierDicom; // URL/chemin vers image DICOM
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="patient_id", nullable=false)
    private Patient patient;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="medecin_id", nullable=false)
    private Medecin medecin;
}