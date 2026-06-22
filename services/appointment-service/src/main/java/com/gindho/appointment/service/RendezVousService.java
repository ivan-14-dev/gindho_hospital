package com.gindho.appointment.service;

import com.gindho.appointment.dto.RendezVousCancelRequest;
import com.gindho.appointment.dto.RendezVousUpdateRequest;
import com.gindho.appointment.dto.RendezVousDto;
import com.gindho.appointment.model.Disponibilite;
import com.gindho.appointment.model.RendezVous;
import com.gindho.appointment.model.StatutRDV;
import com.gindho.appointment.repository.DisponibiliteRepository;
import com.gindho.appointment.repository.RendezVousRepository;
import com.gindho.kafka.BaseEvent;
import com.gindho.kafka.EventProducer;
import com.gindho.kafka.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RendezVousService {

    private final RendezVousRepository rendezVousRepository;
    private final DisponibiliteRepository disponibiliteRepository;
    private final EventProducer eventProducer;

    public Page<RendezVousDto> findAll(Pageable pageable) {
        return rendezVousRepository.findAll(pageable)
                .map(this::toDto);
    }

    public List<RendezVousDto> findByUtilisateurId(Long utilisateurId) {
        return rendezVousRepository.findAll().stream()
                .filter(rdv -> utilisateurId.equals(rdv.getPatientId()) || utilisateurId.equals(rdv.getMedecinId()))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Page<RendezVousDto> findByUtilisateurId(Long utilisateurId, Pageable pageable) {
        List<RendezVousDto> list = findByUtilisateurId(utilisateurId);
        return new PageImpl<>(list, pageable, list.size());
    }

    public List<RendezVousDto> upcoming(Long medecinId, Long patientId) {
        LocalDateTime now = LocalDateTime.now();
        List<RendezVous> rendezVous;
        if (medecinId != null) {
            rendezVous = rendezVousRepository.findByMedecinIdAndDateHeureDebutGreaterThanEqualOrderByDateHeureDebutAsc(medecinId, now);
        } else if (patientId != null) {
            rendezVous = rendezVousRepository.findByPatientIdAndDateHeureDebutGreaterThanEqualOrderByDateHeureDebutAsc(patientId, now);
        } else {
            rendezVous = rendezVousRepository.findByDateHeureDebutGreaterThanEqualOrderByDateHeureDebutAsc(now);
        }
        return rendezVous.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public Optional<RendezVousDto> update(Long id, RendezVousUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Requête de mise à jour manquante");
        }
        return rendezVousRepository.findById(id)
                .map(rdv -> {
                    if (request.getPatientId() != null) rdv.setPatientId(request.getPatientId());
                    if (request.getMedecinId() != null) rdv.setMedecinId(request.getMedecinId());
                    if (request.getDateHeureDebut() != null) rdv.setDateHeureDebut(request.getDateHeureDebut());
                    if (request.getDateHeureFin() != null) rdv.setDateHeureFin(request.getDateHeureFin());
                    if (request.getMotif() != null) rdv.setMotif(request.getMotif());
                    if (request.getStatut() != null) rdv.setStatut(StatutRDV.valueOf(request.getStatut().toUpperCase()));
                    if (request.getNotes() != null) rdv.setNotes(request.getNotes());
                    if (request.getTypeConsultation() != null) rdv.setTypeConsultation(request.getTypeConsultation());
                    RendezVous saved = rendezVousRepository.save(rdv);
                    publishStatusEvent(saved, EventType.APPOINTMENT_BOOKED);
                    return toDto(saved);
                });
    }

    @Transactional
    public Optional<RendezVousDto> updateStatut(Long id, String statut) {
        if (statut == null || statut.isBlank()) {
            throw new IllegalArgumentException("Statut obligatoire");
        }
        return rendezVousRepository.findById(id)
                .map(rdv -> {
                    rdv.setStatut(StatutRDV.valueOf(statut.trim().toUpperCase()));
                    RendezVous saved = rendezVousRepository.save(rdv);
                    publishStatusEvent(saved, EventType.APPOINTMENT_BOOKED);
                    return toDto(saved);
                });
    }

    @Transactional
    public Optional<RendezVousDto> cancelWithReason(Long id, String motif) {
        return rendezVousRepository.findById(id)
                .map(rdv -> {
                    rdv.setStatut(StatutRDV.ANNULE);
                    if (motif != null && !motif.isBlank()) {
                        rdv.setNotes((rdv.getNotes() == null ? "" : rdv.getNotes()) + "\nMotif annulation: " + motif.trim());
                    }
                    RendezVous saved = rendezVousRepository.save(rdv);
                    publishStatusEvent(saved, EventType.APPOINTMENT_CANCELLED);
                    return toDto(saved);
                });
    }

    @Transactional
    public void delete(Long id) {
        if (!rendezVousRepository.existsById(id)) {
            throw new jakarta.persistence.EntityNotFoundException("Rendez-vous introuvable: " + id);
        }
        rendezVousRepository.deleteById(id);
    }

    public Optional<RendezVousDto> findById(Long id) {
        return rendezVousRepository.findById(id)
                .map(this::toDto);
    }

    public List<RendezVousDto> findByPatient(Long patientId) {
        return rendezVousRepository.findByPatientIdOrderByDateHeureDebutDesc(patientId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<RendezVousDto> findByMedecin(Long medecinId, LocalDateTime start, LocalDateTime end) {
        return rendezVousRepository.findByMedecinIdAndDateHeureDebutBetween(medecinId, start, end)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RendezVousDto create(RendezVousDto dto) {
        RendezVous rendezVous = new RendezVous();
        rendezVous.setPatientId(dto.getPatientId());
        rendezVous.setMedecinId(dto.getMedecinId());
        rendezVous.setDateHeureDebut(dto.getDateHeureDebut());
        rendezVous.setDateHeureFin(dto.getDateHeureFin());
        rendezVous.setMotif(dto.getMotif());
        rendezVous.setStatut(StatutRDV.PROGRAMME);
        rendezVous.setNotes(dto.getNotes());
        rendezVous.setTypeConsultation(dto.getTypeConsultation());

        // Check availability and mark slot as unavailable
        LocalDate date = dto.getDateHeureDebut().toLocalDate();
        List<Disponibilite> disponibilites = disponibiliteRepository
                .findByMedecinIdAndDateAndDisponibleTrue(dto.getMedecinId(), date);

        boolean slotFound = false;
        for (Disponibilite dispo : disponibilites) {
            if (!dispo.getHeureDebut().isAfter(dto.getDateHeureDebut().toLocalTime())
                    && !dispo.getHeureFin().isBefore(dto.getDateHeureFin().toLocalTime())) {
                dispo.setDisponible(false);
                disponibiliteRepository.save(dispo);
                slotFound = true;
                break;
            }
        }

        if (!slotFound) {
            throw new IllegalStateException("Aucun créneau disponible pour cette plage horaire");
        }

        RendezVous saved = rendezVousRepository.save(rendezVous);

        // Publish AppointmentBooked event
        Map<String, Object> payload = new HashMap<>();
        payload.put("rendezVousId", saved.getId());
        payload.put("patientId", saved.getPatientId());
        payload.put("medecinId", saved.getMedecinId());
        payload.put("dateHeureDebut", saved.getDateHeureDebut().toString());
        payload.put("statut", saved.getStatut().name());

        BaseEvent event = new BaseEvent();
        event.setEventId(java.util.UUID.randomUUID().toString());
        event.setEventType(EventType.APPOINTMENT_BOOKED);
        event.setSource("appointment-service");
        event.setTimestamp(LocalDateTime.now());
        event.setPayload(payload);

        eventProducer.publish("appointment", event);
        log.info("AppointmentBooked event published for RDV id: {}", saved.getId());

        return toDto(saved);
    }

    @Transactional
    public Optional<RendezVousDto> confirm(Long id) {
        return rendezVousRepository.findById(id)
                .map(rdv -> {
                    rdv.setStatut(StatutRDV.CONFIRME);
                    RendezVous saved = rendezVousRepository.save(rdv);
                    return toDto(saved);
                });
    }

    @Transactional
    public Optional<RendezVousDto> cancel(Long id) {
        return rendezVousRepository.findById(id)
                .map(rdv -> {
                    rdv.setStatut(StatutRDV.ANNULE);
                    RendezVous saved = rendezVousRepository.save(rdv);

                    // Restore availability slot
                    LocalDate date = rdv.getDateHeureDebut().toLocalDate();
                    List<Disponibilite> dispoList = disponibiliteRepository
                            .findByMedecinIdAndDateAndDisponibleTrue(rdv.getMedecinId(), date);
                    // Re-enable the matching slot if it was disabled
                    disponibiliteRepository.findByMedecinIdAndDate(rdv.getMedecinId(), date)
                            .stream()
                            .filter(d -> !d.isDisponible()
                                    && !d.getHeureDebut().isAfter(rdv.getDateHeureDebut().toLocalTime())
                                    && !d.getHeureFin().isBefore(rdv.getDateHeureFin().toLocalTime()))
                            .findFirst()
                            .ifPresent(d -> {
                                d.setDisponible(true);
                                disponibiliteRepository.save(d);
                            });

                    // Publish AppointmentCancelled event
                    Map<String, Object> payload = new HashMap<>();
                    payload.put("rendezVousId", saved.getId());
                    payload.put("patientId", saved.getPatientId());
                    payload.put("medecinId", saved.getMedecinId());
                    payload.put("statut", saved.getStatut().name());

                    BaseEvent event = new BaseEvent();
                    event.setEventId(java.util.UUID.randomUUID().toString());
                    event.setEventType(EventType.APPOINTMENT_CANCELLED);
                    event.setSource("appointment-service");
                    event.setTimestamp(LocalDateTime.now());
                    event.setPayload(payload);

                    eventProducer.publish("appointment", event);
                    log.info("AppointmentCancelled event published for RDV id: {}", saved.getId());

                    return toDto(saved);
                });
    }

    @Transactional
    public Optional<RendezVousDto> complete(Long id) {
        return rendezVousRepository.findById(id)
                .map(rdv -> {
                    rdv.setStatut(StatutRDV.TERMINE);
                    RendezVous saved = rendezVousRepository.save(rdv);
                    return toDto(saved);
                });
    }

    private void publishStatusEvent(RendezVous saved, String eventType) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("rendezVousId", saved.getId());
        payload.put("patientId", saved.getPatientId());
        payload.put("medecinId", saved.getMedecinId());
        payload.put("dateHeureDebut", saved.getDateHeureDebut() == null ? null : saved.getDateHeureDebut().toString());
        payload.put("statut", saved.getStatut() == null ? null : saved.getStatut().name());

        BaseEvent event = new BaseEvent();
        event.setEventId(java.util.UUID.randomUUID().toString());
        event.setEventType(eventType);
        event.setSource("appointment-service");
        event.setTimestamp(LocalDateTime.now());
        event.setPayload(payload);

        eventProducer.publish("appointment", event);
        log.info("{} event published for RDV id: {}", eventType, saved.getId());
    }

    private RendezVousDto toDto(RendezVous rdv) {
        RendezVousDto dto = new RendezVousDto();
        dto.setId(rdv.getId());
        dto.setPatientId(rdv.getPatientId());
        dto.setMedecinId(rdv.getMedecinId());
        dto.setDateHeureDebut(rdv.getDateHeureDebut());
        dto.setDateHeureFin(rdv.getDateHeureFin());
        dto.setMotif(rdv.getMotif());
        dto.setStatut(rdv.getStatut() != null ? rdv.getStatut().name() : null);
        dto.setNotes(rdv.getNotes());
        dto.setTypeConsultation(rdv.getTypeConsultation());
        return dto;
    }

    private RendezVous toEntity(RendezVousDto dto) {
        RendezVous rdv = new RendezVous();
        rdv.setId(dto.getId());
        rdv.setPatientId(dto.getPatientId());
        rdv.setMedecinId(dto.getMedecinId());
        rdv.setDateHeureDebut(dto.getDateHeureDebut());
        rdv.setDateHeureFin(dto.getDateHeureFin());
        rdv.setMotif(dto.getMotif());
        rdv.setStatut(dto.getStatut() != null ? StatutRDV.valueOf(dto.getStatut()) : null);
        rdv.setNotes(dto.getNotes());
        rdv.setTypeConsultation(dto.getTypeConsultation());
        return rdv;
    }
}