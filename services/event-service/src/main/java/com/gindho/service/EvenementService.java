package com.gindho.event.service;

import com.gindho.event.dto.EvenementDto;
import com.gindho.event.model.Evenement;
import com.gindho.event.repository.EvenementRepository;
import com.gindho.kafka.BaseEvent;
import com.gindho.kafka.EventProducer;
import com.gindho.kafka.EventType;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EvenementService {

    private final EvenementRepository repository;
    private final EventProducer eventProducer;

    public List<EvenementDto> list() {
        return repository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Page<EvenementDto> list(Pageable pageable) {
        return repository.findAll(pageable)
                .map(this::toDto);
    }

    public EvenementDto findById(Long id) {
        Evenement e = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Événement non trouvé: " + id));
        return toDto(e);
    }

    @Transactional
    public EvenementDto create(EvenementDto dto) {
        Evenement e = new Evenement();
        e.setTitre(dto.getTitre());
        e.setDescription(dto.getDescription());
        e.setTypeEvenement(dto.getTypeEvenement());
        e.setDateDebut(dto.getDateDebut());
        e.setDateFin(dto.getDateFin());
        e.setValide(false);

        Evenement saved = repository.save(e);
        eventProducer.publish("events", BaseEvent.builder()
                .eventType(EventType.EVENT_CREATED)
                .source("event-service")
                .payload(dto)
                .build());
        log.info("Événement créé: id={} titre={}", saved.getId(), saved.getTitre());
        return toDto(saved);
    }

    @Transactional
    public EvenementDto valider(Long id) {
        Evenement e = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Événement non trouvé: " + id));
        e.setValide(true);
        Evenement saved = repository.save(e);
        return toDto(saved);
    }

    private EvenementDto toDto(Evenement e) {
        return EvenementDto.builder()
                .id(e.getId())
                .titre(e.getTitre())
                .description(e.getDescription())
                .typeEvenement(e.getTypeEvenement())
                .dateDebut(e.getDateDebut())
                .dateFin(e.getDateFin())
                .valide(e.isValide())
                .build();
    }
}
