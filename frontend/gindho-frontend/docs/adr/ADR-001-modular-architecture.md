# 001 - Architecture Frontend Modulaire

## Statut
Accepté

## Contexte
Le frontend GinDHO doit être modulaire, scalable et maintenable pour supporter une application hospitalière complexe avec de multiples rôles utilisateurs.

## Décision
Adopter une architecture en couches :
- `app/` - Configuration et providers globaux
- `shared/` - Composants et hooks réutilisables
- `entities/` - Types et logiques métier
- `features/` - Modules fonctionnels
- `services/` - APIs et accès données
- `pages/` - Routes et vues

## Alternatives Considérées
- Next.js App Router : Rejeté, trop verrouillé
- Redux : Rejeté, trop lourd pour nos besoins
- Module Federation : Reporté, complexité non justifiée

## Conséquences
- Structure claire et maintenable
- Tests plus faciles
- Onboarding développeur simplifié