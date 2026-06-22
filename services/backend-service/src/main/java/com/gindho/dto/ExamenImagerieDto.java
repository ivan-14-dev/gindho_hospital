package com.gindho.dto;
import lombok.*; import java.time.LocalDateTime;
@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class ExamenImagerieDto {
    private Long id; private String typeExamen; private LocalDateTime dateExamen;
    private String compteRendu; private String fichierDicom;
    private Long patientId; private String patientNom;
    private Long medecinId; private String medecinNom;
}