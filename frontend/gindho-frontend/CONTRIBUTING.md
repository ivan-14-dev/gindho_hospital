# Guide de Contribution - GinDHO Frontend

## 🎯 Philosophie du Projet

GinDHO est un système hospitalier critique. Chaque contribution doit respecter les standards médicaux et de sécurité les plus élevés.

## 📋 Workflow de Développement

### 1. Avant toute modification

```
ANALYSE → BACKLOG → ROADMAP → TODO LIST → CODE → TEST → VERIFY → DOCUMENT
```

**Règle absolue** : Ne jamais développer sans avoir :
- ✅ Analysé l'impact
- ✅ Créé la tâche dans le backlog
- ✅ Vérifié les dépendances
- ✅ Planifié les tests

### 2. Standards de Code

#### TypeScript
```typescript
// ✅ INTERDIT
const data: any = {};
function test(x) { }

// ✅ OBLIGATOIRE
interface PatientData {
  id: string;
  nom: string;
  prenom: string;
}

function test(x: string): void { }
```

#### React
```typescript
// ✅ INTERDIT
export default function App() {
  const [data, setData] = useState();
}

// ✅ OBLIGATOIRE
export function App() {
  const [data, setData] = useState<string | null>(null);
}
```

#### Nommage
- **Components** : PascalCase (`PatientCard.tsx`)
- **Hooks** : camelCase avec préfixe `use` (`useAuth.ts`)
- **Utils** : camelCase (`formatDate.ts`)
- **Constants** : UPPER_SNAKE_CASE (`API_ENDPOINTS`)
- **Types/Interfaces** : PascalCase (`Patient`, `Appointment`)

### 3. Architecture

```
src/
├── components/
│   ├── ui/              # ShadCN/UI (ne pas modifier directement)
│   ├── layout/          # Layout components
│   └── auth/            # Auth components
├── pages/
│   ├── auth/            # Login, Register
│   ├── dashboard/       # Dashboards
│   ├── patients/        # Module Patients
│   └── ...              # Autres modules
├── services/            # API services (1 par microservice)
├── hooks/               # Custom hooks
├── contexts/            # React contexts
├── lib/                 # Utilitaires
│   ├── utils.ts
│   ├── api-client.ts
│   ├── validations.ts
│   └── monitoring/
├── test/                # Tests
│   ├── components/
│   ├── pages/
│   ├── hooks/
│   └── services/
└── types/               # TypeScript types
```

### 4. Tests OBLIGATOIRES

#### Pour chaque fonctionnalité :

```typescript
// 1. Test unitaire
describe('ComponentName', () => {
  it('should render correctly', () => { });
  it('should handle user interaction', () => { });
  it('should display error state', () => { });
});

// 2. Test d'intégration
describe('Feature Integration', () => {
  it('should integrate with API', () => { });
  it('should handle loading states', () => { });
});

// 3. Test de sécurité
describe('Security', () => {
  it('should respect RBAC', () => { });
  it('should validate JWT', () => { });
});

// 4. Test d'accessibilité
describe('Accessibility', () => {
  it('should have proper ARIA labels', () => { });
  it('should support keyboard navigation', () => { });
});
```

#### Coverage minimum
- **Composants UI** : 100%
- **Hooks** : 90%
- **Pages** : 80%
- **Services** : 70%

### 5. Vérifications AVANT commit

```bash
# 1. Lint
npm run lint

# 2. TypeScript
npm run typecheck

# 3. Tests
npm run test:coverage

# 4. Build
npm run build

# 5. Security audit
npm audit

# TOUS DOIVENT PASSER ✅
```

### 6. Convention de Commits

```
type(scope): description

Types:
- feat: Nouvelle fonctionnalité
- fix: Correction de bug
- docs: Documentation
- style: Formatage (pas de changement de code)
- refactor: Refactoring
- perf: Performance
- test: Tests
- chore: Maintenance

Exemples:
feat(patients): add patient search functionality
fix(auth): resolve JWT refresh token issue
docs(api): update API integration guide
test(patients): add unit tests for PatientCard
```

### 7. Pull Requests

#### Checklist PR

