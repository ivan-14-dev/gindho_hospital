package com.gindho.dto;

//
// IMPORTANT (FR)
// DTO de requête pour activer / désactiver un utilisateur (ADMIN).
// Utilisé avec : PUT /api/admin/users/{id}/active
//
// Exemple (FR) :
// curl -s -X PUT -H "Content-Type: application/json" \
//   -H "Authorization: Bearer <TOKEN>" \
//   -d '{"actif":false}' \
//   http://localhost:9001/api/admin/users/8/active
//
public class UserActiveUpdateRequest {
    private boolean actif;

    public UserActiveUpdateRequest() {}

    public UserActiveUpdateRequest(boolean actif) {
        this.actif = actif;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }
}
