# GinDHO - Documentation projet et architecture

Date de mise a jour: 2026-06-14

## Vue d'ensemble

GinDHO est une plateforme hospitaliere construite autour d'un monorepo Java/Spring Boot. Le depot contient:

- un ancien backend monolithique dans `backend/`;
- 29 microservices Spring Boot dans `services/`;
- un module partage `services/common`;
- un generateur/support technique `services/generator-core`;
- un client desktop JavaFX dans `javafx-client/`;
- un frontend web React dans `react-web/`;
- l'infrastructure locale Docker dans `docker/` et `infra/`;
- les manifests Kubernetes/Helm dans `k8s/`;
- la documentation fonctionnelle et technique dans `docs/`.

Le projet suit une migration progressive vers une architecture microservices: les domaines metier sont decoupes par service, chaque service possede son propre artefact Docker, et la communication passe par REST et Kafka.

## Architecture cible

```text
Utilisateurs
  |
  | Web / desktop / API
  v
Kong ou api-gateway
  |
  +-- identity-service / Keycloak
  +-- authorization-service
  +-- services cliniques
  +-- services financiers
  +-- services logistiques
  +-- services RH et reporting
       |
       +-- PostgreSQL par service
       +-- Kafka pour les evenements
       +-- Redis pour cache/session si active
       +-- MongoDB pour usages documentaires si active

Observabilite: Prometheus, Grafana, Jaeger, Loki
Deploiement: Docker Compose en local, Kubernetes/Helm en production
```

## Composants principaux

| Zone | Chemin | Role |
|---|---|---|
| Parent Maven | `pom.xml` | Agrege les modules microservices, Java 21, Spring Boot 3.2.0 |
| Microservices | `services/*-service` | Services Spring Boot deployables en conteneur |
| Common | `services/common` | Types, reponses API et base technique partagee |
| Monolithe | `backend` | Backend historique Spring Boot |
| Web | `react-web` | Interface web React |
| Desktop | `javafx-client` | Interface desktop JavaFX |
| Docker local | `docker/docker-compose.yml` | PostgreSQL, Kafka, Keycloak, Kong, observabilite, backend |
| Kubernetes | `k8s` | Namespaces, manifests, Helm, guides de production |
| Documentation | `docs` | ADR, catalogue Kafka, KPI, architecture |

## Carte des microservices

Tous les services applicatifs utilisent Java 21, Spring Boot, PostgreSQL et exposent Actuator lorsque le demarrage reussit.

| Service | Domaine | Responsabilite principale | Base par defaut |
|---|---|---|---|
| `api-gateway` | Edge/API | Point d'entree Spring pour routage applicatif | `api_gateway` |
| `identity-service` | Securite | Authentification, proxy Keycloak, token login/refresh | `identity_service` |
| `authorization-service` | Securite | RBAC, roles, permissions dynamiques | `authorization_service` |
| `audit-service` | Securite | Journal d'audit et tracabilite | `audit_service` |
| `patient-service` | Patient | Patients, contacts, assurances, documents | `patient_service` |
| `appointment-service` | Rendez-vous | Rendez-vous, disponibilites, liste d'attente | `appointment_service` |
| `admission-service` | Hospitalisation | Admissions et sorties | `admission_service` |
| `emergency-service` | Urgence | Triage et prise en charge urgente | `emergency_service` |
| `ward-service` | Hospitalisation | Services/unites hospitalieres | `ward_service` |
| `bed-service` | Hospitalisation | Lits, affectation, liberation | `bed_service` |
| `round-service` | Clinique | Rondes medicales et checklists | `round_service` |
| `surgery-service` | Clinique | Chirurgie et bloc operatoire | `surgery_service` |
| `medical-record-service` | Dossier medical | Dossiers, consultations, historique medical | `medical_record_service` |
| `prescription-service` | Pharmacie | Ordonnances | `prescription_service` |
| `pharmacy-service` | Pharmacie | Medicaments, lots, prescriptions pharmacie | `pharmacy_service` |
| `laboratory-service` | Plateau technique | Analyses et resultats labo | `laboratory_service` |
| `imaging-service` | Plateau technique | Imagerie medicale | `imaging_service` |
| `billing-service` | Finance | Factures et paiements associes | `billing_service` |
| `insurance-service` | Finance | Assurances et remboursements | `insurance_service` |
| `payment-service` | Finance | Transactions et remboursements | `payment_service` |
| `inventory-service` | Logistique | Produits, mouvements, alertes de stock | `inventory_service` |
| `procurement-service` | Logistique | Fournisseurs, commandes, receptions | `procurement_service` |
| `asset-service` | Logistique | Equipements et maintenance | `asset_service` |
| `ambulance-service` | Transport | Ambulances et missions | `ambulance_service` |
| `hr-service` | RH | Employes, medecins, contrats | `hr_service` |
| `scheduling-service` | RH | Gardes, absences, planning | `scheduling_service` |
| `event-service` | Evenements | Evenements hospitaliers | `event_service` |
| `notification-service` | Communication | Notifications email/SMS/push | `notification_service` |
| `reporting-service` | Pilotage | Rapports et statistiques | `reporting_service` |

