package com.gindho.dto;

import java.time.LocalDateTime;

public class PermissionSpecDto {

    // Format permission "libre" compatible avec CustomUserDetailsService / DynamicPermissionFilter
    // ex: "PATIENTS:READ" ou "patients:read"
    private String permission;

    // Format optionnel ressource/action
    private String ressource;
    private String action;

    // Validité temporelle (optionnel)
    private LocalDateTime validFrom;
    private LocalDateTime validTo;

    // Scope / conditions (optionnel, pour phase suivante)
    private String scope;
    private String conditionType;

    public PermissionSpecDto() {
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getRessource() {
        return ressource;
    }

    public void setRessource(String ressource) {
        this.ressource = ressource;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getConditionType() {
        return conditionType;
    }

    public void setConditionType(String conditionType) {
        this.conditionType = conditionType;
    }
}
