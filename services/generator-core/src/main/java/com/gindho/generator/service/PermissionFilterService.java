package com.gindho.generator.service;
import com.gindho.generator.config.*;
import org.springframework.stereotype.Service;
import java.util.List; import java.util.stream.Collectors;
@Service
public class PermissionFilterService {
    public List<UISectionDefinition> filterSections(List<UISectionDefinition> sections, List<String> userPermissions) {
        if (userPermissions == null || userPermissions.contains("SUPER_ADMIN")) return sections;
        return sections.stream()
            .filter(s -> s.getPermissions() == null || s.getPermissions().isEmpty()
                || s.getPermissions().stream().anyMatch(p -> userPermissions.contains(p)))
            .collect(Collectors.toList());
    }
    public boolean isAllowed(String permissionCode, List<String> userPermissions) {
        return userPermissions == null || permissionCode == null
            || userPermissions.contains("SUPER_ADMIN") || userPermissions.contains(permissionCode);
    }
}