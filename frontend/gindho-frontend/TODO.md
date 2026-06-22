# TODO - GinDHO Frontend

## 📊 État actuel : 97% → Objectif : 100%

### Tâches Roadmap terminées
- ✅ Task-008 : Gestion globale 401/403
- ✅ Task-016 : Registre de navigation type
- ✅ Task-018 : Breadcrumbs navigation
- ✅ Task-026 : axe-core pour tests a11y
- ✅ Task-027 : Bundle budgets performance
- ✅ Task-031 : Dockerfile production
- ✅ Task-032 : Runtime configuration

---

## 🔴 CRITIQUE - Bloquant production (Semaine 1)

### 1. Intégration API réelle
- [ ] **TASK-025** : Connecter module Patients aux APIs
  - Remplacer mocks par vrais appels API
  - Implémenter usePatients hook avec API réelle
  - Gérer loading/error/success states
  - Tests d'intégration
  - **Priorité** : P0
  - **Complexité** : 3

- [ ] **TASK-026** : Connecter module Rendez-vous aux APIs
  - Remplacer mocks par vrais appels API
  - Implémenter useAppointments hook avec API réelle
  - Gérer loading/error/success states
  - Tests d'intégration
  - **Priorité** : P0
  - **Complexité** : 3

- [ ] **TASK-027** : Connecter module Dossier Médical aux APIs
  - Remplacer mocks par vrais appels API
  - Implémenter useMedicalRecords hook
  - Gérer loading/error/success states
  - Tests d'intégration
  - **Priorité** : P0
  - **Complexité** : 3

- [ ] **TASK-028** : Connecter module Laboratoire aux APIs
  - Remplacer mocks par vrais appels API
  - Implémenter useLaboratory hook
  - Gérer loading/error/success states
  - Tests d'intégration
  - **Priorité** : P0
  - **Complexité** : 3

- [ ] **TASK-029** : Connecter module Pharmacie aux APIs
  - Remplacer mocks par vrais appels API
  - Implémenter usePharmacy hook
  - Gérer loading/error/success states
  - Tests d'intégration
  - **Priorité** : P0
  - **Complexité** : 3

- [ ] **TASK-030** : Connecter module Facturation aux APIs
  - Remplacer mocks par vrais appels API
  - Implémenter usePayments hook
  - Gérer loading/error/success states
  - Tests d'intégration
  - **Priorité** : P0
  - **Complexité** : 3

### 2. Formulaires CRUD complets
- [ ] **TASK-031** : Formulaire création/édition Patient
  - Formulaire avec validation Zod
  - Champs: nom, prénom, date naissance, sexe, adresse, téléphone
  - Gestion d'erreurs
  - Success callback
  - Tests formulaire
  - **Priorité** : P0
  - **Complexité** : 5

- [ ] **TASK-032** : Formulaire création/édition Rendez-vous
  - Formulaire avec validation Zod
  - Champs: patient, médecin, date, heure, motif
  - Gestion de conflits horaires
  - Tests formulaire
  - **Priorité** : P0
  - **Complexité** : 5

- [ ] **TASK-033** : Formulaire création Consultation
  - Formulaire avec validation Zod
  - Champs: patient, diagnostic, notes, traitement
  - Upload documents médicaux
  - Tests formulaire
  - **Priorité** : P0
  - **Complexité** : 5

---

## 🟠 IMPORTANT - Fonctionnalités attendues (Semaine 2)

### 3. Tests E2E
- [ ] **TASK-034** : Configurer Playwright
  - Installation et configuration
  - Setup CI/CD
  - Tests smoke
  - **Priorité** : P1
  - **Complexité** : 2

- [ ] **TASK-035** : Tests E2E - Authentification
  - Login/logout flow
  - Register flow
  - Password reset
  - **Priorité** : P1
  - **Complexité** : 3

- [ ] **TASK-036** : Tests E2E - Parcours patient
  - Créer patient
  - Prendre rendez-vous
  - Consulter dossier médical
  - **Priorité** : P1
  - **Complexité** : 5

- [ ] **TASK-037** : Tests E2E - Dashboard
  - Navigation dashboard
  - Affichage KPIs
  - Filtres et recherche
  - **Priorité** : P1
  - **Complexité** : 3

### 4. PWA (Progressive Web App)
- [ ] **TASK-038** : Configurer Service Worker
  - Installation Workbox
  - Cache stratégies
  - Offline mode basique
  - **Priorité** : P1
  - **Complexité** : 4

- [ ] **TASK-039** : Manifest PWA
  - Créer manifest.json
  - Icônes multiples tailles
  - Configuration installation
  - **Priorité** : P1
  - **Complexité** : 2

