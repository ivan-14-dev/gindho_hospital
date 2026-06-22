package com.gindho.authorization.dto;

public class PermissionCatalogEntry {
    private String module;
    private String code;

    public PermissionCatalogEntry() {}

    public PermissionCatalogEntry(String module, String code) {
        this.module = module;
        this.code = code;
    }

    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
