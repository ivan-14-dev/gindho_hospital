package com.gindho.emr.repository;

import com.gindho.emr.model.Diagnostic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiagnosticRepository extends JpaRepository<Diagnostic, Long> {
    List<Diagnostic> findByConsultationId(Long consultationId);
}
