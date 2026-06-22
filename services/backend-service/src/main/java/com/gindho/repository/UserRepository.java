package com.gindho.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gindho.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = "permissions")
    @Query("select u from User u where u.email = :email")
    Optional<User> findByEmailWithPermissions(@Param("email") String email);

    @EntityGraph(attributePaths = "permissions")
    @Query("select u from User u where u.id = :id")
    Optional<User> findUserWithPermissionsById(@Param("id") Long id);

    @EntityGraph(attributePaths = "permissions")
    @Query("select u from User u")
    List<User> findAllWithPermissions();

    boolean existsByEmail(String email);
}
