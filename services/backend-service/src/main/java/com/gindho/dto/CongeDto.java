package com.gindho.dto;
import lombok.*; import java.time.LocalDate;
@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class CongeDto {
    private Long id; private LocalDate dateDebut; private LocalDate dateFin;
    private String typeConge; private String motif; private boolean valide;
    private Long personnelId; private String personnelNom;
}