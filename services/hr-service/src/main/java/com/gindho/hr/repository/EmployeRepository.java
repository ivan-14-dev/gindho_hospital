package com.gindho.hr.repository;
import com.gindho.hr.model.Employe; import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional;
public interface EmployeRepository extends JpaRepository<Employe, Long> {
    Optional<Employe> findByMatricule(String matricule);
    List<Employe> findByFonction(Employe.Fonction fonction);
}