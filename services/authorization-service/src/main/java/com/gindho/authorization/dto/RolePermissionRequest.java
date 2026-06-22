package com.gindho.authorization.dto;

import java.util.List;

public class RolePermissionRequest {
    private List<String> permissions;

    public List<String> getPermissions() { return permissions; }
    public void setPermissions(List<String> permissions) { this.permissions = permissions; }
}
