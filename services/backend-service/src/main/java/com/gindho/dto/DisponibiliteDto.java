package com.gindho.dto;

import java.time.LocalDateTime;

import com.gindho.model.Disponibilite;

public class DisponibiliteDto {
    private Long id;
    private Long medecinId;
    private Disponibilite.JourSemaine jour;
    private LocalDateTime heureDebut;
    private LocalDateTime heureFin;
    private boolean actif = true;

    public DisponibiliteDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMedecinId() {
        return medecinId;
    }

    public void setMedecinId(Long medecinId) {
        this.medecinId = medecinId;
    }

    public Disponibilite.JourSemaine getJour() {
        return jour;
    }

    public void setJour(Disponibilite.JourSemaine jour) {
        this.jour = jour;
    }

    public LocalDateTime getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(LocalDateTime heureDebut) {
        this.heureDebut = heureDebut;
    }

    public LocalDateTime getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(LocalDateTime heureFin) {
        this.heureFin = heureFin;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }
}
