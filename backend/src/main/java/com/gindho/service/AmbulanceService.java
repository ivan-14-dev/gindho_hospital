package com.gindho.service;
import com.gindho.dto.AmbulanceDto;
import com.gindho.model.Ambulance;
import com.gindho.repository.AmbulanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class AmbulanceService {
    private final AmbulanceRepository ambulanceRepository;

    public List<AmbulanceDto> list() {
        return ambulanceRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public AmbulanceDto create(AmbulanceDto dto) {
        Ambulance a = Ambulance.builder().immatriculation(dto.getImmatriculation()).statut("DISPONIBLE").build();
        return toDto(ambulanceRepository.save(a));
    }

    @Transactional
    public AmbulanceDto updatePosition(Long id, Double lat, Double lng) {
        Ambulance a = ambulanceRepository.findById(id).orElseThrow();
        a.setDerniereLatitude(lat); a.setDerniereLongitude(lng);
        a.setDerniereMiseAJour(java.time.LocalDateTime.now());
        return toDto(ambulanceRepository.save(a));
    }

    private AmbulanceDto toDto(Ambulance a) {
        return AmbulanceDto.builder().id(a.getId()).immatriculation(a.getImmatriculation())
            .statut(a.getStatut()).derniereLatitude(a.getDerniereLatitude())
            .derniereLongitude(a.getDerniereLongitude()).derniereMiseAJour(a.getDerniereMiseAJour()).build();
    }
}