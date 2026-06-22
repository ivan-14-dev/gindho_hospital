package com.gindho.service;

import com.gindho.dto.RevenuDto;
import com.gindho.model.Medecin;
import com.gindho.model.Patient;
import com.gindho.model.Revenu;
import com.gindho.repository.MedecinRepository;
import com.gindho.repository.PatientRepository;
import com.gindho.repository.RevenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RevenuService {
    
    private final RevenuRepository revenuRepository;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;
    private final PatientAccessService patientAccessService;
    
    public Page<RevenuDto> getAllRevenus(Pageable pageable) {
        return revenuRepository.findAll(pageable).map(this::convertToDto);
    }
    
    public Page<RevenuDto> getRevenusByPatient(Long patientId, Pageable pageable) {
        patientAccessService.assertPatientAccess(patientId);
        return revenuRepository.findByPatientId(patientId, pageable).map(this::convertToDto);
    }
    
    public Page<RevenuDto> getRevenusByMedecin(Long medecinId, Pageable pageable) {
        return revenuRepository.findByMedecinId(medecinId, pageable).map(this::convertToDto);
    }
    
    public Page<RevenuDto> getRevenusByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return revenuRepository.findByDateRevenuBetween(start, end, pageable).map(this::convertToDto);
    }
    
    public Page<RevenuDto> getRevenusByType(String type, Pageable pageable) {
        return revenuRepository.findByTypeRevenu(type, pageable).map(this::convertToDto);
    }
    
    public BigDecimal getTotalRevenus(LocalDateTime start, LocalDateTime end) {
        BigDecimal sum = revenuRepository.sumBetweenDates(start, end);
        return sum != null ? sum : BigDecimal.ZERO;
    }
    
    public BigDecimal getTotalByType(String type, LocalDateTime start, LocalDateTime end) {
        BigDecimal sum = revenuRepository.sumByTypeBetween(type, start, end);
        return sum != null ? sum : BigDecimal.ZERO;
    }
    
    public RevenuDto getRevenuById(Long id) {
        Revenu revenu = revenuRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Revenu non trouvé"));
        return convertToDto(revenu);
    }
    
    public RevenuDto createRevenu(RevenuDto dto) {
        Patient patient = null;
        if (dto.getPatientId() != null) {
            patient = patientRepository.findById(dto.getPatientId())
                .orElse(null);
        }
        
        Medecin medecin = null;
        if (dto.getMedecinId() != null) {
            medecin = medecinRepository.findById(dto.getMedecinId())
                .orElse(null);
        }
        
        Revenu revenu = Revenu.builder()
            .montant(dto.getMontant())
            .description(dto.getDescription())
            .typeRevenu(Revenu.TypeRevenu.valueOf(dto.getTypeRevenu()))
            .notes(dto.getNotes())
            .dateRevenu(dto.getDateRevenu() != null ? dto.getDateRevenu() : LocalDateTime.now())
            .patient(patient)
            .medecin(medecin)
            .build();
        
        return convertToDto(revenuRepository.save(revenu));
    }
    
    public RevenuDto updateRevenu(Long id, RevenuDto dto) {
        Revenu revenu = revenuRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Revenu non trouvé"));
        
        if (dto.getMontant() != null) revenu.setMontant(dto.getMontant());
        if (dto.getDescription() != null) revenu.setDescription(dto.getDescription());
        if (dto.getTypeRevenu() != null) revenu.setTypeRevenu(Revenu.TypeRevenu.valueOf(dto.getTypeRevenu()));
        if (dto.getNotes() != null) revenu.setNotes(dto.getNotes());
        if (dto.getDateRevenu() != null) revenu.setDateRevenu(dto.getDateRevenu());
        if (dto.getPatientId() != null) {
            Patient patient = patientRepository.findById(dto.getPatientId()).orElse(null);
            revenu.setPatient(patient);
        }
        if (dto.getMedecinId() != null) {
            Medecin medecin = medecinRepository.findById(dto.getMedecinId()).orElse(null);
            revenu.setMedecin(medecin);
        }
        
        return convertToDto(revenuRepository.save(revenu));
    }
    
    public void deleteRevenu(Long id) {
        if (!revenuRepository.existsById(id)) {
            throw new RuntimeException("Revenu non trouvé");
        }
        revenuRepository.deleteById(id);
    }
    
    private RevenuDto convertToDto(Revenu revenu) {
        return RevenuDto.builder()
            .id(revenu.getId())
            .montant(revenu.getMontant())
            .description(revenu.getDescription())
            .typeRevenu(revenu.getTypeRevenu() != null ? revenu.getTypeRevenu().name() : null)
            .notes(revenu.getNotes())
            .dateRevenu(revenu.getDateRevenu())
            .patientId(revenu.getPatient() != null ? revenu.getPatient().getId() : null)
            .medecinId(revenu.getMedecin() != null ? revenu.getMedecin().getId() : null)
            .patientNom(revenu.getPatient() != null && revenu.getPatient().getUser() != null ? 
                revenu.getPatient().getUser().getPrenom() + " " + revenu.getPatient().getUser().getNom() : "")
            .medecinNom(revenu.getMedecin() != null && revenu.getMedecin().getUser() != null ? 
                revenu.getMedecin().getUser().getPrenom() + " " + revenu.getMedecin().getUser().getNom() : "")
            .build();
    }
}
