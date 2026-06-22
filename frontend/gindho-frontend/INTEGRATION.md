# Intégration Frontend - Microservices Kubernetes

## Architecture de Communication

### Point d'Entrée Unique : Kong API Gateway

Le frontend communique **exclusivement** via Kong API Gateway qui route vers les microservices :

```
Frontend (React)
    ↓
Kong API Gateway (http://localhost:8080)
    ↓
    ├── patient-service.patient.svc.cluster.local
    ├── appointment-service.appointment.svc.cluster.local
    ├── medical-record-service.emr.svc.cluster.local
    ├── laboratory-service.laboratory.svc.cluster.local
    ├── pharmacy-service.pharmacy.svc.cluster.local
    ├── billing-service.billing.svc.cluster.local
    ├── identity-service.security.svc.cluster.local
    └── ...
```

### Configuration

**Fichier** : `.env.example`

```env
# Kong API Gateway
VITE_GATEWAY_URL=http://localhost:8080

# Keycloak
VITE_KEYCLOAK_URL=http://localhost:8081
VITE_KEYCLOAK_REALM=gindho
VITE_KEYCLOAK_CLIENT_ID=gindho-frontend
```

### Services API Disponibles

| Service | Endpoint | Description |
|---------|----------|-------------|
| **Auth** | `/api/auth/*` | Login, register, permissions |
| **Patients** | `/api/patients/*` | CRUD patients |
| **Appointments** | `/api/appointments/*` | Gestion rendez-vous |
| **Medical Records** | `/api/*` | Dossier médical |
| **Laboratory** | `/api/*` | Analyses médicales |
| **Pharmacy** | `/api/*` | Prescriptions & médicaments |
| **Billing** | `/api/*` | Facturation |
| **Payments** | `/api/payments/*` | Paiements |
| **Notifications** | `/api/notifications/*` | Notifications |
| **Emergency** | `/api/*` | Urgences |
| **HR** | `/api/hr/*` | Ressources humaines |

## Fonctionnalités Implémentées

### ✅ Phase 1 : Fondations (COMPLÉTÉE)

- [x] React 19 + TypeScript + Vite
- [x] Tailwind CSS 4.x + ShadCN/UI
- [x] TanStack Query v5 (cache, revalidation)
- [x] React Router v6 (routes protégées)
- [x] Zod + React Hook Form (validation)
- [x] Architecture modulaire feature-based

### ✅ Phase 2 : Authentification (COMPLÉTÉE)

- [x] Login / Register / Logout
- [x] JWT token management
- [x] AuthContext global
- [x] Routes protégées (PrivateRoute/PublicRoute)
- [x] RBAC avec PermissionGuard component

### ✅ Phase 3 : Navigation (COMPLÉTÉE)

- [x] Sidebar dynamique par rôle
- [x] Header avec profil utilisateur
- [x] Layout responsive
- [x] Menu items filtrés par permissions

### ✅ Phase 4 : Dashboard (COMPLÉTÉE)

- [x] Dashboard principal avec KPIs
- [x] Cartes statistiques
- [x] Intégration TanStack Query
- [x] Design responsive

### ✅ Phase 5 : Module Patients (COMPLÉTÉE)

- [x] Liste patients avec recherche
- [x] Pagination
- [x] Hooks personnalisés (usePatients, useCreatePatient, etc.)
- [x] Service API pour microservices
- [x] Badges statut patient
- [x] Actions rapides (voir, éditer, supprimer)

## Structure du Projet

```
gindho-frontend/
├── src/
│   ├── components/
│   │   ├── ui/                    # Design System (ShadCN/UI)
│   │   │   ├── button.tsx
│   │   │   ├── card.tsx
│   │   │   ├── input.tsx
│   │   │   ├── label.tsx
│   │   │   ├── badge.tsx
│   │   │   ├── form.tsx
│   │   │   ├── alert.tsx
│   │   │   └── avatar.tsx
│   │   ├── layout/                # Layout components
│   │   │   ├── sidebar.tsx        # Navigation dynamique
│   │   │   ├── header.tsx         # Header avec profil
│   │   │   └── layout.tsx         # Layout principal
│   │   └── auth/                  # Auth components
│   │       └── permission-guard.tsx  # RBAC guard
│   ├── pages/
│   │   ├── auth/                  # Login/Register
│   │   ├── dashboard/             # Dashboard
│   │   ├── patients/              # Module Patients ✅
│   │   └── ...                    # Autres modules
│   ├── services/
│   │   ├── api.service.ts         # API unifiée microservices
│   │   └── auth.service.ts        # Auth service
│   ├── hooks/
│   │   ├── use-auth.ts            # Auth hooks
│   │   └── use-patients.ts        # Patients hooks ✅
│   ├── contexts/
│   │   ├── auth-context.tsx       # Auth provider
│   │   └── auth-context-core.tsx  # Context core
│   ├── lib/
│   │   ├── utils.ts               # cn() helper
│   │   ├── api-client.ts          # HTTP client avec retry
│   │   ├── config.ts              # Configuration microservices
│   │   └── validations.ts         # Schémas Zod
│   └── types/
│       └── index.ts               # TypeScript interfaces
├── .env.example                   # Configuration environnement
├── ARCHITECTURE.md                # Documentation architecture
├── README.md                      # Guide utilisateur
└── package.json
```

