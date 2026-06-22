package com.gindho.dto;
import lombok.*; import java.time.LocalDate; import java.time.LocalTime;
@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class PresenceDto {
    private Long id; private LocalDate date; private LocalTime heureArrivee; private LocalTime heureDepart;
    private boolean present; private Long personnelId; private String personnelNom;
}