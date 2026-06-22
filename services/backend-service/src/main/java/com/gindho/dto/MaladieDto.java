package com.gindho.dto;

import java.util.Set;

public class MaladieDto {

    private Long id;
    private String nom;
    private String description;
    private boolean actif = true;

    // Symptômes déclarés (MVP)
    private Set<String> symptomes;

    // Critères d’analyses (MVP)
    private Set<MaladieAnalyseCritereDto> analyseCriteres;

    public MaladieDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public Set<String> getSymptomes() {
        return symptomes;
    }

    public void setSymptomes(Set<String> symptomes) {
        this.symptomes = symptomes;
    }

    public Set<MaladieAnalyseCritereDto> getAnalyseCriteres() {
        return analyseCriteres;
    }

    public void setAnalyseCriteres(Set<MaladieAnalyseCritereDto> analyseCriteres) {
        this.analyseCriteres = analyseCriteres;
    }
}
