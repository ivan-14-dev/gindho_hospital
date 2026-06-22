package com.gindho.identity.service;

import com.gindho.identity.dto.AuthenticationRequest;
import com.gindho.identity.dto.AuthenticationResponse;
import com.gindho.identity.dto.ForgotPasswordRequest;
import com.gindho.identity.dto.ResetPasswordRequest;
import com.gindho.identity.dto.AppUserDto;
import com.gindho.identity.model.AppUser;
import com.gindho.identity.model.Role;
import com.gindho.identity.repository.UserRepository;
import com.gindho.identity.repository.PasswordResetOtpRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdentityServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordResetOtpRepository otpRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private KeycloakService keycloakService;

    private IdentityService identityService;

    @BeforeEach
    void setUp() throws Exception {
        var constructor = IdentityService.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        identityService = (IdentityService) constructor.newInstance(
                userRepository, otpRepository, passwordEncoder, keycloakService);
    }

    @Test
    void testRegister() {
        var request = new AuthenticationRequest();
        request.setEmail("test@test.com");
        request.setPassword("password123");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any())).thenAnswer(inv -> {
            var u = inv.<AppUser>getArgument(0);
            u.setId(1L);
            return u;
        });

        var response = identityService.register(request);
        assertNotNull(response);
        assertNotNull(response.getToken());
        verify(userRepository).save(any());
    }

    @Test
    void testLoginInvalidPassword() {
        var user = new AppUser();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setPasswordHash("encoded");
        user.setActif(true);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> identityService.login("test@test.com", "wrong"));
    }

    @Test
    void testForgotPassword() {
        var user = new AppUser();
        user.setId(1L);
        user.setEmail("test@test.com");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        var request = new ForgotPasswordRequest("test@test.com");
        assertDoesNotThrow(() -> identityService.forgotPassword(request));
        verify(otpRepository).save(any());
    }

    @Test
    void testResetPassword() {
        when(otpRepository.findByEmailAndConsumedFalse(anyString()))
                .thenReturn(List.of(new com.gindho.identity.model.PasswordResetOtp()));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new AppUser()));

        var request = new ResetPasswordRequest("test@test.com", "123456", "newPass123");
        assertDoesNotThrow(() -> identityService.resetPassword(request));
        verify(userRepository).save(any());
    }

    @Test
    void testMe() {
        var user = new AppUser();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setNom("Test");
        user.setPrenom("User");
        user.setActif(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        var response = identityService.me(1L);
        assertNotNull(response);
        assertEquals(1L, response.getUserId());
    }

    @Test
    void testListUsers() {
        when(userRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of()));

        var result = identityService.listUsers(null, PageRequest.of(0, 10));
        assertNotNull(result);
    }

    @Test
    void testDoctors() {
        when(userRepository.findByRoleIn(anyList(), any(Sort.class)))
                .thenReturn(List.of(new AppUser()));
        var doctors = identityService.doctors();
        assertNotNull(doctors);
    }

    @Test
    void testStaff() {
        when(userRepository.findByRoleNot(any(com.gindho.identity.model.Role.class), any(org.springframework.data.domain.Sort.class)))
                .thenReturn(java.util.List.of(new AppUser()));
        var staff = identityService.staff();
        assertNotNull(staff);
    }

    @Test
    void testGetUser() {
        var user = new AppUser();
        user.setId(1L);
        user.setEmail("test@test.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        var result = identityService.getUser(1L);
        assertTrue(result.isPresent());
    }
}
