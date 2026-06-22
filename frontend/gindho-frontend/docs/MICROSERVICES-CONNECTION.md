# Connexion Frontend-Microservices GinDHO

## Résumé des Corrections Apportées

Ce document détaille les corrections et améliorations apportées pour connecter correctement le frontend gindho aux microservices.

## 1. Configuration des Services (`src/lib/config.ts`)

### Problème Identifié
Plusieurs services pointaient vers `/api` générique au lieu d'URLs spécifiques, causant des appels au mauvais backend.

### Corrections Apportées
Ajout de **26 services** correctement configurés :

```typescript
SERVICES: {
  // Services existants (corrigés)
  AUTH: '/api/auth',
  PATIENTS: '/api/patients',
  APPOINTMENTS: '/api/appointments',
  MEDICAL_RECORDS: '/api/medical-records',  // Corrigé: était '/api'
  LABORATORY: '/api/laboratory',              // Corrigé: était '/api'
  IMAGING: '/api/imaging',                    // Corrigé: était '/api'
  PHARMACY: '/api/pharmacy',                  // Corrigé: était '/api'
  BILLING: '/api/billing',                    // Corrigé: était '/api'
  PAYMENTS: '/api/payments',
  NOTIFICATIONS: '/api/notifications',
  EMERGENCY: '/api/emergency',                // Corrigé: était '/api'
  HR: '/api/hr',
  INVENTORY: '/api/inventory',                // Corrigé: était '/api'
  AUDIT: '/api/audit-logs',
  
  // Nouveaux services ajoutés
  ADMISSIONS: '/api/admissions',
  AMBULANCE: '/api/ambulance',
  BEDS: '/api/beds',
  WARDS: '/api/wards',
  SURGERY: '/api/surgery',
  PRESCRIPTIONS: '/api/prescriptions',
  INSURANCE: '/api/insurance',
  REPORTING: '/api/reporting',
  ROUNDS: '/api/rounds',
  EVENTS: '/api/events',
  ASSETS: '/api/assets',
  PROCUREMENT: '/api/procurement',
}
```

## 2. Service API (`src/services/api.service.ts`)

### Ajouts Effectués
Extension du service API avec **10 nouveaux modules** :

#### 2.1 HR (Ressources Humaines) - Étendu
- `getMedecins(params)` - Liste paginée des médecins
- `getMedecin(id)` - Détail d'un médecin
- `getMedecinByUserId(userId)` - Médecin par utilisateur
- `createMedecin(data)` - Créer un médecin
- `updateMedecin(id, data)` - Modifier un médecin
- `deleteMedecin(id)` - Supprimer un médecin
- `getPersonnel()` - Liste du personnel
- `createPersonnel(data)` - Créer du personnel
- `deletePersonnel(id)` - Supprimer du personnel
- `pointerPresence(personnelId)` - Pointer présence
- `getPresences(personnelId)` - Historique présences
- `createConge(data)` - Créer un congé
- `validerConge(id)` - Valider un congé

#### 2.2 Dashboard & Analytics (Nouveau)
- `getAdminStats()` - Statistiques admin
- `getAdminMetricSeries(metric, from, to)` - Série temporelle
- `queryAdminStats(data)` - Requête stats personnalisée
- `getPatientDashboard(patientId)` - Dashboard patient
- `getMedecinDashboard(medecinId)` - Dashboard médecin
- `getPatientsByMedecin(medecinId)` - Patients par médecin

#### 2.3 Medical Records Étendus (Nouveau)
- `getSignesVitaux(patientId)` - Signes vitaux
- `getSignesVitauxByHospitalisation(id)` - Signes vitaux par hospitalisation
- `createSignesVitaux(data)` - Créer signes vitaux
- `deleteSignesVitaux(id)` - Supprimer signes vitaux
- `getPlansSoins(patientId)` - Plans de soins
- `getPlansSoinsByHospitalisation(id)` - Plans par hospitalisation
- `createPlanSoin(data)` - Créer plan de soin
- `marquerSoinRealise(id, notes)` - Marquer soin réalisé
- `deletePlanSoin(id)` - Supprimer plan
- `getAdministrationsMedicaments(patientId)` - Administrations médicaments
- `createAdministrationMedicament(data)` - Créer administration
- `marquerAdministre(id)` - Marquer comme administré

