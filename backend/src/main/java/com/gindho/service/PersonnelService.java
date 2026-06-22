package com.gindho.service;
import com.gindho.dto.PersonnelDto;
import com.gindho.model.Personnel;
import com.gindho.repository.PersonnelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class PersonnelService {
    private final PersonnelRepository personnelRepository;

    public List<PersonnelDto> list() {
        return personnelRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public PersonnelDto create(PersonnelDto dto) {
        Personnel p = Personnel.builder().nom(dto.getNom()).prenom(dto.getPrenom()).email(dto.getEmail())
            .telephone(dto.getTelephone()).poste(dto.getPoste()).departement(dto.getDepartement())
            .dateEmbauche(dto.getDateEmbauche()).salaireBase(dto.getSalaireBase()).actif(true).build();
        return toDto(personnelRepository.save(p));
    }

    @Transactional
    public void delete(Long id) { personnelRepository.deleteById(id); }

    private PersonnelDto toDto(Personnel p) {
        return PersonnelDto.builder().id(p.getId()).nom(p.getNom()).prenom(p.getPrenom()).email(p.getEmail())
            .telephone(p.getTelephone()).poste(p.getPoste()).departement(p.getDepartement())
            .dateEmbauche(p.getDateEmbauche()).salaireBase(p.getSalaireBase()).actif(p.isActif())
            .userId(p.getUser() != null ? p.getUser().getId() : null).build();
    }
}