package com.gindho.identity.dto;

import com.gindho.identity.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUserDto {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private Role role;
    private Boolean actif;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
}
