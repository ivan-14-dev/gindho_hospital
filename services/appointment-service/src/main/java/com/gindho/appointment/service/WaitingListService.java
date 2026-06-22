package com.gindho.appointment.service;

import com.gindho.appointment.dto.WaitingListDto;
import com.gindho.appointment.model.WaitingListEntry;
import com.gindho.appointment.model.WaitingStatus;
import com.gindho.appointment.repository.WaitingListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WaitingListService {

    private final WaitingListRepository waitingListRepository;

    public List<WaitingListDto> findByMedecin(Long medecinId) {
        return waitingListRepository.findByMedecinIdOrderByPrioriteDescDateAjoutAsc(medecinId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public WaitingListDto create(WaitingListDto dto) {
        WaitingListEntry entry = new WaitingListEntry();
        entry.setPatientId(dto.getPatientId());
        entry.setMedecinId(dto.getMedecinId());
        entry.setDateAjout(LocalDateTime.now());
        entry.setPriorite(dto.getPriorite());
        entry.setStatut(WaitingStatus.EN_ATTENTE);
        entry.setMotif(dto.getMotif());

        WaitingListEntry saved = waitingListRepository.save(entry);
        log.info("Added patient {} to waiting list for medecin {}", saved.getPatientId(), saved.getMedecinId());
        return toDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        waitingListRepository.deleteById(id);
        log.info("Deleted waiting list entry {}", id);
    }

    @Transactional
    public Optional<WaitingListDto> markPlaced(Long id) {
        return waitingListRepository.findById(id)
                .map(entry -> {
                    entry.setStatut(WaitingStatus.PLACE);
                    WaitingListEntry saved = waitingListRepository.save(entry);
                    return toDto(saved);
                });
    }

    private WaitingListDto toDto(WaitingListEntry entry) {
        WaitingListDto dto = new WaitingListDto();
        dto.setId(entry.getId());
        dto.setPatientId(entry.getPatientId());
        dto.setMedecinId(entry.getMedecinId());
        dto.setDateAjout(entry.getDateAjout());
        dto.setPriorite(entry.getPriorite());
        dto.setStatut(entry.getStatut() != null ? entry.getStatut().name() : null);
        dto.setMotif(entry.getMotif());
        return dto;
    }
}