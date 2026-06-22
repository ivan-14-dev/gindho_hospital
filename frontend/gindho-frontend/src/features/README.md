# Features Layer

Responsabilite:

- Workflows utilisateur.
- Composants de cas d'usage.
- Formulaires metier.
- Orchestration entre entities et services.

Regles:

- Une feature correspond a un besoin utilisateur mesurable.
- Les permissions requises doivent etre explicites.
- Une feature ne doit pas contourner les guards RBAC.
- Les appels API passent par `src/services`.
