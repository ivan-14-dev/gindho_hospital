package com.gindho.service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gindho.dto.PatientDto;
import com.gindho.dto.RendezVousDto;
import com.gindho.model.Disponibilite;
import com.gindho.model.Medecin;
import com.gindho.model.Patient;
import com.gindho.model.RendezVous;
import com.gindho.repository.DisponibiliteRepository;
import com.gindho.repository.MedecinRepository;
import com.gindho.repository.PatientRepository;
import com.gindho.repository.RendezVousRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RendezVousService {

    private final RendezVousRepository rendezVousRepository;
    private final MedecinRepository medecinRepository;
    private final PatientRepository patientRepository;
    private final DisponibiliteRepository disponibiliteRepository;
    private final MailService mailService;

    // Create
    public RendezVousDto prendreRDV(RendezVousDto dto) {
        Medecin medecin = medecinRepository.findById(dto.getMedecinId())
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé"));
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient non trouvé"));

        if (!verifierHorairesMedecin(dto.getMedecinId(), dto.getDateHeureDebut(), dto.getDateHeureFin())) {
            throw new RuntimeException("RDV en dehors des horaires de travail du médecin");
        }

        if (verifierConflits(dto.getMedecinId(), dto.getDateHeureDebut(), dto.getDateHeureFin())) {
            throw new RuntimeException("Conflit d'horaire détecté");
        }

        // Par défaut :
        // - si le créateur est PATIENT => EN_ATTENTE (confirmation requise)
        // - sinon (médecin/réception) => CONFIRME
        com.gindho.model.RendezVous.StatutRDV statutToUse = dto.getStatut() != null
                ? dto.getStatut()
                : resolveStatutInitialParRole();

        RendezVous rdv = RendezVous.builder()
                .dateHeureDebut(dto.getDateHeureDebut())
                .dateHeureFin(dto.getDateHeureFin())
                .statut(statutToUse)
                .motif(dto.getMotif())
                .notes(dto.getNotes())
                .patient(patient)
                .medecin(medecin)
                .build();

        rdv = rendezVousRepository.save(rdv);
        return convertToDto(rdv);
    }

    // Read - Single
    public RendezVousDto getRendezVousById(Long id) {
        RendezVous rdv = rendezVousRepository.findByIdWithPatientAndMedecin(id)
                .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé"));

        assertCanAccessRendezVous(rdv);
        return convertToDto(rdv);
    }

    // Read - All
    public List<RendezVousDto> getAllRendezVous() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Non authentifié");
        }

        if (hasAnyRole(auth, "ROLE_ADMIN", "ROLE_SUPER_ADMIN")) {
            return rendezVousRepository.findAllWithPatientAndMedecin().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }

        if (hasAnyRole(auth, "ROLE_MEDECIN")) {
            Medecin medecin = medecinRepository.findByUserEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Médecin non trouvé pour votre compte"));

            return rendezVousRepository.findByMedecinId(medecin.getId()).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }

        if (hasAnyRole(auth, "ROLE_PATIENT")) {
            Patient patient = patientRepository.findByUserEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Patient non trouvé pour votre compte"));

            return rendezVousRepository.findByPatientIdWithPatientAndMedecin(patient.getId()).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }

        throw new AccessDeniedException("Accès refusé");
    }

    // Read - Patients by medecin (needed for MEDECIN dashboard)
    public List<PatientDto> getPatientsByMedecin(Long medecinId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && hasAnyRole(auth, "ROLE_MEDECIN")) {
            Medecin current = medecinRepository.findByUserEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Médecin non trouvé pour votre compte"));
            if (!current.getId().equals(medecinId)) {
                throw new AccessDeniedException("Accès refusé (médecin non propriétaire)");
            }
        }

        return rendezVousRepository.findPatientsByMedecinId(medecinId).stream()
                .map(this::convertPatientToDtoLite)
                .collect(Collectors.toList());
    }

    // Read - Upcoming (future appointments)
    public List<RendezVousDto> getAllUpcoming() {
        LocalDateTime now = LocalDateTime.now();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Non authentifié");
        }

        if (hasAnyRole(auth, "ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_RECEPTION")) {
            return rendezVousRepository
                    .findUpcomingWithPatientAndMedecin(now, RendezVous.StatutRDV.ANNULE)
                    .stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }

        if (hasAnyRole(auth, "ROLE_MEDECIN")) {
            Medecin medecin = medecinRepository.findByUserEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Médecin non trouvé pour votre compte"));

            return rendezVousRepository
                    .findUpcomingByMedecinWithPatientAndMedecin(medecin.getId(), now, RendezVous.StatutRDV.ANNULE)
                    .stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }

        if (hasAnyRole(auth, "ROLE_PATIENT")) {
            Patient patient = patientRepository.findByUserEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Patient non trouvé pour votre compte"));

            return rendezVousRepository
                    .findUpcomingByPatientWithPatientAndMedecin(patient.getId(), now, RendezVous.StatutRDV.ANNULE)
                    .stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }

        throw new AccessDeniedException("Accès refusé");
    }

    public List<RendezVousDto> getUpcomingByMedecin(Long medecinId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && hasAnyRole(auth, "ROLE_MEDECIN")) {
            Medecin current = medecinRepository.findByUserEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Médecin non trouvé pour votre compte"));
            if (!current.getId().equals(medecinId)) {
                throw new AccessDeniedException("Accès refusé (médecin non propriétaire)");
            }
        }

        LocalDateTime now = LocalDateTime.now();
        return rendezVousRepository
                .findUpcomingByMedecinWithPatientAndMedecin(medecinId, now, RendezVous.StatutRDV.ANNULE)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<RendezVousDto> getUpcomingByPatient(Long patientId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && hasAnyRole(auth, "ROLE_PATIENT")) {
            Patient current = patientRepository.findByUserEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Patient non trouvé pour votre compte"));
            if (!current.getId().equals(patientId)) {
                throw new AccessDeniedException("Accès refusé (patient non propriétaire)");
            }
        }

        LocalDateTime now = LocalDateTime.now();
        return rendezVousRepository
                .findUpcomingByPatientWithPatientAndMedecin(patientId, now, RendezVous.StatutRDV.ANNULE)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Update
    public RendezVousDto updateRendezVous(Long id, RendezVousDto dto) {
        RendezVous rdv = rendezVousRepository.findByIdWithPatientAndMedecin(id)
                .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé"));

        assertCanAccessRendezVous(rdv);

        LocalDateTime newDebut = dto.getDateHeureDebut() != null ? dto.getDateHeureDebut() : rdv.getDateHeureDebut();
        LocalDateTime newFin = dto.getDateHeureFin() != null ? dto.getDateHeureFin() : rdv.getDateHeureFin();
        Long newMedecinId = dto.getMedecinId() != null
                ? dto.getMedecinId()
                : (rdv.getMedecin() != null ? rdv.getMedecin().getId() : null);

        if (dto.getDateHeureDebut() != null) rdv.setDateHeureDebut(dto.getDateHeureDebut());
        if (dto.getDateHeureFin() != null) rdv.setDateHeureFin(dto.getDateHeureFin());
        if (dto.getMotif() != null) rdv.setMotif(dto.getMotif());
        if (dto.getNotes() != null) rdv.setNotes(dto.getNotes());
        if (dto.getStatut() != null) rdv.setStatut(dto.getStatut());

        if (dto.getPatientId() != null) {
            Patient patient = patientRepository.findById(dto.getPatientId())
                    .orElseThrow(() -> new RuntimeException("Patient non trouvé"));

            // Si un MEDECIN essaye de modifier le patient, on limite à des patients déjà liés à son activité
            // (sinon il pourrait rattacher le RDV à un autre patient).
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && hasAnyRole(auth, "ROLE_MEDECIN")) {
                Medecin current = medecinRepository.findByUserEmail(auth.getName())
                        .orElseThrow(() -> new RuntimeException("Médecin non trouvé pour votre compte"));

                boolean allowed = rendezVousRepository.existsByMedecinIdAndPatientId(current.getId(), patient.getId());
                if (!allowed) {
                    throw new AccessDeniedException("Accès refusé (médecin non affilié au patient)");
                }
            }

            rdv.setPatient(patient);
        }

        if (dto.getMedecinId() != null) {
            Medecin medecin = medecinRepository.findById(dto.getMedecinId())
                    .orElseThrow(() -> new RuntimeException("Médecin non trouvé"));

            // Un MEDECIN ne peut pas changer le medecin propriétaire du RDV.
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && hasAnyRole(auth, "ROLE_MEDECIN")) {
                Medecin current = medecinRepository.findByUserEmail(auth.getName())
                        .orElseThrow(() -> new RuntimeException("Médecin non trouvé pour votre compte"));

                if (!current.getId().equals(medecin.getId())) {
                    throw new AccessDeniedException("Accès refusé (vous ne pouvez pas modifier le médecin du RDV)");
                }
            }

            rdv.setMedecin(medecin);
        }

        if (newMedecinId != null && newDebut != null && newFin != null) {
            if (!verifierHorairesMedecin(newMedecinId, newDebut, newFin)) {
                throw new RuntimeException("RDV en dehors des horaires de travail du médecin");
            }
        }

        return convertToDto(rendezVousRepository.save(rdv));
    }

    // Update Status
    public RendezVousDto updateStatut(Long id, String statut) {
        RendezVous rdv = rendezVousRepository.findByIdWithPatientAndMedecin(id)
                .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé"));

        assertCanAccessRendezVous(rdv);

        RendezVous.StatutRDV oldStatut = rdv.getStatut();
        RendezVous.StatutRDV newStatut = RendezVous.StatutRDV.valueOf(statut);

        rdv.setStatut(newStatut);
        RendezVous saved = rendezVousRepository.save(rdv);

        // Quand on passe à CONFIRME : notifier le patient
        if (newStatut == RendezVous.StatutRDV.CONFIRME && oldStatut != RendezVous.StatutRDV.CONFIRME) {
            mailService.sendConfirmationRDV(saved);
        }

        return convertToDto(saved);
    }

    private com.gindho.model.RendezVous.StatutRDV resolveStatutInitialParRole() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isPatientRole = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a != null && "ROLE_PATIENT".equals(a.getAuthority()));

        return isPatientRole ? RendezVous.StatutRDV.EN_ATTENTE : RendezVous.StatutRDV.CONFIRME;
    }

    // Delete / Cancel
    public void annuler(Long id, String motif) {
        RendezVous rdv = rendezVousRepository.findByIdWithPatientAndMedecin(id)
                .orElseThrow(() -> new RuntimeException("Rendez-vous not found"));

        assertCanAccessRendezVous(rdv);

        rdv.setStatut(RendezVous.StatutRDV.ANNULE);
        rdv.setNotes(rdv.getNotes() != null ? rdv.getNotes() + "\nAnnulé: " + motif : "Annulé: " + motif);
        rendezVousRepository.save(rdv);
    }

    // Conflicts check
    public boolean verifierConflits(Long medecinId, LocalDateTime debut, LocalDateTime fin) {
        List<RendezVous> conflits = rendezVousRepository
                .findByMedecinIdAndDateHeureDebutBetweenAndStatutNot(
                        medecinId, debut, fin, RendezVous.StatutRDV.ANNULE);
        return !conflits.isEmpty();
    }

    // By Patient
    public List<RendezVousDto> getRDVsPatient(Long patientId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Non authentifié");
        }

        if (hasAnyRole(auth, "ROLE_PATIENT")) {
            Patient current = patientRepository.findByUserEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Patient non trouvé pour votre compte"));
            if (!current.getId().equals(patientId)) {
                throw new AccessDeniedException("Accès refusé (patient non propriétaire)");
            }
            return rendezVousRepository.findByPatientIdWithPatientAndMedecin(patientId).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }

        if (hasAnyRole(auth, "ROLE_MEDECIN")) {
            Medecin current = medecinRepository.findByUserEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Médecin non trouvé pour votre compte"));

            boolean allowed = rendezVousRepository.existsByMedecinIdAndPatientId(current.getId(), patientId);
            if (!allowed) {
                throw new AccessDeniedException("Accès refusé (médecin non affilié au patient)");
            }

            // Filtrage mémoire (safe) : le repository ne filtre pas encore par medecinId sur ce endpoint.
            return rendezVousRepository.findByPatientIdWithPatientAndMedecin(patientId).stream()
                    .filter(r -> r.getMedecin() != null && current.getId().equals(r.getMedecin().getId()))
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }

        if (hasAnyRole(auth, "ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_RECEPTION")) {
            return rendezVousRepository.findByPatientIdWithPatientAndMedecin(patientId).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }

        throw new AccessDeniedException("Accès refusé");
    }

    // By Medecin (by date)
    public List<RendezVousDto> getRDVsMedecin(Long medecinId, LocalDateTime date) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && hasAnyRole(auth, "ROLE_MEDECIN")) {
            Medecin current = medecinRepository.findByUserEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Médecin non trouvé pour votre compte"));
            if (!current.getId().equals(medecinId)) {
                throw new AccessDeniedException("Accès refusé (médecin non propriétaire)");
            }
        }

        LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = date.toLocalDate().atTime(23, 59, 59);
        return rendezVousRepository
                .findByMedecinIdAndDateHeureDebutBetweenWithPatientAndMedecin(medecinId, startOfDay, endOfDay).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private boolean verifierHorairesMedecin(Long medecinId, LocalDateTime debut, LocalDateTime fin) {
        if (medecinId == null) {
            throw new RuntimeException("medecinId requis");
        }
        if (debut == null || fin == null) {
            throw new RuntimeException("Horaires du rendez-vous requis");
        }
        if (!fin.isAfter(debut)) {
            throw new RuntimeException("Horaire invalide: dateHeureFin doit être après dateHeureDebut");
        }

        // On ne supporte pas les RDV qui traversent deux jours pour l’instant.
        if (!debut.toLocalDate().equals(fin.toLocalDate())) {
            return false;
        }

        DayOfWeek day = debut.getDayOfWeek();
        Disponibilite.JourSemaine jourSemaine = mapJour(day);

        LocalTime rdvDebut = debut.toLocalTime();
        LocalTime rdvFin = fin.toLocalTime();

        List<Disponibilite> disponibilites = disponibiliteRepository.findByMedecinIdAndActifTrue(medecinId);
        if (disponibilites == null || disponibilites.isEmpty()) {
            return false;
        }

        for (Disponibilite d : disponibilites) {
            if (d.getJour() != jourSemaine) continue;

            LocalTime dispoDebut = d.getHeureDebut().toLocalTime();
            LocalTime dispoFin = d.getHeureFin().toLocalTime();

            // RDV doit être entièrement inclus dans la fenêtre de disponibilité
            boolean debutOk = !rdvDebut.isBefore(dispoDebut);
            boolean finOk = !rdvFin.isAfter(dispoFin);
            if (debutOk && finOk) {
                return true;
            }
        }

        return false;
    }

    private Disponibilite.JourSemaine mapJour(DayOfWeek day) {
        return switch (day) {
            case MONDAY -> Disponibilite.JourSemaine.LUNDI;
            case TUESDAY -> Disponibilite.JourSemaine.MARDI;
            case WEDNESDAY -> Disponibilite.JourSemaine.MERCREDI;
            case THURSDAY -> Disponibilite.JourSemaine.JEUDI;
            case FRIDAY -> Disponibilite.JourSemaine.VENDREDI;
            case SATURDAY -> Disponibilite.JourSemaine.SAMEDI;
            case SUNDAY -> Disponibilite.JourSemaine.DIMANCHE;
        };
    }

    private RendezVousDto convertToDto(RendezVous rdv) {
        String patientNom = "";
        String medecinNom = "";
        Long patientId = null;
        Long medecinId = null;

        if (rdv.getPatient() != null) {
            patientId = rdv.getPatient().getId();
            if (rdv.getPatient().getUser() != null) {
                patientNom = rdv.getPatient().getUser().getPrenom() + " " + rdv.getPatient().getUser().getNom();
            }
        }

        if (rdv.getMedecin() != null) {
            medecinId = rdv.getMedecin().getId();
            if (rdv.getMedecin().getUser() != null) {
                medecinNom = rdv.getMedecin().getUser().getPrenom() + " " + rdv.getMedecin().getUser().getNom();
            }
        }

        return RendezVousDto.builder()
                .id(rdv.getId())
                .dateHeureDebut(rdv.getDateHeureDebut())
                .dateHeureFin(rdv.getDateHeureFin())
                .statut(rdv.getStatut())
                .motif(rdv.getMotif())
                .notes(rdv.getNotes())
                .patientId(patientId)
                .medecinId(medecinId)
                .patientNom(patientNom)
                .medecinNom(medecinNom)
                .build();
    }

    private PatientDto convertPatientToDtoLite(Patient patient) {
        String nom = "";
        String prenom = "";
        String email = "";
        if (patient.getUser() != null) {
            nom = patient.getUser().getNom();
            prenom = patient.getUser().getPrenom();
            email = patient.getUser().getEmail();
        }

        return PatientDto.builder()
                .id(patient.getId())
                .numeroPatient(patient.getNumeroPatient())
                .nom(nom)
                .prenom(prenom)
                .email(email)
                .build();
    }

    private void assertCanAccessRendezVous(RendezVous rdv) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Non authentifié");
        }

        if (hasAnyRole(auth, "ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_RECEPTION")) {
            return;
        }

        if (hasAnyRole(auth, "ROLE_PATIENT")) {
            if (rdv.getPatient() == null || rdv.getPatient().getId() == null) {
                throw new AccessDeniedException("Accès refusé");
            }
            Patient current = patientRepository.findByUserEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Patient non trouvé pour votre compte"));

            if (!current.getId().equals(rdv.getPatient().getId())) {
                throw new AccessDeniedException("Accès refusé au RDV");
            }
            return;
        }

        if (hasAnyRole(auth, "ROLE_MEDECIN")) {
            if (rdv.getMedecin() == null || rdv.getMedecin().getId() == null) {
                throw new AccessDeniedException("Accès refusé");
            }
            Medecin current = medecinRepository.findByUserEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Médecin non trouvé pour votre compte"));

            if (!current.getId().equals(rdv.getMedecin().getId())) {
                throw new AccessDeniedException("Accès refusé au RDV");
            }
            return;
        }

        throw new AccessDeniedException("Accès refusé");
    }

    private boolean hasAnyRole(Authentication auth, String... roles) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a != null && a.getAuthority() != null && List.of(roles).contains(a.getAuthority()));
    }
}
