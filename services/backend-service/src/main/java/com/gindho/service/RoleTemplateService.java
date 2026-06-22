package com.gindho.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gindho.dto.RoleTemplateCreateRequest;
import com.gindho.dto.RoleTemplateDto;
import com.gindho.model.RoleTemplate;
import com.gindho.repository.RoleTemplateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleTemplateService {

    private final RoleTemplateRepository roleTemplateRepository;
    private final UserService userService;

    public RoleTemplateDto createTemplate(RoleTemplateCreateRequest request) {
        if (request == null || request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Template name manquant");
        }

        List<String> permissions = request.getPermissions();

        RoleTemplate template = new RoleTemplate();
        template.setName(request.getName().trim());

        // Permissions comme "permission seule" (format attendu par CustomUserDetailsService / DynamicPermissionFilter)
        // ex: "PATIENTS:READ", "patients:read", etc.
        if (permissions != null) {
            for (String perm : permissions) {
                if (perm == null) continue;
                String cleaned = perm.trim();
                if (cleaned.isBlank()) continue;

                var tp = new com.gindho.model.RoleTemplatePermission();
                tp.setTemplate(template);
                tp.setPermission(cleaned);
                template.getPermissions().add(tp);
            }
        }

        RoleTemplate saved = roleTemplateRepository.save(template);
        return toDto(saved);
    }

    public List<RoleTemplateDto> getAllTemplates() {
        return roleTemplateRepository.findAllWithPermissions().stream().map(this::toDto).toList();
    }

    public com.gindho.dto.UserDto applyTemplateToUser(Long templateId, Long userId) {
        if (templateId == null) throw new IllegalArgumentException("templateId manquant");
        if (userId == null) throw new IllegalArgumentException("userId manquant");

        RoleTemplate template = roleTemplateRepository.findByIdWithPermissions(templateId)
                .orElseThrow(() -> new RuntimeException("Template introuvable: id=" + templateId));

        // On applique le template en copiant aussi validité/scope/conditions (si renseignés)
        // afin d'activer un RBAC dynamique complet.
        return userService.updatePermissionsFromTemplate(userId,
                template.getPermissions() == null ? List.of() : template.getPermissions());
    }

    private RoleTemplateDto toDto(RoleTemplate template) {
        return RoleTemplateDtoBuilder.from(template);
    }

    private static class RoleTemplateDtoBuilder {

        static RoleTemplateDto from(RoleTemplate template) {
            RoleTemplateDto dto = new RoleTemplateDto();
            dto.setId(template.getId());
            dto.setName(template.getName());
            dto.setCreeLe(template.getCreeLe());

            List<String> permissions = template.getPermissions() == null
                    ? List.of()
                    : template.getPermissions().stream()
                            .map(com.gindho.model.RoleTemplatePermission::getPermission)
                            .filter(p -> p != null && !p.isBlank())
                            .map(String::trim)
                            .toList();

            dto.setPermissions(permissions);
            return dto;
        }
    }
}
