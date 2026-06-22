# ADR 0003 — Unification RBAC : Keycloak comme unique IdP et PDP

**Date** : 2026-06-09  
**Statut** : Accepté  
**Décideurs** : Architecture Team, Security Team  

## Contexte

Le RBAC actuel est hybride et fragmenté :
- **Monolithe** (`backend/`) : JWT maison avec `CustomUserDetailsService` + `RolePermission` temporelles
- **Microservices** : Keycloak OIDC déjà partiellement implémenté dans `identity-service`
- **Permissions** : format hybride (`RESOURCE:ACTION` et `RESOURCE_ACTION` coexistants)
- **PDP** : pas de point de décision centralisé — chaque service implémente sa propre logique

## Décision

1. **Un seul IdP** : Keycloak est l'unique fournisseur d'identité. Le JWT maison est supprimé après migration.
2. **PDP unifié** : `authorization-service` devient le Policy Decision Point unique, avec cache Redis (TTL 5 min).
3. **Permissions standardisées** : format unique `{domaine}:{ressource}:{action}` (ex: `patient:profile:read`).
4. **Rôles Keycloak** :
   - `ADMIN` → accès total
   - `MEDECIN` → patient:*, appointment:*, prescription:write
   - `PHARMACIEN` → prescription:read, medication:*
   - `PATIENT` → patient:self:*, appointment:self:*
5. **MFA** : obligatoire pour ADMIN, MEDECIN, PHARMACIEN (via Keycloak OTP).
6. **Cache** : Redis avec TTL 5 min pour les décisions PDP.

## Plan de migration

1. Définir tous les rôles et permissions dans Keycloak (realm-export.json)
2. Implémenter `authorization-service` avec interface REST `POST /api/authorize`
3. Ajouter un filtre Spring Security dans chaque service qui appelle le PDP
4. Déprécier le JWT maison : double support pendant 2 semaines
5. Supprimer `CustomUserDetailsService` et le JWT maison

## Conséquences

- **Positives** : RBAC cohérent, audit centralisé, MFA possible, suppression de code mort
- **Négatives** : effort de migration, latence PDP (compensée par cache Redis)
- **Risques** : outage si le PDP est down (cache + fallback à la décision locale)

## Références

- PLAN_AMELIORATION_SYSTEME.md §1.2
- ADR-0002 (Zero-Trust Security)
- backend/src/main/java/com/gindho/security/CustomUserDetailsService.java
- services/identity-service/
