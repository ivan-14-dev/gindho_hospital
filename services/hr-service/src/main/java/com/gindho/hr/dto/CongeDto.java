package com.gindho.hr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CongeDto {
    private Long id;
    private Long employeId;
    private java.time.LocalDate dateDebut;
    private java.time.LocalDate dateFin;
    private String type;
    private String statut;
    private String motif;
}
