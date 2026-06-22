package com.gindho.bed.dto; import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LitDto { private Long id; private Long chambreId; private String code;
    private boolean occupe; private Long patientId; private String observation; }