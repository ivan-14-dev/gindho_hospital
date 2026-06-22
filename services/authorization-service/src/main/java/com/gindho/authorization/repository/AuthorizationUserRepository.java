package com.gindho.authorization.repository;

import com.gindho.authorization.model.AuthorizationUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorizationUserRepository extends JpaRepository<AuthorizationUser, Long> {
    Optional<AuthorizationUser> findByEmail(String email);
    boolean existsByEmail(String email);
}
