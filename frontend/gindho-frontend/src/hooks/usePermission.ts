import { useMemo } from 'react';
import { useAuth } from '@/contexts/use-auth-context';
import type { NavigationPermission } from '@/shared/navigation/navigation-registry';

export function usePermission(permission: NavigationPermission): boolean {
  const { permissions, isLoading } = useAuth();
  return useMemo(() => {
    if (isLoading) return false;
    return permissions.includes(permission);
  }, [permissions, isLoading, permission]);
}

export function useRole(roles: string[]): boolean {
  const { user, isLoading } = useAuth();

  return useMemo(() => {
    if (isLoading || !user?.role) return false;
    return roles.includes(user.role);
  }, [roles, user?.role, isLoading]);
}

export function useCan(permission: NavigationPermission): boolean {
  const hasPermission = usePermission(permission);
  const { isAuthenticated } = useAuth();

  return useMemo(() => {
    if (!isAuthenticated) return false;
    return hasPermission;
  }, [isAuthenticated, hasPermission]);
}
