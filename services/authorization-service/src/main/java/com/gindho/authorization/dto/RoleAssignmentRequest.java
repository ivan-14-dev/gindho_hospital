package com.gindho.authorization.dto;

public class RoleAssignmentRequest {
    private String role;
    private String expiresAt;

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getExpiresAt() { return expiresAt; }
    public void setExpiresAt(String expiresAt) { this.expiresAt = expiresAt; }
}
