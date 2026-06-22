package com.gindho.bed.service;

import com.gindho.bed.dto.ChambreDto;
import com.gindho.bed.dto.LitDto;
import com.gindho.bed.model.Chambre;
import com.gindho.bed.model.Lit;
import com.gindho.bed.repository.ChambreRepository;
import com.gindho.bed.repository.LitRepository;
import com.gindho.kafka.BaseEvent;
import com.gindho.kafka.EventProducer;
import com.gindho.kafka.EventType;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BedService {
    private final LitRepository litRepository;
    private final ChambreRepository chambreRepository;
    private final EventProducer eventProducer;

    public List<ChambreDto> listChambres() {
        return chambreRepository.findAll().stream().map(this::toChambreDto).toList();
    }

    public ChambreDto chambre(Long id) {
        return toChambreDto(chambreRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Chambre introuvable")));
    }

    @Transactional
    public ChambreDto createChambre(Chambre chambre) {
        return toChambreDto(chambreRepository.save(chambre));
    }

    @Transactional
    public ChambreDto updateChambre(Long id, Chambre chambre) {
        Chambre existing = chambreRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Chambre introuvable"));
        existing.setCode(chambre.getCode());
        existing.setNom(chambre.getNom());
        existing.setEtage(chambre.getEtage());
        existing.setAile(chambre.getAile());
        existing.setCapacite(chambre.getCapacite());
        existing.setType(chambre.getType());
        return toChambreDto(chambreRepository.save(existing));
    }

    @Transactional
    public void deleteChambre(Long id) {
        chambreRepository.deleteById(id);
    }

    public List<LitDto> listLits() {
        return litRepository.findAll().stream().map(this::toLitDto).toList();
    }

    public List<LitDto> listLitsByChambre(Long chambreId) {
        return litRepository.findByChambreId(chambreId).stream().map(this::toLitDto).toList();
    }

    @Transactional
    public LitDto createLit(Lit lit) {
        return toLitDto(litRepository.save(lit));
    }

    @Transactional
    public LitDto updateLit(Long id, Lit lit) {
        Lit existing = litRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Lit introuvable"));
        existing.setChambreId(lit.getChambreId());
        existing.setCode(lit.getCode());
        existing.setOccupe(lit.isOccupe());
        existing.setPatientId(lit.getPatientId());
        existing.setObservation(lit.getObservation());
        return toLitDto(litRepository.save(existing));
    }

    @Transactional
    public void deleteLit(Long id) {
        litRepository.deleteById(id);
    }

    @Transactional
    public void assignerLit(Long litId, Long patientId) {
        Lit lit = litRepository.findById(litId).orElseThrow(() -> new EntityNotFoundException("Lit not found"));
        lit.setOccupe(true); lit.setPatientId(patientId); litRepository.save(lit);
        eventProducer.publish("medical", BaseEvent.builder().eventType(EventType.BED_ASSIGNED).source("bed-service").build());
    }

    @Transactional
    public void libererLit(Long litId) {
        Lit lit = litRepository.findById(litId).orElseThrow(() -> new EntityNotFoundException("Lit not found"));
        lit.setOccupe(false); lit.setPatientId(null); litRepository.save(lit);
        eventProducer.publish("medical", BaseEvent.builder().eventType(EventType.BED_RELEASED).source("bed-service").build());
    }

    private ChambreDto toChambreDto(Chambre c) {
        long total = litRepository.countByChambreId(c.getId());
        long occupied = litRepository.countByChambreIdAndOccupeTrue(c.getId());
        ChambreDto dto = new ChambreDto();
        dto.setId(c.getId());
        dto.setCode(c.getCode());
        dto.setNom(c.getNom());
        dto.setEtage(c.getEtage());
        dto.setAile(c.getAile());
        dto.setCapacite(c.getCapacite());
        dto.setType(c.getType() == null ? null : c.getType().name());
        dto.setLitsTotal(total);
        dto.setLitsOccupes(occupied);
        return dto;
    }

    private LitDto toLitDto(Lit l) {
        LitDto dto = new LitDto();
        dto.setId(l.getId());
        dto.setChambreId(l.getChambreId());
        dto.setCode(l.getCode());
        dto.setOccupe(l.isOccupe());
        dto.setPatientId(l.getPatientId());
        dto.setObservation(l.getObservation());
        return dto;
    }
}
