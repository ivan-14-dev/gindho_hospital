package com.gindho.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gindho.dto.HospitalisationDto;
import com.gindho.model.Chambre;
import com.gindho.model.DossierHospitalisation;
import com.gindho.model.Hospitalisation;
import com.gindho.model.Lit;
import com.gindho.model.Patient;
import com.gindho.repository.ChambreRepository;
import com.gindho.repository.DossierHospitalisationRepository;
import com.gindho.repository.HospitalisationRepository;
import com.gindho.repository.LitRepository;
import com.gindho.repository.PatientRepository;

@Service
@Transactional
public class HospitalisationService {

    private final PatientRepository patientRepository;
    private final ChambreRepository chambreRepository;
    private final LitRepository litRepository;
    private final HospitalisationRepository hospitalisationRepository;
    private final DossierHospitalisationRepository dossierHospitalisationRepository;

    public HospitalisationService(
            PatientRepository patientRepository,
            ChambreRepository chambreRepository,
            LitRepository litRepository,
            HospitalisationRepository hospitalisationRepository,
            DossierHospitalisationRepository dossierHospitalisationRepository
    ) {
        this.patientRepository = patientRepository;
        this.chambreRepository = chambreRepository;
        this.litRepository = litRepository;
        this.hospitalisationRepository = hospitalisationRepository;
        this.dossierHospitalisationRepository = dossierHospitalisationRepository;
    }

    // ====== Hospitaliser (création hospitalisation + blocage lit) ======
    public HospitalisationDto hospitaliser(HospitalisationDto dto) {
        if (dto == null) throw new IllegalArgumentException("Hospitalisation payload manquant");
        if (dto.getPatientId() == null) throw new IllegalArgumentException("patientId manquant");
        if (dto.getLitId() == null) throw new IllegalArgumentException("litId manquant");

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient non trouvé"));

        Lit lit = litRepository.findById(dto.getLitId())
                .orElseThrow(() -> new RuntimeException("Lit non trouvé"));

        Chambre chambre = lit.getChambre();
        if (chambre == null) {
            throw new RuntimeException("Chambre du lit introuvable");
        }

        // Vérif : lit bloqué si chambre indisponible / lit indisponible
        if (!lit.isActif() || lit.getStatut() != Lit.StatutLit.DISPONIBLE) {
            throw new RuntimeException("Lit indisponible");
        }
        if (!chambre.isActif()) {
            throw new RuntimeException("Chambre indisponible");
        }

        // Prévention (anti-fuite / anti incohérence) : aucun hospitalisation EN_COURS pour ce lit
        // (le statut lit devrait suffire, mais on sécurise)
        List<Hospitalisation> existing = hospitalisationRepository.findAll()
                .stream()
                .filter(h -> h != null && h.getLit() != null && h.getLit().getId() != null && h.getLit().getId().equals(lit.getId()))
                .filter(h -> h.getStatut() == Hospitalisation.StatutHospitalisation.EN_COURS)
                .toList();
        if (!existing.isEmpty()) {
            throw new RuntimeException("Lit déjà occupé");
        }

        Hospitalisation hospitalisation = new Hospitalisation();
        hospitalisation.setPatient(patient);
        hospitalisation.setLit(lit);
        hospitalisation.setDateAdmission(dto.getDateAdmission() != null ? dto.getDateAdmission() : LocalDateTime.now());
        hospitalisation.setStatut(Hospitalisation.StatutHospitalisation.EN_COURS);
        hospitalisation.setMotifAdmission(dto.getMotifAdmission());

        hospitalisation = hospitalisationRepository.save(hospitalisation);

        // Blocage du lit
        lit.setStatut(Lit.StatutLit.OCCUPE);
        litRepository.save(lit);

        // Dossier hospitalisation (optionnel mais pour cohérence UI admin + rapport sortie)
        DossierHospitalisation dossier = new DossierHospitalisation();
        dossier.setHospitalisation(hospitalisation);
        dossier.setDiagnostic(dto.getDiagnostic());
        dossier.setTraitement(dto.getTraitement());
        dossier.setObservations(dto.getObservations());
        dossier = dossierHospitalisationRepository.save(dossier);

        return convertToDto(hospitalisation, dossier);
    }