## Communication avec les Microservices

### API Client

**Fichier** : `src/lib/api-client.ts`

```typescript
// Caractéristiques :
- Timeout: 30s
- Retry automatique: 3 tentatives
- JWT token automatique dans les headers
- Gestion d'erreurs centralisée
- Support GET/POST/PUT/PATCH/DELETE
```

### Configuration des Services

**Fichier** : `src/lib/config.ts`

```typescript
API_CONFIG = {
  GATEWAY_URL: 'http://localhost:8080',  // Kong Gateway
  SERVICES: {
    AUTH: '/api/auth',
    PATIENTS: '/api/patients',
    APPOINTMENTS: '/api/appointments',
    // ... autres services
  }
}
```

### Exemple d'Utilisation

```typescript
// Hook avec TanStack Query
export function usePatients(params?: { search?: string; page?: number }) {
  return useQuery({
    queryKey: ['patients', params],
    queryFn: () => patientsApi.getPatients(params),
  });
}

// Composant
function PatientsPage() {
  const { data, isLoading } = usePatients({ search: 'John' });
  
  // Utilisation des données...
}
```

## RBAC (Role-Based Access Control)

### Rôles Supportés

```typescript
- ADMIN
- SUPER_ADMIN
- MEDECIN
- NURSE
- RECEPTION
- PHARMACIST
- LABORATORY
- ACCOUNTING
- URGENCY
- HOSPITALIZATION_SERVICE
- PATIENT
- UTILISATEUR_SECONDAIRE
```

### PermissionGuard Component

```tsx
<PermissionGuard roles={['ADMIN', 'MEDECIN']} permission="patients:WRITE">
  <Button>Nouveau patient</Button>
</PermissionGuard>
```

## Prochaines Étapes

### Phase 5 : Modules Métier (EN COURS)

- [ ] Module Rendez-vous
- [ ] Module Dossier Médical
- [ ] Module Laboratoire
- [ ] Module Pharmacie
- [ ] Module Facturation

### Phase 6 : IA (À VENIR)

- [ ] Assistant IA médical
- [ ] Analyse prédictive

### Phase 7 : Optimisation (À VENIR)

- [ ] Performance & lazy loading
- [ ] Tests automatisés
- [ ] CI/CD pipeline

### Phase 8 : Production (À VENIR)

- [ ] Monitoring & logging
- [ ] Déploiement Kubernetes
- [ ] Documentation finale

## Build & Déploiement

### Build Réussi

```bash
✓ TypeScript compilation
✓ Vite build
✓ Bundle: 303 KB (94 KB gzipped)
✓ CSS: 25 KB (5 KB gzipped)
```

### Commandes Disponibles

```bash
# Développement
npm run dev

# Build production
npm run build

# Preview
npm run preview

# Lint
npm run lint

# Type check
npm run typecheck
```

## Points d'Attention

### Sécurité

- ✅ JWT tokens dans localStorage
- ✅ RBAC implémenté
- ✅ Routes protégées
- ✅ Permissions vérifiées côté frontend
- ⚠️ À venir : Refresh token automatique
- ⚠️ À venir : Keycloak OIDC complet

### Performance

- ✅ TanStack Query cache (5min stale time)
- ✅ Code splitting (Vite automatique)
- ✅ Lazy loading des routes
- ⚠️ À venir : Virtual scrolling
- ⚠️ À venir : Image optimization

### Tests

- ⚠️ À venir : Unit tests (Vitest)
- ⚠️ À venir : Integration tests (MSW)
- ⚠️ À venir : E2E tests (Playwright)
- ⚠️ À venir : Accessibility (axe-core)

## Support

Pour toute question sur l'intégration :

1. Consulter `ARCHITECTURE.md` pour l'architecture détaillée
2. Consulter `README.md` pour le guide utilisateur
3. Vérifier les microservices Kubernetes dans `GinDHO_Hospital/k8s/`

---

**Statut** : ✅ Frontend opérationnel connecté aux microservices Kubernetes  
**Build** : ✅ Réussi  
**Prêt pour** : Développement des modules métier restants