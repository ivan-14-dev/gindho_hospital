package com.gindho.surgery.dto; import lombok.*;
import java.time.LocalDateTime;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class InterventionDto {
    private Long id; private Long patientId; private Long medecinId;
    private String salle; private LocalDateTime dateProgrammee;
    private String description; private String statut; private String equipe;
}