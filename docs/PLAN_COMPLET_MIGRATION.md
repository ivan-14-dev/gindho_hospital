# GinDHO - Plan Complet de Migration et d'Amélioration

**Date** : 2026-06-14
**Objectif** : Compléter et stabiliser l'ensemble des 31 microservices, mettre à niveau les tests, et finaliser la migration depuis le monolithe.

---

## Phase 0 : Stabilisation Immédiate (Sprint 0 - 2 jours)

### Problèmes bloquants résolus
- ✅ `authorization-service` : clé YAML dupliquée `logging.file` supprimée
- ✅ `identity-service` : variables d'environnement compatibles (PORT, SPRING_DATASOURCE_URL, KEYCLOAK_URL)
- ✅ `docker/postgres-init/init-dbs.sh` : base `api_gateway` ajoutée

### Reste à vérifier
- [ ] `patient-service` : image Docker - investiguer l'échec repository
- [ ] Builder et tester chaque service individuellement
- [ ] Vérifier le docker-compose complet (956 lignes)

---

## Phase 1 : Services Critiques Auth & Patients (Sprint 1 - 5 jours)

### 1.1 `identity-service` — COMPLETER AUTH

**Ce qui existe déjà en code :**
- ✅ `POST /api/auth/register` (AuthController.java)
- ✅ `POST /api/auth/login`
- ✅ `POST /api/auth/forgot-password`
- ✅ `POST /api/auth/reset-password`
- ✅ `GET /api/auth/me`
- ✅ `GET /api/users` (liste, pagination, search)
- ✅ `GET /api/users/doctors`
- ✅ `GET /api/users/staff`
- ✅ `GET /api/users/role/{roleName}`
- ✅ `GET /api/users/{id}`
- ✅ `PUT /api/users/{id}`
- ✅ `PUT /api/users/{id}/role`
- ✅ `PUT /api/users/{id}/activate`
- ✅ `PUT /api/users/{id}/deactivate`
- ✅ `DELETE /api/users/{id}`

**À améliorer :**
- [ ] `PasswordResetOtp` entity/repository — vérifier si complet
- [ ] `MailService` — intégrer dans identity-service (ou externaliser dans notification-service)
- [ ] Anti-enumeration validation (forgot-password)
- [ ] Tests unitaires (contrôleur + service)
- [ ] Tests smoke Docker

### 1.2 `authorization-service` — COMPLETER ADMIN PERMISSIONS

**Ce qui existe déjà en code :**
- ✅ `PermissionCatalog` (security/PermissionCatalog.java)
- ✅ `RoleTemplate`, `RoleTemplatePermission` models
- ✅ `RoleTemplateRepository`
- ✅ DTOs : `RoleTemplateDto`, `RoleTemplateCreateRequest`, `PermissionCatalogEntry`

**Endpoints à ajouter/vérifier :**
- [ ] `GET /api/admin/permissions/catalog`
- [ ] `POST /api/admin/role-templates`
- [ ] `GET /api/admin/role-templates`
- [ ] `PUT /api/admin/role-templates/{templateId}/apply/{userId}`
- [ ] `PUT /api/admin/users/{id}/active`
- [ ] `PUT /api/admin/users/{id}/permissions`

**Tests :**
- [ ] Compléter `AuthorizationServiceTest.java`
- [ ] Ajouter test controller

### 1.3 `patient-service` — COMPLETER DOSSIER & DASHBOARD

**Ce qui existe déjà en code :**
- ✅ `PatientController` avec CRUD complet
- ✅ `PatientDossierResponse`, `PatientDashboardDto`
- ✅ `ContactService`, `AssuranceService`, `DocumentService`, `PatientAccessService`
- ✅ `PatientEvent` (Kafka)

**Endpoints à vérifier/ajouter :**
- [ ] `GET /api/patients/{id}/dossier`
- [ ] `GET /api/patients/{id}/dossier-complet` (agrégation)
- [ ] `GET /api/patients/me`
- [ ] `GET /api/patients/{id}/dashboard`

**Tests :**
- [ ] Compléter `PatientControllerTest.java`
- [ ] Compléter `PatientServiceTest.java`

---

## Phase 2 : Rendez-vous & Planning (Sprint 2 - 4 jours)

### 2.1 `appointment-service`

**Existant :** liste, détail, par patient, par médecin, création, confirm/cancel/complete, disponibilités, waiting list

