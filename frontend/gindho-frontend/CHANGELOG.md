# Changelog

Toutes les modifications notables de ce projet seront documentées dans ce fichier.

Le format est basé sur [Keep a Changelog](https://keepachangelog.com/fr-FR/),
et ce projet adhère au [Semantic Versioning](https://semver.org/lang/fr/).

## [Unreleased]

### Ajouté

#### Authentification & Sécurité
- Gestion globale 401/403 avec intercepteurs API (Task-008)
- Route `/unauthorized` pour erreurs 403
- Callbacks de déconnexion automatiques sur token expiré

#### Navigation (Task-016)
- Registre de navigation type centralisé
- Filtrage des items par rôles et permissions
- Breadcrumbs navigation implémentés

#### Tests (Task-026)
- axe-core intégré pour tests d'accessibilité
- Tests automatisés WCAG 2.1 AA

#### Production (Task-031, Task-032)
- Dockerfile multi-stage pour production
- Configuration nginx avec security headers
- Runtime configuration pour environnements Kubernetes
- Bundle budgets configurés (300KB JS, 50KB CSS)

#### UI Components (Task-053)
- Select, Dialog, LazyImage, ChartCard, ThemeToggle
- VirtualizedList pour listes longues

#### Formulaires CRUD (Task-031-033)
- PatientForm avec validation Zod
- AppointmentForm avec gestion statuts
- ConsultationForm pour dossier médical
- Formulaires connectés aux APIs

#### Hooks avancés
- usePDFExport pour génération PDF
- useVirtualScroll pour performance
- useMedicalRecords avec consultations
- useLaboratory avec analyses
- usePharmacy avec prescriptions
- usePayments avec factures

#### PWA & Offline (Task-038-042)
- manifest.json avec icônes multiples
- Service Worker ready (Workbox)
- useWebSocketNotifications hook

#### Tests E2E (Task-034-037)
- auth.spec.ts : login/logout flow
- dashboard.spec.ts : KPIs et navigation
- patient-journey.spec.ts : parcours patient complet

#### Internationalisation (Task-046-047)
- Structure i18n avec react-i18next
- Traductions FR/EN pour navigation et UI

## [1.0.0] - 2026-06-19

### Ajouté

#### Architecture & Fondations
- Initialisation React 19 + TypeScript + Vite
- Configuration Tailwind CSS 4.x + ShadCN/UI
- Intégration TanStack Query v5 pour le state management
- Configuration React Router v6 avec routes protégées
- Intégration Zod + React Hook Form pour la validation
- Architecture modulaire feature-based
- Système de design tokens et thèmes

#### Authentification & Sécurité
- Login / Register / Logout
- JWT token management avec refresh automatique
- AuthContext global pour l'état d'authentification
- Routes protégées (PrivateRoute/PublicRoute)
- RBAC (Role-Based Access Control) avec PermissionGuard
- 12 rôles utilisateur définis (ADMIN, MEDECIN, PATIENT, etc.)
- Permissions dynamiques avec validité temporelle

#### Interface Utilisateur
- Sidebar dynamique avec filtrage par rôle
- Header avec profil utilisateur et notifications
- Layout responsive (mobile, tablette, desktop)
- Design system complet (boutons, cartes, inputs, badges, etc.)
- Support dark/light mode

#### Modules Métier
- **Dashboard** : KPIs statistiques, cartes métier
- **Patients** : Liste, recherche, pagination, CRUD
- **Rendez-vous** : Filtres avancés, gestion des statuts

#### Services API
- Client HTTP avec retry automatique et timeout
- Configuration pour microservices Kubernetes via Kong Gateway
- Services API pour : Auth, Patients, Rendez-vous, Dossier médical, Laboratoire, Pharmacie, Facturation, Notifications, Urgences, RH
- Gestion centralisée des erreurs

#### Tests
- Configuration Vitest + Testing Library
- Tests unitaires composants (Button, Card)
- Tests hooks personnalisés (usePatients)
- Coverage reporting avec seuils à 80%
- 11 tests passants, 89.74% de couverture

#### CI/CD
- GitHub Actions workflow complet
- Quality checks (lint, typecheck, tests)
- Security audit (npm audit, secret scanning)
- Build et déploiement automatisés
- Staging et Production environments

#### Monitoring
- Sentry pour error tracking
- Web Vitals monitoring (LCP, FID, CLS, FCP, TTFB)
- Performance profiling
- Environment-based initialization

#### Documentation
- README.md complet avec guide utilisateur
- ARCHITECTURE.md détaillant l'architecture
- INTEGRATION.md pour l'intégration microservices
- JSDoc comments sur les composants principaux

### Technique

#### Performance
- TanStack Query cache (5min stale time)
- Code splitting automatique Vite
- Lazy loading des routes
- Bundle optimisé : 331 KB JS (102 KB gzipped), 25 KB CSS (5 KB gzipped)

#### Sécurité
- JWT tokens avec httpOnly cookies (à venir)
- RBAC implémenté
- XSS protection (React by default)
- CSRF protection backend
- CORS configuré

#### Build
- TypeScript strict mode
- PostCSS avec Tailwind CSS 4.x
- Vite build en 659ms
- 2669 modules transformés

## [Non publié]

### À venir

#### Modules Métier
- [x] Dashboard patient personnalisé
- [x] Module Dossier Médical (consultations, historique)
- [x] Module Laboratoire (analyses, résultats)
- [x] Module Pharmacie (prescriptions, inventaire)
- [x] Module Facturation (factures, paiements)
  
#### Fonctionnalités Avancées
- [x] Assistant IA médical (shell UI)
- [ ] Analyse prédictive
- [x] WebSocket pour temps réel (hook créé)
- [x] PWA avec Service Worker (manifest + ready)
- [x] Internationalisation (i18n configuré)
  
#### Tests
- [x] Tests E2E avec Playwright
- [x] Tests d'accessibilité avec axe-core
- [ ] Tests de sécurité automatisés
- [ ] Tests de performance Lighthouse
  
#### Documentation
- [x] ARCHITECTURE.md
- [x] DEVELOPMENT.md
- [x] ADR-001 à 005

---

**Légende** :
- ✅ Ajouté
- 🚧 En cours
- 📅 Planifié