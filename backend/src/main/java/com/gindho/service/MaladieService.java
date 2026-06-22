package com.gindho.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.gindho.dto.MaladieAnalyseCritereDto;
import com.gindho.dto.MaladieDto;
import com.gindho.model.Maladie;
import com.gindho.model.MaladieAnalyseCritere;
import com.gindho.repository.MaladieRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MaladieService {

    private final MaladieRepository maladieRepository;

    public MaladieDto create(MaladieDto dto) {
        if (dto == null) throw new IllegalArgumentException("payload manquant");
        if (dto.getNom() == null || dto.getNom().isBlank()) throw new IllegalArgumentException("nom manquant");

        Maladie maladie = new Maladie();
        maladie.setNom(dto.getNom().trim());
        maladie.setDescription(dto.getDescription());
        maladie.setActif(dto.isActif());

        maladie.setSymptomes(dto.getSymptomes() != null ? new HashSet<>(dto.getSymptomes()) : Set.of());
        maladie.setAnalyseCriteres(convertCriters(dto.getAnalyseCriteres()));

        Maladie saved = maladieRepository.save(maladie);
        return convertToDto(saved);
    }

    public List<MaladieDto> list() {
        return maladieRepository.findByActifTrue().stream().map(this::convertToDto).toList();
    }

    public MaladieDto update(Long id, MaladieDto dto) {
        if (id == null) throw new IllegalArgumentException("id manquant");
        if (dto == null) throw new IllegalArgumentException("payload manquant");

        Maladie maladie = maladieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maladie non trouvée"));

        if (dto.getNom() != null && !dto.getNom().isBlank()) {
            maladie.setNom(dto.getNom().trim());
        }
        if (dto.getDescription() != null) {
            maladie.setDescription(dto.getDescription());
        }
        maladie.setActif(dto.isActif());

        if (dto.getSymptomes() != null) {
            maladie.setSymptomes(new HashSet<>(dto.getSymptomes()));
        }
        if (dto.getAnalyseCriteres() != null) {
            maladie.setAnalyseCriteres(convertCriters(dto.getAnalyseCriteres()));
        }

        Maladie saved = maladieRepository.save(maladie);
        return convertToDto(saved);
    }

    public void delete(Long id) {
        if (id == null) throw new IllegalArgumentException("id manquant");
        // MVP: delete hard
        maladieRepository.deleteById(id);
    }

    private Set<MaladieAnalyseCritere> convertCriters(Set<MaladieAnalyseCritereDto> dtos) {
        if (dtos == null) return Set.of();

        Set<MaladieAnalyseCritere> result = new HashSet<>();
        for (MaladieAnalyseCritereDto c : dtos) {
            if (c == null) continue;
            if (c.getTypeAnalyse() == null || c.getTypeAnalyse().isBlank()) continue;
            if (c.getResultatRequis() == null) continue;

            result.add(new MaladieAnalyseCritere(
                    c.getTypeAnalyse().trim(),
                    c.getResultatRequis()
            ));
        }
        return result;
    }

    private MaladieDto convertToDto(Maladie maladie) {
        if (maladie == null) return null;

        MaladieDto dto = new MaladieDto();
        dto.setId(maladie.getId());
        dto.setNom(maladie.getNom());
        dto.setDescription(maladie.getDescription());
        dto.setActif(maladie.isActif());

        dto.setSymptomes(maladie.getSymptomes());

        Set<MaladieAnalyseCritereDto> crit = new HashSet<>();
        if (maladie.getAnalyseCriteres() != null) {
            for (MaladieAnalyseCritere c : maladie.getAnalyseCriteres()) {
                if (c == null) continue;
                MaladieAnalyseCritereDto cd = new MaladieAnalyseCritereDto();
                cd.setTypeAnalyse(c.getTypeAnalyse());
                cd.setResultatRequis(c.getResultatRequis());
                crit.add(cd);
            }
        }
        dto.setAnalyseCriteres(crit);

        return dto;
    }
}
