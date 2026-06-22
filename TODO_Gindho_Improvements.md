# GinDHO — Todo liste d’améliorations (Backend + JavaFX + React)

## Phase 0 — Préparation (structure / conventions)
- [ ] Fixer les objectifs “MVP sécurité + stabilité” (RBAC unifié, DTO patient-safe, suppression duplication client, secrets)
- [ ] Définir les points de vérification (tests manuels: login + accès protégé patient + pages React + endpoints majeurs)

## Phase 1 — Backend (sécurité & architecture)
- [x] Corriger CORS dangereux (restreindre `allowedOrigins`, vérifier `allowCredentials`)
- [x] Sécuriser `application.properties` (sortir `jwt.secret` + `spring.security.user.password` hors repo)
- [x] Réduire le “RBAC hybride drift” (choisir une seule stratégie dominante + limiter le bypass filter)
- [x] Déplacer la logique “masquage patient” du client vers le backend (DTO/projection “patient-safe” pour dossier/analyses)
- [ ] Découper `ApiController` (au moins split logique par domaine : auth/patients/analyses/prescriptions/dashboard/accounting/hospitalisations)
- [x] Améliorer logs audit (ne pas logger querystring brute; whitelister/masquer champs sensibles)
- [x] Rendre `DataInitializer` non destructif en prod (pas de `clear()` écrasant permissions)

## Phase 2 — JavaFX Client (code & robustesse)
- [x] Remplacer l’état global `static` (token/role/userId) par une “Session” injectée (ou singleton non mutagène + thread-safe)
- [ ] Typage: remplacer une partie des `Map<String,Object>` par DTOs (au minimum pour les réponses utilisées dans UI)
- [x] Centraliser la configuration: `BASE_URL` en config (pas hardcodé)
- [x] Phase 2 — LoginController utilise `ApiService.login()` (suppression requête HTTP manuelle)
- [ ] Supprimer duplication “API parsing”: uniformiser sur un seul client interne (au lieu de parsing Map partout)
- [ ] Nettoyer patterns anti-pattern: supprimer `finalize()` + encapsuler threads via `ExecutorService`/`Task`

## Phase 3 — React Patient Portal (structure & sécurité)
- [ ] Supprimer duplication API (`App.tsx` → réutiliser `react-web/src/lib/api.ts`)
- [ ] Séparer `App.tsx` en composants + hooks (`useAuth`, `usePatientDossier`, `useAnalyses`, etc.)
- [ ] Sécurité: limiter exposition token (si possible migrer de `localStorage` vers cookie HttpOnly, sinon durcir CSP et réduire surface)
- [ ] Utiliser `AbortController` pour éviter race conditions lors des changements de page
- [ ] Nettoyer types: remplacer `any`/`Record<string, any>` par des types minimaux
- [ ] Vérifier design brutalist (accessibilité: focus, aria-label, overflow)

## Phase 4 — Validation (qualité)
- [ ] Backend: vérifier tous les endpoints patient-safe (dossier/analyses/prescriptions/rendezvous/paiements) renvoient uniquement ce que le patient doit voir
- [ ] React: ouvrir pages login/dashboard/dossier/analyses/prescriptions/rendezvous/paiements sans erreurs console
- [ ] Build: `npm run build` (react-web), et compilation Maven backend + JavaFX
- [ ] tsc `--noEmit` (react-web) + corriger erreurs TS
- [ ] Optionnel: ajouter tests backend (at least Security + endpoint patient-safe)

## Règles de travail
- Une seule modification logique à la fois (sécurité d’abord).
- Après chaque changement majeur: vérifier (au minimum) login + 1-2 écrans React + un endpoint protégé.
- Ne pas “cacher” des erreurs: corriger à la source.
