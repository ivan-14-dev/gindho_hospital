import { useContext } from 'react';
import { AuthContext } from '@/contexts/auth-context-core';
import type { NavigationPermission } from '@/shared/navigation/navigation-registry';

interface PermissionGuardProps {
  children: React.ReactNode;
  permission?: NavigationPermission;
  roles?: string[];
  fallback?: React.ReactNode;
}

export function PermissionGuard({
  children,
  permission,
  roles,
  fallback = null,
}: PermissionGuardProps) {
  const context = useContext(AuthContext);
  const { permissions, user } = context || { permissions: [] as NavigationPermission[], user: null };

  // Vérifier les rôles
  if (roles && user?.role) {
    const hasRequiredRole = roles.includes(user.role);
    if (!hasRequiredRole) {
      return <>{fallback}</>;
    }
  }

  // Vérifier les permissions
  if (permission && permissions) {
    const hasRequiredPermission = permissions.includes(permission);
    if (!hasRequiredPermission) {
      return <>{fallback}</>;
    }
  }

  return <>{children}</>;
}
