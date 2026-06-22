package com.gindho.ward.dto; import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class WardDto { private Long id; private String code; private String nom;
    private String specialite; private int capaciteLits; private String chefService;
    private String telephone; }