# App Layer

Responsabilite:

- Bootstrap React.
- Providers globaux.
- Router applicatif.
- Error boundary globale.
- Configuration d'initialisation.

Regles:

- Peut composer `shared`, `entities`, `features`, `services` et `pages`.
- Ne contient pas de logique metier hospitaliere.
- Ne consomme pas directement `fetch`.
- Ne depend pas du backend monolithique.