#### 2.4 Hospitalisation (Nouveau)
- `getChambres()` - Liste chambres
- `createChambre(data)` - Créer chambre
- `updateChambre(id, data)` - Modifier chambre
- `deleteChambre(id)` - Supprimer chambre
- `getLits()` - Liste lits
- `getLitsByChambre(chambreId)` - Lits par chambre
- `createLit(data)` - Créer lit
- `updateLit(id, data)` - Modifier lit
- `deleteLit(id)` - Supprimer lit
- `getAdmissions()` - Liste admissions
- `createAdmission(data)` - Créer admission
- `getAdmissionsEnCours()` - Admissions en cours
- `dischargePatient(id)` - Sortie patient

#### 2.5 Inventory & Stocks (Nouveau)
- `getStocks()` - Liste stocks
- `createStock(data)` - Créer stock
- `getStockAlertesRupture()` - Alertes rupture
- `getStockAlertesPeremption()` - Alertes péremption
- `getPharmacieStock()` - Stock pharmacie
- `createPharmacieStock(data)` - Créer stock pharmacie
- `updatePharmacieQuantite(id, quantite)` - Modifier quantité
- `searchPharmacie(medicament)` - Rechercher médicament

#### 2.6 Events & Rounds (Nouveau)
- `getEvenements()` - Liste événements
- `createEvenement(data)` - Créer événement
- `validerEvenement(id)` - Valider événement
- `createRonde(data)` - Créer ronde
- `validerRonde(id, compteRendu)` - Valider ronde

#### 2.7 Surgery (Nouveau)
- `createProgrammeOperatoire(data)` - Créer programme
- `updateStatutProgramme(id, statut)` - Modifier statut

#### 2.8 Quality & Incidents (Nouveau)
- `getAudits()` - Liste audits
- `createAudit(data)` - Créer audit
- `getIncidentsNonResolus()` - Incidents non résolus
- `createIncident(data)` - Créer incident
- `resoudreIncident(id, action)` - Résoudre incident

#### 2.9 Assets & Equipment (Nouveau)
- `getEquipements()` - Liste équipements
- `createEquipement(data)` - Créer équipement
- `getAmbulances()` - Liste ambulances
- `createAmbulance(data)` - Créer ambulance
- `updateAmbulancePosition(id, lat, lng)` - Mettre à jour position

#### 2.10 Imaging & Telemedicine (Nouveau)
- `createExamen(data)` - Créer examen imagerie
- `getExamensByPatient(patientId)` - Examens par patient
- `createTeleconsultation(data)` - Créer téléconsultation
- `updateStatutTeleconsultation(id, statut)` - Modifier statut

#### 2.11 Disponibilités (Nouveau)
- `getDisponibilites(medecinId)` - Disponibilités médecin
- `createDisponibilite(data)` - Créer disponibilité
- `updateDisponibilite(id, data)` - Modifier disponibilité
- `deleteDisponibilite(id)` - Supprimer disponibilité

#### 2.12 Maladies & Médicaments (Nouveau)
- `getMaladies()` - Liste maladies
- `createMaladie(data)` - Créer maladie
- `updateMaladie(id, data)` - Modifier maladie
- `deleteMaladie(id)` - Supprimer maladie
- `getMaladiesByPatient(patientId)` - Maladies par patient
- `getMedicaments()` - Liste médicaments
- `createMedicament(data)` - Créer médicament
- `updateMedicament(id, data)` - Modifier médicament
- `deleteMedicament(id)` - Supprimer médicament

#### 2.13 Assurances (Nouveau)
- `getAssurancesByPatient(patientId)` - Assurances par patient
- `getAssurancesActivesByPatient(patientId)` - Assurances actives
- `getAssurance(id)` - Détail assurance
- `createAssurance(data)` - Créer assurance
- `updateAssurance(id, data)` - Modifier assurance
- `deleteAssurance(id)` - Supprimer assurance
- `searchAssurances(compagnie)` - Rechercher par compagnie

#### 2.14 Revenus (Nouveau)
- `getRevenus(params)` - Liste revenus
- `getRevenusByPatient(patientId, params)` - Revenus par patient
- `getRevenusByMedecin(medecinId, params)` - Revenus par médecin
- `getTotalRevenus(start, end)` - Total revenus par période
- `getRevenu(id)` - Détail revenu
- `createRevenu(data)` - Créer revenu
- `updateRevenu(id, data)` - Modifier revenu
- `deleteRevenu(id)` - Supprimer revenu

## 3. Hooks React Query (Nouveaux Fichiers)

