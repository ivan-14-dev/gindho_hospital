# ADR 0005 — Propriété des Schémas de Données

**Date** : 2026-06-09  
**Statut** : Accepté  
**Décideurs** : Architecture Team, DBA  

## Contexte

Plusieurs services partagent actuellement des bases de données ou lisent les tables d'autres services. Le monolithe `backend/` utilise `ddl-auto=update` en parallèle de Flyway, ce qui peut causer des incohérences de schéma.

## Décision

1. **Un schéma PostgreSQL = un service** : chaque microservice possède son propre schéma (ex: `patient_service`, `appointment_service`)
2. **Aucun service ne lit la DB d'un autre** : toute communication inter-service passe par API (REST) ou événement (Kafka)
3. **Flyway exclusif** : `ddl-auto=validate` partout, les migrations sont uniquement gérées par Flyway
4. **Base dédiée** : chaque service a sa propre base de données (ou schéma isolé dans une base partagée, avec des credentials différents)
5. **Convention de nommage** : `{domain}_service` pour le schéma (ex: `patient_service`, `appointment_service`, `billing_service`)
6. **Migration standardisée** : `V{YYYY}{MM}{DD}_{HH}{MM}__{description}.sql`

## Mapping Schéma ↔ Service

| Service | Schéma | Base |
|---|---|---|
| patient-service | `patient_service` | `gindho_patient` |
| appointment-service | `appointment_service` | `gindho_appointment` |
| medical-record-service | `emr_service` | `gindho_emr` |
| billing-service | `billing_service` | `gindho_billing` |
| admission-service | `admission_service` | `gindho_admission` |
| laboratory-service | `laboratory_service` | `gindho_laboratory` |
| prescription-service | `prescription_service` | `gindho_prescription` |
| pharmacy-service | `pharmacy_service` | `gindho_pharmacy` |
| notification-service | `notification_service` | `gindho_notification` |
| audit-service | `audit_service` | `gindho_audit` |
| identity-service | `identity_service` | `gindho_identity` |
| authorization-service | `authorization_service` | `gindho_authorization` |

## Plan de Migration

1. Créer les bases/schémas dédiés dans PostgreSQL
2. Pour chaque service : mettre à jour `spring.datasource.url` pour pointer vers son schéma
3. Copier les tables existantes depuis la base monolithe vers chaque schéma
4. Ajouter les migrations Flyway dans chaque service
5. Supprimer les accès croisés entre services
6. Valider avec `ddl-auto=validate`

## Conséquences

- **Positives** : isolation complète, pas de conflit de schéma, scalabilité horizontale possible
- **Négatives** : complexité opérationnelle, migrations synchronisées entre services
- **Risques** : données dupliquées entre services (résolu par event-driven eventual consistency)

## Références

- PLAN_AMELIORATION_SYSTEME.md §2.4
- ADR-0001 (Strangler Fig)
- Documentation Flyway : https://documentation.red-gate.com/flyway
