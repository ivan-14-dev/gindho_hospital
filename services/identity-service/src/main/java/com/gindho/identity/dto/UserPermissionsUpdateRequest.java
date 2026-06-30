package com.gindho.identity.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UserPermissionsUpdateRequest {
    @NotNull(message = "permissions obligatoires")
    private List<String> permissions;
}
