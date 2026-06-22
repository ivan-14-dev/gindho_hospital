# Architecture Frontend GinDHO

## Vue d'ensemble

Architecture enterprise React 19 avec TypeScript, suivant les principes Clean Architecture et SOLID.

## Principes Architecturaux

### 1. Séparation des Concerns
- **UI Layer** : Composants présentatifs uniquement
- **Business Logic Layer** : Hooks et services
- **Data Layer** : API client et cache

### 2. Modularité
- Feature-based structure
- Composants réutilisables dans `/components/ui`
- Pages isolées par module métier

### 3. State Management
- **TanStack Query** : Server state (cache, revalidation, mutations)
- **React Context** : Auth state global
- **Local State** : useState pour état local UI

### 4. Routing
- React Router v6 avec routes protégées
- RBAC intégré dans les routes
- Lazy loading des pages

## Structure des Dossiers

```
src/
├── components/
│   ├── ui/                      # Design System (ShadCN/UI)
│   │   ├── button.tsx
│   │   ├── card.tsx
│   │   ├── input.tsx
│   │   ├── label.tsx
│   │   ├── badge.tsx
│   │   ├── form.tsx
│   │   ├── alert.tsx
│   │   └── avatar.tsx
│   └── layout/                  # Layout components
│       ├── sidebar.tsx          # Navigation dynamique par rôle
│       ├── header.tsx           # Header avec profil utilisateur
│       └── layout.tsx           # Layout principal
├── pages/                       # Pages par module métier
│   ├── auth/                    # Authentification
│   ├── dashboard/               # Dashboard principal
│   ├── patients/                # Module patients
│   ├── appointments/            # Rendez-vous
│   ├── medical-records/         # Dossier médical
│   ├── lab/                     # Laboratoire
│   ├── imaging/                 # Imagerie
│   ├── medications/             # Pharmacie
│   ├── wellness/                # Bien-être
│   ├── emergency/               # Urgences
│   ├── chat/                    # Messagerie
│   ├── facilities/              # Établissements
│   ├── payments/                # Facturation
│   ├── notifications/           # Notifications
│   ├── public-health/           # Santé publique
│   └── profile/                 # Profil
├── services/                    # API Services
│   ├── auth.service.ts          # Authentification
│   └── patient.service.ts       # Patients
├── hooks/                       # Custom Hooks
│   └── use-auth.ts              # Auth hooks (login, logout, permissions)
├── contexts/                    # React Contexts
│   └── auth-context.tsx         # Auth context global
├── lib/                         # Utilitaires
│   ├── utils.ts                 # cn() helper
│   ├── api-client.ts            # Client HTTP générique
│   ├── query-client.ts          # TanStack Query config
│   └── validations.ts           # Schémas Zod
└── types/                       # Types TypeScript
    └── index.ts                 # Interfaces métier
```

## Flux de Données

### Authentification
```
Login Page → useLogin() → authService.login() → apiClient.post()
    ↓
Stockage token dans localStorage
    ↓
AuthProvider détecte le token
    ↓
useCurrentUser() + usePermissions()
    ↓
Mise à jour du contexte Auth
    ↓
Accès aux routes protégées
```

### Récupération de Données
```
Page Component
    ↓
useQuery({ queryKey, queryFn })
    ↓
TanStack Query vérifie le cache
    ↓
Si pas en cache → service.getPatients()
    ↓
apiClient.get() avec token JWT
    ↓
Backend GinDHO (Spring Boot)
    ↓
Réponse JSON → mise en cache
    ↓
Composant re-render avec données
```

### Mutations
```
Formulaire
    ↓
useMutation({ mutationFn, onSuccess })
    ↓
service.createPatient(data)
    ↓
apiClient.post()
    ↓
onSuccess → queryClient.invalidateQueries()
    ↓
Cache invalidé → refetch automatique
    ↓
UI se met à jour
```

## Sécurité

### RBAC (Role-Based Access Control)

**Rôles** :
- ADMIN, SUPER_ADMIN, MEDECIN, NURSE, RECEPTION
- PHARMACIST, LABORATORY, ACCOUNTING, URGENCY
- HOSPITALIZATION_SERVICE, PATIENT, UTILISATEUR_SECONDAIRE

