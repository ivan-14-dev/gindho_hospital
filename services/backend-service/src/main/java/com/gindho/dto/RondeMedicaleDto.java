package com.gindho.dto;
import lombok.*; import java.time.LocalDateTime;
@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class RondeMedicaleDto {
    private Long id; private String typeRonde; private LocalDateTime dateDebut; private LocalDateTime dateFin;
    private boolean validee; private String compteRendu; private Long medecinId; private String medecinNom;
}