package com.gindho.repository;

import com.gindho.model.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {

    Optional<PasswordResetOtp> findFirstByEmailAndConsumedFalseAndExpiresAtAfterOrderByIdDesc(
            String email,
            LocalDateTime now
    );
}
