import { useContext } from 'react';
import { AuthContext } from '@/contexts/auth-context-core';
import { Loader2 } from 'lucide-react';
import type { NavigationPermission } from '@/shared/navigation/navigation-registry';

interface PermissionGuardProps {
  children: React.ReactNode;
  permission?: NavigationPermission | NavigationPermission[];
  roles?: string[];
  requireAll?: boolean;
  fallback?: React.ReactNode;
  loadingFallback?: React.ReactNode;
}

export function PermissionGuard({
  children,
  permission,
  roles,
  requireAll = false,
  fallback = null,
  loadingFallback,
}: PermissionGuardProps) {
  const context = useContext(AuthContext);
  const { permissions, user, isLoading } = context || {
    permissions: [] as NavigationPermission[],
    user: null,
    isLoading: false,
  };

  if (isLoading) {
    if (loadingFallback !== undefined) {
      return <>{loadingFallback}</>;
    }
    return (
      <div className="flex items-center justify-center min-h-[200px]">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
      </div>
    );
  }

  const permissionsArray = Array.isArray(permission) ? permission : permission ? [permission] : [];

  if (permissionsArray.length > 0) {
    const hasPermission = requireAll
      ? permissionsArray.every((p) => permissions.includes(p))
      : permissionsArray.some((p) => permissions.includes(p));

    if (!hasPermission) {
      if (fallback !== undefined) {
        return <>{fallback}</>;
      }
      return (
        <div className="flex flex-col items-center justify-center min-h-[200px] text-center">
          <p className="text-lg font-medium text-destructive">Accès refusé</p>
          <p className="text-sm text-muted-foreground mt-2">
            Vous n'avez pas les permissions nécessaires pour accéder à cette section.
          </p>
        </div>
      );
    }
  }

  if (roles && roles.length > 0 && user?.role) {
    const hasRequiredRole = roles.includes(user.role);
    if (!hasRequiredRole) {
      if (fallback !== undefined) {
        return <>{fallback}</>;
      }
      return (
        <div className="flex flex-col items-center justify-center min-h-[200px] text-center">
          <p className="text-lg font-medium text-destructive">Accès refusé</p>
          <p className="text-sm text-muted-foreground mt-2">
            Votre rôle ne permet pas d'accéder à cette section.
          </p>
        </div>
      );
    }
  }

  return <>{children}</>;
}
