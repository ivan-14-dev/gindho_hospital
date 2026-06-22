package com.gindho.authorization.controller;

import com.gindho.authorization.dto.AdminUserDto;
import com.gindho.authorization.dto.PermissionCatalogEntry;
import com.gindho.authorization.dto.PermissionRemoveRequest;
import com.gindho.authorization.dto.RoleAssignmentRequest;
import com.gindho.authorization.dto.RoleDto;
import com.gindho.authorization.dto.RolePermissionRequest;
import com.gindho.authorization.dto.RoleTemplateCreateRequest;
import com.gindho.authorization.dto.RoleTemplateDto;
import com.gindho.authorization.dto.UserActiveUpdateRequest;
import com.gindho.authorization.dto.UserPermissionsUpdateRequest;
import com.gindho.authorization.dto.UserRoleUpdateRequest;
import com.gindho.authorization.model.Permission;
import com.gindho.authorization.model.Role;
import com.gindho.authorization.model.UserRole;
import com.gindho.authorization.service.AuthorizationService;
import com.gindho.base.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthorizationController {

    private final AuthorizationService authService;

    @PostMapping("/authorize")
    public ResponseEntity<ApiResponse<Boolean>> authorize(@RequestBody com.gindho.authorization.model.AuthorizeRequest request) {
        return ResponseEntity.ok(ApiResponse.of(authService.hasPermission(request.userId(), request.permission())));
    }

    @GetMapping("/authorization/check")
    public ResponseEntity<ApiResponse<Boolean>> check(@RequestParam String permission,
                                                       @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(ApiResponse.of(authService.hasPermission(userId, permission)));
    }

    @GetMapping("/authorization/permissions")
    public ResponseEntity<ApiResponse<List<Permission>>> getPermissions(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ApiResponse.of(authService.getUserPermissions(jwt.getSubject())));
    }

    @GetMapping("/authorization/roles")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Role>>> getRoles() {
        return ResponseEntity.ok(ApiResponse.of(authService.getAllRoles()));
    }

    @GetMapping("/authorization/roles/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Role>> getRole(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.of(authService.getRoleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role introuvable"))));
    }

    @PutMapping("/authorization/roles/{id}/permissions")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> addPermissionsToRole(@PathVariable Long id,
                                                                   @RequestBody RolePermissionRequest request) {
        authService.addPermissionsToRole(id, request.getPermissions());
        return ResponseEntity.ok(ApiResponse.of(null));
    }

    @DeleteMapping("/authorization/roles/{id}/permissions")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> removePermissionFromRole(@PathVariable Long id,
                                                                      @RequestBody PermissionRemoveRequest request) {
        authService.removePermissionFromRole(id, request.getPermission());
        return ResponseEntity.ok(ApiResponse.of(null));
    }

    @GetMapping("/authorization/users/{userId}/permissions")
    public ResponseEntity<ApiResponse<List<String>>> getUserPermissions(@AuthenticationPrincipal Jwt jwt,
                                                                         @PathVariable String userId) {
        List<Permission> perms = authService.getUserEffectivePermissions(userId);
        List<String> codes = perms.stream().map(Permission::getCode).toList();
        return ResponseEntity.ok(ApiResponse.of(codes));
    }

    @PostMapping("/authorization/users/{userId}/roles")
    public ResponseEntity<ApiResponse<UserRole>> assignRole(@PathVariable String userId,
                                                            @RequestBody RoleAssignmentRequest request) {
        if (request.getRole() == null || request.getRole().isBlank()) {
            throw new IllegalArgumentException("Role obligatoire");
        }
        Role roleEntity = authService.getAllRoles().stream()
                .filter(r -> r.getCode().equalsIgnoreCase(request.getRole().trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Role introuvable: " + request.getRole()));
        java.time.LocalDateTime expiresAt = null;
        if (request.getExpiresAt() != null && !request.getExpiresAt().isBlank()) {
            expiresAt = java.time.LocalDateTime.parse(request.getExpiresAt().trim());
        }
        UserRole ur = authService.assignRoleWithExpiry(userId, roleEntity.getId(), expiresAt);
        return ResponseEntity.ok(ApiResponse.of(ur));
    }

    @DeleteMapping("/authorization/users/{userId}/roles/{roleId}")
    public ResponseEntity<ApiResponse<Void>> removeRole(@PathVariable String userId,
                                                        @PathVariable Long roleId) {
        authService.removeRoleFromUser(userId, roleId);
        return ResponseEntity.ok(ApiResponse.of(null));
    }

    @GetMapping("/authorization/role-templates")
    public ResponseEntity<ApiResponse<List<RoleTemplateDto>>> listTemplates() {
        return ResponseEntity.ok(ApiResponse.of(authService.getAllTemplates()));
    }

    @PostMapping("/authorization/role-templates")
    public ResponseEntity<ApiResponse<RoleTemplateDto>> createTemplate(@RequestBody RoleTemplateCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.of(authService.createTemplate(request)));
    }

    @GetMapping("/authorization/permissions/catalog")
    public ResponseEntity<ApiResponse<List<PermissionCatalogEntry>>> permissionCatalog() {
        return ResponseEntity.ok(ApiResponse.of(authService.getPermissionCatalogGrouped()));
    }

    @PostMapping("/authorization/roles")
    public ResponseEntity<ApiResponse<Role>> createRole(@RequestBody Role role) {
        return ResponseEntity.ok(ApiResponse.of(authService.createRole(role)));
    }

    @GetMapping("/authorization/permissions/all")
    public ResponseEntity<ApiResponse<List<Permission>>> allPermissions() {
        return ResponseEntity.ok(ApiResponse.of(authService.getAllPermissions()));
    }

    @PostMapping("/authorization/permissions")
    public ResponseEntity<ApiResponse<Permission>> createPermission(@RequestBody Permission perm) {
        return ResponseEntity.ok(ApiResponse.of(authService.createPermission(perm)));
    }

    @PostMapping("/authorization/assign")
    public ResponseEntity<ApiResponse<UserRole>> assignRoleLegacy(@RequestBody UserRole ur) {
        return ResponseEntity.ok(ApiResponse.of(authService.assignRole(ur)));
    }

    @GetMapping("/authorization/users/{userId}/roles")
    public ResponseEntity<ApiResponse<List<UserRole>>> getUserRoles(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.of(authService.getUserRoles(userId)));
    }

    @GetMapping("/admin/permissions/catalog")
    public ResponseEntity<ApiResponse<List<String>>> permissionCatalogLegacy() {
        return ResponseEntity.ok(ApiResponse.of(authService.getPermissionCatalog()));
    }

    @PostMapping("/admin/role-templates")
    public ResponseEntity<ApiResponse<RoleTemplateDto>> createTemplateLegacy(@RequestBody RoleTemplateCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.of(authService.createTemplate(request)));
    }

    @PutMapping("/admin/role-templates/{templateId}/apply/{userId}")
    public ResponseEntity<ApiResponse<AdminUserDto>> applyTemplate(
            @PathVariable Long templateId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.of(authService.applyTemplateToUser(templateId, userId)));
    }

    @GetMapping("/admin/users")
    public ResponseEntity<ApiResponse<List<AdminUserDto>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.of(authService.getAllUsers()));
    }

    @PutMapping("/admin/users/{id}/role")
    public ResponseEntity<ApiResponse<AdminUserDto>> updateRole(
            @PathVariable Long id,
            @RequestBody UserRoleUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.of(authService.changeRole(id, request)));
    }

    @PutMapping("/admin/users/{id}/active")
    public ResponseEntity<ApiResponse<AdminUserDto>> updateActive(
            @PathVariable Long id,
            @RequestBody UserActiveUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.of(authService.setActive(id, request)));
    }

    @PutMapping("/admin/users/{id}/permissions")
    public ResponseEntity<ApiResponse<AdminUserDto>> updatePermissions(
            @PathVariable Long id,
            @RequestBody UserPermissionsUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.of(authService.updatePermissions(id, request)));
    }
}
