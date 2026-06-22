package com.gindho.authorization.repository;

import com.gindho.authorization.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByUserId(String userId);
    List<UserRole> findByRoleIdIn(List<Long> roleIds);
    List<UserRole> findByRoleId(Long roleId);
}
