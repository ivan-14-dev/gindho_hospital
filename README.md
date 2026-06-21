# GinDHO Hospital

Microservices hospitaliers - 35 services indépendants.

## Services

- patient-service, appointment-service, billing-service, etc.
- Voir hospital/docker/docker-compose.yml pour la liste complète

## Base de données

31 bases PostgreSQL indépendantes + MongoDB pour dossiers médicaux.

## Schéma SQL

Le script d'initialisation est dans `db/sql/init-all-schemas.sql`.

## Démarrage

```bash
cd hospital
./run-all.sh
# ou
docker-compose -f docker/docker-compose.yml up -d
```
