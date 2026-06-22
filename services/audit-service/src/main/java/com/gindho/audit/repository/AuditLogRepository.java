package com.gindho.audit.repository;
import com.gindho.audit.model.AuditLog; import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByResourceIdAndResourceOrderByDateActionDesc(String resourceId, String resource);
    List<AuditLog> findByActeurOrderByDateActionDesc(String acteur);
}