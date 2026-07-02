import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { MemoryRouter } from 'react-router-dom';
import { PermissionGuard } from '@/components/auth/permission-guard';
import { AuthProvider } from '@/contexts/auth-context';

vi.mock('@/hooks/use-auth', () => ({
  useCurrentUser: vi.fn(),
  usePermissions: vi.fn(),
  useLogout: vi.fn(),
}));

import { useCurrentUser, usePermissions } from '@/hooks/use-auth';

function TestWrapper({ children }: { children: React.ReactNode }) {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
    },
  });
  return (
    <MemoryRouter>
      <QueryClientProvider client={queryClient}>
        <AuthProvider>
          {children}
        </AuthProvider>
      </QueryClientProvider>
    </MemoryRouter>
  );
}

describe('PermissionGuard', () => {
  it('should render children when permission exists', () => {
    vi.mocked(useCurrentUser).mockReturnValue({ data: { role: 'MEDECIN' }, isLoading: false } as any);
    vi.mocked(usePermissions).mockReturnValue({ data: ['patients:READ'], isLoading: false } as any);

    render(
      <TestWrapper>
        <PermissionGuard permission="patients:READ">
          <p data-testid="child">Content</p>
        </PermissionGuard>
      </TestWrapper>
    );
    expect(screen.getByTestId('child').textContent).toBe('Content');
  });

  it('should render fallback when permission is missing', () => {
    vi.mocked(useCurrentUser).mockReturnValue({ data: { role: 'MEDECIN' }, isLoading: false } as any);
    vi.mocked(usePermissions).mockReturnValue({ data: ['appointments:READ'], isLoading: false } as any);

    render(
      <TestWrapper>
        <PermissionGuard permission="patients:READ" fallback={<p data-testid="fallback">Denied</p>}>
          <p data-testid="child">Content</p>
        </PermissionGuard>
      </TestWrapper>
    );
    expect(screen.getByTestId('fallback').textContent).toBe('Denied');
  });

  it('should render loading spinner when loading', () => {
    vi.mocked(useCurrentUser).mockReturnValue({ data: null, isLoading: true } as any);
    vi.mocked(usePermissions).mockReturnValue({ data: null, isLoading: true } as any);

    render(
      <TestWrapper>
        <PermissionGuard permission="patients:READ">
          <p data-testid="child">Content</p>
        </PermissionGuard>
      </TestWrapper>
    );
    expect(screen.queryByTestId('child')).not.toBeInTheDocument();
  });

  it('should check role-based access', () => {
    vi.mocked(useCurrentUser).mockReturnValue({ data: { role: 'PATIENT' }, isLoading: false } as any);
    vi.mocked(usePermissions).mockReturnValue({ data: [], isLoading: false } as any);

    render(
      <TestWrapper>
        <PermissionGuard roles={['MEDECIN']} fallback={<p data-testid="role-fallback">Role denied</p>}>
          <p data-testid="child">Content</p>
        </PermissionGuard>
      </TestWrapper>
    );
    expect(screen.getByTestId('role-fallback').textContent).toBe('Role denied');
  });

  it('should support requireAll AND logic for multiple permissions', () => {
    vi.mocked(useCurrentUser).mockReturnValue({ data: { role: 'MEDECIN' }, isLoading: false } as any);
    vi.mocked(usePermissions).mockReturnValue({ data: ['patients:READ'], isLoading: false } as any);

    render(
      <TestWrapper>
        <PermissionGuard permission={['patients:READ', 'appointments:READ']} requireAll={true} fallback={<p data-testid="fallback">Denied</p>}>
          <p data-testid="child">Content</p>
        </PermissionGuard>
      </TestWrapper>
    );
    expect(screen.getByTestId('fallback').textContent).toBe('Denied');
  });

  it('should render children when at least one permission matches with OR logic', () => {
    vi.mocked(useCurrentUser).mockReturnValue({ data: { role: 'MEDECIN' }, isLoading: false } as any);
    vi.mocked(usePermissions).mockReturnValue({ data: ['patients:READ'], isLoading: false } as any);

    render(
      <TestWrapper>
        <PermissionGuard permission={['patients:READ', 'appointments:READ']} requireAll={false}>
          <p data-testid="child">Content</p>
        </PermissionGuard>
      </TestWrapper>
    );
    expect(screen.getByTestId('child').textContent).toBe('Content');
  });
});
