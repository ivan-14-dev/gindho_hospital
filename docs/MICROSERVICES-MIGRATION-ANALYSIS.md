# Analyse Migration Monolithe → Microservices

## Résumé

Le monolithe contient ~150 endpoints répartis dans ApiController.java. Cette analyse identifie les gaps par rapport aux 28 microservices existants.

## Endpoints du Monolithe par Module

### 1. Authentification (4 endpoints)
- POST /api/auth/register
- POST /api/auth/login
- POST /api/auth/forgot-password
- POST /api/auth/reset-password

**Status**: ✅ identity-service

### 2. Patients (8 endpoints)
- GET /api/patients | POST /api/patients | PUT /api/patients/{id}
- GET /api/patients/{id} | DELETE /api/patients/{id}
- GET /api/patients/{id}/dossier | GET /api/patients/{id}/dossier-complet
- GET /api/patients/me

**Status**: ✅ patient-service

### 3. Médecins (6 endpoints)
- CRUD médecins + by-user

**Status**: ✅ patient-service / hr-service

### 4. Rendez-vous (9 endpoints)
- CRUD RDV, upcoming, statut, par patient/médecin

**Status**: ✅ appointment-service

### 5. Disponibilités (4 endpoints)
**Status**: 🔴 Manquant dans scheduling-service

### 6. Analyses (9 endpoints dont PUT /{id}/result)
**Status**: ✅ laboratory-service (tous présents)

### 7. Maladies (5 endpoints)
**Status**: ✅ medical-record-service / pharmacy-service

### 8. Médicaments (4 endpoints)
**Status**: ✅ pharmacy-service

### 9. Revenus (8 endpoints)
**Status**: ✅ billing-service

### 10. Factures (3 endpoints)
**Status**: ✅ billing-service

### 11. Paiements (3 endpoints)
**Status**: ✅ payment-service

### 12. Prescriptions (6 endpoints)
**Status**: ✅ prescription-service

### 13. Assurances (7 endpoints)
**Status**: ✅ insurance-service

### 14. Dashboards (6 endpoints)
**Status**: ⚠️ reporting-service à vérifier

### 15. Signes Vitaux (4 endpoints)
**Status**: 🔴 nursing-service manquant

### 16. Plans de Soins (5 endpoints)
**Status**: 🔴 nursing-service manquant

### 17. Administration Médicaments (3 endpoints)
**Status**: 🔴 nursing-service manquant

### 18. Pharmacie Stock (4 endpoints)
**Status**: ✅ pharmacy-service (compatibles /api/pharmacie/*)

### 19. Gardes Médicales (3 endpoints)
**Status**: ✅ scheduling-service (/api/schedules/gardes)

### 20. Stocks Consommables (4 endpoints)
**Status**: ⚠️ inventory-service à vérifier

### 21. RH Personnel (3 endpoints)
**Status**: ✅ hr-service

### 22. RH Présence (2 endpoints)
**Status**: ✅ hr-service

### 23. RH Congés (2 endpoints)
**Status**: ✅ hr-service

### 24. Événements (3 endpoints)
**Status**: ✅ event-service (/api/evenements/*)

### 25. Rondes Médicales (2 endpoints)
**Status**: ✅ round-service (/api/rondes/*)

### 26. Bloc Opératoire (2 endpoints)
**Status**: ✅ surgery-service (/api/bloc/*)

### 27. Qualité (2 endpoints)
**Status**: 🔴 quality-service manquant

### 28. Incidents (3 endpoints)
**Status**: 🔴 quality-service manquant

### 29. Équipements (2 endpoints)
**Status**: ✅ asset-service

### 30. Ambulances (3 endpoints)
**Status**: ✅ ambulance-service

### 31. Imagerie (2 endpoints)
**Status**: ✅ imaging-service

### 32. Téléconsultation (2 endpoints)
**Status**: 🔴 teleconsultation-service manquant

### 33. Hospitalisations (2 endpoints)
**Status**: ✅ admission-service (/api/admissions/*)

### 34. Chambres (4 endpoints)
**Status**: ⚠️ ward-service à vérifier

### 35. Lits (5 endpoints)
**Status**: ✅ bed-service

### 36. Admin RBAC
**Status**: ⚠️ authorization-service à vérifier

### 37. Audit Logs
**Status**: ✅ audit-service

## Bilan Final (après audit complet)

| Catégorie | Nombre | Pourcentage |
|-----------|--------|-------------|
| ✅ Endpoints couverts | ~110 | **~73%** |
| ⚠️ À vérifier | ~20 | **~13%** |
| 🔴 Services à créer | ~20 | **~14%** |

## Services à Créer

### 1. nursing-service
Signes vitaux, plans de soins, administrations médicaments.

### 2. quality-service
Audits qualité, incidents.

### 3. teleconsultation-service
Création et gestion de téléconsultations.