- [ ] Code reviewé par au moins 1 développeur
- [ ] Tests ajoutés/mis à jour
- [ ] Coverage maintenu/augmenté
- [ ] Documentation mise à jour
- [ ] CHANGELOG.md mis à jour
- [ ] Build passe (`npm run build`)
- [ ] Tests passent (`npm run test:coverage`)
- [ ] Lint passe (`npm run lint`)
- [ ] TypeScript passe (`npm run typecheck`)
- [ ] Security audit passe (`npm audit`)

#### Template PR

```markdown
## Description
[Description de la PR]

## Type
- [ ] feat
- [ ] fix
- [ ] docs
- [ ] refactor

## Tests
- [ ] Tests unitaires ajoutés
- [ ] Tests d'intégration ajoutés
- [ ] Tests d'accessibilité ajoutés

## Screenshots (si applicable)
[Captures d'écran]

## Checklist
- [ ] Code reviewé
- [ ] Tests passent
- [ ] Documentation à jour
```

### 8. Performance

#### Règles d'or

1. **Lazy Loading** : Toutes les pages doivent être lazy-loaded
2. **Code Splitting** : Utiliser `React.lazy()` pour les composants lourds
3. **Memoization** : Utiliser `memo`, `useMemo`, `useCallback` quand nécessaire
4. **Bundle Size** : Surveiller la taille des chunks (< 500 KB)

```typescript
// ✅ BON
const HeavyComponent = lazy(() => import('./HeavyComponent'));

<Suspense fallback={<Loading />}>
  <HeavyComponent />
</Suspense>

// ❌ MAUVAIS
import HeavyComponent from './HeavyComponent';
```

### 9. Sécurité

#### Checklist sécurité

- [ ] RBAC vérifié (permissions)
- [ ] JWT validé
- [ ] XSS protégé (pas de `dangerouslySetInnerHTML` sans sanitization)
- [ ] CSRF protégé
- [ ] Inputs validés (Zod)
- [ ] Erreurs sensibles cachées en production
- [ ] Pas de secrets dans le code

```typescript
// ✅ BON
const schema = z.object({
  email: z.string().email(),
  password: z.string().min(8),
});

// ❌ MAUVAIS
const schema = (data: any) => data;
```

### 10. Accessibilité (WCAG 2.1 AA)

#### Obligatoire pour chaque composant

```typescript
// ✅ BON
<button
  aria-label="Fermer la fenêtre"
  onClick={handleClose}
>
  <X className="h-4 w-4" />
</button>

// ❌ MAUVAIS
<button onClick={handleClose}>
  <X />
</button>
```

#### Checklist accessibilité

- [ ] Labels ARIA sur tous les éléments interactifs
- [ ] Navigation clavier complète
- [ ] Contraste couleurs vérifié (4.5:1 minimum)
- [ ] Focus visible
- [ ] Semantic HTML (`<button>`, `<nav>`, `<main>`)
- [ ] `alt` text sur toutes les images

## 🚫 Interdictions

### Jamais autorisé

```typescript
// ❌ INTERDIT
const data: any = {};
function test(x) { }
console.log('debug'); // En production
dangerouslySetInnerHTML={{ __html: data }}
localStorage.setItem('token', token); // Utiliser le contexte auth
```

### À éviter

- Composants de plus de 300 lignes
- Fonctions de plus de 50 lignes
- Plus de 3 niveaux d'imbrication
- Duplication de code (DRY principle)

## 📚 Ressources

- [React 19 Docs](https://react.dev)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/)
- [Tailwind CSS](https://tailwindcss.com/docs)
- [ShadCN/UI](https://ui.shadcn.com)
- [TanStack Query](https://tanstack.com/query/latest/docs/react/overview)
- [WCAG 2.1 AA](https://www.w3.org/WAI/WCAG21/quickref/)
- [Vitest](https://vitest.dev)

## 🆘 Support

- **Questions** : Slack #gindho-frontend
- **Bugs** : GitHub Issues
- **Architecture** : Voir ARCHITECTURE.md
- **API** : Voir INTEGRATION.md

## 📝 License

Propriétaire - GinDHO Hospital Platform