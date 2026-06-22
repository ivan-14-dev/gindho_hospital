package com.gindho.patient.service;

import com.gindho.patient.dto.MedecinDto;
import com.gindho.patient.model.Medecin;
import com.gindho.patient.repository.MedecinRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedecinService {

    private final MedecinRepository repository;

    public Page<MedecinDto> findAll(String search, Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return repository.findBySpecialisationContainingIgnoreCaseOrTelephoneCabinetContainingIgnoreCase(
                    search, search, pageable
            ).map(this::toDto);
        }
        return repository.findAll(pageable).map(this::toDto);
    }

    public MedecinDto findById(Long id) {
        Medecin m = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Médecin non trouvé: " + id));
        return toDto(m);
    }

    public MedecinDto findByUserId(Long userId) {
        Medecin m = repository.findByUserId(userId);
        if (m == null) {
            throw new EntityNotFoundException("Médecin non trouvé pour l'utilisateur: " + userId);
        }
        return toDto(m);
    }

    @Transactional
    public MedecinDto create(MedecinDto dto) {
        Medecin m = new Medecin();
        m.setNumeroOrdre(dto.getNumeroOrdre());
        m.setSpecialisation(dto.getSpecialisation());
        m.setTelephoneCabinet(dto.getTelephoneCabinet());
        m.setDisponible(dto.getDisponible() != null ? dto.getDisponible() : true);
        m.setUserId(dto.getUserId());
        Medecin saved = repository.save(m);
        return toDto(saved);
    }

    @Transactional
    public MedecinDto update(Long id, MedecinDto dto) {
        Medecin m = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Médecin non trouvé: " + id));
        if (dto.getNumeroOrdre() != null) m.setNumeroOrdre(dto.getNumeroOrdre());
        if (dto.getSpecialisation() != null) m.setSpecialisation(dto.getSpecialisation());
        if (dto.getTelephoneCabinet() != null) m.setTelephoneCabinet(dto.getTelephoneCabinet());
        if (dto.getDisponible() != null) m.setDisponible(dto.getDisponible());
        if (dto.getUserId() != null) m.setUserId(dto.getUserId());
        Medecin saved = repository.save(m);
        return toDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Médecin non trouvé: " + id);
        }
        repository.deleteById(id);
    }

    private MedecinDto toDto(Medecin m) {
        return MedecinDto.builder()
                .id(m.getId())
                .numeroOrdre(m.getNumeroOrdre())
                .specialisation(m.getSpecialisation())
                .telephoneCabinet(m.getTelephoneCabinet())
                .disponible(m.isDisponible())
                .userId(m.getUserId())
                .build();
    }
}
