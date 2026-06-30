import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { authService } from '@/services/auth.service';
import type { Permission } from '@/types';

export function useCurrentUser() {
  return useQuery({
    queryKey: ['auth', 'me'],
    queryFn: authService.getCurrentUser,
    retry: false,
  });
}

export function usePermissions() {
  return useQuery({
    queryKey: ['auth', 'permissions'],
    queryFn: authService.getPermissions,
    retry: false,
  });
}

export function useLogin() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ email, password }: { email: string; password: string }) =>
      authService.login(email, password),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['auth'] });
    },
  });
}

export function useRegister() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: { email: string; password: string; nom: string; prenom: string; role?: string }) =>
      authService.register(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['auth'] });
    },
  });
}

export function useLogout() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async () => {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
    },
    onSuccess: () => {
      queryClient.clear();
    },
  });
}

export function hasPermission(userPermissions: Permission[], requiredPermission: string): boolean {
  return userPermissions.some(
    (p) => p.name === requiredPermission ||
      (p.ressource && p.action && `${p.ressource}:${p.action}` === requiredPermission)
  );
}

export function hasRole(userRole: string, allowedRoles: string[]): boolean {
  return allowedRoles.includes(userRole);
}