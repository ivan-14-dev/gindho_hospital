package com.gindho.bed.model;
import com.gindho.base.BaseEntity; import jakarta.persistence.*; import lombok.*;
@Entity @Table(name = "chambres") @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Chambre extends BaseEntity {
    private String code; private String nom; private String etage; private String aile;
    private int capacite; @Enumerated(EnumType.STRING) private TypeChambre type;
    public enum TypeChambre { INDIVIDUELLE, DOUBLE, COLLECTIVE, SOINS_INTENSIFS, ISOLEMENT }
}