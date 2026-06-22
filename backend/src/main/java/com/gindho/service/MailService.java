package com.gindho.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.gindho.model.Analyse;
import com.gindho.model.RendezVous;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Async
    public void sendRappelRDV(RendezVous rdv) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            String patientNom = "";
            String medecinNom = "";

            if (rdv.getPatient() != null && rdv.getPatient().getUser() != null) {
                patientNom = rdv.getPatient().getUser().getNom() + " " + rdv.getPatient().getUser().getPrenom();
            }
            if (rdv.getMedecin() != null && rdv.getMedecin().getUser() != null) {
                medecinNom = rdv.getMedecin().getUser().getNom() + " " + rdv.getMedecin().getUser().getPrenom();
            }

            context.setVariable("patientNom", patientNom);
            context.setVariable("medecinNom", medecinNom);
            context.setVariable("dateRdv", rdv.getDateHeureDebut());
            context.setVariable("motif", rdv.getMotif());

            String htmlContent = templateEngine.process("rappel-email", context);

            if (rdv.getPatient() != null && rdv.getPatient().getUser() != null) {
                helper.setTo(rdv.getPatient().getUser().getEmail());
            }
            helper.setSubject("Rappel de rendez-vous médical");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email", e);
        }
    }

    @Async
    public void sendAnalysesDisponibles(Analyse analyse) {
        if (analyse == null || analyse.getPatient() == null || analyse.getPatient().getUser() == null) {
            return;
        }

        String toEmail = analyse.getPatient().getUser().getEmail();
        if (toEmail == null || toEmail.isBlank()) {
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String prenom = analyse.getPatient().getUser().getPrenom() == null ? "" : analyse.getPatient().getUser().getPrenom();
            String nom = analyse.getPatient().getUser().getNom() == null ? "" : analyse.getPatient().getUser().getNom();
            String patientNom = (prenom + " " + nom).trim();

            String typeAnalyse = analyse.getTypeAnalyse() == null ? "" : analyse.getTypeAnalyse();
            String resultat = analyse.getResultat() == null ? "" : analyse.getResultat();
            String dateAnalyse = analyse.getDateAnalyse() == null ? "" : analyse.getDateAnalyse().toString();

            String html = ""
                    + "<html><body>"
                    + "<p>Bonjour " + patientNom + ",</p>"
                    + "<p>Vos analyses sont disponibles.</p>"
                    + "<ul>"
                    + "<li><b>Type</b>: " + typeAnalyse + "</li>"
                    + "<li><b>Date</b>: " + dateAnalyse + "</li>"
                    + "<li><b>Résultat</b>: " + resultat + "</li>"
                    + "</ul>"
                    + "<p>Merci.</p>"
                    + "</body></html>";

            helper.setTo(toEmail);
            helper.setSubject("Analyses disponibles");
            helper.setText(html, true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email Analyses disponibles", e);
        }
    }

    @Async
    public void sendConfirmationRDV(RendezVous rdv) {
        if (rdv == null || rdv.getPatient() == null || rdv.getPatient().getUser() == null) {
            return;
        }

        String toEmail = rdv.getPatient().getUser().getEmail();
        if (toEmail == null || toEmail.isBlank()) {
            return;
        }

        String patientNom = "";
        if (rdv.getPatient().getUser().getNom() != null || rdv.getPatient().getUser().getPrenom() != null) {
            patientNom = (rdv.getPatient().getUser().getNom() == null ? "" : rdv.getPatient().getUser().getNom())
                    + " "
                    + (rdv.getPatient().getUser().getPrenom() == null ? "" : rdv.getPatient().getUser().getPrenom());
            patientNom = patientNom.trim();
        }

        String medecinNom = "";
        if (rdv.getMedecin() != null && rdv.getMedecin().getUser() != null) {
            medecinNom = (rdv.getMedecin().getUser().getNom() == null ? "" : rdv.getMedecin().getUser().getNom())
                    + " "
                    + (rdv.getMedecin().getUser().getPrenom() == null ? "" : rdv.getMedecin().getUser().getPrenom());
            medecinNom = medecinNom.trim();
        }

        String motif = rdv.getMotif() == null ? "" : rdv.getMotif();
        String dateRdv = rdv.getDateHeureDebut() == null ? "" : rdv.getDateHeureDebut().toString();

        String html = ""
                + "<html><body>"
                + "<p>Bonjour " + patientNom + ",</p>"
                + "<p>Votre rendez-vous a été <b>confirmé</b>.</p>"
                + "<ul>"
                + "<li><b>Date & heure</b>: " + dateRdv + "</li>"
                + "<li><b>Médecin</b>: " + medecinNom + "</li>"
                + (motif.isBlank() ? "" : "<li><b>Motif</b>: " + motif + "</li>")
                + "</ul>"
                + "<p>Merci.</p>"
                + "</body></html>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Confirmation de rendez-vous médical");
            helper.setText(html, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email de confirmation RDV", e);
        }
    }
}
