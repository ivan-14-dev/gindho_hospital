# GinDHO — Plan d'Amélioration Global du Système

> Plan d'évolution étape par étape couvrant : **Design**, **Architecture**, **Sécurité**, **Dynamisme** et **Qualité**.
> Le système actuel combine un backend Spring Boot monolithique (`backend/`), une architecture microservices parallèle (`services/`, 31 services), un client JavaFX et un client React.

---

## État des Lieux (constat synthétique)

| Domaine | Constat | Risque |
|---|---|---|
| Architecture | Double pile coexistante : monolithe `backend/` **et** microservices `services/*`. Pas de stratégie de migration claire. | Dérive, dette technique, code mort |
| Sécurité | Credentials par défaut dans `application.properties` et `docker-compose.yml` (`admin/admin123`, `mon_mot_de_passe`, `gindho123`). `logging.level.org.springframework.security=DEBUG`. mTLS / Vault mentionnés mais non implémentés. | Fuite secrets, élévation de privilèges |
| Design / Code | `ApiController.java` = **1033 lignes**, `App.tsx` = **2351 lignes**. RBAC hybride non unifié. Pas de design system React (aucune lib UI dans `package.json`). | Maintenabilité, cohérence UI |
| Dynamisme | Communication majoritairement synchrone côté monolithe. Kafka présent côté `services/*` mais consommateurs partiels. WebSocket existant mais non généralisé. | Couplage fort, latence, scalabilité |
| Qualité | **0 test** dans `backend/src/test` et `javafx-client/src/test`. Pas de CI visible. `ddl-auto=update` + Flyway en parallèle. | Régressions, schéma incohérent |

---

## Vision Cible

1. **Une seule pile dominante** : microservices `services/*` derrière Kong + Keycloak ; le monolithe `backend/` devient un *legacy* progressivement décommissionné via *Strangler Fig*.
2. **Sécurité Zero-Trust** : mTLS inter-services, secrets dans Vault, MFA obligatoire pour les rôles cliniques, audit immuable.
3. **Design System unifié** : tokens partagés JavaFX ↔ React, composants réutilisables, accessibilité AA.
4. **Event-driven** : Kafka comme source d'événements métier (PatientCreated, AppointmentBooked, …) avec *outbox pattern*.
5. **Qualité mesurable** : couverture ≥ 70 %, CI/CD obligatoire, *quality gate* SonarQube.

---

## Phase 0 — Préparation & Gouvernance (Semaine 1)

- [ ] Geler la création de nouveaux endpoints sur le monolithe `backend/` (sauf hotfix).
- [ ] Inventaire complet des endpoints exposés (monolithe vs microservices) → matrice de migration.
- [ ] Définir les **KPI** : MTTR, p95 latence, couverture tests, # vulnérabilités CVE > Medium.
- [ ] Mettre en place un dépôt unique de **ADR** (`docs/adr/NNNN-titre.md`).
- [ ] Choix outillage : SonarQube, Trivy, Dependabot/Renovate, OWASP ZAP.

---

## Phase 1 — Sécurité (Semaines 2-4) — **PRIORITÉ ABSOLUE**

### 1.1 Secrets & Configuration
- [ ] Retirer **tous** les mots de passe en clair de `application.properties`, `docker-compose.yml`, `k8s/*/secret.yaml`.
- [ ] Externaliser via **HashiCorp Vault** (ou Sealed Secrets en attendant).
- [ ] Rotation automatique JWT secret (≥ 256 bits) + clés Keycloak.
- [ ] `.env.example` versionné, `.env` ignoré ; pre-commit hook **gitleaks**.

### 1.2 Authentification & Autorisation
- [ ] Migrer JWT maison du monolithe vers **Keycloak OIDC** (un seul IdP).
- [ ] MFA obligatoire pour rôles : `ADMIN`, `MEDECIN`, `PHARMACIEN`.
- [ ] Unifier le RBAC : supprimer le *hybrid drift* (cf. `TODO_Gindho_Improvements.md` Phase 1).
- [ ] `authorization-service` devient l'unique PDP (Policy Decision Point) avec cache Redis.
- [ ] Désactiver `logging.level.org.springframework.security=DEBUG` en prod.

