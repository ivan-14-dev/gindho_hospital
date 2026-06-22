# Navigation Registry

Registre centralisé de navigation pour le frontend GinDHO.

## Usage

```typescript
import { NAVIGATION_REGISTRY, filterNavigationByRole } from '@/shared/navigation/navigation-registry';

// Filtrer par rôle utilisateur
const visibleItems = filterNavigationByRole(
  NAVIGATION_REGISTRY,
  user.role,
  user.permissions
);
```

## Structure

Chaque entrée de navigation contient :
- `id` : Identifiant unique
- `label` : Libellé affiché
- `path` : Chemin de route
- `icon` : Icône Lucide
- `roles` : Rôles autorisés (optionnel)
- `permissions` : Permissions requises (optionnel)
- `visible` : Fonction de visibilité dynamique (optionnel)
- `children` : Sous-menus (optionnel)

## Ajout d'une nouvelle entrée

1. Ajouter au registre dans `navigation-registry.ts`
2. Créer la page correspondante dans `src/pages/`
3. Ajouter la route dans `App.tsx`
4. Les permissions sont vérifiées automatiquement

## Sécurité

Les permissions sont vérifiées côtec client mais **la sécurité d'accès est garantie côté API**.