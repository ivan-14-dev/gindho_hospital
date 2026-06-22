package com.gindho.dto;
import lombok.*; import java.time.LocalDateTime;
@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class IncidentDto {
    private Long id; private String titre; private String description;
    private String typeIncident; private String gravite; private LocalDateTime dateDeclaration;
    private String actionCorrective; private boolean resolu;
}