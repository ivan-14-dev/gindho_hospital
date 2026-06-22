package com.gindho.dto;
import lombok.*; import java.time.LocalDateTime;
@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class ProgrammeOperatoireDto {
    private Long id; private String salle; private LocalDateTime dateDebut; private LocalDateTime dateFin;
    private String intervention; private String statut;
    private Long patientId; private String patientNom;
    private Long chirurgienId; private String chirurgienNom;
    private Long anesthesisteId; private String anesthesisteNom;
}