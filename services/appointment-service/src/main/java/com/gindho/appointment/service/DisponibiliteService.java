package com.gindho.appointment.service;

import com.gindho.appointment.dto.DisponibiliteDto;
import com.gindho.appointment.model.Disponibilite;
import com.gindho.appointment.repository.DisponibiliteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DisponibiliteService {

    private final DisponibiliteRepository disponibiliteRepository;

    public List<DisponibiliteDto> findByMedecinAndDateRange(Long medecinId, LocalDate start, LocalDate end) {
        return disponibiliteRepository.findByMedecinIdAndDateBetween(medecinId, start, end)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<DisponibiliteDto> createWeekAvailability(Long medecinId, LocalDate startDate, LocalTime heureDebut,
                                                         LocalTime heureFin, int slotDurationMinutes) {
        List<Disponibilite> disponibilites = new ArrayList<>();

        for (int day = 0; day < 7; day++) {
            LocalDate date = startDate.plusDays(day);
            LocalTime slotStart = heureDebut;

            while (slotStart.plusMinutes(slotDurationMinutes).isBefore(heureFin)
                    || slotStart.plusMinutes(slotDurationMinutes).equals(heureFin)) {
                Disponibilite dispo = new Disponibilite();
                dispo.setMedecinId(medecinId);
                dispo.setDate(date);
                dispo.setHeureDebut(slotStart);
                dispo.setHeureFin(slotStart.plusMinutes(slotDurationMinutes));
                dispo.setDisponible(true);
                disponibilites.add(dispo);

                slotStart = slotStart.plusMinutes(slotDurationMinutes);
            }
        }

        List<Disponibilite> saved = disponibiliteRepository.saveAll(disponibilites);
        log.info("Created {} availability slots for medecin {} starting {}", saved.size(), medecinId, startDate);
        return saved.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public DisponibiliteDto create(DisponibiliteDto dto) {
        Disponibilite dispo = new Disponibilite();
        dispo.setMedecinId(dto.getMedecinId());
        dispo.setDate(dto.getDate());
        dispo.setHeureDebut(dto.getHeureDebut());
        dispo.setHeureFin(dto.getHeureFin());
        dispo.setDisponible(dto.isDisponible());
        Disponibilite saved = disponibiliteRepository.save(dispo);
        return toDto(saved);
    }

    @Transactional
    public Optional<DisponibiliteDto> update(Long id, DisponibiliteDto dto) {
        return disponibiliteRepository.findById(id)
                .map(dispo -> {
                    dispo.setMedecinId(dto.getMedecinId());
                    dispo.setDate(dto.getDate());
                    dispo.setHeureDebut(dto.getHeureDebut());
                    dispo.setHeureFin(dto.getHeureFin());
                    dispo.setDisponible(dto.isDisponible());
                    Disponibilite saved = disponibiliteRepository.save(dispo);
                    return toDto(saved);
                });
    }

    @Transactional
    public void delete(Long id) {
        if (!disponibiliteRepository.existsById(id)) {
            throw new jakarta.persistence.EntityNotFoundException("Disponibilité introuvable: " + id);
        }
        disponibiliteRepository.deleteById(id);
    }

    private DisponibiliteDto toDto(Disponibilite dispo) {
        DisponibiliteDto dto = new DisponibiliteDto();
        dto.setId(dispo.getId());
        dto.setMedecinId(dispo.getMedecinId());
        dto.setDate(dispo.getDate());
        dto.setHeureDebut(dispo.getHeureDebut());
        dto.setHeureFin(dispo.getHeureFin());
        dto.setDisponible(dispo.isDisponible());
        return dto;
    }
}