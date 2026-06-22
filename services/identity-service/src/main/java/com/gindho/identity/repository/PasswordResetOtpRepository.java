package com.gindho.identity.repository;

import com.gindho.identity.model.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {
    List<PasswordResetOtp> findByEmailAndConsumedFalse(String email);
}
