import { useAuth } from '@/contexts/use-auth-context';

/**
 * Hook to check if the current user has a specific permission.
 * Returns true if the permission string is present in the user's permissions array.
 */
export function useHasPermission(permission: string): boolean {
  const auth = useAuth();
  const permissions = auth?.permissions ?? [];
  return permissions.includes(permission as any);
}
