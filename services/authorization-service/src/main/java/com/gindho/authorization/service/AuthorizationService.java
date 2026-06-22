package com.gindho.authorization.service;

import com.gindho.authorization.dto.AdminUserDto;
import com.gindho.authorization.dto.RoleTemplateCreateRequest;
import com.gindho.authorization.dto.RoleTemplateDto;
import com.gindho.authorization.dto.UserActiveUpdateRequest;
import com.gindho.authorization.dto.UserPermissionsUpdateRequest;
import com.gindho.authorization.dto.UserRoleUpdateRequest;
import com.gindho.authorization.model.AuthorizationUser;
import com.gindho.authorization.model.Permission;
import com.gindho.authorization.model.Role;
import com.gindho.authorization.model.RoleName;
import com.gindho.authorization.model.RolePermission;
import com.gindho.authorization.model.RoleTemplate;
import com.gindho.authorization.model.RoleTemplatePermission;
import com.gindho.authorization.model.UserRole;
import com.gindho.authorization.repository.AuthorizationUserRepository;
import com.gindho.authorization.repository.PermissionRepository;
import com.gindho.authorization.repository.RolePermissionRepository;
import com.gindho.authorization.repository.RoleRepository;
import com.gindho.authorization.repository.RoleTemplateRepository;
import com.gindho.authorization.repository.UserRoleRepository;
import com.gindho.authorization.security.PermissionCatalog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthorizationService {
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final PermissionCacheService cacheService;
    private final RoleTemplateRepository roleTemplateRepository;
    private final AuthorizationUserRepository authorizationUserRepository;

    public boolean hasPermission(String userId, String permissionCode) {
        if (userId == null || permissionCode == null) {
            return false;
        }
        List<Permission> perms = getUserPermissions(userId);
        return perms.stream().anyMatch(p -> p.getCode().equalsIgnoreCase(permissionCode));
    }

    public List<Permission> getUserPermissions(String userId) {
        List<Permission> cached = cacheService.get(userId);
        if (cached != null) {
            return cached;
        }
        List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
        List<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList());
        List<RolePermission> rps = rolePermissionRepository.findByRoleIdIn(roleIds);
        List<Long> permIds = rps.stream().map(RolePermission::getPermissionId).collect(Collectors.toList());
        List<Permission> permissions = permissionRepository.findAllById(permIds);
        cacheService.put(userId, permissions);
        return permissions;
    }

    public List<String> getUsersWithPermission(String permissionCode) {
        Permission permission = permissionRepository.findByCode(permissionCode).orElse(null);
        if (permission == null) {
            return List.of();
        }
        List<RolePermission> rps = rolePermissionRepository.findByPermissionId(permission.getId());
        List<Long> roleIds = rps.stream().map(RolePermission::getRoleId).collect(Collectors.toList());
        List<UserRole> userRoles = userRoleRepository.findByRoleIdIn(roleIds);
        return userRoles.stream().map(UserRole::getUserId).distinct().collect(Collectors.toList());
    }

    @Transactional
    public Role createRole(Role role) {
        if (role.getCode() == null || role.getCode().isBlank()) {
            throw new IllegalArgumentException("Role code obligatoire");
        }
        role.setCode(role.getCode().toUpperCase(Locale.ROOT));
        return roleRepository.save(role);
    }

    @Transactional
    public Permission createPermission(Permission p) {
        if (p.getCode() == null || p.getCode().isBlank()) {
            throw new IllegalArgumentException("Permission code obligatoire");
        }
        p.setCode(p.getCode().toUpperCase(Locale.ROOT));
        return permissionRepository.save(p);
    }

    @Transactional
    public UserRole assignRole(UserRole ur) {
        cacheService.invalidate(ur.getUserId());
        return userRoleRepository.save(ur);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    @Transactional
    public void addPermissionsToRole(Long roleId, List<String> permissionCodes) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role introuvable"));
        for (String code : permissionCodes) {
            if (code == null || code.isBlank()) continue;
            String cleaned = code.trim().toUpperCase(Locale.ROOT);
            Permission p = permissionRepository.findByCode(cleaned)
                    .orElseGet(() -> permissionRepository.save(toPermission(cleaned)));
            List<RolePermission> existing = rolePermissionRepository.findByRoleIdIn(List.of(role.getId()));
            boolean alreadyExists = existing.stream().anyMatch(rp -> rp.getPermissionId().equals(p.getId()));
            if (!alreadyExists) {
                RolePermission rp = new RolePermission();
                rp.setRoleId(role.getId());
                rp.setPermissionId(p.getId());
                rp.setConditionType("ALL");
                rolePermissionRepository.save(rp);
            }
        }
        cacheService.invalidateAll();
    }

    @Transactional
    public void removePermissionFromRole(Long roleId, String permissionCode) {
        if (permissionCode == null || permissionCode.isBlank()) return;
        Permission permission = permissionRepository.findByCode(permissionCode.trim().toUpperCase(Locale.ROOT))
                .orElseThrow(() -> new IllegalArgumentException("Permission introuvable"));
        List<RolePermission> rps = rolePermissionRepository.findByRoleId(roleId);
        rps.stream()
                .filter(rp -> rp.getPermissionId().equals(permission.getId()))
                .findFirst()
                .ifPresent(rp -> rolePermissionRepository.delete(rp));
        cacheService.invalidateAll();
    }

    public List<Permission> getUserEffectivePermissions(String userId) {
        return getUserPermissions(userId);
    }

    @Transactional
    public UserRole assignRoleWithExpiry(String userId, Long roleId, java.time.LocalDateTime expiresAt) {
        UserRole existing = userRoleRepository.findByUserId(userId).stream()
                .filter(ur -> ur.getRoleId().equals(roleId))
                .findFirst()
                .orElse(null);
        if (existing != null) {
            existing.setExpiresAt(expiresAt);
            cacheService.invalidate(userId);
            return userRoleRepository.save(existing);
        }
        UserRole ur = new UserRole();
        ur.setUserId(userId);
        ur.setRoleId(roleId);
        ur.setExpiresAt(expiresAt);
        cacheService.invalidate(userId);
        return userRoleRepository.save(ur);
    }

    @Transactional
    public void removeRoleFromUser(String userId, Long roleId) {
        UserRole toDelete = userRoleRepository.findByUserId(userId).stream()
                .filter(ur -> ur.getRoleId().equals(roleId))
                .findFirst()
                .orElse(null);
        if (toDelete != null) {
            userRoleRepository.delete(toDelete);
            cacheService.invalidate(userId);
        }
    }

    public List<com.gindho.authorization.dto.PermissionCatalogEntry> getPermissionCatalogGrouped() {
        return PermissionCatalog.getCatalogGrouped();
    }

    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    public List<UserRole> getUserRoles(String userId) {
        return userRoleRepository.findByUserId(userId);
    }

    public List<String> getPermissionCatalog() {
        return PermissionCatalog.getCatalog();
    }

    @Transactional
    public RoleTemplateDto createTemplate(RoleTemplateCreateRequest request) {
        if (request == null || request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Template name obligatoire");
        }
        RoleTemplate template = new RoleTemplate();
        template.setName(request.getName().trim());
        template.setDescription(request.getDescription());
        List<String> permissions = request.getPermissions() == null ? List.of() : request.getPermissions();
        for (String permission : permissions) {
            if (permission == null || permission.isBlank()) {
                continue;
            }
            String cleaned = permission.trim();
            RoleTemplatePermission tp = new RoleTemplatePermission();
            tp.setTemplate(template);
            tp.setPermission(cleaned.toUpperCase(Locale.ROOT));
            String[] parts = cleaned.split(":");
            if (parts.length == 2) {
                tp.setRessource(parts[0].trim());
                tp.setAction(parts[1].trim());
            }
            template.getPermissions().add(tp);
        }
        return toDto(roleTemplateRepository.save(template));
    }

    public List<RoleTemplateDto> getAllTemplates() {
        return roleTemplateRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public AdminUserDto applyTemplateToUser(Long templateId, Long userId) {
        RoleTemplate template = roleTemplateRepository.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException("Template introuvable"));
        AuthorizationUser user = authorizationUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        List<String> permissions = template.getPermissions() == null ? List.of()
                : template.getPermissions().stream().map(RoleTemplatePermission::getPermission).toList();
        return updatePermissions(userId, permissions);
    }

    public List<AdminUserDto> getAllUsers() {
        return authorizationUserRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public AdminUserDto changeRole(Long id, UserRoleUpdateRequest request) {
        if (request == null || request.getRole() == null || request.getRole().isBlank()) {
            throw new IllegalArgumentException("Role obligatoire");
        }
        AuthorizationUser user = authorizationUserRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        user.setRole(RoleName.valueOf(request.getRole().trim().toUpperCase(Locale.ROOT)));
        cacheService.invalidate(String.valueOf(user.getId()));
        return toDto(authorizationUserRepository.save(user));
    }

    @Transactional
    public AdminUserDto setActive(Long id, UserActiveUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Champ actif manquant");
        }
        AuthorizationUser user = authorizationUserRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        user.setActif(request.isActif());
        cacheService.invalidate(String.valueOf(user.getId()));
        return toDto(authorizationUserRepository.save(user));
    }

    @Transactional
    public AdminUserDto updatePermissions(Long id, UserPermissionsUpdateRequest request) {
        return updatePermissions(id, request == null ? List.of() : request.getPermissions());
    }

    @Transactional
    public AdminUserDto updatePermissions(Long id, List<String> permissions) {
        AuthorizationUser user = authorizationUserRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        List<Long> roleIds = userRoleRepository.findByUserId(String.valueOf(user.getId())).stream()
                .map(UserRole::getRoleId).toList();
        rolePermissionRepository.deleteByRoleIdIn(roleIds);

        List<RolePermission> created = new ArrayList<>();
        if (permissions != null) {
            for (String permission : permissions) {
                if (permission == null || permission.isBlank()) {
                    continue;
                }
                Permission p = permissionRepository.findByCode(permission.trim().toUpperCase(Locale.ROOT))
                        .orElseGet(() -> permissionRepository.save(toPermission(permission.trim())));
                Role role = roleRepository.findByCode(user.getRole().name()).orElseGet(() -> roleRepository.save(defaultRole(user.getRole())));
                RolePermission rp = new RolePermission();
                rp.setRoleId(role.getId());
                rp.setPermissionId(p.getId());
                rp.setConditionType("ALL");
                created.add(rp);
            }
        }
        if (!created.isEmpty()) {
            rolePermissionRepository.saveAll(created);
        }
        cacheService.invalidate(String.valueOf(user.getId()));
        return toDto(authorizationUserRepository.findById(id).orElse(user));
    }

    private Permission toPermission(String permission) {
        String code = permission.trim().toUpperCase(Locale.ROOT);
        Permission p = new Permission();
        p.setCode(code);
        p.setLibelle(code);
        p.setModule("DYNAMIC");
        p.setAction(code.contains(":") ? code.substring(code.indexOf(':') + 1) : "WRITE");
        return p;
    }

    private Role defaultRole(RoleName roleName) {
        Role role = new Role();
        role.setCode(roleName.name());
        role.setLibelle(roleName.name());
        return role;
    }

    private RoleTemplateDto toDto(RoleTemplate template) {
        RoleTemplateDto dto = new RoleTemplateDto();
        dto.setId(template.getId());
        dto.setName(template.getName());
        dto.setDescription(template.getDescription());
        dto.setCreeLe(template.getCreeLe());
        dto.setPermissions(template.getPermissions() == null ? List.of()
                : template.getPermissions().stream().map(RoleTemplatePermission::getPermission).toList());
        return dto;
    }

    private AdminUserDto toDto(AuthorizationUser user) {
        AdminUserDto dto = new AdminUserDto();
        dto.setId(user.getId());
        dto.setNom(user.getNom());
        dto.setPrenom(user.getPrenom());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        dto.setActif(user.isActif());
        dto.setCreeLe(user.getCreeLe());
        if (user.getRole() == RoleName.SUPER_ADMIN) {
            dto.setPermissions(PermissionCatalog.getCatalog());
        } else {
            List<Long> roleIds = userRoleRepository.findByUserId(String.valueOf(user.getId())).stream()
                    .map(UserRole::getRoleId).toList();
            List<RolePermission> rps = rolePermissionRepository.findByRoleIdIn(roleIds);
            List<Long> permIds = rps.stream().map(RolePermission::getPermissionId).toList();
            dto.setPermissions(permissionRepository.findAllById(permIds).stream().map(Permission::getCode).toList());
        }
        return dto;
    }
}
