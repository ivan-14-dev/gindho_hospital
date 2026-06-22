# Guide de Développement GinDHO Frontend

## Setup Environnement

```bash
# Installation
npm install

# Développement
npm run dev

# Tests
npm run test

# Build production
npm run build
```

## Architecture

```
src/
├── components/     # UI réutilisable
├── hooks/       # Custom hooks React Query
├── pages/       # Routes/principes vues
├── services/    # API clients
└── lib/         # Utils et configuration
```

## Conventions

- Composants: PascalCase
- Fichiers: kebab-case
- Hooks: use- prefix
- Types: PascalCase avec préfixe (ex: PatientFormValues)

## Tests

- Vitest + React Testing Library
- Tests dans `src/test/`
- E2E avec Playwright dans `src/test/e2e/`

## Build & Deploy

```bash
# Lint + Typecheck + Test
npm run lint && npm run typecheck && npm run test

# Build
npm run build

# Docker
docker build -t gindho-frontend .
```