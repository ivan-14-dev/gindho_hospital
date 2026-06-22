# GinDHO — Setup Production des Schémas PostgreSQL

## Objectif

Ce dossier contient les scripts SQL nécessaires à la création des schémas de base de données lors du premier déploiement en production.

- `hospital/db/sql/init-all-schemas.sql` : script maître pour toutes les bases microservices
- `admin-zone/db/sql/init-admin-zone.sql` : script dédié à la base `admin_zone`
- `hospital/docker/postgres-init/init-dbs.sh` : script d'initialisation PostgreSQL intégré au conteneur

## Principe

Chaque microservice possède sa propre base de données. Le script `init-dbs.sh` :

1. Crée toutes les bases de données
2. Exécute `init-all-schemas.sql` dans chaque base
3. Exécute `init-admin-zone.sql` dans `admin_zone`

Les sections SQL sont protégées par `current_database()` pour garantir qu'un script ne modifie que la base correspondante.

## Exécution manuelle

### 1. Création des bases

```bash
docker exec -it gindho-postgres bash
./docker-entrypoint-initdb.d/init-dbs.sh
```

### 2. Application des schémas

```bash
psql -U gindho -d gindho -f /path/to/hospital/db/sql/init-all-schemas.sql
psql -U gindho -d patient_service -f /path/to/hospital/db/sql/init-all-schemas.sql
psql -U gindho -d admin_zone -f /path/to/admin-zone/db/sql/init-admin-zone.sql
```

## Microservices couverts

Le script crée les schémas pour :

- `gindho` (backend-service + legacy backend)
- `patient_service`
- `appointment_service`
- `medical_record_service`
- `admission_service`
- `emergency_service`
- `ward_service`
- `bed_service`
- `round_service`
- `surgery_service`
- `prescription_service`
- `pharmacy_service`
- `laboratory_service`
- `billing_service`
- `insurance_service`
- `payment_service`
- `inventory_service`
- `procurement_service`
- `asset_service`
- `ambulance_service`
- `imaging_service`
- `event_service`
- `hr_service`
- `scheduling_service`
- `notification_service`
- `audit_service`
- `reporting_service`
- `interconnect_service`
- `authorization_service`
- `identity_service`
- `outgoing_service`

## Admin Zone

Les controllers Admin Zone (`gateway` et `web-api`) ne sont plus des stubs JDBC. Ils utilisent maintenant :

- Entités JPA dans `admin-zone/core`
- Repositories Spring Data JPA
- Services transactionnels avec vraie logique métier

Tables créées dans `admin_zone` :

- `az_plateformes`
- `az_incoming_requests`
- `az_outgoing_events`
- `az_patients`

## Notes importantes

- Les scripts sont idempotents (`CREATE TABLE IF NOT EXISTS`)
- Les données existantes ne sont jamais modifiées
- Les contraintes FK sont définies au niveau applicatif (Hibernate/JPA) pour préserver l'autonomie des microservices
- Pour `medical_record_service`, les documents médicaux restent stockés dans MongoDB ; PostgreSQL ne contient que les métadonnées et relations
