import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { apiClient } from '@/lib/api-client';
import { buildApiUrl } from '@/lib/config';
import type { User } from '@/types';

/**
 * Hook to manage users, roles and permissions.
 */
export function useUserManagement() {
  const queryClient = useQueryClient();

  const fetchUsers = async (): Promise<User[]> => {
    // Assuming the backend provides a /users endpoint under AUTH service
    return apiClient.get(buildApiUrl('AUTH', '/users'));
  };

  const { data: users, isLoading, error, refetch } = useQuery({
    queryKey: ['admin', 'users'],
    queryFn: fetchUsers,
    retry: false,
  });

  const updateUserRole = useMutation({
    mutationFn: async ({ userId, role }: { userId: string; role: string }) => {
      await apiClient.put(buildApiUrl('AUTH', `/users/${userId}/role`), { role });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin', 'users'] });
    },
  });

  const updateUserPermissions = useMutation({
    mutationFn: async ({ userId, permissions }: { userId: string; permissions: string[] }) => {
      await apiClient.put(buildApiUrl('AUTH', `/users/${userId}/permissions`), { permissions });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin', 'users'] });
    },
  });

  return {
    users,
    isLoading,
    error,
    refetch,
    updateUserRole,
    updateUserPermissions,
  };
}
