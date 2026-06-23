package com.gindho.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gindho.dto.UserActiveUpdateRequest;
import com.gindho.dto.UserDto;
import com.gindho.dto.UserPermissionsUpdateRequest;
import com.gindho.dto.UserRoleUpdateRequest;
import com.gindho.model.Role;
import com.gindho.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    // IMPORTANT (FR)
    // Backoffice ADMIN : gestion des comptes (rôles + activation/désactivation).
    //
    // Commandes (FR) :
    // 1) Lancer le backend :
    //    mvn -f backend/pom.xml spring-boot:run -DskipTests
    //
    // 2) Login admin (récupérer un TOKEN) :
    //    curl -s -X POST http://localhost:9001/api/auth/login \
    //      -H "Content-Type: application/json" \
    //      -d '{"email":"admin@gindho.com","password":"admin123"}'
    //
    // 3) Lister les users :
    //    curl -s -H "Authorization: Bearer <TOKEN>" http://localhost:9001/api/admin/users
    //
    // 4) Changer le rôle :
    //    curl -s -X PUT -H "Authorization: Bearer <TOKEN>" -H "Content-Type: application/json" \
    //      -d '{"role":"PHARMACIST"}' http://localhost:9001/api/admin/users/8/role
    //
    // 5) Activer / désactiver :
    //    curl -s -X PUT -H "Authorization: Bearer <TOKEN>" -H "Content-Type: application/json" \
    //      -d '{"actif":false}' http://localhost:9001/api/admin/users/8/active
    //
    // Note: l’accès à /api/admin/** est contrôlé dans SecurityConfig.
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/{id}/role")
    public UserDto updateRole(
            @PathVariable Long id,
            @RequestBody UserRoleUpdateRequest request
    ) {
        if (request == null || request.getRole() == null || request.getRole().isBlank()) {
            throw new IllegalArgumentException("Role manquant");
        }
        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Role invalide: " + request.getRole());
        }
        return userService.changeRole(id, role);
    }

    @PutMapping("/{id}/active")
    public UserDto updateActive(
            @PathVariable Long id,
            @RequestBody UserActiveUpdateRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException("Champ actif manquant");
        }
        return userService.setActive(id, request.isActif());
    }

    @PutMapping("/{id}/permissions")
    public UserDto updatePermissions(
            @PathVariable Long id,
            @RequestBody UserPermissionsUpdateRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException("Champ permissions manquant");
        }
        // request.permissions peut être null => on vide les permissions
        return userService.updatePermissions(id, request.getPermissions());
    }
}
