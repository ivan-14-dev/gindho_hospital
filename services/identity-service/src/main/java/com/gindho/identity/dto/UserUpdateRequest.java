package com.gindho.identity.dto;

import com.gindho.identity.model.Role;
import lombok.Data;

@Data
public class UserUpdateRequest {
    private String nom;
    private String prenom;
    private String email;
    private Role role;
    private Boolean actif;
}
