package com.gindho.repository;
import com.gindho.model.AuditQualite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface AuditQualiteRepository extends JpaRepository<AuditQualite, Long> {
    List<AuditQualite> findByTypeAudit(String type);
    List<AuditQualite> findByScoreLessThan(int seuil);
}