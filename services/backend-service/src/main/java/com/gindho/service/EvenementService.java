package com.gindho.service;
import com.gindho.dto.EvenementDto;
import com.gindho.model.Evenement;
import com.gindho.repository.EvenementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class EvenementService {
    private final EvenementRepository evenementRepository;

    public List<EvenementDto> list() {
        return evenementRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public EvenementDto create(EvenementDto dto) {
        Evenement e = Evenement.builder().titre(dto.getTitre()).description(dto.getDescription())
            .typeEvenement(dto.getTypeEvenement()).dateDebut(dto.getDateDebut()).dateFin(dto.getDateFin())
            .valide(false).build();
        return toDto(evenementRepository.save(e));
    }

    @Transactional
    public EvenementDto valider(Long id) {
        Evenement e = evenementRepository.findById(id).orElseThrow(() -> new RuntimeException("Événement non trouvé"));
        e.setValide(true);
        return toDto(evenementRepository.save(e));
    }

    private EvenementDto toDto(Evenement e) {
        return EvenementDto.builder().id(e.getId()).titre(e.getTitre()).description(e.getDescription())
            .typeEvenement(e.getTypeEvenement()).dateDebut(e.getDateDebut()).dateFin(e.getDateFin())
            .valide(e.isValide()).build();
    }
}