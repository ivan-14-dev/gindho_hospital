package com.gindho.authorization.repository;

import com.gindho.authorization.model.RoleTemplate;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleTemplateRepository extends JpaRepository<RoleTemplate, Long> {
    Optional<RoleTemplate> findByName(String name);

    @EntityGraph(attributePaths = "permissions")
    List<RoleTemplate> findAll();
}
