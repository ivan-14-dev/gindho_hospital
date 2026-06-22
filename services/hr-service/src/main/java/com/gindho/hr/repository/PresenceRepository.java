package com.gindho.hr.repository;

import com.gindho.hr.model.Presence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PresenceRepository extends JpaRepository<Presence, Long> {
    List<Presence> findByEmployeId(Long employeId);
}
