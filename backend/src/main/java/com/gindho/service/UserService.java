package com.gindho.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gindho.dto.AuthenticationRequest;
import com.gindho.dto.AuthenticationResponse;
import com.gindho.dto.DashboardStatsDto;
import com.gindho.dto.UserDto;
import com.gindho.model.Medecin;
import com.gindho.model.Patient;
import com.gindho.model.RendezVous;
import com.gindho.model.Role;
import com.gindho.model.User;
import com.gindho.repository.MedecinRepository;
import com.gindho.repository.PatientRepository;
import com.gindho.repository.RendezVousRepository;
import com.gindho.repository.RevenuRepository;
import com.gindho.repository.UserRepository;
import com.gindho.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final MedecinRepository medecinRepository;
    private final PatientRepository patientRepository;
    private final RendezVousRepository rendezVousRepository;
    private final RevenuRepository revenuRepository;

    public AuthenticationResponse register(AuthenticationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        var role = request.getRole() != null
                ? Role.valueOf(request.getRole().toUpperCase())
                : Role.PATIENT;

        var nom = request.getNom() != null ? request.getNom() : request.getEmail().split("@")[0];
        var prenom = request.getPrenom() != null ? request.getPrenom() : "Utilisateur";

        var user = User.builder()
                .nom(nom)
                .prenom(prenom)
                .email(request.getEmail())
                .motDePasseHash(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .actif(true)
                .build();

        user = userRepository.save(user);

        // Si le user devient MEDECIN (enregistrement), créer aussi l'entité Medecin.
        ensureMedecinEntityIfNeeded(user);

        // Si le user devient PATIENT (enregistrement), créer aussi l'entité Patient.
        ensurePatientEntityIfNeeded(user);

        var jwtToken = jwtService.generateToken(user, user.getRole().name());

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .role(user.getRole().name())
                .userId(user.getId())
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        var jwtToken = jwtService.generateToken(user, user.getRole().name());

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .role(user.getRole().name())
                .userId(user.getId())
                .build();
    }

    public UserDto updatePermissions(Long id, java.util.List<String> permissions) {
        var user = userRepository.findUserWithPermissionsById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.getPermissions().clear();

        if (permissions != null) {
            for (String perm : permissions) {
                if (perm == null) continue;
                String cleaned = perm.trim();
                if (cleaned.isBlank()) continue;

                com.gindho.model.RolePermission rp = new com.gindho.model.RolePermission();
                rp.setUser(user);
                rp.setPermission(cleaned);
                user.getPermissions().add(rp);
            }
        }

        user = userRepository.save(user);
        return convertToDto(user);
    }

    /**
     * Backoffice: applique un ensemble de permissions provenant d'un template, en copiant aussi
     * validité/scope/condition_type si renseignés.
     *
     * Permet d'activer un RBAC dynamique complet quand les templates contiennent des métadonnées.
     */
    public UserDto updatePermissionsFromTemplate(Long id, java.util.List<com.gindho.model.RoleTemplatePermission> templatePermissions) {
        var user = userRepository.findUserWithPermissionsById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.getPermissions().clear();

        if (templatePermissions != null) {
            for (com.gindho.model.RoleTemplatePermission tp : templatePermissions) {
                if (tp == null) continue;
                String permission = tp.getPermission();
                if (permission == null || permission.isBlank()) continue;

                com.gindho.model.RolePermission rp = new com.gindho.model.RolePermission();
                rp.setUser(user);
                rp.setPermission(permission.trim());
                rp.setRessource(tp.getRessource());
                rp.setAction(tp.getAction());
                rp.setValidFrom(tp.getValidFrom());
                rp.setValidTo(tp.getValidTo());
                rp.setScope(tp.getScope());
                rp.setConditionType(tp.getConditionType());

                user.getPermissions().add(rp);
            }
        }

        user = userRepository.save(user);
        return convertToDto(user);
    }

    public UserDto changeRole(Long id, Role role) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole(role);
        var updated = userRepository.save(user);

        // Si le user passe en MEDECIN via le backoffice, créer l'entité Medecin manquante.
        ensureMedecinEntityIfNeeded(updated);

        // Si le user passe en PATIENT via le backoffice, créer l'entité Patient manquante.
        ensurePatientEntityIfNeeded(updated);

        // Recharger avec permissions pour que convertToDto puisse remplir le champ
        Long updatedId = updated.getId();
        return userRepository.findUserWithPermissionsById(updatedId)
                .map(this::convertToDto)
                .orElseGet(() -> convertToDto(updated));
    }

    public UserDto setActive(Long id, boolean actif) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActif(actif);

        var updated = userRepository.save(user);

        Long updatedId = updated.getId();
        return userRepository.findUserWithPermissionsById(updatedId)
                .map(this::convertToDto)
                .orElseGet(() -> convertToDto(updated));
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAllWithPermissions()
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    public org.springframework.data.domain.Page<UserDto> getAllUsers(org.springframework.data.domain.Pageable pageable) {
        return userRepository.findAllWithPermissions(pageable)
                .map(this::convertToDto);
    }

    public void resetPassword(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public DashboardStatsDto getAdminStats() {
        var totalPatients = patientRepository.count();
        var totalMedecins = medecinRepository.count();
        var totalRendezVous = rendezVousRepository.count();
        var rendezVousEnAttente = rendezVousRepository.countByStatut(RendezVous.StatutRDV.EN_ATTENTE);

        var aujourdhui = LocalDateTime.now();
        var debutJour = aujourdhui.withHour(0).withMinute(0).withSecond(0);
        var finJour = aujourdhui.withHour(23).withMinute(59).withSecond(59);

        var rendezVousAujourdhui = rendezVousRepository.countByDateHeureDebutBetween(debutJour, finJour);
        var totalRevenus = revenuRepository.sumMontant();

        return DashboardStatsDto.builder()
                .totalPatients(totalPatients)
                .totalMedecins(totalMedecins)
                .totalRendezVous(totalRendezVous)
                .rendezVousEnAttente(rendezVousEnAttente)
                .rendezVousAujourdhui(rendezVousAujourdhui)
                .totalRevenus(totalRevenus != null ? totalRevenus.doubleValue() : 0.0)
                .build();
    }

    private void ensurePatientEntityIfNeeded(User user) {
        if (user == null) return;
        if (user.getRole() == null) return;
        if (user.getRole() != Role.PATIENT) return;

        Patient existing = patientRepository.findByUser_Id(user.getId()).orElse(null);
        if (existing != null) return;

        Patient patient = new Patient();

        // numero_patient est nullable en DB, mais l'UI préfère généralement un identifiant stable.
        patient.setNumeroPatient("PAT-" + user.getId());
        patient.setUser(user);

        // Champs optionnels laissés null (DB les accepte).
        patientRepository.save(patient);
    }

    private void ensureMedecinEntityIfNeeded(User user) {
        if (user == null) return;
        if (user.getRole() == null) return;
        if (user.getRole() != Role.MEDECIN) return;

        Medecin existing = medecinRepository.findByUserId(user.getId());
        if (existing != null) return;

        Medecin medecin = new Medecin();
        // numeroOrdre est NOT NULL/UNIQUE en DB => on génère un identifiant stable.
        medecin.setNumeroOrdre("ORD-" + user.getId());
        medecin.setSpecialisation(null);
        medecin.setTelephoneCabinet(null);
        medecin.setDisponible(true);
        medecin.setUser(user);

        medecinRepository.save(medecin);
    }

    private UserDto convertToDto(User user) {
        // IMPORTANT:
        // Dans la DB, SUPER_ADMIN n'a peut-être aucune ligne role_permissions.
        // Pourtant l'UI attend un catalogue complet pour l'affichage.
        if (user != null && user.getRole() == Role.SUPER_ADMIN) {
            return UserDto.builder()
                    .id(user.getId())
                    .nom(user.getNom())
                    .prenom(user.getPrenom())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .actif(user.isActif())
                    .creeLe(user.getCreeLe())
                    .permissions(com.gindho.security.PermissionsCatalog.getCatalog())
                    .build();
        }

        java.util.List<String> permissions = (user.getPermissions() == null)
                ? java.util.List.of()
                : user.getPermissions().stream()
                .flatMap(rp -> {
                    if (rp == null) return java.util.stream.Stream.empty();

                    if (rp.getPermission() != null && !rp.getPermission().isBlank()) {
                        return java.util.stream.Stream.of(rp.getPermission().trim());
                    }

                    if (rp.getRessource() != null && !rp.getRessource().isBlank()
                            && rp.getAction() != null && !rp.getAction().isBlank()) {
                        String res = rp.getRessource().trim();
                        String action = rp.getAction().trim();
                        return java.util.stream.Stream.of(res + ":" + action);
                    }

                    return java.util.stream.Stream.empty();
                })
                .filter(p -> p != null && !p.isBlank())
                .toList();

        return UserDto.builder()
                .id(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .email(user.getEmail())
                .role(user.getRole().name())
                .actif(user.isActif())
                .creeLe(user.getCreeLe())
                .permissions(permissions)
                .build();
    }
}
