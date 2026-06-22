# Guide JSDoc - GinDHO Frontend

## 📋 Vue d'ensemble

Ce guide définit les standards de documentation JSDoc pour le projet GinDHO Frontend.

## 🎯 Objectifs

- Documentation complète de tous les composants, hooks et services
- Génération automatique de documentation avec TypeDoc
- Amélioration de l'IDE support (IntelliSense)
- Faciliter l'onboarding des nouveaux développeurs

## 📝 Standards de Documentation

### 1. Composants React

```tsx
/**
 * Button component for user interactions
 * @component
 * @example
 * ```tsx
 * <Button variant="default" onClick={handleClick}>
 *   Click me
 * </Button>
 * ```
 */
export function Button({ 
  variant = 'default',
  size = 'default',
  children,
  ...props 
}: ButtonProps) {
  // Implementation
}
```

### 2. Hooks

```tsx
/**
 * Custom hook for managing patient data
 * @param {Object} options - Hook options
 * @param {string} options.search - Search query
 * @param {number} options.page - Page number
 * @param {number} options.size - Items per page
 * @returns {UseQueryResult<PatientResponse>} Query result with patients data
 * @example
 * ```tsx
 * const { data, isLoading } = usePatients({ 
 *   search: 'Dupont', 
 *   page: 0, 
 *   size: 10 
 * });
 * ```
 */
export function usePatients(options: UsePatientsOptions) {
  // Implementation
}
```

### 3. Services

```tsx
/**
 * API service for HTTP requests
 * @class
 * @example
 * ```ts
 * const api = new ApiService();
 * const data = await api.get('/patients');
 * ```
 */
export class ApiService {
  /**
   * Perform GET request
   * @param {string} endpoint - API endpoint
   * @param {Object} params - Query parameters
   * @returns {Promise<T>} Response data
   * @template T
   */
  async get<T>(endpoint: string, params?: Record<string, any>): Promise<T> {
    // Implementation
  }
}
```

### 4. Types et Interfaces

```tsx
/**
 * Patient entity representation
 * @interface Patient
 * @property {string} id - Unique identifier
 * @property {string} nom - Last name
 * @property {string} prenom - First name
 * @property {string} email - Email address
 * @property {'M' | 'F' | 'Autre'} sexe - Gender
 * @property {string} dateNaissance - Birth date (ISO format)
 */
export interface Patient {
  id: string;
  nom: string;
  prenom: string;
  email?: string;
  sexe: 'M' | 'F' | 'Autre';
  dateNaissance: string;
}
```

## 🏷️ Tags JSDoc Recommandés

| Tag | Description | Exemple |
|-----|-------------|---------|
| `@component` | Marque un composant React | `@component` |
| `@hook` | Marque un hook personnalisé | `@hook` |
| `@param` | Documente un paramètre | `@param {string} name - Description` |
| `@returns` | Documente la valeur de retour | `@returns {Promise<Patient>}` |
| `@throws` | Documente les erreurs possibles | `@throws {Error} Si la requête échoue` |
| `@example` | Fournit un exemple d'usage | `@example <caption>Usage</caption>` |
| `@template` | Pour les types génériques | `@template T` |
| `@interface` | Documente une interface | `@interface User` |
| `@class` | Documente une classe | `@class ApiService` |
| `@see` | Référence à un autre élément | `@see usePatients` |
| `@deprecated` | Marque comme déprécié | `@deprecated Depuis v2.0` |

## 📂 Structure de Documentation

### Par Module

Chaque module doit avoir un fichier `README.md` ou une documentation en tête de fichier:

```tsx
/**
 * @module patients
 * @description Gestion complète des patients
 * 
 * @todos
 * - [ ] Connecter aux APIs réelles (TASK-025)
 * - [x] Implémenter CRUD de base
 * - [x] Ajouter recherche et filtres
 * 
 * @dependencies
 * - use-patients.ts (hook)
 * - patient.service.ts (API)
 * - PatientForm.tsx (formulaire)
 */
```