**À ajouter :**
- [ ] `GET /api/appointments/upcoming`
- [ ] `PUT /api/appointments/{id}` (update complet)
- [ ] `PATCH /api/appointments/{id}/statut` (statut générique)
- [ ] `DELETE /api/appointments/{id}` (annulation avec motif)
- [ ] `DELETE /api/availabilities/{id}`

**Tests :**
- [ ] Compléter `RendezVousServiceTest.java`

---

## Phase 3 : RH & Personnel (Sprint 3 - 3 jours)

### 3.1 `hr-service`

**Existant :** employees, doctors (lecture), presence, conges

**À ajouter :**
- [ ] Modèle/service Medecin complet (ou mapper avec Employe)
- [ ] `GET /api/hr/doctors/{id}`
- [ ] `GET /api/hr/doctors/by-user/{userId}`
- [ ] `POST /api/hr/doctors`
- [ ] `PUT /api/hr/doctors/{id}`
- [ ] `DELETE /api/hr/doctors/{id}`
- [ ] Dashboard médecin
- [ ] Endpoints présence et congés complets

**Tests :**
- [ ] Créer tests unitaires HR

---

## Phase 4 : Soins Médicaux & Dossier (Sprint 4 - 5 jours)

### 4.1 `medical-record-service` — AJOUTER SOINS INFIRMIERS

**Existant :** consultation, diagnostic, observation

**Déjà en code (NursingController, SigneVitaux, PlanSoin, AdministrationMedicament) :**
- ✅ `NursingController.java`
- ✅ `SigneVitauxService`, `PlanSoinService`, `AdministrationMedicamentService`
- ✅ `SigneVitauxRepository`, `PlanSoinRepository`, `AdministrationMedicamentRepository`
- ✅ DTOs : `SigneVitauxDto`, `PlanSoinDto`, `AdministrationMedicamentDto`

**À vérifier/ajouter :**
- [ ] Vérifier que les endpoints nursing sont exposés
- [ ] Agrégation dossier médical complet
- [ ] Événements Kafka pour observations et soins

**Tests :**
- [ ] Créer tests nursing

---

## Phase 5 : Facturation & Financial (Sprint 5 - 3 jours)

### 5.1 `billing-service` + `payment-service`

**Existant :** factures CRUD, paiements, transactions

**À ajouter :**
- [ ] Endpoints revenus (`/api/revenus/*`)
- [ ] Paiements par patient
- [ ] Clarifier ownership entre billing et payment

**Tests :**
- [ ] Compléter `BillingServiceTest.java`
- [ ] Compléter `PaymentServiceTest.java`

---

## Phase 6 : Pharmacie & Prescriptions (Sprint 6 - 3 jours)

### 6.1 `pharmacy-service` + `prescription-service`

**Existant :** médicaments/lots, prescriptions partielles

**À ajouter :**
- [ ] Maladies CRUD (`/api/maladies/*`)
- [ ] Maladies patient
- [ ] Update/delete prescription
- [ ] Stock pharmacie (recherche, update quantité)

**Tests :**
- [ ] Créer tests pharmacy/prescription

---

## Phase 7 : Laboratoire & Imagerie (Sprint 7 - 2 jours)

### 7.1 `laboratory-service`

**Existant :** liste, détail, patient, création, résultat

**À ajouter :**
- [ ] `GET /api/analyses/medecin/{medecinId}`
- [ ] `GET /api/analyses/urgentes`
- [ ] `PUT /api/analyses/{id}`
- [ ] `DELETE /api/analyses/{id}`

**Tests :**
- [ ] Créer tests laboratoire

---

## Phase 8 : Hospitalisation & Lits (Sprint 8 - 3 jours)

### 8.1 `admission-service`, `bed-service`, `ward-service`

**Existant :** admissions, sortie simple, assign/release lit, wards

**À compléter :**
- [ ] CRUD chambres complet (`bed-service`)
- [ ] CRUD lits complet
- [ ] Hospitalisations en cours/all/update/delete
- [ ] Rapport de sortie

**Tests :**
- [ ] Créer tests admission/bed/ward

---

## Phase 9 : Nouveaux Services (Sprint 9 - 5 jours)

### 9.1 `nursing-service` ou extension `medical-record-service`
- [ ] Déjà implémenté dans medical-record-service (NursingController)
- [ ] Ajouter événements Kafka

