package com.gindho.appointment.repository;

import com.gindho.appointment.model.WaitingListEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaitingListRepository extends JpaRepository<WaitingListEntry, Long> {

    List<WaitingListEntry> findByMedecinIdOrderByPrioriteDescDateAjoutAsc(Long medecinId);

    List<WaitingListEntry> findByPatientIdOrderByDateAjoutDesc(Long patientId);
}