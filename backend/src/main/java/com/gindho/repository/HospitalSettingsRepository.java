package com.gindho.repository;

import com.gindho.model.HospitalSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HospitalSettingsRepository extends JpaRepository<HospitalSettings, Long> {
    Optional<HospitalSettings> findFirstByOrderByIdAsc();
}
