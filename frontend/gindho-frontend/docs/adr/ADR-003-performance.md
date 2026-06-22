# 003 - Performance Frontend

## Statut
Accepté

## Contexte
Le frontend hospitalier doit être performant avec des listes de patients pouvant atteindre plusieurs milliers d'entrées.

## Décision
Implémenter :
- Virtual scrolling pour listes longues (>100 items)
- Lazy loading pour images et composants
- Bundle budgets avec `build.chunkSizeWarningLimit`
- Dynamic imports pour les routes

## Conséquences
- Meilleure UX sur mobile
- Chargement initial réduit
- Score Lighthouse >90