# 004 - Stratégie de Tests

## Statut
Accepté

## Contexte
Le frontend doit avoir une couverture de tests robuste pour garantir la qualité dans un contexte médical.

## Décision
- Vitest comme framework de test
- Testing Library pour tests unitaires
- Playwright pour tests E2E
- Axe-core pour tests d'accessibilité
- Targets : pages 80%, hooks 90%, services 70%

## Conséquences
- Détection précoce des régressions
- Qualité maintenue pendant le développement
- Documentation vivante via tests