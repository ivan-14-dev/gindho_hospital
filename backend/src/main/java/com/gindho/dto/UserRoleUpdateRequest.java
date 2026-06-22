package com.gindho.dto;

//
// IMPORTANT (FR)
// DTO de requête pour changer le rôle d’un utilisateur (ADMIN).
// Utilisé avec : PUT /api/admin/users/{id}/role
//
// Exemples (FR) :
// curl -s -X PUT -H "Content-Type: application/json" \
//   -H "Authorization: Bearer <TOKEN>" \
//   -d '{"role":"PHARMACIST"}' \
//   http://localhost:8080/api/admin/users/8/role
//
public class UserRoleUpdateRequest {
    private String role;

    public UserRoleUpdateRequest() {}

    public UserRoleUpdateRequest(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
