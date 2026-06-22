package com.gindho.identity.controller;

import com.gindho.base.ApiResponse;
import com.gindho.identity.dto.AuthenticationRequest;
import com.gindho.identity.dto.AuthenticationResponse;
import com.gindho.identity.dto.ForgotPasswordRequest;
import com.gindho.identity.dto.MeAuthoritiesResponse;
import com.gindho.identity.dto.ResetPasswordRequest;
import com.gindho.identity.service.IdentityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IdentityService identityService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> register(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(ApiResponse.of(identityService.register(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(@RequestBody AuthenticationRequest request) {
        if (request.getEmail() != null && request.getPassword() != null) {
            return ResponseEntity.ok(ApiResponse.of(identityService.login(request.getEmail(), request.getPassword())));
        }
        return ResponseEntity.ok(ApiResponse.of(identityService.register(request)));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        identityService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Si le compte existe, un code de réinitialisation a été envoyé").build());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody ResetPasswordRequest request) {
        identityService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Mot de passe réinitialisé").build());
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MeAuthoritiesResponse>> me(@AuthenticationPrincipal Jwt jwt) {
        Long userId = resolveUserId(jwt);
        if (userId == null) {
            return ResponseEntity.ok(ApiResponse.error("Utilisateur non authentifié"));
        }
        return ResponseEntity.ok(ApiResponse.of(identityService.me(userId)));
    }

    @GetMapping("/me-authorities")
    public ResponseEntity<ApiResponse<MeAuthoritiesResponse>> meAuthorities(@AuthenticationPrincipal Jwt jwt) {
        return me(jwt);
    }

    private Long resolveUserId(Jwt jwt) {
        if (jwt == null || jwt.getSubject() == null) {
            return null;
        }
        String subject = jwt.getSubject();
        if (subject.startsWith("local-token-")) {
            subject = subject.substring("local-token-".length());
        }
        try {
            return Long.valueOf(subject);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
