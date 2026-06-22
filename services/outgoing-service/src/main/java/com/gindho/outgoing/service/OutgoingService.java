package com.gindho.outgoing.service;

import com.gindho.kafka.BaseEvent;
import com.gindho.kafka.EventProducer;
import com.gindho.kafka.EventType;
import com.gindho.outgoing.dto.EvenementDto;
import com.gindho.outgoing.dto.MedecinDisponibiliteDto;
import com.gindho.outgoing.dto.TransfertPatientDto;
import com.gindho.outgoing.model.Evenement;
import com.gindho.outgoing.model.MedecinDisponibilite;
import com.gindho.outgoing.model.TransfertPatient;
import com.gindho.outgoing.repository.EvenementRepository;
import com.gindho.outgoing.repository.MedecinDisponibiliteRepository;
import com.gindho.outgoing.repository.TransfertPatientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OutgoingService {

    private final MedecinDisponibiliteRepository disponibiliteRepository;
    private final TransfertPatientRepository transfertRepository;
    private final EvenementRepository evenementRepository;
    private final EventProducer eventProducer;

    // ========== MÉDECIN DISPONIBILITÉS (emploi du temps) ==========

    public List<MedecinDisponibiliteDto> getDisponibilitesMedecin(Long medecinId) {
        return disponibiliteRepository.findByMedecinIdAndActifTrueOrderByJourSemaine(medecinId)
                .stream().map(this::toDispoDto).toList();
    }

    @Transactional
    public MedecinDisponibiliteDto creerDisponibilite(MedecinDisponibiliteDto dto) {
        var d = new MedecinDisponibilite();
        d.setMedecinId(dto.getMedecinId());
        d.setJourSemaine(dto.getJourSemaine());
        d.setHeureDebut(dto.getHeureDebut());
        d.setHeureFin(dto.getHeureFin());
        d.setDureeConsultationMinutes(dto.getDureeConsultationMinutes() > 0 ? dto.getDureeConsultationMinutes() : 30);
        d.setNotes(dto.getNotes());
        d = disponibiliteRepository.save(d);

        eventProducer.publish("outgoing", BaseEvent.builder()
                .eventType(EventType.DOCTOR_AVAILABILITY_UPDATED)
                .source("outgoing-service").build());

        return toDispoDto(d);
    }

    @Transactional
    public void supprimerDisponibilite(Long id) {
        disponibiliteRepository.deleteById(id);
    }

    // ========== TRANSFERTS PATIENTS ==========

    public List<TransfertPatientDto> getTransfertsByStatut(String statut) {
        var s = TransfertPatient.StatutTransfert.valueOf(statut.toUpperCase());
        return transfertRepository.findByStatutOrderByDateDemandeDesc(s)
                .stream().map(this::toTransfertDto).toList();
    }

    public List<TransfertPatientDto> getTransfertsEnAttente() {
        return getTransfertsByStatut("EN_ATTENTE");
    }

    public TransfertPatientDto getTransfert(Long id) {
        var t = transfertRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transfert introuvable: " + id));
        return toTransfertDto(t);
    }

    @Transactional
    public TransfertPatientDto creerTransfert(TransfertPatientDto dto) {
        var t = new TransfertPatient();
        t.setPatientId(dto.getPatientId());
        t.setPatientNom(dto.getPatientNom());
        t.setHopitalSource(dto.getHopitalSource());
        t.setHopitalDestination(dto.getHopitalDestination());
        t.setMotifTransfert(dto.getMotifTransfert());
        t.setMedecinReferentId(dto.getMedecinReferentId());
        t.setDateDemande(LocalDateTime.now());
        t.setUrgence(dto.isUrgence());
        t.setNotesTransport(dto.getNotesTransport());
        t = transfertRepository.save(t);

        eventProducer.publish("outgoing", BaseEvent.builder()
                .eventType(EventType.PATIENT_TRANSFER_REQUESTED)
                .source("outgoing-service").build());

        return toTransfertDto(t);
    }

    @Transactional
    public TransfertPatientDto validerTransfert(Long id, Long valideParId) {
        var t = transfertRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transfert introuvable: " + id));
        t.setStatut(TransfertPatient.StatutTransfert.VALIDE);
        t.setDateValidation(LocalDateTime.now());
        t.setValideParId(valideParId);
        return toTransfertDto(transfertRepository.save(t));
    }

    @Transactional
    public TransfertPatientDto refuserTransfert(Long id, Long valideParId) {
        var t = transfertRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transfert introuvable: " + id));
        t.setStatut(TransfertPatient.StatutTransfert.REFUSE);
        t.setDateValidation(LocalDateTime.now());
        t.setValideParId(valideParId);
        return toTransfertDto(transfertRepository.save(t));
    }

    @Transactional
    public void effectuerTransfert(Long id) {
        var t = transfertRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transfert introuvable: " + id));
        t.setStatut(TransfertPatient.StatutTransfert.EFFECTUE);
        transfertRepository.save(t);
    }

    // ========== ÉVÉNEMENTS PUBLICS (dons sang, campagnes) ==========

    public List<EvenementDto> getEvenementsAPublier() {
        return evenementRepository.findByPubliqueTrueAndDateDebutAfterOrderByDateDebutAsc(LocalDateTime.now())
                .stream().map(this::toEvenementDto).toList();
    }

    @Transactional
    public EvenementDto creerEvenement(EvenementDto dto) {
        var e = new Evenement();
        e.setTitre(dto.getTitre());
        e.setDescription(dto.getDescription());
        e.setType(Evenement.TypeEvenement.valueOf(dto.getType()));
        e.setDateDebut(dto.getDateDebut());
        e.setDateFin(dto.getDateFin());
        e.setLieu(dto.getLieu());
        e.setOrganisateur(dto.getOrganisateur());
        e.setContenuPublication(dto.getContenuPublication());
        e.setPublique(dto.isPublique());
        e.setImageUrl(dto.getImageUrl());
        e.setLienInscription(dto.getLienInscription());
        e = evenementRepository.save(e);

        eventProducer.publish("outgoing", BaseEvent.builder()
                .eventType(EventType.PUBLIC_EVENT_CREATED)
                .source("outgoing-service").build());

        return toEvenementDto(e);
    }

    @Transactional
    public EvenementDto publierEvenement(Long id) {
        var e = evenementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Événement introuvable: " + id));
        e.setStatut(Evenement.StatutEvenement.EN_COURS);
        e.setPublique(true);
        return toEvenementDto(evenementRepository.save(e));
    }

    // ========== DTO HELPERS ==========

    private MedecinDisponibiliteDto toDispoDto(MedecinDisponibilite d) {
        return MedecinDisponibiliteDto.builder()
                .id(d.getId()).medecinId(d.getMedecinId())
                .jourSemaine(d.getJourSemaine()).heureDebut(d.getHeureDebut())
                .heureFin(d.getHeureFin())
                .dureeConsultationMinutes(d.getDureeConsultationMinutes())
                .notes(d.getNotes()).actif(d.isActif()).build();
    }

    private TransfertPatientDto toTransfertDto(TransfertPatient t) {
        return TransfertPatientDto.builder()
                .id(t.getId()).patientId(t.getPatientId()).patientNom(t.getPatientNom())
                .hopitalSource(t.getHopitalSource()).hopitalDestination(t.getHopitalDestination())
                .motifTransfert(t.getMotifTransfert())
                .statut(t.getStatut() != null ? t.getStatut().name() : null)
                .medecinReferentId(t.getMedecinReferentId())
                .dateDemande(t.getDateDemande()).dateValidation(t.getDateValidation())
                .notesTransport(t.getNotesTransport()).urgence(t.isUrgence()).build();
    }

    private EvenementDto toEvenementDto(Evenement e) {
        return EvenementDto.builder()
                .id(e.getId()).titre(e.getTitre()).description(e.getDescription())
                .type(e.getType() != null ? e.getType().name() : null)
                .dateDebut(e.getDateDebut()).dateFin(e.getDateFin())
                .lieu(e.getLieu()).organisateur(e.getOrganisateur())
                .statut(e.getStatut() != null ? e.getStatut().name() : null)
                .contenuPublication(e.getContenuPublication())
                .publique(e.isPublique()).imageUrl(e.getImageUrl())
                .lienInscription(e.getLienInscription()).build();
    }
}