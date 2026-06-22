package com.gindho.security;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gindho.model.RolePermission;
import com.gindho.model.User;
import com.gindho.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailWithPermissions(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        // Permissions dynamiques (RBAC) :
        // - si RolePermission.permission est renseigné => authority = permission
        // - si RolePermission.ressource & action sont renseignés => authority = ressource + ":" + action
        for (RolePermission rp : user.getPermissions()) {
            if (rp == null) continue;
            if (!isPermissionActive(rp)) continue;

            if (rp.getPermission() != null && !rp.getPermission().isBlank()) {
                authorities.add(new SimpleGrantedAuthority(rp.getPermission().trim()));
            }

            if (rp.getRessource() != null && !rp.getRessource().isBlank()
                    && rp.getAction() != null && !rp.getAction().isBlank()) {
                authorities.add(
                        new SimpleGrantedAuthority(
                                rp.getRessource().trim() + ":" + rp.getAction().trim()
                        )
                );
            }
        }

        // IMPORTANT: auth.getName() doit correspondre à l'email, car PatientAccessService
        // utilise auth.getName() pour retrouver le Patient via findByUserEmail(...).
        return new org.springframework.security.core.userdetails.User(
                email,
                user.getPassword(),
                user.isActive(),
                true,
                true,
                true,
                authorities
        );
    }

    private boolean isPermissionActive(RolePermission rp) {
        // Règle temporelle:
        // - si valid_from est défini => maintenant doit être >= valid_from
        // - si valid_to est défini => maintenant doit être <= valid_to
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime validFrom = rp.getValidFrom();
        if (validFrom != null && now.isBefore(validFrom)) {
            return false;
        }

        LocalDateTime validTo = rp.getValidTo();
        if (validTo != null && now.isAfter(validTo)) {
            return false;
        }

        // TODO (phase suivante): scope/condition_type
        // Pour l’instant, on considère qu’en l’absence de metadata, la permission est valide.
        return true;
    }
}
