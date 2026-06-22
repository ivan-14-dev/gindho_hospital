# GinDHO - Écarts d'implémentation microservices → UI (MIS À JOUR)

**Date** : 14/06/2026 - Analyse complète du code source JavaFX vs backend microservices

---

## État actuel : 31 services backend ↔ UI JavaFX

## 1. Pages/contrôleurs JavaFX existants et connectés ✅

| Module | Contrôleur | FXML/Programmatique | Enregistré dans ComponentRegistry |
|---|---|---|---|
| Accueil | LayoutFrameWidget | Programmatique | ✅ "home" |
| Patients | PatientsControllerProgrammatic | Programmatique | ✅ "patients" |
| Médecins | MedecinsController | FXML (/views/medecins.fxml) | ✅ "medecins" |
| Rendez-vous | RendezVousControllerProgrammatic | Programmatique | ✅ "rendezvous" |
| Analyses | AnalysesControllerProgrammatic | Programmatique | ✅ "analyses" |
| Prescriptions | PrescriptionsController | FXML (/views/prescriptions.fxml) | ✅ "prescriptions" |
| Téléconsultation | TeleconsultationController | Programmatique | ✅ "teleconsultation" |
| Statistiques | StatsController | FXML (/views/stats.fxml) | ✅ "stats" |
| Hospitalisations | HospitalisationsController | FXML (/views/hospitalisations.fxml) | ✅ "hospitalisations" |
| Chambres | ChambresController | FXML (/views/chambres.fxml) | ✅ "chambres" |
| Lits | LitsController | FXML (/views/lits.fxml) | ✅ "lits_gestion" |
| Gestion Hospitalisations | GestionHospitalisationsController | FXML (/views/gestion_hospitalisations.fxml) | ✅ "gestion_hospitalisations" |
| Revenus | RevenusController | FXML (/views/revenus.fxml) | ✅ "revenus" |
| Utilisateurs (admin) | AdminUsersControllerProgrammatic | Programmatique | ✅ "adminusers" |
| Audit Logs | AuditLogsController | FXML (/views/audit_logs.fxml) | ✅ "auditlogs" |
| Paramètres | SettingsController | FXML (/views/settings.fxml) | ✅ "settings" |
| Disponibilités | DisponibilitesController | FXML (/views/disponibilites.fxml) | ✅ "disponibilites" |
| Assurances | AssurancesController | Programmatique | ✅ "assurances" |
| Soins Infirmiers | SoinsInfirmiersController | Programmatique | ✅ "soins_infirmiers" |
| Pharmacie | PharmacieController | Programmatique | ✅ "pharmacie" |
| Cockpit Médecin | MedecinCockpitController | Programmatique | ✅ "medecin_cockpit" |
| Laboratoire | LaboratoireController | Programmatique | ✅ "laboratoire" |
| Gardes | GardesController | Programmatique | ✅ "gardes" |
| Stocks | StocksController | Programmatique | ✅ "stocks" |
| RH | RhController | Programmatique | ✅ "rh" |
| Événements | EvenementsController | Programmatique | ✅ "evenements" |
| Rondes | RondesController | Programmatique | ✅ "rondes" |
| Bloc | BlocController | Programmatique | ✅ "bloc" |
| Qualité | QualiteController | Programmatique | ✅ "qualite" |
| Incidents | IncidentsController | Programmatique | ✅ "incidents" |
| Équipements | EquipementsController | Programmatique | ✅ "equipements" |
| Ambulances | AmbulancesController | Programmatique | ✅ "ambulances" |
| Stock Pharma | PharmacieController | Programmatique | ✅ "pharmacie_stock" |
| Demandes dotation | DemandesDotationController | Programmatique | ✅ "demandes_dotation" |
| Paillasses | PaillassesController | Programmatique | ✅ "paillasses" |
| Validation résultats | ValidationResultatsController | Programmatique | ✅ "validation_resultats" |
| Portail Médecin | MedecinDashboardController | Programmatique | ✅ "medecin_portail" |
| Dossier Médical | DossierMedicalController | FXML (/views/dossier-medical.fxml) | ✅ "dossier" |
| Imagerie | ImagerieController | Programmatique | ✅ "imagerie" **(NOUVEAU)** |
| Prochains RDV | UpcomingAppointmentsController | FXML | ✅ "upcoming" **(NOUVEAU)** |
| **Factures** | **FacturesController** | **Programmatique** | ✅ "factures" **(NOUVEAU)** |
| **Paiements** | **PaiementsController** | **Programmatique** | ✅ "paiements" **(NOUVEAU)** |
| **Notifications** | **NotificationController** | **Programmatique** | ✅ "notifications" **(NOUVEAU)** |

