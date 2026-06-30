import { Suspense, lazy, useState } from 'react';
import { useUserManagement } from '@/hooks/use-user-management';
import { Loader2 } from 'lucide-react';

const UserRow = lazy(() => import('@/components/admin/UserRow').then(m => ({ default: m.UserRow })));

export function UserManagementPage() {
  const {
    users,
    isLoading,
    error,
    refetch,
    updateUserRole,
    updateUserPermissions,
  } = useUserManagement();

  const [refreshing, setRefreshing] = useState(false);

  const handleRefresh = async () => {
    setRefreshing(true);
    await refetch();
    setRefreshing(false);
  };

  if (isLoading) {
    return (
      <div className="flex h-screen items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="p-6 text-destructive">
        <h2 className="text-xl font-semibold mb-4">Erreur de chargement des utilisateurs</h2>
        <pre>{JSON.stringify(error, null, 2)}</pre>
        <button
          className="mt-4 px-4 py-2 bg-primary text-primary-foreground rounded"
          onClick={handleRefresh}
        >
          Réessayer
        </button>
      </div>
    );
  }

  return (
    <div className="p-6 space-y-6">
      <header className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">Gestion des utilisateurs</h1>
        <button
          className="px-4 py-2 bg-primary text-primary-foreground rounded hover:bg-primary/90 transition"
          onClick={handleRefresh}
          disabled={refreshing}
        >
          {refreshing ? 'Actualisation…' : 'Actualiser'}
        </button>
      </header>

      <div className="overflow-x-auto rounded-lg border">
        <table className="min-w-full divide-y divide-muted bg-card">
          <thead className="bg-muted/50">
            <tr>
              <th className="px-4 py-2 text-left font-medium text-muted-foreground">Nom</th>
              <th className="px-4 py-2 text-left font-medium text-muted-foreground">Email</th>
              <th className="px-4 py-2 text-left font-medium text-muted-foreground">Rôle</th>
              <th className="px-4 py-2 text-left font-medium text-muted-foreground">Permissions</th>
              <th className="px-4 py-2 text-left font-medium text-muted-foreground">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-muted">
            {users?.map(user => (
              <Suspense
                fallback={
                  <tr>
                    <td colSpan={5} className="p-4 text-center">
                      <Loader2 className="h-5 w-5 animate-spin text-primary" />
                    </td>
                  </tr>
                }
                key={user.id}
              >
                <UserRow
                  user={user}
                  onRoleChange={role =>
                    updateUserRole.mutate({ userId: user.id, role })
                  }
                  onPermissionsChange={permissions =>
                    updateUserPermissions.mutate({ userId: user.id, permissions })
                  }
                />
              </Suspense>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
