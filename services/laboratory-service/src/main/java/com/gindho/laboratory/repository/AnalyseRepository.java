package com.gindho.laboratory.repository;
import com.gindho.laboratory.model.Analyse;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface AnalyseRepository extends JpaRepository<Analyse, Long> {
    List<Analyse> findByPatientIdOrderByDatePrescriptionDesc(Long patientId);
    List<Analyse> findByMedecinIdOrderByDatePrescriptionDesc(Long medecinId);
    List<Analyse> findByStatut(Analyse.StatutAnalyse statut);
    List<Analyse> findByUrgentTrueOrderByDatePrescriptionDesc();
}