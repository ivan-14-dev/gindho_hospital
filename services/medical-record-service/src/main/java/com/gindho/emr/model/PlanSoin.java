package com.gindho.emr.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "plans_soin")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanSoin extends BaseEntity {
    private Long patientId;
    private Long hospitalisationId;
    private String intitule;
    @Column(columnDefinition = "TEXT")
    private String description;
    private LocalDateTime datePrevu;
    private boolean realise;
    private LocalDateTime dateRealisation;
    @Column(columnDefinition = "TEXT")
    private String notesRealisation;
}
