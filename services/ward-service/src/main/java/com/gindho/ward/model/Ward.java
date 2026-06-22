package com.gindho.ward.model;
import com.gindho.base.BaseEntity; import jakarta.persistence.*; import lombok.*;
@Entity @Table(name = "wards") @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Ward extends BaseEntity {
    private String code; private String nom; private String specialite;
    private int capaciteLits; private String chefService; private String telephone;
}