package com.gindho.dto;

import java.util.List;

public class UserPermissionsUpdateRequest {

    private List<String> permissions;

    public UserPermissionsUpdateRequest() {
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
