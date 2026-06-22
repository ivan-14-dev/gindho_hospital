package com.gindho.identity.repository;

import com.gindho.identity.model.AppUser;
import com.gindho.identity.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<AppUser> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String nom,
            String prenom,
            String email,
            Pageable pageable
    );

    List<AppUser> findByRole(Role role, Sort sort);

    List<AppUser> findByRoleIn(List<Role> roles, Sort sort);

    List<AppUser> findByRoleNot(Role role, Sort sort);
}
