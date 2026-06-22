import type { ReactNode } from 'react';
import { AuthContext, type AuthContextType } from '@/contexts/auth-context-core';
import { useCurrentUser, usePermissions, useLogout } from '@/hooks/use-auth';
import type { NavigationPermission } from '@/shared/navigation/navigation-registry';
import { useEffect } from 'react';
import { setUnauthorizedHandler, setForbiddenHandler } from '@/lib/api-client';
import { useNavigate } from 'react-router-dom';

export function AuthProvider({ children }: { children: ReactNode }) {
  const navigate = useNavigate();
  const { data: userData, isLoading: userLoading } = useCurrentUser();
  const { data: permissionsData, isLoading: permissionsLoading } = usePermissions();
  const logoutMutation = useLogout();

  useEffect(() => {
    setUnauthorizedHandler(() => {
      logoutMutation.mutate();
      navigate('/login', { replace: true });
    });
    setForbiddenHandler(() => {
      navigate('/unauthorized', { replace: true });
    });
  }, [logoutMutation, navigate]);

  const value: AuthContextType = {
    user: userData ?? null,
    permissions: (permissionsData ?? []) as NavigationPermission[],
    isLoading: userLoading || permissionsLoading,
    isAuthenticated: !!userData,
    logout: () => {
      logoutMutation.mutate();
      navigate('/login', { replace: true });
    },
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
