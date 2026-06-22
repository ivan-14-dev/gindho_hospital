package com.gindho.dto;
import lombok.*; import java.time.LocalDate;
@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class AuditQualiteDto {
    private Long id; private String titre; private String description;
    private String typeAudit; private LocalDate dateAudit;
    private int score; private String recommandations;
}