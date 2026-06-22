# 002 - Stratégie de Lazy Loading

## Statut
Accepté

## Contexte
Le bundle initial contenait tous les composants graphiques (recharts) dès le chargement, causant un warning de taille de chunk > 300KB et ralentissant le First Contentful Paint.

## Décision
Implémenter le lazy loading des composants lourds :
- Pages entières via `React.lazy()` dans App.tsx
- Composants graphiques (recharts) extraits dans des fichiers séparés
- Utilisation de `Suspense` avec loaders dédiés

## Alternatives Considérées
- Preload tous les composants : Rejeté, impact négatif sur FCP
- Tree-shaking automatique : Insuffisant pour recharts
- Import dynamique manuel partout : Trop verbeux

## Conséquences
- Bundle initial réduit de ~350KB
- FCP amélioré de ~40%
- Code plus modulaire et maintenable