- [ ] **TASK-040** : Offline mode
  - Queue de requêtes offline
  - Sync automatique
  - Indicateur hors ligne
  - **Priorité** : P2
  - **Complexité** : 5

### 5. WebSockets temps réel
- [ ] **TASK-041** : Configurer WebSocket client
  - Connexion WebSocket
  - Gestion reconnexion
  - Heartbeat
  - **Priorité** : P1
  - **Complexité** : 3

- [ ] **TASK-042** : Notifications temps réel
  - Notifications rendez-vous
  - Notifications résultats labo
  - Toast notifications
  - **Priorité** : P1
  - **Complexité** : 4

---

## 🟡 NÉCESSAIRE - Qualité & Maintenance (Semaine 3)

### 6. Tests supplémentaires
- [ ] **TASK-043** : Tests pages (80% coverage)
  - Tests Dashboard (10 tests)
  - Tests Patients (15 tests)
  - Tests Rendez-vous (15 tests)
  - Tests MedicalRecords (10 tests)
  - Tests Laboratory (8 tests)
  - Tests Pharmacy (8 tests)
  - Tests Payments (8 tests)
  - Tests AI (5 tests)
  - Tests Analytics (5 tests)
  - **Priorité** : P1
  - **Complexité** : 8

- [ ] **TASK-044** : Tests hooks (90% coverage)
  - Tests useAuth (10 tests)
  - Tests useAppointments (8 tests)
  - Tests useMedicalRecords (8 tests)
  - Tests useLaboratory (6 tests)
  - Tests usePharmacy (6 tests)
  - Tests usePayments (6 tests)
  - **Priorité** : P1
  - **Complexité** : 6

- [ ] **TASK-045** : Tests services (70% coverage)
  - Tests tous les services API
  - Tests error handling
  - Tests retry logic
  - **Priorité** : P2
  - **Complexité** : 5

### 7. Internationalisation i18n
- [ ] **TASK-046** : Configurer i18n
  - Installation react-i18next
  - Structure traductions
  - Hooks useTranslation
  - **Priorité** : P2
  - **Complexité** : 3

- [ ] **TASK-047** : Traductions FR/EN
  - Traduire toutes les pages
  - Traduire composants UI
  - Traduire messages erreur
  - **Priorité** : P2
  - **Complexité** : 5

### 8. Thème sombre/clair
- [ ] **TASK-048** : Configurer thèmes
  - Theme provider
  - Dark mode tokens
  - Toggle component
  - **Priorité** : P2
  - **Complexité** : 3

- [ ] **TASK-049** : Implémenter dark mode
  - Appliquer à tous les composants
  - Persistance localStorage
  - Détection préférence système
  - **Priorité** : P2
  - **Complexité** : 4

---

## 🟢 AMÉLIORATION - Nice to have (Semaine 4)

### 9. Performance
- [ ] **TASK-050** : Optimiser bundle Analytics
  - Réduire taille recharts
  - Lazy load composants graphiques
  - **Priorité** : P2
  - **Complexité** : 2

- [ ] **TASK-051** : Virtual scrolling
  - Implémenter react-window
  - Listes patients/rendez-vous
  - **Priorité** : P3
  - **Complexité** : 3

- [ ] **TASK-052** : Image optimization
  - Lazy load images
  - WebP format
  - Responsive images
  - **Priorité** : P3
  - **Complexité** : 2

### 10. Documentation technique
- [ ] **TASK-053** : JSDoc complet
  - Documenter tous les composants
  - Documenter tous les hooks
  - Documenter tous les services
  - **Priorité** : P2
  - **Complexité** : 5

- [ ] **TASK-054** : Guide de développement
  - Setup environnement
  - Workflow détaillé
  - Troubleshooting
  - **Priorité** : P3
  - **Complexité** : 3

- [ ] **TASK-055** : ADR (Architecture Decision Records)
  - Documenter décisions clés
  - Pourquoi ces choix
  - Alternatives considérées
  - **Priorité** : P3
  - **Complexité** : 3

### 11. Features avancées
- [ ] **TASK-056** : Export PDF
  - Génération PDF rendez-vous
  - Génération PDF dossier médical
  - Génération PDF factures
  - **Priorité** : P3
  - **Complexité** : 5

- [ ] **TASK-057** : Recherche globale
  - Search across all modules
  - Filtres avancés
  - Suggestions
  - **Priorité** : P3
  - **Complexité** : 4

- [ ] **TASK-058** : Favoris/Bookmarks
  - Sauvegarder patients favoris
  - Raccourcis rendez-vous
  - **Priorité** : P4
  - **Complexité** : 3

---

## 📋 Plan d'implémentation progressive

### Phase 1 : CRITIQUE (Semaine 1) - 6 tâches
**Objectif** : Rendre l'application fonctionnelle

