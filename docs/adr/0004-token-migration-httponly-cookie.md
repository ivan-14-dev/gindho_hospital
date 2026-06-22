# ADR 0004 — Migration Token : localStorage → HttpOnly Cookie

**Date** : 2026-06-09  
**Statut** : Accepté  
**Décideurs** : Security Team, Frontend Team  

## Contexte

Actuellement, le token JWT est stocké dans `localStorage` côté React :
- `react-web/src/services/apiClient.ts` ligne 5 : `localStorage.getItem('auth_token')`
- `react-web/src/hooks/useAuth.ts` lignes 12, 49, 66 : `localStorage.setItem/removeItem('auth_token')`

Un token dans `localStorage` est vulnérable au XSS : n'importe quel script injecté peut le voler.

## Décision

Migrer vers un **cookie HttpOnly + SameSite=Strict** :

1. Le backend (`identity-service` ou Kong) émet un cookie `Set-Cookie: gindho_token=<JWT>; HttpOnly; Secure; SameSite=Strict; Path=/; Max-Age=86400`
2. Le frontend React n'a plus accès au token via JavaScript : plus de `localStorage` pour l'authentification
3. Le token est envoyé automatiquement par le navigateur sur chaque requête
4. `apiClient.ts` et `useAuth.ts` sont simplifiés : suppression de la gestion manuelle du token

## Étapes

1. **Backend** : Ajouter un filtre dans Kong ou le service d'identité pour émettre le cookie lors du login
2. **React** : 
   - Supprimer `localStorage.getItem('auth_token')` dans `apiClient.ts`
   - Modifier `useAuth.ts` pour ne plus gérer le token manuellement
   - Le token est envoyé automatiquement via le cookie
3. **CSRF** : Ajouter un token CSRF (double submit cookie pattern) pour les mutations (POST/PUT/DELETE)
4. **Transition** : Période de double support (2 semaines) : le backend accepte à la fois le header `Authorization` et le cookie

## Conséquences

- **Positives** : Plus vulnérable au XSS, meilleure sécurité, simplification du frontend
- **Négatives** : Impossible de lire le token côté JS (pas de décodage JWT pour les permissions côté client)
- **Risques** : CSRF (mitigé par SameSite=Strict + token CSRF pour les mutations)

## Références

- PLAN_AMELIORATION_SYSTEME.md §1.5
- ADR-0002 (Zero-Trust Security)
- OWASP: https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html
