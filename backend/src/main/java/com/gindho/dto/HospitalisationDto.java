package com.gindho.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.gindho.model.Hospitalisation;

public class HospitalisationDto {
    private Long id;

    private Long patientId;
    private String patientNom;

    private Long litId;
    private String litNumero;

    private String chambreNumero;

    private LocalDateTime dateAdmission;
    private LocalDateTime dateSortie;

    private Hospitalisation.StatutHospitalisation statut;

    private String motifAdmission;

    private String diagnostic;
    private String traitement;
    private String observations;

    private String rapportSortie;
    private LocalDate dateRapportSortie;

    public HospitalisationDto() {}

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

    public String getPatientNom() {
        return patientNom;
    }

    public void setPatientNom(String patientNom) {
        this.patientNom = patientNom;
    }

    public Long getLitId() {
        return litId;
    }

    public void setLitId(Long litId) {
        this.litId = litId;
    }

    public String getLitNumero() {
        return litNumero;
    }

    public void setLitNumero(String litNumero) {
        this.litNumero = litNumero;
    }

    public String getChambreNumero() {
        return chambreNumero;
    }

    public void setChambreNumero(String chambreNumero) {
        this.chambreNumero = chambreNumero;
    }

    public LocalDateTime getDateAdmission() {
        return dateAdmission;
    }

    public void setDateAdmission(LocalDateTime dateAdmission) {
        this.dateAdmission = dateAdmission;
    }

    public LocalDateTime getDateSortie() {
        return dateSortie;
    }

    public void setDateSortie(LocalDateTime dateSortie) {
        this.dateSortie = dateSortie;
    }

    public Hospitalisation.StatutHospitalisation getStatut() {
        return statut;
    }

    public void setStatut(Hospitalisation.StatutHospitalisation statut) {
        this.statut = statut;
    }

    public String getMotifAdmission() {
        return motifAdmission;
    }

    public void setMotifAdmission(String motifAdmission) {
        this.motifAdmission = motifAdmission;
    }

    public String getDiagnostic() {
        return diagnostic;
    }

    public void setDiagnostic(String diagnostic) {
        this.diagnostic = diagnostic;
    }

    public String getTraitement() {
        return traitement;
    }

    public void setTraitement(String traitement) {
        this.traitement = traitement;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getRapportSortie() {
        return rapportSortie;
    }

    public void setRapportSortie(String rapportSortie) {
        this.rapportSortie = rapportSortie;
    }

    public LocalDate getDateRapportSortie() {
        return dateRapportSortie;
    }

    public void setDateRapportSortie(LocalDate dateRapportSortie) {
        this.dateRapportSortie = dateRapportSortie;
    }
}
