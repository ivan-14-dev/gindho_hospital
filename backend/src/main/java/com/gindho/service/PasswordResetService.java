package com.gindho.service;

import com.gindho.model.PasswordResetOtp;
import com.gindho.model.User;
import com.gindho.repository.PasswordResetOtpRepository;
import com.gindho.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;

import jakarta.mail.internet.MimeMessage;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final int OTP_LENGTH = 6;
    private static final int OTP_TTL_MINUTES = 3;

    // Ne pas réutiliser un Random faible.
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final PasswordResetOtpRepository passwordResetOtpRepository;
    private final PasswordEncoder passwordEncoder;

    // On utilise JavaMailSender en direct pour éviter de dépendre d'un template manquant au début.
    private final JavaMailSender mailSender;

    public void sendForgotPasswordOtp(String email) {
        // Anti-enumeration: on ne révèle pas si l'email existe ou non.
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return;
        }

        String code = generateOtpCode();
        String codeHash = sha256Hex(code);

        PasswordResetOtp otp = PasswordResetOtp.builder()
                .email(email)
                .codeHash(codeHash)
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_TTL_MINUTES))
                .consumed(false)
                .build();

        passwordResetOtpRepository.save(otp);

        // Email: code 6 chiffres + expiration 3 minutes
        sendOtpEmail(email, code);
    }

    public void resetPasswordWithOtp(String email, String code, String newPassword) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email manquant");
        }
        if (code == null || !code.matches("\\d{" + OTP_LENGTH + "}") ) {
            throw new IllegalArgumentException("Code invalide");
        }
        if (newPassword == null || newPassword.isBlank() || newPassword.length() < 8) {
            throw new IllegalArgumentException("Mot de passe invalide (min 8 caractères)");
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            // Anti-enumeration
            throw new IllegalArgumentException("Impossible de réinitialiser le mot de passe");
        }

        var now = LocalDateTime.now();
        var otpOpt = passwordResetOtpRepository.findFirstByEmailAndConsumedFalseAndExpiresAtAfterOrderByIdDesc(
                email,
                now
        );

        if (otpOpt.isEmpty()) {
            throw new IllegalArgumentException("Code expiré ou déjà utilisé");
        }

        PasswordResetOtp otp = otpOpt.get();
        String codeHash = sha256Hex(code);

        if (!otp.getCodeHash().equals(codeHash)) {
            throw new IllegalArgumentException("Code incorrect");
        }

        // Consomme OTP avant update password (sécurité)
        otp.setConsumed(true);
        passwordResetOtpRepository.save(otp);

        User user = userOpt.get();
        user.setMotDePasseHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private String generateOtpCode() {
        int n = SECURE_RANDOM.nextInt(1_000_000); // 0..999999
        return String.format("%0" + OTP_LENGTH + "d", n);
    }

    private String sha256Hex(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Erreur hashing OTP", e);
        }
    }

    private void sendOtpEmail(String email, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Votre code de réinitialisation de mot de passe");
            helper.setText(
                    "Bonjour,\n\n" +
                    "Votre code de réinitialisation est : " + code + "\n" +
                    "Il expire dans 3 minutes.\n\n" +
                    "Si vous n'êtes pas à l'origine de cette demande, ignorez cet email.\n",
                    false
            );

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email OTP", e);
        }
    }
}
