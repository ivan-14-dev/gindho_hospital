import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { apiClient } from '@/lib/api-client';
import type { User } from '@/types';

function unwrap<T>(response: { data?: T } | T): T {
  if (response && typeof response === 'object' && 'data' in response) {
    return (response as { data: T }).data;
  }
  return response as T;
}

/**
 * Hook to manage users, roles and permissions.
 */
export function useUserManagement() {
  const queryClient = useQueryClient();

  const fetchUsers = async (): Promise<User[]> => {
    const response = await apiClient.get<{ data: User[] }>('/api/users');
    return unwrap(response);
  };

  const { data: users, isLoading, error, refetch } = useQuery({
    queryKey: ['admin', 'users'],
    queryFn: fetchUsers,
    retry: false,
  });

  const updateUserRole = useMutation({
    mutationFn: async ({ userId, role }: { userId: string; role: string }) => {
      await apiClient.put(`/api/users/${userId}/role`, { role });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin', 'users'] });
    },
  });

  const updateUserPermissions = useMutation({
    mutationFn: async ({ userId, permissions }: { userId: string; permissions: string[] }) => {
      await apiClient.put(`/api/users/${userId}/permissions`, { permissions });
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
