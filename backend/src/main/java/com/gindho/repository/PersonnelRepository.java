package com.gindho.repository;
import com.gindho.model.Personnel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface PersonnelRepository extends JpaRepository<Personnel, Long> {
    Optional<Personnel> findByEmail(String email);
    Optional<Personnel> findByUserId(Long userId);
}