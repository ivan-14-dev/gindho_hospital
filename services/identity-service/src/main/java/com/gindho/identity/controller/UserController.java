package com.gindho.identity.controller;

import com.gindho.base.ApiResponse;
import com.gindho.identity.dto.AppUserDto;
import com.gindho.identity.dto.RoleUpdateRequest;
import com.gindho.identity.dto.UserUpdateRequest;
import com.gindho.identity.service.IdentityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gindho.identity.dto.UserPermissionsUpdateRequest;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final IdentityService identityService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Page<AppUserDto>>> listUsers(
            @RequestParam(required = false) String search,
            Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Utilisateurs récupérés", identityService.listUsers(search, pageable)));
    }

    @GetMapping("/doctors")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','MEDECIN')")
    public ResponseEntity<ApiResponse<List<AppUserDto>>> doctors() {
        return ResponseEntity.ok(ApiResponse.ok("Médecins récupérés", identityService.doctors()));
    }

    @GetMapping("/staff")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','MEDECIN','NURSE','RECEPTION','PHARMACIST','LABORATORY','HOSPITALIZATION_SERVICE','ACCOUNTING','URGENCY','UTILISATEUR_SECONDAIRE')")
    public ResponseEntity<ApiResponse<List<AppUserDto>>> staff() {
        return ResponseEntity.ok(ApiResponse.ok("Personnel récupéré", identityService.staff()));
    }

    @GetMapping("/role/{roleName}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<AppUserDto>>> usersByRole(@PathVariable String roleName) {
        return ResponseEntity.ok(ApiResponse.ok("Utilisateurs par rôle récupérés", identityService.usersByRole(roleName)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<AppUserDto>> getUser(@PathVariable Long id) {
        return identityService.getUser(id)
                .map(user -> ResponseEntity.ok(ApiResponse.ok("Utilisateur récupéré", user)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Utilisateur introuvable")));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<AppUserDto>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Utilisateur mis à jour", identityService.updateUser(id, request)));
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<AppUserDto>> activateUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Compte activé", identityService.activateUser(id)));
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<AppUserDto>> deactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Compte désactivé", identityService.deactivateUser(id)));
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<AppUserDto>> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Rôle mis à jour", identityService.updateRole(id, request)));
    }

    @PutMapping("/{id}/permissions")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<AppUserDto>> updatePermissions(
            @PathVariable Long id,
            @Valid @RequestBody UserPermissionsUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Permissions mises à jour", identityService.updatePermissions(id, request.getPermissions())));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<AppUserDto>> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Utilisateur supprimé", identityService.deleteUser(id)));
    }}
