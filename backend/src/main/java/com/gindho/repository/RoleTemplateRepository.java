package com.gindho.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gindho.model.RoleTemplate;

public interface RoleTemplateRepository extends JpaRepository<RoleTemplate, Long> {

    Optional<RoleTemplate> findByName(String name);

    @EntityGraph(attributePaths = "permissions")
    @Query("select t from RoleTemplate t where t.id = :id")
    Optional<RoleTemplate> findByIdWithPermissions(Long id);

    @EntityGraph(attributePaths = "permissions")
    @Query("select t from RoleTemplate t")
    List<RoleTemplate> findAllWithPermissions();
}
