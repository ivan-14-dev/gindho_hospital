package com.gindho.dto;

import java.util.List;

public class RoleTemplateCreateRequest {

    private String name;

    // Permissions au format attendu par DynamicPermissionFilter / CustomUserDetailsService
    // ex: ["PATIENTS:READ", "FACTURES:WRITE"] ou ["patients:read"]
    private List<String> permissions;

    public RoleTemplateCreateRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