## Donnees et integration

Le principe d'architecture est "une base logique par service". Le script `docker/postgres-init/init-dbs.sh` cree les bases principales pour les microservices. Les services se connectent via:

- `SPRING_DATASOURCE_URL`;
- `DB_USERNAME`;
- `DB_PASSWORD`;
- `SPRING_JPA_HIBERNATE_DDL_AUTO`.

Kafka est utilise pour publier et consommer les evenements inter-services. Les topics documentes dans `docs/kafka-event-catalog.md` couvrent notamment les patients, rendez-vous, factures, notifications, audit, medical, stock et urgences.

## Securite

La securite est repartie en plusieurs couches:

- `identity-service` pour l'authentification et l'integration Keycloak;
- `authorization-service` pour les roles et permissions;
- JWT dans les services Spring;
- CORS configure dans les applications;
- secrets fournis par variables d'environnement ou Kubernetes Secrets;
- audit applicatif via `audit-service`.

Les ADR de securite sont dans `docs/adr/`, notamment:

- `0002-zero-trust-security-architecture.md`;
- `0003-rbac-unification-keycloak.md`;
- `0004-token-migration-httponly-cookie.md`.

## Observabilite

L'infrastructure Docker declare:

- Prometheus pour les metriques;
- Grafana pour les dashboards;
- Jaeger pour le tracing;
- Loki pour les logs;
- endpoints Spring Actuator `health`, `info` et `prometheus` sur les services.

## Deploiement

### Local

L'infrastructure locale est decrite dans `docker/docker-compose.yml`.

```bash
docker compose -f docker/docker-compose.yml up -d postgres kafka keycloak kong prometheus grafana jaeger loki
```

Les images de services locales suivent la convention:

```text
gindho/<service>:latest
```

### Kubernetes

Le dossier `k8s/` contient les manifests, overlays, guides et charts Helm. Les guides principaux sont:

- `k8s/INSTALL-RAPIDE.md`;
- `k8s/ARCHITECTURE.md`;
- `k8s/PRODUCTION-GUIDE.md`;
- `README-K8S-ARCHITECTURE.md`.

## Tests conteneurs du 2026-06-14

Une passe de smoke tests Docker a ete executee service par service. Le protocole:

1. Creation d'un reseau Docker temporaire `gindho-smoke-net`.
2. Demarrage de PostgreSQL, ZooKeeper et Kafka.
3. Creation/utilisation des bases par service.
4. Demarrage d'un seul microservice applicatif a la fois.
5. Verification `GET /actuator/health` sur `127.0.0.1:18080`.
6. Sauvegarde des logs puis suppression du conteneur avant le service suivant.

Rapport complet: `test-results/container-smoke-2026-06-14/CONTAINER-SMOKE-TESTS.md`

Resultat global:

| Statut | Nombre |
|---|---:|
| Services testes | 29 |
| Reussites | 26 |
| Echecs | 3 |

Services en echec:

| Service | Cause observee |
|---|---|
| `authorization-service` | YAML invalide: cle `logging.file` dupliquee dans `services/authorization-service/src/main/resources/application.yml` |
| `identity-service` | Placeholder non resolu pour `keycloak.client-secret` au demarrage du bean `KeycloakService` |
| `patient-service` | Repository Spring Data invalide: la requete derivee cherche `nom`/`prenom`, alors que l'image testee ne les expose pas comme proprietes reconnues |

Point d'attention: le workspace contient des modifications non commitees sur `patient-service` et `identity-service`. Les tests Docker ci-dessus valident les images locales `gindho/*:latest`; si le code source a change sans rebuild, il faut reconstruire les images avant de conclure sur l'etat du code courant.

## Commandes utiles

Compiler tous les modules Maven:

```bash
mvn clean test
```

Construire un service:

```bash
mvn -pl services/patient-service -am clean package
docker build -t gindho/patient-service:latest services/patient-service
```

Lancer un service avec PostgreSQL/Kafka Docker:

```bash
docker run --rm --network gindho_default \
  -e SERVER_PORT=8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/patient_service \
  -e DB_USERNAME=gindho \
  -e DB_PASSWORD=gindho123 \
  -e KAFKA_BOOTSTRAP_SERVERS=kafka:29092 \
  -p 8080:8080 \
  gindho/patient-service:latest
```

Verifier la sante:

```bash
curl http://localhost:8080/actuator/health
```

## Recommandations

1. Corriger les trois services en echec, puis reconstruire leurs images.
2. Ajouter `api_gateway` au script d'initialisation PostgreSQL si `api-gateway` conserve sa base dediee.
3. Versionner un script de smoke test Docker sequentiel pour reproduire exactement cette passe.
4. Ajouter une verification CI qui lance au minimum `mvn test` et un smoke test par image.
5. Harmoniser les ports entre la documentation historique, Docker Compose et les manifests Kubernetes.

## Backlog de migration

La liste detaillee des fonctionnalites encore presentes dans le monolithe et manquantes ou partielles dans les microservices est maintenue dans `docs/MICROSERVICES-IMPLEMENTATION-GAPS.md`.
