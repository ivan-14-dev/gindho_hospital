# ADR 0002 — Architecture Sécurité Zero-Trust

**Date** : 2026-06-09  
**Statut** : Accepté  
**Décideurs** : Architecture Team, Security Team  

## Contexte

Le système expose actuellement :
- Des credentials en clair dans `application.properties` et `docker-compose.yml`
- Un JWT maison sans rotation automatique
- Un RBAC hybride non unifié entre monolithe et microservices
- `logging.level.org.springframework.security=DEBUG` en prod
- Aucun mTLS entre services

## Décision

Appliquer une architecture **Zero-Trust** :

1. **Secrets** : HashiCorp Vault (ou Sealed Secrets en attendant) — plus aucun secret en clair dans le repo
2. **Authentification unique** : Keycloak OIDC comme unique IdP, suppression du JWT maison
3. **MFA obligatoire** : rôles ADMIN, MEDECIN, PHARMACIEN
4. **mTLS inter-services** : Istio ou Linkerd pour chiffrer tout le trafic service→service
5. **PDP unifié** : `authorization-service` comme unique Policy Decision Point avec cache Redis
6. **Audit immuable** : `audit-service` → Kafka log-compacted → stockage WORM
7. **Transport** : TLS 1.3 minimum, HSTS, CSP stricte

## Conséquences

- **Positives** : suppression des failles critiques, traçabilité complète, conformité RGPD
- **Négatives** : complexité opérationnelle (Vault, Istio), effort de migration du JWT maison
- **Risques** : latence d'authentification accrue (compensée par cache Redis)

## Références

- PLAN_AMELIORATION_SYSTEME.md §1.1-1.5
- TODO_Gindho_Improvements.md Phase 1
- ARCHITECTURE.md §Sécurité
