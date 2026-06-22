package com.gindho.outgoing.dto;
import lombok.*;
import java.time.DayOfWeek; import java.time.LocalTime;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MedecinDisponibiliteDto {
    private Long id; private Long medecinId; private String medecinNom;
    private DayOfWeek jourSemaine; private LocalTime heureDebut; private LocalTime heureFin;
    private int dureeConsultationMinutes; private String periode; private String notes; private boolean actif;
}
