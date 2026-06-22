package com.gindho.authorization.dto;

import java.time.LocalDateTime;
import java.util.List;

public class RoleTemplateDto {
    private Long id;
    private String name;
    private String description;
    private List<String> permissions;
    private LocalDateTime creeLe;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getPermissions() { return permissions; }
    public void setPermissions(List<String> permissions) { this.permissions = permissions; }
    public LocalDateTime getCreeLe() { return creeLe; }
    public void setCreeLe(LocalDateTime creeLe) { this.creeLe = creeLe; }
}
