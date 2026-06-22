package com.gindho.dto;
import lombok.*; import java.math.BigDecimal; import java.time.LocalDate;
@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class PersonnelDto {
    private Long id; private String nom; private String prenom; private String email;
    private String telephone; private String poste; private String departement;
    private LocalDate dateEmbauche; private BigDecimal salaireBase; private boolean actif;
    private Long userId;
}