package com.gindho.dto;

public record ResetPasswordRequest(
        String email,
        String code,
        String newPassword
) {}
