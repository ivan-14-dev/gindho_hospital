package com.gindho.emr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObservationDto {
    private Long id;
    private Long consultationId;
    private String type;
    private String contenu;
}
