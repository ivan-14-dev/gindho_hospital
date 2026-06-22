package com.gindho.model;

//
// IMPORTANT (FR)
// Enum des rôles du système.
// Correspondances typiques (FR) :
// - ADMIN / SUPER_ADMIN : backoffice (gestion comptes + stats)
// - RECEPTION : accueil + création de rendez-vous
// - MEDECIN : analyses/diagnostic (création/validation), prescriptions
// - NURSE : lecture/participation analyses
// - LABORATORY : saisie/résultats analyses
// - PHARMACIST : prescriptions (lecture)
// - ACCOUNTING : revenus / facturation
// - URGENCY : lectures urgentes (route /analyses/urgentes)
//
// Commande (FR) pour vérifier l’acceptation en DB (check constraint) :
// psql -h localhost -U mon_user1 -d gindho -c "SELECT conname, pg_get_constraintdef(oid) FROM pg_constraint WHERE conname='users_role_check';"
//
public enum Role {
    // Backoffice / supervision
    ADMIN,
    SUPER_ADMIN,

    // Clinique
    MEDECIN,
    PATIENT,

    // Personnel hospitalier
    NURSE,
    RECEPTION,
    PHARMACIST,

    // Spécialités / services (MVP progressif)
    LABORATORY,
    HOSPITALIZATION_SERVICE,
    ACCOUNTING,

    // Urgence dédiée
    URGENCY,

    // Rôle générique existant
    UTILISATEUR_SECONDAIRE
}
