package com.gindho.model;
import jakarta.persistence.*; import lombok.*;
import java.time.LocalDate; import java.time.LocalTime;
@Entity @Table(name = "presences")
@Data @Builder @EqualsAndHashCode(callSuper=true) @NoArgsConstructor @AllArgsConstructor
public class Presence extends BaseEntity {
    @Column(nullable=false) private LocalDate date;
    private LocalTime heureArrivee;
    private LocalTime heureDepart;
    @Column(nullable=false) private boolean present = false;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="personnel_id", nullable=false)
    private Personnel personnel;
}