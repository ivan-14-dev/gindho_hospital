package com.gindho.identity.dto;

import com.gindho.identity.model.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoleUpdateRequest {
    @NotNull(message = "role obligatoire")
    private Role role;
}