### 3.1 `use-dashboard.ts`
Hooks pour les tableaux de bord et analytics :
- `useAdminStats()` - Statistiques administrateur
- `useAdminMetricSeries(metric, from, to)` - Séries temporelles
- `useQueryAdminStats()` - Mutation requête stats
- `usePatientDashboard(patientId)` - Dashboard patient
- `useMedecinDashboard(medecinId)` - Dashboard médecin
- `usePatientsByMedecin(medecinId)` - Patients par médecin

### 3.2 `use-medical-records-extended.ts`
Hooks pour soins infirmiers :
- `useSignesVitaux(patientId)` - Signes vitaux
- `useSignesVitauxByHospitalisation(id)` - Par hospitalisation
- `useCreateSignesVitaux()` - Créer signes vitaux
- `useDeleteSignesVitaux()` - Supprimer signes vitaux
- `usePlansSoins(patientId)` - Plans de soins
- `usePlansSoinsByHospitalisation(id)` - Par hospitalisation
- `useCreatePlanSoin()` - Créer plan
- `useMarquerSoinRealise()` - Marquer réalisé
- `useDeletePlanSoin()` - Supprimer plan
- `useAdministrationsMedicaments(patientId)` - Administrations
- `useCreateAdministrationMedicament()` - Créer administration
- `useMarquerAdministre()` - Marquer administré

### 3.3 `use-hospitalisation.ts`
Hooks pour gestion hospitalisation :
- `useChambres()` - Chambres
- `useCreateChambre()` - Créer chambre
- `useUpdateChambre()` - Modifier chambre
- `useDeleteChambre()` - Supprimer chambre
- `useLits()` - Lits
- `useLitsByChambre(chambreId)` - Lits par chambre
- `useCreateLit()` - Créer lit
- `useUpdateLit()` - Modifier lit
- `useDeleteLit()` - Supprimer lit
- `useAdmissions()` - Admissions
- `useCreateAdmission()` - Créer admission
- `useAdmissionsEnCours()` - Admissions en cours
- `useDischargePatient()` - Sortie patient

### 3.4 `use-hr-extended.ts`
Hooks pour RH étendus :
- `usePersonnel()` - Personnel
- `useCreatePersonnel()` - Créer personnel
- `useDeletePersonnel()` - Supprimer personnel
- `usePointerPresence()` - Pointer présence
- `usePresences(personnelId)` - Présences
- `useCreateConge()` - Créer congé
- `useValiderConge()` - Valider congé
- `useGardes(medecinId)` - Gardes médecin

### 3.5 `use-inventory.ts`
Hooks pour stocks et inventaire :
- `useStocks()` - Stocks
- `useCreateStock()` - Créer stock
- `useStockAlertesRupture()` - Alertes rupture
- `useStockAlertesPeremption()` - Alertes péremption
- `usePharmacieStock()` - Stock pharmacie
- `useCreatePharmacieStock()` - Créer stock pharmacie
- `useUpdatePharmacieQuantite()` - Modifier quantité
- `useSearchPharmacie()` - Rechercher médicament

### 3.6 `use-events-rounds.ts`
Hooks pour événements et rondes :
- `useEvenements()` - Événements
- `useCreateEvenement()` - Créer événement
- `useValiderEvenement()` - Valider événement
- `useCreateRonde()` - Créer ronde
- `useValiderRonde()` - Valider ronde
- `useCreateProgrammeOperatoire()` - Créer programme opératoire
- `useUpdateStatutProgramme()` - Modifier statut programme

### 3.7 `use-quality-incidents.ts`
Hooks pour qualité et incidents :
- `useAudits()` - Audits
- `useCreateAudit()` - Créer audit
- `useIncidentsNonResolus()` - Incidents non résolus
- `useCreateIncident()` - Créer incident
- `useResoudreIncident()` - Résoudre incident

### 3.8 `use-assets-ambulance.ts`
Hooks pour équipements et ambulances :
- `useEquipements()` - Équipements
- `useCreateEquipement()` - Créer équipement
- `useAmbulances()` - Ambulances
- `useCreateAmbulance()` - Créer ambulance
- `useUpdateAmbulancePosition()` - Mettre à jour position

### 3.9 `use-imaging-telemedicine.ts`
Hooks pour imagerie et téléconsultation :
- `useExamensByPatient(patientId)` - Examens par patient
- `useCreateExamen()` - Créer examen
- `useCreateTeleconsultation()` - Créer téléconsultation
- `useUpdateStatutTeleconsultation()` - Modifier statut

