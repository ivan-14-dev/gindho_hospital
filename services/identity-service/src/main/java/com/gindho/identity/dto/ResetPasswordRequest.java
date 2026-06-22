package com.gindho.identity.dto;

public record ResetPasswordRequest(String email, String code, String newPassword) {
}
