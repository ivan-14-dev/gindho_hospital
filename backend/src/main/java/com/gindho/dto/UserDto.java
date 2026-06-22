package com.gindho.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String role;
    private boolean actif;
    private LocalDateTime creeLe;

    // permissions dynamiques (RolePermission.permission)
    private List<String> permissions;
}