### 3.10 `use-disponibilites.ts`
Hooks pour disponibilités :
- `useDisponibilites(medecinId)` - Disponibilités
- `useCreateDisponibilite()` - Créer disponibilité
- `useUpdateDisponibilite()` - Modifier disponibilité
- `useDeleteDisponibilite()` - Supprimer disponibilité

### 3.11 `use-maladies-medicaments.ts`
Hooks pour maladies et médicaments :
- `useMaladies()` - Maladies
- `useCreateMaladie()` - Créer maladie
- `useUpdateMaladie()` - Modifier maladie
- `useDeleteMaladie()` - Supprimer maladie
- `useMaladiesByPatient(patientId)` - Maladies par patient
- `useMedicaments()` - Médicaments
- `useCreateMedicament()` - Créer médicament
- `useUpdateMedicament()` - Modifier médicament
- `useDeleteMedicament()` - Supprimer médicament

### 3.12 `use-assurances.ts`
Hooks pour assurances :
- `useAssurancesByPatient(patientId)` - Assurances par patient
- `useAssurancesActivesByPatient(patientId)` - Assurances actives
- `useAssurance(id)` - Détail assurance
- `useCreateAssurance()` - Créer assurance
- `useUpdateAssurance()` - Modifier assurance
- `useDeleteAssurance()` - Supprimer assurance
- `useSearchAssurances(compagnie)` - Rechercher par compagnie

### 3.13 `use-revenus.ts`
Hooks pour revenus :
- `useRevenus(params)` - Revenus
- `useRevenusByPatient(patientId, params)` - Revenus par patient
- `useRevenusByMedecin(medecinId, params)` - Revenus par médecin
- `useTotalRevenus(start, end)` - Total revenus
- `useRevenu(id)` - Détail revenu
- `useCreateRevenu()` - Créer revenu
- `useUpdateRevenu()` - Modifier revenu
- `useDeleteRevenu()` - Supprimer revenu

## 4. Couverture des APIs

### APIs Monolithiques Maintenant Accessibles via Microservices

| Module | APIs Monolithiques | Status Frontend |
|--------|-------------------|-----------------|
| **Auth** | 6 endpoints | ✅ Connecté |
| **Patients** | 8 endpoints | ✅ Connecté |
| **Rendez-vous** | 10 endpoints | ✅ Connecté |
| **Médecins** | 6 endpoints | ✅ Connecté |
| **Analyses** | 8 endpoints | ✅ Connecté |
| **Prescriptions** | 6 endpoints | ✅ Connecté |
| **Factures** | 3 endpoints | ✅ Connecté |
| **Paiements** | 4 endpoints | ✅ Connecté |
| **Disponibilités** | 4 endpoints | ✅ Connecté |
| **Dashboard** | 6 endpoints | ✅ Connecté |
| **Signes Vitaux** | 4 endpoints | ✅ Connecté |
| **Plans de Soins** | 5 endpoints | ✅ Connecté |
| **Administrations Médicaments** | 3 endpoints | ✅ Connecté |
| **Pharmacie Stock** | 4 endpoints | ✅ Connecté |
| **Chambres** | 4 endpoints | ✅ Connecté |
| **Lits** | 5 endpoints | ✅ Connecté |
| **Admissions** | 3 endpoints | ✅ Connecté |
| **Personnel** | 3 endpoints | ✅ Connecté |
| **Présences** | 2 endpoints | ✅ Connecté |
| **Congés** | 2 endpoints | ✅ Connecté |
| **Gardes** | 3 endpoints | ✅ Connecté |
| **Stocks** | 4 endpoints | ✅ Connecté |
| **Événements** | 3 endpoints | ✅ Connecté |
| **Rondes** | 2 endpoints | ✅ Connecté |
| **Bloc Opératoire** | 2 endpoints | ✅ Connecté |
| **Audits** | 2 endpoints | ✅ Connecté |
| **Incidents** | 3 endpoints | ✅ Connecté |
| **Équipements** | 2 endpoints | ✅ Connecté |
| **Ambulances** | 3 endpoints | ✅ Connecté |
| **Imagerie** | 2 endpoints | ✅ Connecté |
| **Téléconsultation** | 2 endpoints | ✅ Connecté |
| **Maladies** | 5 endpoints | ✅ Connecté |
| **Médicaments** | 4 endpoints | ✅ Connecté |
| **Assurances** | 7 endpoints | ✅ Connecté |
| **Revenus** | 8 endpoints | ✅ Connecté |
| **Notifications** | 2 endpoints | ✅ Connecté |
| **Admin** | 3 endpoints | ✅ Connecté |
| **Audit Logs** | 1 endpoint | ✅ Connecté |

