# GinDHO Frontend Roadmap

Date de mise a jour: 2026-06-18

## Regle de cadrage

Cette roadmap cible uniquement les microservices GinDHO exposes via Kong ou
`api-gateway`. Le backend monolithique est hors cible pour les nouveaux travaux.

## Phase 1 - Fondations

Objectif: rendre le frontend gouvernable, testable et extensible.

| ID | Description | Priorite | Complexite | Dependances | Criteres d'acceptation |
|---|---|---:|---:|---|---|
| Task-001 | Creer documentation de gouvernance frontend | P0 | M | Analyse | `ARCHITECTURE.md`, `ROADMAP.md`, `TODO.md`, `CHANGELOG.md` presents |
| Task-002 | Definir structure modulaire enterprise | P0 | L | Task-001 | Arborescence `app/shared/entities/features/services/pages` documentee |
| Task-003 | Ajouter scripts qualite `typecheck`, `test`, `test:a11y`, `test:security` | P0 | M | Task-001 | Scripts npm executables |
| Task-004 | Configurer Vitest et Testing Library | P0 | M | Task-003 | Test unitaire exemple vert |
| Task-005 | Ajouter Error Boundary globale | P0 | M | Task-002 | Fallback accessible et teste |
| Task-006 | Activer lazy loading des routes | P1 | M | Task-002 | Modules charges par chunk |

## Phase 2 - Authentification

Objectif: aligner l'auth sur `identity-service`, `authorization-service`, Keycloak et
la strategie microservices.

| ID | Description | Priorite | Complexite | Dependances | Criteres d'acceptation |
|---|---|---:|---:|---|---|
| Task-007 | Centraliser session/auth dans un service dedie | P0 | M | Task-002 | Stockage session unique |
| Task-008 | Ajouter gestion globale 401/403 | P0 | M | Task-007 | Session invalide traitee globalement |
| Task-009 | Creer modele RBAC frontend roles + permissions | P0 | L | Task-007 | Guards testes |
| Task-010 | Proteger routes par permissions | P0 | L | Task-009 | Acces interdit verifie |
| Task-011 | Preparer migration cookie HttpOnly | P1 | L | identity-service, ADR 0004 | Frontend compatible cookie |

## Phase 3 - Navigation

Objectif: fournir une navigation hospitaliere permissionnelle et scalable.

| ID | Description | Priorite | Complexite | Dependances | Criteres d'acceptation |
|---|---|---:|---:|---|---|
| Task-016 | Remplacer menu statique par registre de navigation type | P0 | M | Task-009 | Menu filtre par permission |
| Task-017 | Renforcer layout responsive | P1 | M | Task-016 | Mobile, tablette, desktop, ultra-wide valides |
| Task-018 | Ajouter breadcrumbs et etats actifs robustes | P1 | S | Task-016 | Navigation claire |

## Phase 4 - Dashboard

Objectif: livrer un cockpit operationnel base sur `reporting-service` et les endpoints
agreges exposes par la gateway.

| ID | Description | Priorite | Complexite | Dependances | Criteres d'acceptation |
|---|---|---:|---:|---|---|
| Task-033 | Cartographier endpoints dashboard microservices | P1 | M | Task-012 | Contrats documentes |
| Task-034 | Creer dashboard par role | P1 | L | Task-009, Task-033 | Widgets visibles selon RBAC |
| Task-035 | Ajouter etats temps reel via API/SSE/WebSocket si disponible | P2 | L | Service temps reel | Pas de consommation Kafka directe |

## Phase 5 - Modules Metier

Objectif: remplacer les placeholders par des modules branches aux microservices.

| ID | Description | Priorite | Complexite | Dependances | Criteres d'acceptation |
|---|---|---:|---:|---|---|
| Task-019 | Refondre module Patients sur `patient-service` | P0 | L | Task-012, Task-016 | Liste, recherche, pagination, erreurs |
| Task-020 | Ajouter formulaire patient valide Zod | P0 | L | Task-019 | Creation/modification testees |
| Task-021 | Module Rendez-vous sur `appointment-service` | P1 | L | Task-012 | Liste et creation |
| Task-022 | Module Dossier medical sur `medical-record-service` | P1 | XL | Task-010 | Donnees sensibles protegees |
| Task-023 | Module Facturation sur `billing-service` et `payment-service` | P2 | L | Task-010 | Acces accounting seulement |

## Phase 6 - IA

Objectif: definir les usages IA sans compromettre les donnees medicales.

| ID | Description | Priorite | Complexite | Dependances | Criteres d'acceptation |
|---|---|---:|---:|---|---|
| Task-024 | Definir politique IA et donnees interdites | P2 | M | Revue securite | Politique documentee |
| Task-025 | Ajouter shell UI assistant IA | P3 | L | Task-024 | Aucun envoi de PHI non valide |

## Phase 7 - Optimisation

Objectif: industrialiser qualite, performance et accessibilite.

| ID | Description | Priorite | Complexite | Dependances | Criteres d'acceptation |
|---|---|---:|---:|---|---|
| Task-026 | Ajouter tests accessibilite automatises | P0 | M | Task-004 | Axe ou equivalent configure |
| Task-027 | Ajouter budgets performance frontend | P1 | M | Task-004 | Budget documente et mesure |
| Task-028 | Ajouter audit anti-dette automatique | P1 | M | Task-003 | Imports/code mort detectes |
| Task-029 | Ajouter Playwright smoke tests | P1 | L | Routes stables | Auth/navigation testees |

## Phase 8 - Production

Objectif: rendre le frontend deployable en environnement hospitalier.

| ID | Description | Priorite | Complexite | Dependances | Criteres d'acceptation |
|---|---|---:|---:|---|---|
| Task-030 | Ajouter CI frontend complete | P0 | M | Task-003, Task-004 | lint/typecheck/test/build obligatoires |
| Task-031 | Ajouter Dockerfile frontend production | P1 | M | Build stable | Image buildable |
| Task-032 | Ajouter configuration runtime env | P1 | M | Task-012 | API URL configurable sans rebuild |
