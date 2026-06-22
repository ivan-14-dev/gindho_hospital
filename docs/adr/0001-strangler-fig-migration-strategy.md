# ADR 0001 — Stratégie de Migration Strangler Fig

**Date** : 2026-06-09  
**Statut** : Accepté  
**Décideurs** : Architecture Team  

## Contexte

Le projet GinDHO possède actuellement deux piles coexistantes :
- Un monolithe Spring Boot (`backend/`) exposant toutes les API
- Une architecture microservices (`services/`, 31 services) en parallèle

Cette dualité génère de la dette technique, du code mort, et des incohérences de schéma.

## Décision

Adopter le **Strangler Fig Pattern** pour migrer progressivement du monolithe vers les microservices :

1. **Gel** des nouveaux endpoints sur le monolithe (hors hotfix critiques)
2. **Router** progressivement le trafic via Kong API Gateway :
   - `/patient/*` → `patient-service`
   - `/appointment/*` → `appointment-service`
   - `/emr/*` → `medical-record-service`
   - `/billing/*` → `billing-service`
3. **Feature Flags** (Unleash/FF4J) pour activer/désactiver chaque migration
4. **Décommissionnement** endpoint par endpoint une fois la migration validée

## Conséquences

- **Positives** : migration sans interruption de service, rollback possible par feature flag
- **Négatives** : complexité opérationnelle temporaire (deux piles en parallèle)
- **Risques** : latence accrue pendant la phase de double-routage

## Références

- PLAN_AMELIORATION_SYSTEME.md §2.1
- ARCHITECTURE.md
- TODO_Gindho_Improvements.md