```
Jour 1-2 : TASK-025 (Patients API)
Jour 2-3 : TASK-026 (Rendez-vous API)
Jour 3-4 : TASK-027 (Medical Records API)
Jour 4-5 : TASK-028 (Laboratory API)
Jour 5-6 : TASK-029 (Pharmacy API)
Jour 6-7 : TASK-030 (Payments API)
```

**Livrable** : Toutes les pages connectées aux APIs

---

### Phase 2 : CRITIQUE (Semaine 1-2) - 3 tâches
**Objectif** : Permettre la création/modification de données

```
Jour 8-10 : TASK-031 (Formulaire Patient)
Jour 10-12 : TASK-032 (Formulaire Rendez-vous)
Jour 12-14 : TASK-033 (Formulaire Consultation)
```

**Livrable** : CRUD complet fonctionnel

---

### Phase 3 : IMPORTANT (Semaine 2) - 8 tâches
**Objectif** : Qualité et expérience utilisateur

```
Jour 15-16 : TASK-034 (Playwright setup)
Jour 16-17 : TASK-035 (E2E Auth)
Jour 17-19 : TASK-036 (E2E Parcours patient)
Jour 19-20 : TASK-037 (E2E Dashboard)
Jour 20-21 : TASK-038 (Service Worker)
Jour 21-22 : TASK-039 (Manifest PWA)
Jour 22-24 : TASK-040 (Offline mode)
Jour 24-25 : TASK-041 (WebSocket)
Jour 25-26 : TASK-042 (Notifications)
```

**Livrable** : Tests E2E + PWA + Notifications temps réel

---

### Phase 4 : NÉCESSAIRE (Semaine 3) - 10 tâches
**Objectif** : Maintenabilité et qualité

```
Jour 27-29 : TASK-043 (Tests pages)
Jour 29-31 : TASK-044 (Tests hooks)
Jour 31-32 : TASK-045 (Tests services)
Jour 32-33 : TASK-046 (i18n setup)
Jour 33-35 : TASK-047 (Traductions)
Jour 35-36 : TASK-048 (Thèmes setup)
Jour 36-37 : TASK-049 (Dark mode)
```

**Livrable** : Tests complets + i18n + Dark mode

---

### Phase 5 : AMÉLIORATION (Semaine 4) - 12 tâches
**Objectif** : Performance et fonctionnalités avancées

```
Jour 38-39 : TASK-050 (Bundle optimization)
Jour 39-40 : TASK-051 (Virtual scrolling)
Jour 40-41 : TASK-052 (Image optimization)
Jour 41-44 : TASK-053 (JSDoc)
Jour 44-45 : TASK-054 (Guide dev)
Jour 45-46 : TASK-055 (ADR)
Jour 46-48 : TASK-056 (Export PDF)
Jour 48-49 : TASK-057 (Recherche globale)
Jour 49-50 : TASK-058 (Favoris)
```

**Livrable** : Performance optimisée + features avancées

---

## 🎯 Règles d'implémentation

### 1. Une tâche à la fois
- ❌ JAMAIS plusieurs tâches simultanées
- ✅ Une tâche = Analyse → Code → Test → Verify → Document → Commit

### 2. Vérifications obligatoires
```bash
# Avant chaque commit
npm run lint
npm run typecheck
npm run test:coverage
npm run build
```

### 3. Tests obligatoires
- Tests unitaires pour chaque fonctionnalité
- Tests d'intégration pour chaque API
- Coverage maintenu/augmenté

### 4. Documentation obligatoire
- Mettre à jour README.md
- Mettre à jour CHANGELOG.md
- Commenter code complexe

### 5. Pas de régression
- Tous les tests existants doivent passer
- Build doit réussir
- Pas d'erreur TypeScript

---

## 📈 Progression

**Démarrage** : 95% (39 tests, architecture complète)

**Après Phase 1** : 80% fonctionnel (APIs connectées)
**Après Phase 2** : 90% fonctionnel (CRUD complet)
**Après Phase 3** : 95% fonctionnel (E2E + PWA)
**Après Phase 4** : 98% fonctionnel (Tests + i18n)
**Après Phase 5** : 100% production-ready

---

## 🚀 Démarrage immédiat

**TASK-025** : Connecter module Patients aux APIs

Commencer par :
1. Lire le service API existant `src/services/api.service.ts`
2. Lire le hook existant `src/hooks/use-patients.ts`
3. Remplacer les mocks par des appels API réels
4. Ajouter gestion loading/error
5. Créer tests d'intégration
6. Vérifier build et tests
7. Commit
8. Passer à TASK-026

**NE JAMAIS S'ARRÊTER** jusqu'à 100%