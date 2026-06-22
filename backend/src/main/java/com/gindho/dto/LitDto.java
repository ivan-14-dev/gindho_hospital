package com.gindho.dto;

import com.gindho.model.Lit;

public class LitDto {
    private Long id;

    private String numeroLit;
    private boolean actif = true;

    private Lit.StatutLit statut = Lit.StatutLit.DISPONIBLE;

    private Long chambreId;
    private String chambreNumero;

    public LitDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroLit() {
        return numeroLit;
    }

    public void setNumeroLit(String numeroLit) {
        this.numeroLit = numeroLit;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public Lit.StatutLit getStatut() {
        return statut;
    }

    public void setStatut(Lit.StatutLit statut) {
        this.statut = statut;
    }

    public Long getChambreId() {
        return chambreId;
    }

    public void setChambreId(Long chambreId) {
        this.chambreId = chambreId;
    }

    public String getChambreNumero() {
        return chambreNumero;
    }

    public void setChambreNumero(String chambreNumero) {
        this.chambreNumero = chambreNumero;
    }
}
