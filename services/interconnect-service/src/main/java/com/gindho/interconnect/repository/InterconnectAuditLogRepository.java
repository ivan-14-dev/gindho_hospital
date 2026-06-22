package com.gindho.interconnect.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gindho.interconnect.model.InterconnectAuditLog;
import com.gindho.interconnect.model.InterconnectAuditLog.ActionType;

@Repository
public interface InterconnectAuditLogRepository extends JpaRepository<InterconnectAuditLog, Long> {

    List<InterconnectAuditLog> findBySourceHospitalId(String sourceHospitalId);

    List<InterconnectAuditLog> findByTargetHospitalId(String targetHospitalId);

    List<InterconnectAuditLog> findByActionType(ActionType actionType);

    List<InterconnectAuditLog> findByCorrelationId(String correlationId);

    List<InterconnectAuditLog> findByTimestampBetween(LocalDateTime from, LocalDateTime to);

    long countBySuccess(boolean success);
}