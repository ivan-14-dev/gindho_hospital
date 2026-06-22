package com.gindho.admission.repository;
import com.gindho.admission.model.Hospitalisation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface HospitalisationRepository extends JpaRepository<Hospitalisation, Long> {
    List<Hospitalisation> findByPatientIdOrderByDateAdmissionDesc(Long patientId);
    List<Hospitalisation> findByStatut(Hospitalisation.StatutHospitalisation statut);
}