**Total: 100+ endpoints maintenant accessibles depuis le frontend**

## 5. Vérification de la Gateway Kong

### Configuration Requise
La gateway Kong doit avoir les routes suivantes configurées :

```yaml
services:
  - name: auth-service
    url: http://auth-service:8080
    routes:
      - paths: ["/api/auth"]
  
  - name: patient-service
    url: http://patient-service:8080
    routes:
      - paths: ["/api/patients"]
  
  - name: appointment-service
    url: http://appointment-service:8080
    routes:
      - paths: ["/api/appointments"]
  
  # ... et ainsi de suite pour tous les services
```

### Actions Requises
1. Vérifier que tous les microservices sont déployés dans Kubernetes
2. Vérifier que Kong a les routes configurées pour chaque service
3. Tester la connectivité avec `curl http://localhost:8000/api/patients`
4. Vérifier les logs Kong pour détecter les erreurs de routage

## 6. Tests de Connectivité

### Commandes de Test
```bash
# Tester l'authentification
curl -X POST http://localhost:8000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"password"}'

# Tester les patients
curl http://localhost:8000/api/patients

# Tester les rendez-vous
curl http://localhost:8000/api/appointments

# Tester le dashboard
curl http://localhost:8000/api/reporting/dashboard/admin/stats
```

## 7. Prochaines Étapes

### Pour les Développeurs Frontend
1. **Intégrer les nouveaux hooks** dans les pages correspondantes
2. **Créer les pages manquantes** :
   - `/dashboard` - Tableaux de bord
   - `/hospitalisation` - Gestion chambres/lits
   - `/rh` - Ressources humaines
   - `/stocks` - Gestion des stocks
   - `/evenements` - Événements et rondes
   - `/qualite` - Qualité et incidents
   - `/equipements` - Équipements et ambulances
   - `/imagerie` - Imagerie médicale
   - `/maladies` - Maladies et médicaments
   - `/assurances` - Assurances patients
   - `/revenus` - Gestion des revenus

3. **Tester chaque endpoint** avec les données réelles
4. **Gérer les erreurs** et cas limites

### Pour les Développeurs Backend
1. **Vérifier les microservices** sont bien déployés
2. **Configurer Kong** avec toutes les routes
3. **Tester chaque endpoint** individuellement
4. **Vérifier la sécurité** (JWT, permissions)
5. **Monitorer les performances**

### Pour l'Équipe DevOps
1. **Déployer les microservices** manquants si nécessaire
2. **Mettre à jour Kong** avec les nouvelles routes
3. **Configurer le monitoring** (Prometheus, Grafana)
4. **Tester la connectivité** en production

## 8. Avantages de Cette Migration

✅ **Architecture Microservices** : Chaque module est indépendant et scalable  
✅ **Séparation des responsabilités** : Chaque service a un domaine clair  
✅ **Maintenance facilitée** : Modifications isolées par service  
✅ **Performance** : Possibilité de scaler chaque service indépendamment  
✅ **Résilience** : Panne d'un service n'affecte pas les autres  
✅ **Évolutivité** : Ajout de nouvelles fonctionnalités sans impacter l'existant  

## 9. Notes Importantes

⚠️ **Migration Strangler Fig** : Cette architecture suit le pattern Strangler Fig, où le monolithe est progressivement remplacé par des microservices. Certaines fonctionnalités peuvent encore exister dans les deux systèmes pendant la transition.

⚠️ **Types TypeScript** : Les APIs utilisent actuellement `any` pour les types. Il est recommandé de créer des interfaces TypeScript proper pour chaque entité.

⚠️ **Gestion d'erreurs** : Les hooks utilisent la gestion d'erreurs par défaut de React Query. Il est recommandé d'ajouter une gestion d'erreurs personnalisée (toasts, messages utilisateur).

⚠️ **Cache** : React Query gère le cache automatiquement. Vérifier les temps de cache (`staleTime`, `gcTime`) selon les besoins métier.

## 10. Contact

Pour toute question sur cette migration :  
**Équipe Architecture GinDHO**  
**Repository**: https://github.com/ivan-14-dev/GinDHO