### Par Fonction

```tsx
/**
 * Recherche des patients avec pagination
 * 
 * @description
 * Effectue une recherche paginée des patients selon les critères fournis.
 * Les résultats sont mis en cache par React Query pendant 5 minutes.
 * 
 * @param {UsePatientsOptions} options - Options de recherche
 * @param {string} [options.search] - Terme de recherche (nom, prénom, email)
 * @param {number} [options.page=0] - Numéro de page (0-indexed)
 * @param {number} [options.size=10] - Nombre d'éléments par page
 * 
 * @returns {UseQueryResult<PatientResponse>} Résultat de la requête
 * @returns {boolean} returns.isLoading - État de chargement
 * @returns {PatientResponse | undefined} returns.data - Données des patients
 * @returns {Error | null} returns.error - Erreur éventuelle
 * 
 * @throws {Error} Erreur réseau si le serveur est inaccessible
 * 
 * @example
 * ```tsx
 * const { data, isLoading, error } = usePatients({
 *   search: 'Dupont',
 *   page: 0,
 *   size: 10
 * });
 * 
 * if (isLoading) return <Spinner />;
 * if (error) return <Error message={error.message} />;
 * 
 * return (
 *   <div>
 *     {data?.content.map(patient => (
 *       <PatientCard key={patient.id} patient={patient} />
 *     ))}
 *   </div>
 * );
 * ```
 * 
 * @see {@link PatientResponse} pour la structure de réponse
 * @see {@link Patient} pour l'entité Patient
 * @since 1.0.0
 * @author GinDHO Team
 */
```

## 🔧 Configuration

### TypeDoc

```json
// typedoc.json
{
  "entryPoints": ["src/index.ts"],
  "out": "docs/api",
  "theme": "default",
  "readme": "README.md",
  "excludePrivate": true,
  "excludeProtected": true,
  "includeVersion": true,
  "categorizeByGroup": true,
  "defaultCategory": "Autre"
}
```

### ESLint Plugin

```json
// .eslintrc.json
{
  "plugins": ["jsdoc"],
  "rules": {
    "jsdoc/require-description": "error",
    "jsdoc/require-param": "error",
    "jsdoc/require-returns": "error"
  }
}
```

## ✅ Checklist de Documentation

### Composants
- [ ] Description du composant
- [ ] Documentation des props
- [ ] Exemple d'utilisation
- [ ] États possibles (loading, error, empty)
- [ ] Accessibilité (ARIA labels)

### Hooks
- [ ] Description du hook
- [ ] Paramètres d'entrée
- [ ] Valeur de retour
- [ ] États de chargement/erreur
- [ ] Exemple d'utilisation

### Services
- [ ] Description du service
- [ ] Méthodes documentées
- [ ] Paramètres et retours
- [ ] Erreurs possibles
- [ ] Exemples d'appels API

### Pages
- [ ] Description de la page
- [ ] Permissions requises
- [ ] Données utilisées
- [ ] Actions disponibles
- [ ] États (loading, error, empty)

## 📊 Métriques de Documentation

Objectifs:
- **Composants**: 100% documentés
- **Hooks**: 100% documentés
- **Services**: 100% documentés
- **Pages**: 100% documentées

Vérification:
```bash
npm run docs:generate
npm run docs:check
```

## 🚀 Bonnes Pratiques

1. **Toujours documenter les paramètres optionnels**
2. **Inclure des exemples concrets**
3. **Documenter les cas d'erreur**
4. **Utiliser des types stricts (pas de `any`)**
5. **Maintenir la documentation à jour**
6. **Utiliser `@since` pour les nouvelles fonctionnalités**
7. **Marquer avec `@deprecated` avant suppression**

## 📚 Ressources

- [JSDoc Official](https://jsdoc.app/)
- [TypeDoc](https://typedoc.org/)
- [ESLint JSDoc Plugin](https://github.com/gajus/eslint-plugin-jsdoc)