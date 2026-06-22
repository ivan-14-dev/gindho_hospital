import { Outlet } from 'react-router-dom';
import { Sidebar } from '@/components/layout/sidebar';
import { Header } from '@/components/layout/header';
import { useAuth } from '@/contexts/use-auth-context';
import { getBreadcrumbs } from '@/shared/navigation/navigation-registry';

export function Layout() {
  const { user, logout, permissions } = useAuth();

  const breadcrumbs = user?.role ? getBreadcrumbs(window.location.pathname) : [];

  return (
    <div className="flex h-screen">
      <Sidebar 
        userRole={user?.role || 'PATIENT'} 
        userPermissions={permissions}
        onLogout={logout} 
      />
      <div className="flex-1 lg:ml-64">
        <Header onLogout={logout} breadcrumbs={breadcrumbs} />
        <main className="p-6" id="main-content" tabIndex={-1}>
          <Outlet />
        </main>
      </div>
    </div>
  );
}