## 2. Contrôleurs existants sur le disque mais sans FXML (programmatique) ✅

Les contrôleurs suivants existent et fonctionnent sans FXML :
- `PatientsControllerProgrammatic` (actif) vs `PatientsController` (inactif)
- `RendezVousControllerProgrammatic` (actif) vs `RendezVousController` (inactif)
- `AnalysesControllerProgrammatic` (actif) vs `AnalysesController` (inactif)
- `AdminUsersControllerProgrammatic` (actif) vs `AdminUsersController` (inactif)

## 3. Microservices backend SANS UI JavaFX dédiée

| Service backend | Endpoints API | UI JavaFX | Priorité |
|---|---|---|---|
| **surgery-service** (Chirurgie) | Programmation chirurgies, CR comptes-rendus | ❌ **Aucune UI** | Haute |
| **emergency-service** (Urgences) | Enregistrement urgences, triage | ❌ **Aucune UI** | Haute |
| **scheduling-service** (Plannings) | Plannings général | ❌ **Aucune UI** | Moyenne |
| **reporting-service** (Rapports) | Rapports exportables, stats avancées | ❌ **Aucune UI** | Moyenne |
| **procurement-service** (Approvisionnement) | Demandes d'achat, commandes fournisseurs | ❌ **Aucune UI** | Haute |
| **admission-service** (Admissions) | Rapports de sortie avancés | ✅ Admissions base OK, pas de rapport sortie UI | Basse |
| **ward-service** (Services/Hospitalisation) | Gestion des services | ✅ Via Hospitalisations, pas de page "Services" distincte | Basse |

## 4. Bugs et problèmes résolus (cette session)

| Problème | Statut |
|---|---|
| ⚠️ FXML `dossier_medical.fxml` → `dossier-medical.fxml` (traît d'union vs underscore) | ✅ **Corrigé** |
| ⚠️ `ForgotPasswordController` non enregistré dans ComponentRegistry | ⚠️ **Encore à faire** (dépend de LoginController) |
| ⚠️ `ImagerieController` non enregistré | ✅ **Corrigé** |
| ⚠️ `UpcomingAppointmentsController` non enregistré | ✅ **Corrigé** |
| ⚠️ AccessControl : `notifications`, `chirurgie`, `urgence` manquants | ✅ **Corrigé** |
| ⚠️ API Notifications manquantes dans ApiService | ✅ **Corrigé** (getNotifications, markAsRead, etc.) |
| ⚠️ Contrôleurs `FacturesController`, `PaiementsController`, `NotificationController` inexistants | ✅ **Créés et enregistrés** |
| ⚠️ SidebarIconFactory : icônes manquantes pour les nouveaux modules | ⚠️ **À faire** (fallback sur l'icône par défaut existante) |
| ⚠️ MenuConfig : factures, paiements, notifications non listés dans la sidebar | ⚠️ **À faire** |

## 5. Todo restant

- [ ] Ajouter `factures`, `paiements`, `notifications` dans `MenuConfig.getMenuItems()`
- [ ] Ajouter une icône `notification` / `factures` / `paiements` dans `SidebarIconFactory`
- [ ] Créer les pages : ChirurgieController, UrgenceController, ApprovisionnementController
- [ ] Ajouter un badge de notification (cloche) dans le header du dashboard
- [ ] Enregistrer `ForgotPasswordController` (via le flow Login)
