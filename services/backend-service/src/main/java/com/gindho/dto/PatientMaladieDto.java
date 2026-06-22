package com.gindho.dto;

import java.time.LocalDate;

public class PatientMaladieDto {

    private Long id;

    private Long patientId;
    private Long maladieId;

    private LocalDate dateDiagnostic;
    private String methode;
    private boolean actif = true;

    public PatientMaladieDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getMaladieId() {
        return maladieId;
    }

    public void setMaladieId(Long maladieId) {
        this.maladieId = maladieId;
    }

    public LocalDate getDateDiagnostic() {
        return dateDiagnostic;
    }

    public void setDateDiagnostic(LocalDate dateDiagnostic) {
        this.dateDiagnostic = dateDiagnostic;
    }

    public String getMethode() {
        return methode;
    }

    public void setMethode(String methode) {
        this.methode = methode;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }
}