    // ====== Rapport sortie (mise à jour + libération lit) ======
    public HospitalisationDto rapportSortie(Long hospitalisationId, HospitalisationDto dto) {
        if (hospitalisationId == null) throw new IllegalArgumentException("hospitalisationId manquant");
        Hospitalisation hospitalisation = hospitalisationRepository.findById(hospitalisationId)
                .orElseThrow(() -> new RuntimeException("Hospitalisation non trouvée"));

        if (hospitalisation.getStatut() == Hospitalisation.StatutHospitalisation.SORTIE) {
            throw new RuntimeException("Hospitalisation déjà en sortie");
        }

        // Mise à jour hospitalisation
        hospitalisation.setStatut(Hospitalisation.StatutHospitalisation.SORTIE);
        hospitalisation.setDateSortie(LocalDateTime.now());

        DossierHospitalisation dossier = dossierHospitalisationRepository.findByHospitalisationId(hospitalisationId).orElse(null);
        if (dossier == null) {
            dossier = new DossierHospitalisation();
            dossier.setHospitalisation(hospitalisation);
        }

        if (dto != null) {
            if (dto.getDiagnostic() != null) dossier.setDiagnostic(dto.getDiagnostic());
            if (dto.getTraitement() != null) dossier.setTraitement(dto.getTraitement());
            if (dto.getObservations() != null) dossier.setObservations(dto.getObservations());
            if (dto.getRapportSortie() != null) dossier.setRapportSortie(dto.getRapportSortie());
            if (dto.getDateRapportSortie() != null) dossier.setDateRapportSortie(dto.getDateRapportSortie());
        }

        if (dossier.getDateRapportSortie() == null) {
            dossier.setDateRapportSortie(LocalDate.now());
        }

        dossierHospitalisationRepository.save(dossier);
        hospitalisation = hospitalisationRepository.save(hospitalisation);

        // Libération du lit
        Lit lit = hospitalisation.getLit();
        if (lit != null) {
            lit.setStatut(Lit.StatutLit.DISPONIBLE);
            litRepository.save(lit);
        }

        return convertToDto(hospitalisation, dossier);
    }

    // ====== Read ======
    public List<HospitalisationDto> getAllHospitalisations() {
        return hospitalisationRepository.findAll()
                .stream()
                .map(h -> {
                    DossierHospitalisation d = dossierHospitalisationRepository.findByHospitalisationId(h.getId()).orElse(null);
                    return convertToDto(h, d);
                })
                .toList();
    }

    public List<HospitalisationDto> getHospitalisationsEnCours() {
        return hospitalisationRepository.findByStatut(Hospitalisation.StatutHospitalisation.EN_COURS)
                .stream()
                .map(h -> {
                    DossierHospitalisation d = dossierHospitalisationRepository.findByHospitalisationId(h.getId()).orElse(null);
                    return convertToDto(h, d);
                })
                .toList();
    }

    public List<HospitalisationDto> getHospitalisationsParPatient(Long patientId) {
        if (patientId == null) throw new IllegalArgumentException("patientId manquant");

        return hospitalisationRepository.findByPatientIdOrderByDateAdmissionDesc(patientId)
                .stream()
                .map(h -> {
                    DossierHospitalisation d = dossierHospitalisationRepository.findByHospitalisationId(h.getId()).orElse(null);
                    return convertToDto(h, d);
                })
                .toList();
    }

