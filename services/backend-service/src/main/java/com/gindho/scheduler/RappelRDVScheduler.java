package com.gindho.scheduler;

import com.gindho.model.RendezVous;
import com.gindho.repository.RendezVousRepository;
import com.gindho.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RappelRDVScheduler {

    private final RendezVousRepository rendezVousRepository;
    private final MailService mailService;

    @Scheduled(cron = "0 0 9 * * ?") // Tous les jours à 9h
    public void envoyerRappels() {
        LocalDateTime demain = LocalDateTime.now().plusDays(1);
        LocalDateTime startOfDay = demain.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = demain.toLocalDate().atTime(23, 59, 59);

        List<RendezVous> rendezVous = rendezVousRepository
                .findByDateHeureDebutBetweenAndStatut(startOfDay, endOfDay, RendezVous.StatutRDV.CONFIRME);

        rendezVous.forEach(rdv -> {
            try {
                mailService.sendRappelRDV(rdv);
                log.info("Rappel envoyé pour le RDV {} du {}", rdv.getId(), rdv.getDateHeureDebut());
            } catch (Exception e) {
                log.error("Erreur lors de l'envoi du rappel pour le RDV {}", rdv.getId(), e);
            }
        });
    }
}
