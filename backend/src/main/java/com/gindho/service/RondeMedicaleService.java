package com.gindho.service;
import com.gindho.dto.RondeMedicaleDto;
import com.gindho.model.*;
import com.gindho.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class RondeMedicaleService {
    private final RondeMedicaleRepository rondeRepository;
    private final MedecinRepository medecinRepository;

    @Transactional
    public RondeMedicaleDto create(RondeMedicaleDto dto) {
        Medecin m = medecinRepository.findById(dto.getMedecinId()).orElseThrow(() -> new RuntimeException("Médecin non trouvé"));
        RondeMedicale r = RondeMedicale.builder().typeRonde(dto.getTypeRonde()).dateDebut(dto.getDateDebut())
            .dateFin(dto.getDateFin()).validee(false).medecin(m).build();
        return toDto(rondeRepository.save(r));
    }

    @Transactional
    public RondeMedicaleDto valider(Long id, String compteRendu) {
        RondeMedicale r = rondeRepository.findById(id).orElseThrow(() -> new RuntimeException("Ronde non trouvée"));
        r.setValidee(true);
        if (compteRendu != null) r.setCompteRendu(compteRendu);
        return toDto(rondeRepository.save(r));
    }

    private RondeMedicaleDto toDto(RondeMedicale r) {
        String nom = r.getMedecin() != null && r.getMedecin().getUser() != null
            ? r.getMedecin().getUser().getPrenom() + " " + r.getMedecin().getUser().getNom() : "";
        return RondeMedicaleDto.builder().id(r.getId()).typeRonde(r.getTypeRonde()).dateDebut(r.getDateDebut())
            .dateFin(r.getDateFin()).validee(r.isValidee()).compteRendu(r.getCompteRendu())
            .medecinId(r.getMedecin() != null ? r.getMedecin().getId() : null).medecinNom(nom.trim()).build();
    }
}