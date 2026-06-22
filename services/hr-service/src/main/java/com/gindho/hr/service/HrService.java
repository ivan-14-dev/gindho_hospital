package com.gindho.hr.service;

import com.gindho.hr.dto.CongeDto;
import com.gindho.hr.dto.EmployeDto;
import com.gindho.hr.dto.PresenceDto;
import com.gindho.hr.model.Conge;
import com.gindho.hr.model.Employe;
import com.gindho.hr.model.Presence;
import com.gindho.hr.repository.CongeRepository;
import com.gindho.hr.repository.EmployeRepository;
import com.gindho.hr.repository.PresenceRepository;
import com.gindho.kafka.BaseEvent;
import com.gindho.kafka.EventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HrService {
    private final EmployeRepository employeRepository;
    private final PresenceRepository presenceRepository;
    private final CongeRepository congeRepository;
    private final EventProducer eventProducer;

    public List<EmployeDto> findAll() {
        return employeRepository.findAll().stream().map(this::toEmployeDto).toList();
    }

    public EmployeDto findById(Long id) {
        return toEmployeDto(employeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Employé introuvable")));
    }

    public List<EmployeDto> findDoctors() {
        return employeRepository.findByFonction(Employe.Fonction.MEDECIN).stream().map(this::toEmployeDto).toList();
    }

    public EmployeDto findByUserId(Long userId) {
        return employeRepository.findAll().stream()
                .filter(e -> userId.equals(e.getId()))
                .map(this::toEmployeDto)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Employé introuvable pour userId=" + userId));
    }

    @Transactional
    public EmployeDto create(Employe employe) {
        return toEmployeDto(employeRepository.save(employe));
    }

    @Transactional
    public EmployeDto update(Long id, Employe employe) {
        Employe existing = employeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Employé introuvable"));
        existing.setMatricule(employe.getMatricule());
        existing.setNom(employe.getNom());
        existing.setPrenom(employe.getPrenom());
        existing.setEmail(employe.getEmail());
        existing.setTelephone(employe.getTelephone());
        existing.setFonction(employe.getFonction());
        existing.setSpecialite(employe.getSpecialite());
        existing.setDepartement(employe.getDepartement());
        existing.setDateEmbauche(employe.getDateEmbauche());
        existing.setActif(employe.isActif());
        return toEmployeDto(employeRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        Employe e = employeRepository.findById(id).orElse(null);
        if (e != null && e.getUserId() != null) {
            eventProducer.publish("hr", com.gindho.kafka.BaseEvent.builder()
                    .eventType("EMPLOYEE_DELETED")
                    .source("hr-service")
                    .payload(java.util.Map.of("userId", e.getUserId()))
                    .build());
        }
        employeRepository.deleteById(id);
    }

    @Transactional
    public PresenceDto pointer(Long employeId) {
        Presence presence = new Presence();
        presence.setEmployeId(employeId);
        presence.setDatePresence(LocalDateTime.now());
        presence.setType("ENTREE");
        return toPresenceDto(presenceRepository.save(presence));
    }

    public List<PresenceDto> listPresences(Long employeId) {
        if (employeId == null) {
            return presenceRepository.findAll().stream().map(this::toPresenceDto).toList();
        }
        return presenceRepository.findByEmployeId(employeId).stream().map(this::toPresenceDto).toList();
    }

    @Transactional
    public CongeDto createConge(Conge conge) {
        return toCongeDto(congeRepository.save(conge));
    }

    public List<CongeDto> listConges(Long employeId) {
        return congeRepository.findByEmployeId(employeId).stream().map(this::toCongeDto).toList();
    }

    @Transactional
    public CongeDto valider(Long id) {
        Conge conge = congeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Congé introuvable"));
        conge.setStatut(Conge.StatutConge.APPROUVE);
        return toCongeDto(congeRepository.save(conge));
    }

    private EmployeDto toEmployeDto(Employe e) {
        EmployeDto dto = new EmployeDto();
        dto.setId(e.getId());
        dto.setMatricule(e.getMatricule());
        dto.setNom(e.getNom());
        dto.setPrenom(e.getPrenom());
        dto.setEmail(e.getEmail());
        dto.setTelephone(e.getTelephone());
        dto.setFonction(e.getFonction() == null ? null : e.getFonction().name());
        dto.setSpecialite(e.getSpecialite());
        dto.setDepartement(e.getDepartement());
        dto.setActif(e.isActif());
        return dto;
    }

    private PresenceDto toPresenceDto(Presence p) {
        PresenceDto dto = new PresenceDto();
        dto.setId(p.getId());
        dto.setEmployeId(p.getEmployeId());
        dto.setDatePresence(p.getDatePresence());
        dto.setType(p.getType());
        return dto;
    }

    private CongeDto toCongeDto(Conge c) {
        CongeDto dto = new CongeDto();
        dto.setId(c.getId());
        dto.setEmployeId(c.getEmployeId());
        dto.setDateDebut(c.getDateDebut());
        dto.setDateFin(c.getDateFin());
        dto.setType(c.getType() == null ? null : c.getType().name());
        dto.setStatut(c.getStatut() == null ? null : c.getStatut().name());
        dto.setMotif(c.getMotif());
        return dto;
    }
}
