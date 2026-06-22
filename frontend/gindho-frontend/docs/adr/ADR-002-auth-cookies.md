# 002 - Authentification par Cookies HttpOnly

## Statut
Accepté

## Contexte
Sécuriser l'authentification frontend contre les attaques XSS et CSRF dans un contexte hospitalier.

## Décision
Utiliser des cookies HttpOnly avec SameSite=Strict pour les tokens JWT :
- Access token valide 15 minutes
- Refresh token valide 7 jours
- Rotation des refresh tokens

## Alternatives Considérées
- localStorage : Rejeté, vulnérable XSS
- sessionStorage : Rejeté, vulnérable XSS
- JWT en headers : Rejeté, nécessite gestion manuelle CSRF

## Conséquences
- Meilleure sécurité
- Compatible avec `identity-service`
- Nécessite configuration CORS côté API