package com.gindho.controller;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gindho.dto.RoleTemplateCreateRequest;
import com.gindho.dto.RoleTemplateDto;
import com.gindho.dto.UserDto;
import com.gindho.service.RoleTemplateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/role-templates")
@RequiredArgsConstructor
public class AdminRoleTemplatesController {

    private final RoleTemplateService roleTemplateService;

    @PostMapping
    public RoleTemplateDto create(@RequestBody RoleTemplateCreateRequest request) {
        requireSuperAdmin();
        return roleTemplateService.createTemplate(request);
    }

    @GetMapping
    public List<RoleTemplateDto> list() {
        requireSuperAdmin();
        return roleTemplateService.getAllTemplates();
    }

    @PutMapping("/{templateId}/apply/{userId}")
    public UserDto apply(
            @PathVariable Long templateId,
            @PathVariable Long userId
    ) {
        requireSuperAdmin();
        return roleTemplateService.applyTemplateToUser(templateId, userId);
    }

    private void requireSuperAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isSuperAdmin = authentication != null
                && authentication.getAuthorities() != null
                && authentication.getAuthorities().stream()
                .anyMatch(a -> a != null && "ROLE_SUPER_ADMIN".equals(a.getAuthority()));

        if (!isSuperAdmin) {
            throw new AccessDeniedException("SUPER_ADMIN requis");
        }
    }
}
