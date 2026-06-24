package com.gindho.setup.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SetupRequest {

    @NotBlank(message = "Le nom de l'hôpital est obligatoire")
    private String hospitalName;

    @NotBlank(message = "L'adresse est obligatoire")
    private String hospitalAddress;

    @NotBlank(message = "La ville est obligatoire")
    private String city;

    @NotBlank(message = "Le pays est obligatoire")
    private String country;

    @NotBlank(message = "Le téléphone est obligatoire")
    private String phone;

    @Email(message = "Email invalide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    @NotBlank(message = "Le nom de domaine est obligatoire")
    private String domainName;

    @NotBlank(message = "Le nom d'utilisateur SuperAdmin est obligatoire")
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    private String superAdminUsername;

    @NotBlank(message = "Le mot de passe SuperAdmin est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String superAdminPassword;

    @NotBlank(message = "L'email SuperAdmin est obligatoire")
    @Email(message = "Email SuperAdmin invalide")
    private String superAdminEmail;

    @NotBlank(message = "Le nom du SuperAdmin est obligatoire")
    private String superAdminFirstName;

    @NotBlank(message = "Le prénom du SuperAdmin est obligatoire")
    private String superAdminLastName;

    private String smtpHost;
    private Integer smtpPort;
    private String smtpUsername;
    private String smtpPassword;
    private String smtpFromEmail;

    private String applicationUrl;
    private String applicationEmail;
    private String applicationEmailPassword;

    @NotBlank(message = "Le nom d'utilisateur de la base de données est obligatoire")
    private String databaseUsername;

    @NotBlank(message = "Le mot de passe de la base de données est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String databasePassword;

    private String keycloakUrl;
    private String keycloakAdminUsername;
    private String keycloakAdminPassword;
    private String frontendUrl;
    private java.util.List<String> services;
}
