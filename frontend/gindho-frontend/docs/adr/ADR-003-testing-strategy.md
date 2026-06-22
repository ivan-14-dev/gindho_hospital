# 003 - Stratégie de Tests

## Statut
Accepté

## Contexte
Le projet nécessite une couverture de tests robuste pour garantir la qualité et la maintenabilité d'une application hospitalière critique.

## Décision
Adopter une stratégie de tests en pyramide :
- **Tests unitaires** (70%) : Composants UI, hooks, services
- **Tests d'intégration** (20%) : Pages complètes, flux métier
- **Tests E2E** (10%) : Playwright pour les parcours critiques

Outils choisis :
- **Vitest** : Runner de tests rapide avec coverage V8
- **Testing Library** : Tests orientés utilisateur
- **Playwright** : Tests E2E cross-browser
- **axe-core** : Tests d'accessibilité automatisés

## Alternatives Considérées
- Jest : Rejeté, Vitest est plus rapide et mieux intégré à Vite
- Cypress : Reporté pour E2E, Playwright plus moderne
- Testing Library vs Enzyme : Testing Library retenu pour approche utilisateur

## Conséquences
- Coverage cible : Pages 80%, Hooks 90%, Services 70%
- Tests rapides (< 30s pour la suite complète)
- Détection précoce des régressions
- Documentation vivante via les tests