### 9.2 `quality-service` ou extension `audit-service`
- [ ] Créer service ou module qualité
- [ ] Migrer `AuditQualite`, `Incident` depuis monolithe
- [ ] Endpoints `/api/qualite/audits`, `/api/incidents`

### 9.3 `teleconsultation-service`
- [ ] Déjà présent dans `appointment-service` (TeleconsultationController)
- [ ] Vérifier complétude

---

## Phase 10 : Notifications & Temps Réel (Sprint 10 - 3 jours)

### 10.1 `notification-service`

**Existant :** endpoint send notification, consumer Kafka

**À ajouter :**
- [ ] `MailService` (migrer depuis monolithe)
- [ ] WebSocket ou SSE
- [ ] Consumer Kafka `AppointmentBooked`
- [ ] Scheduler rappels RDV (déjà présent : ReminderScheduler.java)

**Tests :**
- [ ] Créer tests notification

---

## Phase 11 : Monitoring & Observabilité (Sprint 11 - 3 jours)

- [ ] Vérifier Prometheus metrics sur tous les services
- [ ] Compléter dashboards Grafana
- [ ] Compléter alertes
- [ ] Jaeger tracing distribué
- [ ] Loki pour les logs

---

## Phase 12 : Frontend Alignment (Sprint 12 - 5 jours)

- [ ] Rediriger React vers les microservices (via Kong)
- [ ] Rediriger JavaFX vers les microservices
- [ ] Feature flags pour chaque migration
- [ ] Décommissionner les endpoints monolithes

---

## Phase 13 : Tests & Validation Globale (Sprint 13 - 5 jours)

- [ ] Tests d'intégration complets
- [ ] Smoke tests Docker (30+ services)
- [ ] Tests de charge
- [ ] Tests de sécurité
- [ ] Validation des feature flags

---

## Phase 14 : Documentation & Déploiement (Sprint 14 - 3 jours)

- [ ] Mise à jour complète de la documentation API
- [ ] Guides de déploiement
- [ ] Runbooks d'exploitation
- [ ] Dépôt Helm charts finalisés

---

## Récapitulatif : Services par État

| État | Services | Actions |
|------|----------|---------|
| ✅ **Complets (15)** | ambulance, asset, audit, emergency, event, imaging, insurance, inventory, procurement, round, surgery, scheduling, ward + common, generator-core (libs) | Vérification / tests légers |
| ⚠️ **Partiels (12)** | identity, authorization, patient, appointment, hr, notification, medical-record, billing, payment, pharmacy, prescription, laboratory | Compléter endpoints manquants |
| ❌ **À créer (2)** | quality-service, teleconsultation-service (ou modules) | Migrer depuis monolithe |
| 📐 **Infrastructure** | api-gateway, admission, bed | Compléter CRUD |

---

## Totaux

- **31 services** (dont 2 librairies sans Docker)
- **29 Dockerfiles** valides
- **6 fichiers de tests existants** → objectif : **30+ fichiers de tests**
- **~150 endpoints monolithes** à migrer → **~50 déjà migrés** → **~100 restants**

---

## Actions Immédiates (cette session)

### Priorité 0 : Vérifier et corriger les services qui ne démarrent pas
1. [ ] Vérifier `patient-service` (échec repository)
2. [ ] Builder `identity-service` et vérifier démarrage
3. [ ] Builder `authorization-service` et vérifier démarrage

### Priorité 1 : Compléter les services critiques
4. [ ] Ajouter les endpoints manquants dans `authorization-service` (role-templates, permissions)
5. [ ] Ajouter dashboard patient dans `patient-service`
6. [ ] Ajouter upcoming + statut + annulation dans `appointment-service`
7. [ ] Ajouter CRUD docteurs complet dans `hr-service`
8. [ ] Ajouter WebSocket + Mail + Scheduler dans `notification-service`

### Priorité 2 : Tests
9. [ ] Ajouter tests pour identity-service
10. [ ] Ajouter tests pour authorization-service
11. [ ] Ajouter tests pour appointment-service (upcoming, statut)
12. [ ] Ajouter tests pour hr-service
13. [ ] Ajouter tests pour notification-service

---

**Note** : Beaucoup de code existe déjà dans les microservices mais n'est pas listé dans le document `MICROSERVICES-IMPLEMENTATION-GAPS.md`. Une réconciliation complète est nécessaire.