package com.gindho.bed.dto; import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ChambreDto { private Long id; private String code; private String nom;
    private String etage; private String aile; private int capacite; private String type;
    private long litsOccupes; private long litsTotal; }