### 1.3 Transport & Réseau
- [ ] **mTLS** inter-services via Istio ou Linkerd (déjà mentionné dans `ARCHITECTURE.md`).
- [ ] TLS 1.3 minimum côté Kong, HSTS, CSP stricte côté React.
- [ ] Rate-limiting Kong par utilisateur + par IP (anti-brute-force login).
- [ ] WAF (ModSecurity / Kong plugin) devant Kong.

### 1.4 Données & Conformité (RGPD / données de santé)
- [ ] Chiffrement at-rest PostgreSQL (TDE ou pgcrypto pour colonnes sensibles).
- [ ] Pseudonymisation des exports `reporting-service`.
- [ ] Politique de rétention par domaine (patient, audit, billing) documentée.
- [ ] Audit immuable : `audit-service` écrit dans un topic Kafka *log-compacted* + stockage WORM.
- [ ] DPIA / AIPD documenté (hébergement HDS si données patients FR).

### 1.5 Application
- [ ] Migrer le token React de `localStorage` vers **cookie HttpOnly + SameSite=Strict** (cf. TODO Phase 3).
- [ ] DTO *patient-safe* généralisés (jamais d'entité JPA exposée).
- [ ] Validation entrée systématique : Bean Validation côté backend, Zod/Yup côté React.
- [ ] Dépendances : `mvn versions:display-dependency-updates` + `npm audit fix` en CI.

---

## Phase 2 — Architecture (Semaines 5-10)

### 2.1 Consolidation Monolithe → Microservices (Strangler Fig)
- [ ] Cartographier les bounded contexts du monolithe vs microservices `services/*`.
- [ ] Router progressivement via Kong : `/patient/*`, `/appointment/*`, `/emr/*` vers `services/*`.
- [ ] Découper `ApiController.java` (1033 lignes) en contrôleurs par domaine **avant** migration (cf. TODO Phase 1).
- [ ] Plan de décommissionnement endpoint-par-endpoint avec *feature flags*.

### 2.2 API Gateway & Contrats
- [ ] Spécifier toutes les API en **OpenAPI 3.1** versionné (`/v1`, `/v2`).
- [ ] Contract testing : **Pact** entre consommateurs (React, JavaFX) et producteurs.
- [ ] Génération automatique des SDK clients (TS pour React, Java pour JavaFX).
- [ ] Versionnement sémantique des événements Kafka (Avro/Protobuf + Schema Registry).

### 2.3 Communication Asynchrone
- [ ] Implémenter le **Outbox Pattern** dans chaque service (transaction DB + publication Kafka atomique).
- [ ] Topics canoniques alignés sur `ARCHITECTURE.md` § 4 (patient, appointment, billing, …).
- [ ] **Saga** pour transactions distribuées (ex : admission → bed allocation → billing).
- [ ] DLQ (Dead Letter Queue) + retry exponentiel sur tous les consumers.

### 2.4 Données
- [ ] Un schéma = un service (vérifier qu'aucun service ne lit la DB d'un autre).
- [ ] Remplacer `spring.jpa.hibernate.ddl-auto=update` par `validate` + **Flyway** exclusivement.
- [ ] CQRS léger pour `reporting-service` (vue dénormalisée alimentée par Kafka).
- [ ] Cache Redis pour les lectures chaudes (profil patient, disponibilités RDV).

### 2.5 Kubernetes & Plateforme
- [ ] Helm charts paramétrés (un chart par service, un *umbrella* pour l'env).
- [ ] GitOps avec **ArgoCD** ou Flux.
- [ ] NetworkPolicies par namespace (deny-all par défaut).
- [ ] PodSecurityStandards `restricted` ; readOnlyRootFilesystem ; runAsNonRoot.
- [ ] HPA + VPA + PodDisruptionBudget pour les services critiques.

---

## Phase 3 — Design & UX (Semaines 8-12, en parallèle)

### 3.1 Design System
- [ ] Créer `packages/design-tokens` (couleurs, espacements, typographie) consommé par React **et** JavaFX (via CSS variables ↔ JavaFX CSS).
- [ ] Adopter **shadcn/ui + Tailwind** côté React (actuellement aucune lib UI dans `package.json`).
- [ ] Bibliothèque de composants documentée avec **Storybook**.
- [ ] Mode sombre + densité (compact / confort) pour postes médicaux.

### 3.2 Accessibilité & i18n
- [ ] Conformité **WCAG 2.2 AA** : contrastes, focus visible, navigation clavier, ARIA.
- [ ] Tests automatiques `axe-core` en CI.
- [ ] i18n : `react-i18next` + JavaFX `ResourceBundle` (déjà présent : `javafx-client/src/main/resources/i18n`). FR / EN / MG.

### 3.3 Refactor frontend
- [ ] Découper `App.tsx` (**2351 lignes**) en *features* : `auth/`, `dossier/`, `analyses/`, `rendezvous/`, `paiements/`.
- [ ] Routing avec **React Router v6** ; chaque feature lazy-loadée (`React.lazy`).
- [ ] Data fetching avec **TanStack Query** (cache + revalidation + `AbortController`).
- [ ] Remplacer `Map<String,Object>` côté JavaFX par DTO typés (cf. TODO Phase 2).
- [ ] State global JavaFX : `Session` injectable thread-safe (cf. TODO Phase 2 déjà fait partiellement).

### 3.4 Dashboards par rôle
- [ ] Aligner avec `Gindho_ALL_Dashbord_Role.md` : Admin, Médecin, Patient, Secondary.
- [ ] Widgets configurables (drag & drop déjà amorcé dans `javafx-client/dragdrop`).
- [ ] KPI temps réel via WebSocket / SSE.

---

## Phase 4 — Dynamisme & Temps Réel (Semaines 11-14)

- [ ] Étendre WebSocket (`WebSocketController` existant) à : notifications, état des lits, file urgences, RDV.
- [ ] Server-Sent Events pour les flux unidirectionnels (résultats labo, monitoring patient).
- [ ] **Notification-service** : canal unifié (email, SMS, push web, push mobile) piloté par Kafka.
- [ ] *Optimistic UI* côté React pour les mutations fréquentes (RDV, prescription).
- [ ] Hot-reload de configuration via **Spring Cloud Config** + bus Kafka.
- [ ] Feature flags dynamiques (**Unleash** ou **FF4J**) pour activer progressivement les migrations.
- [ ] CDN + cache HTTP (Cache-Control, ETag) pour les assets React et les ressources publiques.

---

## Phase 5 — Qualité & Observabilité (continu, à partir Semaine 2)

### 5.1 Tests
- [ ] **Backend** : créer une base de tests (actuellement 0). Cibles :
  - Unitaires (JUnit 5 + Mockito) : services, mappers, sécurité.
  - Intégration (`@SpringBootTest` + **Testcontainers** PostgreSQL/Kafka).
  - Contract (Pact) producteur côté chaque microservice.
- [ ] **JavaFX** : TestFX pour les écrans critiques (login, dossier, RDV).
- [ ] **React** : Vitest + Testing Library ; Playwright pour E2E.
- [ ] Couverture cible : **70 %** lignes, **80 %** sur `security/`, `service/`.

### 5.2 CI/CD
- [ ] Pipeline GitHub Actions / GitLab CI obligatoire :
  - `mvn verify` + `npm run build` + `tsc --noEmit` + `eslint` + tests.
  - Scan vulnérabilités : **Trivy** (images), **Snyk**/`npm audit`, **OWASP Dependency-Check**.
  - **SonarQube** quality gate bloquant (bugs = 0, vulnerabilities = 0, coverage ≥ 70 %).
- [ ] Build d'images reproductibles (Jib pour Java).
- [ ] Déploiement progressif : canary 5 % → 25 % → 100 % via ArgoCD Rollouts.

### 5.3 Observabilité
- [ ] **Logs structurés** JSON (Logback `logstash-encoder`) avec `traceId`/`spanId`.
- [ ] **OpenTelemetry** : tracing distribué exporté vers Jaeger/Tempo.
- [ ] **Prometheus** : métriques métier (RDV/h, admissions/jour, latence p95 par endpoint).
- [ ] **Grafana** : dashboards par domaine + alerting (PagerDuty/Opsgenie).
- [ ] **Loki** : centralisation logs ; rétention conforme RGPD.
- [ ] SLO/SLI documentés par service (ex : 99,9 % dispo patient-service, p95 < 300 ms).

### 5.4 Documentation
- [ ] Mettre à jour `ARCHITECTURE.md` à chaque ADR.
- [ ] Diagrammes **C4** (Context, Container, Component) versionnés (Structurizr / Mermaid).
- [ ] Runbooks d'incidents par service.
- [ ] Onboarding développeur : `make dev` qui lance l'env local complet en < 5 min.

---

## Phase 6 — Performance & Scalabilité (Semaines 14-16)

- [ ] Profilage avec **JFR** / **async-profiler** sur les services chauds.
- [ ] Index DB audités (EXPLAIN ANALYZE sur les requêtes p95 lentes).
- [ ] Pagination + filtrage côté serveur systématique (limite `size ≤ 100`).
- [ ] Connection pool tuning (HikariCP : `maximumPoolSize` cohérent avec K8s replicas).
- [ ] Load tests **k6** ou **Gatling** : scénarios admission urgence, pic RDV matinal.
- [ ] CDN pour `react-web/dist` (Cloudflare / CloudFront).
- [ ] Compression Brotli sur Kong.

---

## Matrice Priorité × Effort

| Action | Priorité | Effort | Phase |
|---|---|---|---|
| Retirer secrets en clair | 🔴 Critique | S | 1.1 |
| Désactiver DEBUG security en prod | 🔴 Critique | XS | 1.2 |
| Unifier RBAC sur Keycloak | 🔴 Critique | L | 1.2 |
| Tests backend (de 0 à 70 %) | 🔴 Critique | L | 5.1 |
| Découper `ApiController` & `App.tsx` | 🟠 Haute | M | 2.1 / 3.3 |
| mTLS Istio | 🟠 Haute | L | 1.3 |
| Outbox pattern Kafka | 🟠 Haute | M | 2.3 |
| Design System partagé | 🟡 Moyenne | M | 3.1 |
| GitOps ArgoCD | 🟡 Moyenne | M | 2.5 |
| Load tests k6 | 🟢 Basse | S | 6 |

---

## Jalons (Roadmap 16 semaines)

```
S1      | Phase 0 — Préparation, KPI, ADR
S2-S4   | Phase 1 — Sécurité (secrets, Keycloak, mTLS, RGPD)
S5-S10  | Phase 2 — Architecture (Strangler, Kafka, K8s)
S8-S12  | Phase 3 — Design & UX (parallèle)
S11-S14 | Phase 4 — Dynamisme temps réel
S2-S16  | Phase 5 — Qualité & Observabilité (continu)
S14-S16 | Phase 6 — Performance & scalabilité
```

---

## Critères de Sortie (Definition of Done global)

- [ ] Aucun secret en clair dans le repo (vérifié par gitleaks en CI).
- [ ] Couverture tests ≥ 70 % sur `backend/` + `services/*`.
- [ ] 0 vulnérabilité **Critical/High** ouverte (Trivy + Snyk).
- [ ] p95 latence API < 500 ms sur les 10 endpoints les plus appelés.
- [ ] Tous les services microservices déployables via un seul `argocd app sync`.
- [ ] Documentation C4 et runbooks à jour pour chaque service en prod.
- [ ] Conformité WCAG 2.2 AA validée sur les écrans React principaux.

---

## Références internes

- `ARCHITECTURE.md` — architecture cible microservices
- `TODO_Gindho_Improvements.md` — todo sécurité & refacto (Phases 1-4)
- `TODO_UI_Dashboard_Cohérence.md` — cohérence dashboards
- `TODO_UI_UX_Improvements.md` — backlog UX
- `Document_Conception/GinDHO_UI_SaaS_Redesign_Plan.md` — redesign UI
- `Document_Conception/Gindho_ALL_Dashbord_Role.md` — dashboards par rôle
