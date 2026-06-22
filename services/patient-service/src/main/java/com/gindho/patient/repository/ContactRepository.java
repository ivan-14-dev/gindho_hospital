package com.gindho.patient.repository;

import com.gindho.patient.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByPatientId(Long patientId);
}