    // ====== Update (partial update hospitalisation fields) ======
    public HospitalisationDto updateHospitalisation(Long id, HospitalisationDto dto) {
        if (id == null) throw new IllegalArgumentException("id manquant");
        if (dto == null) throw new IllegalArgumentException("dto manquant");

        Hospitalisation hospitalisation = hospitalisationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hospitalisation non trouvée: " + id));

        if (dto.getMotifAdmission() != null) hospitalisation.setMotifAdmission(dto.getMotifAdmission());
        if (dto.getDateAdmission() != null) hospitalisation.setDateAdmission(dto.getDateAdmission());
        if (dto.getDateSortie() != null) hospitalisation.setDateSortie(dto.getDateSortie());
        if (dto.getStatut() != null) {
            // Si on passe à SORTIE, libérer le lit
            if (dto.getStatut() == Hospitalisation.StatutHospitalisation.SORTIE
                    && hospitalisation.getStatut() != Hospitalisation.StatutHospitalisation.SORTIE) {
                Lit lit = hospitalisation.getLit();
                if (lit != null) {
                    lit.setStatut(Lit.StatutLit.DISPONIBLE);
                    litRepository.save(lit);
                }
            }
            hospitalisation.setStatut(dto.getStatut());
        }

        hospitalisation = hospitalisationRepository.save(hospitalisation);

        // Mettre à jour le dossier si fourni
        DossierHospitalisation dossier = dossierHospitalisationRepository.findByHospitalisationId(id).orElse(null);
        if (dossier == null) {
            dossier = new DossierHospitalisation();
            dossier.setHospitalisation(hospitalisation);
        }
        if (dto.getDiagnostic() != null) dossier.setDiagnostic(dto.getDiagnostic());
        if (dto.getTraitement() != null) dossier.setTraitement(dto.getTraitement());
        if (dto.getObservations() != null) dossier.setObservations(dto.getObservations());
        if (dto.getRapportSortie() != null) dossier.setRapportSortie(dto.getRapportSortie());
        if (dto.getDateRapportSortie() != null) dossier.setDateRapportSortie(dto.getDateRapportSortie());
        dossierHospitalisationRepository.save(dossier);

        return convertToDto(hospitalisation, dossier);
    }

    // ====== Delete ======
    public void deleteHospitalisation(Long id) {
        if (id == null) throw new IllegalArgumentException("id manquant");

        Hospitalisation hospitalisation = hospitalisationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hospitalisation non trouvée: " + id));

        // Libérer le lit si occupé
        Lit lit = hospitalisation.getLit();
        if (lit != null && lit.getStatut() == Lit.StatutLit.OCCUPE) {
            lit.setStatut(Lit.StatutLit.DISPONIBLE);
            litRepository.save(lit);
        }

        // Supprimer le dossier associé
        dossierHospitalisationRepository.findByHospitalisationId(id).ifPresent(dossierHospitalisationRepository::delete);

        hospitalisationRepository.delete(hospitalisation);
    }

    // ====== Converters ======
    private HospitalisationDto convertToDto(Hospitalisation h, DossierHospitalisation d) {
        if (h == null) return null;

        HospitalisationDto dto = new HospitalisationDto();
        dto.setId(h.getId());
        dto.setDateAdmission(h.getDateAdmission());
        dto.setDateSortie(h.getDateSortie());
        dto.setStatut(h.getStatut());
        dto.setMotifAdmission(h.getMotifAdmission());

        if (h.getPatient() != null) {
            dto.setPatientId(h.getPatient().getId());
            if (h.getPatient().getUser() != null) {
                String patientNom =
                        (h.getPatient().getUser().getPrenom() != null ? h.getPatient().getUser().getPrenom() : "")
                                + " "
                                + (h.getPatient().getUser().getNom() != null ? h.getPatient().getUser().getNom() : "");
                dto.setPatientNom(patientNom.trim());
            }
        }

        if (h.getLit() != null) {
            dto.setLitId(h.getLit().getId());
            dto.setLitNumero(h.getLit().getNumeroLit());
            if (h.getLit().getChambre() != null) {
                dto.setChambreNumero(h.getLit().getChambre().getNumeroChambre());
            }
        }

        if (d != null) {
            dto.setDiagnostic(d.getDiagnostic());
            dto.setTraitement(d.getTraitement());
            dto.setObservations(d.getObservations());
            dto.setRapportSortie(d.getRapportSortie());
            dto.setDateRapportSortie(d.getDateRapportSortie());
        }

        return dto;
    }
}
