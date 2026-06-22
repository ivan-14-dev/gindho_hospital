package com.gindho.service;

import com.gindho.dto.*;
import com.gindho.model.Medecin;
import com.gindho.model.User;
import com.gindho.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedecinService {

    private final MedecinRepository medecinRepository;
    private final UserRepository userRepository;
    private final RendezVousRepository rendezVousRepository;
    private final PatientRepository patientRepository;
    private final AnalyseRepository analyseRepository;

    public Page<MedecinDto> getAllMedecins(Pageable pageable) {
        Authentication auth = getAuth();

        if (hasAnyRole(auth, "ROLE_MEDECIN")) {
            Long currentMedecinId = getCurrentMedecinIdOrThrow();
            Medecin current = medecinRepository.findById(currentMedecinId)
                    .orElseThrow(() -> new RuntimeException("Médecin non trouvé pour votre compte"));

            return new PageImpl<>(List.of(convertToDto(current)), pageable, 1);
        }

        // Pour les autres rôles: comportement inchangé
        return medecinRepository.findAll(pageable).map(this::convertToDto);
    }

    public MedecinDto getMedecinById(Long id) {
        Authentication auth = getAuth();

        if (hasAnyRole(auth, "ROLE_MEDECIN")) {
            Long currentMedecinId = getCurrentMedecinIdOrThrow();
            if (!currentMedecinId.equals(id)) {
                throw new AccessDeniedException("Accès refusé (médecin non propriétaire)");
            }
        }

        Medecin medecin = medecinRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé"));
        return convertToDto(medecin);
    }

    public MedecinDto getMedecinByUserId(Long userId) {
        Authentication auth = getAuth();

        if (hasAnyRole(auth, "ROLE_MEDECIN")) {
            Medecin current = medecinRepository.findByUserEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Médecin non trouvé pour votre compte"));
            Medecin requested = medecinRepository.findByUserId(userId);

            if (requested == null || !current.getId().equals(requested.getId())) {
                throw new AccessDeniedException("Accès refusé (médecin non propriétaire)");
            }
        }

        Medecin medecin = medecinRepository.findByUserId(userId);
        if (medecin == null) {
            throw new RuntimeException("Médecin non trouvé pour cet utilisateur");
        }
        return convertToDto(medecin);
    }

    public MedecinDto createMedecin(MedecinDto dto) {
        User user = null;
        if (dto.getUserId() != null) {
            user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        }

        Medecin medecin = new Medecin();
        medecin.setNumeroOrdre(dto.getNumeroOrdre());
        medecin.setSpecialisation(dto.getSpecialisation());
        medecin.setTelephoneCabinet(dto.getTelephoneCabinet());
        medecin.setDisponible(dto.getDisponible() != null ? dto.getDisponible() : true);
        medecin.setUser(user);

        return convertToDto(medecinRepository.save(medecin));
    }

    public MedecinDto updateMedecin(Long id, MedecinDto dto) {
        Medecin medecin = medecinRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé"));

        // Optionnel mais cohérent: un MEDECIN ne modifie que lui-même
        Authentication auth = getAuth();
        if (hasAnyRole(auth, "ROLE_MEDECIN")) {
            Long currentMedecinId = getCurrentMedecinIdOrThrow();
            if (!currentMedecinId.equals(id)) {
                throw new AccessDeniedException("Accès refusé (médecin non propriétaire)");
            }
        }

        if (dto.getNumeroOrdre() != null) medecin.setNumeroOrdre(dto.getNumeroOrdre());
        if (dto.getSpecialisation() != null) medecin.setSpecialisation(dto.getSpecialisation());
        if (dto.getTelephoneCabinet() != null) medecin.setTelephoneCabinet(dto.getTelephoneCabinet());
        if (dto.getDisponible() != null) medecin.setDisponible(dto.getDisponible());

        return convertToDto(medecinRepository.save(medecin));
    }

    public void deleteMedecin(Long id) {
        // Le contrôle d’accès “qui peut supprimer” dépend de SecurityConfig.
        // Mais on garde une protection: MEDECIN ne supprime que lui-même.
        Authentication auth = getAuth();
        if (hasAnyRole(auth, "ROLE_MEDECIN")) {
            Long currentMedecinId = getCurrentMedecinIdOrThrow();
            if (!currentMedecinId.equals(id)) {
                throw new AccessDeniedException("Accès refusé (médecin non propriétaire)");
            }
        }

        if (!medecinRepository.existsById(id)) {
            throw new RuntimeException("Médecin non trouvé");
        }
        medecinRepository.deleteById(id);
    }

    public Page<MedecinDto> searchMedecins(String query, Pageable pageable) {
        Authentication auth = getAuth();

        if (hasAnyRole(auth, "ROLE_MEDECIN")) {
            // MEDECIN: ne cherche que lui-même
            Long currentMedecinId = getCurrentMedecinIdOrThrow();
            Medecin current = medecinRepository.findById(currentMedecinId)
                    .orElseThrow(() -> new RuntimeException("Médecin non trouvé pour votre compte"));
            return new PageImpl<>(List.of(convertToDto(current)), pageable, 1);
        }

        return medecinRepository
                .findByNomOrPrenomOrSpecialisationContainingIgnoreCase(query, pageable)
                .map(this::convertToDto);
    }

    public MedecinDashboardDto getDashboard(Long medecinId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdminRole = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()) || "ROLE_SUPER_ADMIN".equals(a.getAuthority()));
        if (!isAdminRole) {
            boolean isMedecinRole = auth != null && auth.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_MEDECIN".equals(a.getAuthority()));

            if (!isMedecinRole) {
                throw new AccessDeniedException("Accès refusé");
            }

            Medecin current = medecinRepository.findByUserEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Médecin non trouvé pour votre compte"));

            if (!current.getId().equals(medecinId)) {
                throw new AccessDeniedException("Accès refusé (médecin non propriétaire du dashboard)");
            }
        }

        Medecin medecin = medecinRepository.findById(medecinId)
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé"));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime debutJour = now.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finJour = now.withHour(23).withMinute(59).withSecond(59);

        List<RendezVousDto> rendezVous = rendezVousRepository.findByMedecinIdAndDateHeureDebutBetweenWithPatientAndMedecin(
                        medecinId, debutJour, finJour).stream()
                .map(this::convertRendezVousToDto)
                .collect(Collectors.toList());

        Long rendezVousAujourdhui = rendezVousRepository.countByMedecinIdAndDateHeureDebutBetween(
                medecinId, debutJour, finJour);
        Long rendezVousEnAttente = (long) rendezVousRepository.findByMedecinIdAndStatutNot(
                medecinId, com.gindho.model.RendezVous.StatutRDV.ANNULE).size();
        Long totalPatients = rendezVousRepository.countDistinctPatientsByMedecinId(medecinId);

        return MedecinDashboardDto.builder()
                .medecinId(medecin.getId())
                .nom(medecin.getUser() != null ? medecin.getUser().getNom() : "")
                .prenom(medecin.getUser() != null ? medecin.getUser().getPrenom() : "")
                .specialisation(medecin.getSpecialisation())
                .rendezVousAujourdhui(rendezVousAujourdhui != null ? rendezVousAujourdhui : 0L)
                .rendezVousEnAttente(rendezVousEnAttente != null ? rendezVousEnAttente : 0L)
                .totalPatients(totalPatients)
                .rendezVous(rendezVous)
                .build();
    }

    private RendezVousDto convertRendezVousToDto(com.gindho.model.RendezVous r) {
        String patientNom = "";
        if (r.getPatient() != null && r.getPatient().getUser() != null) {
            patientNom = r.getPatient().getUser().getPrenom() + " " + r.getPatient().getUser().getNom();
        }
        return RendezVousDto.builder()
                .id(r.getId())
                .dateHeureDebut(r.getDateHeureDebut())
                .dateHeureFin(r.getDateHeureFin())
                .statut(r.getStatut())
                .patientNom(patientNom)
                .medecinId(r.getMedecin() != null ? r.getMedecin().getId() : null)
                .patientId(r.getPatient() != null ? r.getPatient().getId() : null)
                .build();
    }

    private MedecinDto convertToDto(Medecin medecin) {
        return MedecinDto.builder()
                .id(medecin.getId())
                .numeroOrdre(medecin.getNumeroOrdre())
                .specialisation(medecin.getSpecialisation())
                .telephoneCabinet(medecin.getTelephoneCabinet())
                .disponible(medecin.isDisponible())
                .userId(medecin.getUser() != null ? medecin.getUser().getId() : null)
                .nom(medecin.getUser() != null ? medecin.getUser().getNom() : "")
                .prenom(medecin.getUser() != null ? medecin.getUser().getPrenom() : "")
                .email(medecin.getUser() != null ? medecin.getUser().getEmail() : "")
                .build();
    }

    private Authentication getAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Non authentifié");
        }
        return auth;
    }

    private boolean hasAnyRole(Authentication auth, String... roles) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a != null && a.getAuthority() != null && java.util.Set.of(roles).contains(a.getAuthority()));
    }

    private Long getCurrentMedecinIdOrThrow() {
        Authentication auth = getAuth();
        Medecin medecin = medecinRepository.findByUserEmail(auth.getName())
                .orElseThrow(() -> new AccessDeniedException("Médecin non trouvé pour votre compte"));
        return medecin.getId();
    }
}
