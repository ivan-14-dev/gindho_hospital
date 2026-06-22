package com.gindho.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class MaladieAnalyseCritere {

    @Column(name = "type_analyse", nullable = false)
    private String typeAnalyse;

    @Column(name = "resultat_requis", nullable = false)
    private String resultatRequis;

    public MaladieAnalyseCritere() {}

    public MaladieAnalyseCritere(String typeAnalyse, String resultatRequis) {
        this.typeAnalyse = typeAnalyse;
        this.resultatRequis = resultatRequis;
    }

    public String getTypeAnalyse() {
        return typeAnalyse;
    }

    public void setTypeAnalyse(String typeAnalyse) {
        this.typeAnalyse = typeAnalyse;
    }

    public String getResultatRequis() {
        return resultatRequis;
    }

    public void setResultatRequis(String resultatRequis) {
        this.resultatRequis = resultatRequis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MaladieAnalyseCritere that)) return false;
        return Objects.equals(typeAnalyse, that.typeAnalyse)
                && Objects.equals(resultatRequis, that.resultatRequis);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeAnalyse, resultatRequis);
    }
}
