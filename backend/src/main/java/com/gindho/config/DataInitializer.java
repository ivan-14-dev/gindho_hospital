package com.gindho.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.gindho.model.Role;
import com.gindho.model.RolePermission;
import com.gindho.model.User;
import com.gindho.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final String DEFAULT_PASSWORD = "gindho123";

    private static final List<String> ACCOUNTING_PERMISSIONS = List.of(
            "FACTURES:READ",
            "FACTURES:WRITE",
            "PAIEMENTS:READ",
            "PAIEMENTS:WRITE"
    );

    private static final List<String> PATIENT_PERMISSIONS = List.of(
            "MEDECINS:READ",
            "RENDEZVOUS:READ",
            "RENDEZVOUS:WRITE",
            "DISPONIBILITES:READ"
    );

    // Par défaut: NO destructif (prod-friendly).
    // Mets app.seed.destructive=true seulement en dev si tu veux écraser complètement.
    @org.springframework.beans.factory.annotation.Value("${app.seed.destructive:false}")
    private boolean seedDestructive;

    private static final List<String> MALADIES_PERMISSIONS = List.of(
            "MALADIES:READ",
            "MALADIES:WRITE",
            "MALADIES:DELETE"
    );

    private static final List<String> MEDICAMENTS_PERMISSIONS = List.of(
            "MEDICAMENTS:READ",
            "MEDICAMENTS:WRITE",
            "MEDICAMENTS:DELETE"
    );

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Admin(s)
        createUserIfMissing("admin@gindho.com", "admin123", Role.ADMIN, "Admin", "Systeme");
        createUserIfMissing("superadmin@gindho.com", DEFAULT_PASSWORD, Role.SUPER_ADMIN, "Super", "Admin");

        // Clinique / Staff
        createUserIfMissing("reception@gindho.com", DEFAULT_PASSWORD, Role.RECEPTION, "Reception", "Staff");
        createUserIfMissing("medecin@gindho.com", DEFAULT_PASSWORD, Role.MEDECIN, "Dr", "Demo");
        createUserIfMissing("patient@gindho.com", DEFAULT_PASSWORD, Role.PATIENT, "Patient", "Demo");

        createUserIfMissing("nurse@gindho.com", DEFAULT_PASSWORD, Role.NURSE, "Nurse", "Demo");
        createUserIfMissing("pharmacist@gindho.com", DEFAULT_PASSWORD, Role.PHARMACIST, "Pharma", "Demo");
        createUserIfMissing("laboratory@gindho.com", DEFAULT_PASSWORD, Role.LABORATORY, "Lab", "Demo");
        createUserIfMissing("hospitalization@gindho.com", DEFAULT_PASSWORD, Role.HOSPITALIZATION_SERVICE, "HOSP", "Demo");
        createUserIfMissing("accounting@gindho.com", DEFAULT_PASSWORD, Role.ACCOUNTING, "Cash", "Demo");
        createUserIfMissing("urgency@gindho.com", DEFAULT_PASSWORD, Role.URGENCY, "Urgency", "Demo");

        // Rôle générique
        createUserIfMissing("secondary@gindho.com", DEFAULT_PASSWORD, Role.UTILISATEUR_SECONDAIRE, "User", "Secondaire");

        // Seed RBAC dynamic permissions
        ensureAccountingPermissions("accounting@gindho.com");
        ensurePatientPermissions("patient@gindho.com");
    }

    private void ensureAccountingPermissions(String email) {
        userRepository.findByEmailWithPermissions(email).ifPresent(user -> {

            if (seedDestructive) {
                // Mode explicite: écrase tout (dev/test uniquement)
                user.getPermissions().clear();

                for (String perm : ACCOUNTING_PERMISSIONS) {
                    if (perm == null || perm.isBlank()) continue;

                    RolePermission rp = new RolePermission();
                    rp.setUser(user);
                    rp.setPermission(perm);
                    user.getPermissions().add(rp);
                }

                userRepository.save(user);
                return;
            }

            // Mode non destructif (prod-friendly):
            // - supprimer uniquement les permissions "accounting" déjà présentes
            // - ajouter celles manquantes
            user.getPermissions().removeIf(rp ->
                    rp != null
                            && rp.getPermission() != null
                            && ACCOUNTING_PERMISSIONS.contains(rp.getPermission())
            );

            java.util.Set<String> existingPerms = user.getPermissions().stream()
                    .filter(rp -> rp != null && rp.getPermission() != null)
                    .map(RolePermission::getPermission)
                    .collect(java.util.stream.Collectors.toSet());

            for (String perm : ACCOUNTING_PERMISSIONS) {
                if (perm == null || perm.isBlank()) continue;
                if (existingPerms.contains(perm)) continue;

                RolePermission rp = new RolePermission();
                rp.setUser(user);
                rp.setPermission(perm);
                user.getPermissions().add(rp);
            }

            userRepository.save(user);
        });
    }

    private void ensurePatientPermissions(String email) {
        userRepository.findByEmailWithPermissions(email).ifPresent(user -> {
            if (seedDestructive) {
                user.getPermissions().clear();

                for (String perm : PATIENT_PERMISSIONS) {
                    if (perm == null || perm.isBlank()) continue;

                    RolePermission rp = new RolePermission();
                    rp.setUser(user);
                    rp.setPermission(perm);
                    user.getPermissions().add(rp);
                }

                userRepository.save(user);
                return;
            }

            user.getPermissions().removeIf(rp ->
                    rp != null
                            && rp.getPermission() != null
                            && PATIENT_PERMISSIONS.contains(rp.getPermission())
            );

            java.util.Set<String> existingPerms = user.getPermissions().stream()
                    .filter(rp -> rp != null && rp.getPermission() != null)
                    .map(RolePermission::getPermission)
                    .collect(java.util.stream.Collectors.toSet());

            for (String perm : PATIENT_PERMISSIONS) {
                if (perm == null || perm.isBlank()) continue;
                if (existingPerms.contains(perm)) continue;

                RolePermission rp = new RolePermission();
                rp.setUser(user);
                rp.setPermission(perm);
                user.getPermissions().add(rp);
            }

            userRepository.save(user);
        });
    }

    private void createUserIfMissing(String email, String rawPassword, Role role, String nom, String prenom) {
        if (userRepository.findByEmail(email).isPresent()) {
            return;
        }

        User user = new User();
        user.setNom(nom);
        user.setPrenom(prenom);
        user.setEmail(email);
        user.setMotDePasseHash(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setActif(true);

        userRepository.save(user);
        System.out.println("Seed user created: " + email + " / " + rawPassword + " (role=" + role + ")");
    }
}
