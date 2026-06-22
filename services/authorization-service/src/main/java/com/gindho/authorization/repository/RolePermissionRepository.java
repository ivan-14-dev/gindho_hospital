package com.gindho.authorization.repository;

import com.gindho.authorization.model.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    List<RolePermission> findByRoleIdIn(List<Long> roleIds);
    List<RolePermission> findByRoleId(Long roleId);
    List<RolePermission> findByPermissionId(Long permissionId);
    void deleteByRoleIdIn(List<Long> roleIds);
}
