package com.gindho.identity.service;

import com.gindho.identity.dto.AppUserDto;
import com.gindho.identity.dto.AuthenticationRequest;
import com.gindho.identity.dto.AuthenticationResponse;
import com.gindho.identity.dto.ForgotPasswordRequest;
import com.gindho.identity.dto.MeAuthoritiesResponse;
import com.gindho.identity.dto.ResetPasswordRequest;
import com.gindho.identity.dto.RoleUpdateRequest;
import com.gindho.identity.dto.UserUpdateRequest;
import com.gindho.identity.model.AppUser;
import com.gindho.identity.model.PasswordResetOtp;
import com.gindho.identity.model.Role;
import com.gindho.identity.repository.PasswordResetOtpRepository;
import com.gindho.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdentityService {
    private final UserRepository userRepository;
    private final PasswordResetOtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final KeycloakService keycloakService;

    @Transactional
    public AuthenticationResponse register(AuthenticationRequest request) {
        requireNotBlank(request == null ? null : request.getEmail(), "email");
        requireNotBlank(request == null ? null : request.getPassword(), "password");

        String email = normalize(request.getEmail());
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email déjà utilisé");
        }

        Role role = parseRoleOrDefault(request == null ? null : request.getRole());
        String nom = valueOr(request.getNom(), email.split("@")[0]);
        String prenom = valueOr(request.getPrenom(), "Utilisateur");

        AppUser user = new AppUser();
        user.setNom(trim(nom));
        user.setPrenom(trim(prenom));
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setActif(true);
        user = userRepository.save(user);

        try {
            keycloakService.syncUserToKeycloak(user.getId());
        } catch (RuntimeException e) {
            log.warn("Synchronisation Keycloak échouée après inscription de {}: {}", email, e.getMessage());
        }

        return AuthenticationResponse.builder()
                .token("local-token-" + user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .userId(user.getId())
                .build();
    }

    public AuthenticationResponse login(String email, String password) {
        requireNotBlank(email, "email");
        requireNotBlank(password, "password");

        AppUser user = userRepository.findByEmail(normalize(email))
                .orElseThrow(() -> new IllegalArgumentException("Identifiants invalides"));
        if (!user.isActif() || !passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Identifiants invalides");
        }
        return AuthenticationResponse.builder()
                .token("local-token-" + user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .userId(user.getId())
                .build();
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        if (request == null || request.email() == null || request.email().isBlank()) {
            return;
        }
        String email = normalize(request.email());
        otpRepository.findByEmailAndConsumedFalse(email).forEach(otp -> {
            otp.setConsumed(true);
            otpRepository.save(otp);
        });

        String code = String.format("%06d", new SecureRandom().nextInt(1_000_000));
        PasswordResetOtp otp = new PasswordResetOtp();
        otp.setEmail(email);
        otp.setCodeHash(hash(code));
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        otp.setConsumed(false);
        otpRepository.save(otp);
        log.info("Password reset OTP generated for {}", email);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        if (request == null || request.email() == null || request.code() == null || request.newPassword() == null) {
            throw new IllegalArgumentException("Requête de réinitialisation invalide");
        }
        String email = normalize(request.email());
        String codeHash = hash(request.code().trim());
        Optional<PasswordResetOtp> otpOpt = otpRepository.findByEmailAndConsumedFalse(email).stream()
                .filter(otp -> !otp.isConsumed()
                        && otp.getExpiresAt().isAfter(LocalDateTime.now())
                        && otp.getCodeHash().equals(codeHash))
                .findFirst();

        if (otpOpt.isEmpty()) {
            throw new IllegalArgumentException("Code invalide ou expiré");
        }
        PasswordResetOtp otp = otpOpt.get();
        otp.setConsumed(true);
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        otpRepository.save(otp);
    }

    public MeAuthoritiesResponse me(Long userId) {
        AppUser user = getRequiredUser(userId);
        return MeAuthoritiesResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .authorities(List.of("ROLE_" + user.getRole().name(), "AUTHENTICATED"))
                .build();
    }

    public Page<AppUserDto> listUsers(String search, Pageable pageable) {
        Pageable page = normalizePageable(pageable);
        String query = normalize(search);
        Page<AppUser> users = query.isBlank()
                ? userRepository.findAll(page)
                : userRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        query, query, query, page);
        return users.map(this::toDto);
    }

    public Optional<AppUserDto> getUser(Long id) {
        return userRepository.findById(id).map(this::toDto);
    }

    @Transactional
    public AppUserDto updateUser(Long id, UserUpdateRequest request) {
        require(request != null, "Requête de mise à jour obligatoire");
        AppUser user = getRequiredUser(id);

        if (request.getNom() != null) {
            requireNotBlank(request.getNom(), "nom");
            user.setNom(trim(request.getNom()));
        }
        if (request.getPrenom() != null) {
            requireNotBlank(request.getPrenom(), "prenom");
            user.setPrenom(trim(request.getPrenom()));
        }
        if (request.getEmail() != null) {
            String email = normalize(request.getEmail());
            requireNotBlank(email, "email");
            Optional<AppUser> existing = userRepository.findByEmail(email);
            if (existing.isPresent() && !existing.get().getId().equals(id)) {
                throw new IllegalArgumentException("Email déjà utilisé");
            }
            user.setEmail(email);
        }
        if (request.getRole() != null) {
            user.setRole(parseRole(request.getRole().name()));
        }
        if (request.getActif() != null) {
            user.setActif(request.getActif());
        }

        AppUser saved = userRepository.save(user);
        syncUserToKeycloak(saved);
        return toDto(saved);
    }

    @Transactional
    public AppUserDto activateUser(Long id) {
        AppUser user = getRequiredUser(id);
        user.setActif(true);
        AppUser saved = userRepository.save(user);
        syncUserToKeycloak(saved);
        return toDto(saved);
    }

    @Transactional
    public AppUserDto deactivateUser(Long id) {
        AppUser user = getRequiredUser(id);
        user.setActif(false);
        AppUser saved = userRepository.save(user);
        syncUserToKeycloak(saved);
        return toDto(saved);
    }

    @Transactional
    public AppUserDto updateRole(Long id, RoleUpdateRequest request) {
        require(request != null, "Requête de rôle obligatoire");
        require(request.getRole() != null, "role obligatoire");

        AppUser user = getRequiredUser(id);
        user.setRole(parseRole(request.getRole().name()));
        AppUser saved = userRepository.save(user);
        syncUserToKeycloak(saved);
        return toDto(saved);
    }

    public List<AppUserDto> usersByRole(String roleName) {
        Role role = parseRole(roleName);
        return userRepository.findByRole(role, sortedUsers()).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public AppUserDto deleteUser(Long id) {
        AppUser user = getRequiredUser(id);
        user.setActif(false);
        AppUser saved = userRepository.save(user);
        try {
            keycloakService.deleteUserFromKeycloak(saved.getId());
        } catch (RuntimeException e) {
            log.warn("Suppression Keycloak échouée pour l'utilisateur {}: {}", saved.getId(), e.getMessage());
        }
        return toDto(saved);
    }

    public List<AppUserDto> doctors() {
        return userRepository.findByRoleIn(List.of(Role.ADMIN, Role.MEDECIN), sortedUsers()).stream()
                .map(this::toDto)
                .toList();
    }

    public List<AppUserDto> staff() {
        return userRepository.findByRoleNot(Role.PATIENT, sortedUsers()).stream()
                .map(this::toDto)
                .toList();
    }

    private AppUser getRequiredUser(Long id) {
        require(id != null, "id obligatoire");
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
    }

    private void syncUserToKeycloak(AppUser user) {
        try {
            keycloakService.syncUserToKeycloak(user.getId());
        } catch (RuntimeException e) {
            log.warn("Synchronisation Keycloak échouée pour l'utilisateur {}: {}", user.getId(), e.getMessage());
        }
    }

    private Pageable normalizePageable(Pageable pageable) {
        int page = pageable == null ? 0 : Math.max(pageable.getPageNumber(), 0);
        int size = pageable == null ? 20 : Math.min(Math.max(pageable.getPageSize(), 1), 100);
        Sort sort = pageable == null || pageable.getSort().isUnsorted()
                ? Sort.by(Sort.Direction.ASC, "nom", "prenom", "email")
                : pageable.getSort();
        return PageRequest.of(page, size, sort);
    }

    private Sort sortedUsers() {
        return Sort.by(Sort.Direction.ASC, "nom", "prenom", "email");
    }

    private AppUserDto toDto(AppUser user) {
        return AppUserDto.builder()
                .id(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .email(user.getEmail())
                .role(user.getRole())
                .actif(user.isActif())
                .dateCreation(user.getCreeLe())
                .dateModification(user.getMisAJourLe())
                .build();
    }

    private Role parseRoleOrDefault(String role) {
        if (role == null || role.isBlank()) {
            return Role.PATIENT;
        }
        try {
            return Role.valueOf(role.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return Role.PATIENT;
        }
    }

    private Role parseRole(String role) {
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("role obligatoire");
        }
        try {
            return Role.valueOf(role.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Rôle invalide: " + role);
        }
    }

    private String valueOr(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private void requireNotBlank(String value, String field) {
        require(value != null && !value.isBlank(), field + " obligatoire");
    }

    private void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : bytes) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Hash SHA-256 indisponible", e);
        }
    }
}