**Permissions** :
- Format : `RESOURCE:ACTION`
- Exemples : `patients:READ`, `appointments:WRITE`
- Validité temporelle : `valid_from` / `valid_to`

**Implémentation** :
```typescript
// Hook de vérification
hasPermission(permissions, 'patients:READ')

// Protection de route
<PrivateRoute allowedRoles={['ADMIN', 'MEDECIN']}>
  <PatientsPage />
</PrivateRoute>

// Masquage d'éléments
{hasPermission(permissions, 'patients:WRITE') && (
  <Button>Nouveau patient</Button>
)}
```

### JWT Token
- Stockage : localStorage
- Header : `Authorization: Bearer <token>`
- Refresh automatique (à implémenter avec Keycloak)
- Validation côté backend

## Performance

### Optimisations Implémentées
- **Code Splitting** : Lazy loading des routes
- **Memoization** : React.memo pour composants coûteux
- **Cache** : TanStack Query avec staleTime 5min
- **Images** : Optimisation à venir (next/image)
- **Bundle** : Tree-shaking automatique Vite

### À Venir
- Service Worker pour offline mode
- Virtual scrolling pour longues listes
- Image optimization avec Sharp
- Prefetching des routes critiques

## Tests

### Stratégie
- **Unit Tests** : Vitest + Testing Library
- **Integration Tests** : API mocking avec MSW
- **E2E Tests** : Playwright
- **Accessibility** : axe-core

### Coverage Cible
- Unitaires : > 80%
- Intégration : > 70%
- E2E : Critical paths

## CI/CD

### Pipeline
```
1. Lint (ESLint)
2. Type Check (tsc)
3. Tests (Vitest)
4. Build (Vite)
5. Deploy (Kubernetes)
```

### Quality Gates
- Pas d'erreur de lint
- Pas d'erreur de type
- Tests passent
- Build réussit
- Bundle size < 300KB

## Monitoring

### Frontend
- **Sentry** : Error tracking
- **Google Analytics** : User analytics
- **Web Vitals** : Performance metrics

### Backend
- **Prometheus** : Metrics
- **Grafana** : Dashboards
- **Jaeger** : Distributed tracing
- **Loki** : Logs centralisés

## Déploiement

### Environnements
- **Development** : localhost:5173
- **Staging** : staging.gindho.com
- **Production** : app.gindho.com

### Infrastructure
- **Kubernetes** : Orchestration
- **Istio** : Service mesh
- **Kong** : API Gateway
- **CDN** : Assets statiques

## Évolutions Futures

### Phase 2
- [ ] Intégration Keycloak OIDC complète
- [ ] WebSocket pour temps réel
- [ ] PWA avec Service Worker
- [ ] Internationalisation (i18n)

### Phase 3
- [ ] Module IA médicale
- [ ] Analytics avancés
- [ ] Mobile app (React Native)
- [ ] Desktop app (Electron)

## Standards de Code

### TypeScript
- Strict mode activé
- Pas de `any` (utiliser `unknown`)
- Interfaces préférées aux types
- Pas de enum (utiliser des unions)

### React
- Functional components uniquement
- Hooks pour la logique
- Props interfaces explicites
- Pas de default exports

### Naming
- Components : PascalCase
- Hooks : camelCase avec préfixe `use`
- Utils : camelCase
- Types : PascalCase
- Constants : UPPER_SNAKE_CASE

### Imports
- Alias `@/` pour src/
- Groupés : React, libs, components, etc.
- Pas d'imports relatifs complexes

## Documentation

### Par Composant
```tsx
/**
 * Bouton avec variantes ShadCN/UI
 * @example
 * <Button variant="default">Click me</Button>
 * <Button variant="destructive">Delete</Button>
 */
export function Button({ ... }) {
  // ...
}
```

### Par Service
```typescript
/**
 * Service d'authentification
 * Gère login, register, logout, reset password
 */
export const authService = {
  /**
   * Connexion utilisateur
   * @param email - Email de l'utilisateur
   * @param password - Mot de passe
   * @returns Token JWT + données utilisateur
   */
  async login(email: string, password: string): Promise<AuthResponse>
}
```

---

**Maintenu par** : Senior Frontend Architect  
**Dernière revue** : 2026-06-18  
**Version** : 1.0.0