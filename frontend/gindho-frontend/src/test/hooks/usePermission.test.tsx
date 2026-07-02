import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { MemoryRouter } from 'react-router-dom';
import { AuthProvider } from '@/contexts/auth-context';
import { usePermission, useRole, useCan } from '@/hooks/usePermission';

vi.mock('@/hooks/use-auth', () => ({
  useCurrentUser: vi.fn(),
  usePermissions: vi.fn(),
  useLogout: vi.fn(),
}));

import { useCurrentUser, usePermissions } from '@/hooks/use-auth';

function TestConsumer({
  permission,
  roles,
}: {
  permission?: string;
  roles?: string[];
}) {
  const perm = permission ? usePermission(permission as any) : false;
  const role = roles ? useRole(roles) : false;
  const can = permission ? useCan(permission as any) : false;

  return (
    <>
      <p data-testid="permission">{String(perm)}</p>
      <p data-testid="role">{String(role)}</p>
      <p data-testid="can">{String(can)}</p>
    </>
  );
}

describe('usePermission', () => {
  function wrapper({ children }: { children: React.ReactNode }) {
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

  it('should return true when permission exists', () => {
    vi.mocked(useCurrentUser).mockReturnValue({ data: { role: 'MEDECIN' } } as any);
    vi.mocked(usePermissions).mockReturnValue({ data: ['patients:READ', 'appointments:READ'] } as any);

    render(<TestConsumer permission="patients:READ" />, { wrapper });
    expect(screen.getByTestId('permission').textContent).toBe('true');
  });

  it('should return false when permission does not exist', () => {
    vi.mocked(useCurrentUser).mockReturnValue({ data: { role: 'MEDECIN' } } as any);
    vi.mocked(usePermissions).mockReturnValue({ data: ['patients:READ'] } as any);

    render(<TestConsumer permission="facilities:ADMIN" />, { wrapper });
    expect(screen.getByTestId('permission').textContent).toBe('false');
  });

  it('should return false when permissions are loading', () => {
    vi.mocked(useCurrentUser).mockReturnValue({ data: { role: 'MEDECIN' }, isLoading: true } as any);
    vi.mocked(usePermissions).mockReturnValue({ data: ['patients:READ'], isLoading: true } as any);

    render(<TestConsumer permission="patients:READ" />, { wrapper });
    expect(screen.getByTestId('permission').textContent).toBe('false');
  });

  it('should return true for matching role', () => {
    vi.mocked(useCurrentUser).mockReturnValue({ data: { role: 'MEDECIN' } } as any);
    vi.mocked(usePermissions).mockReturnValue({ data: [] } as any);

    render(<TestConsumer roles={['MEDECIN']} />, { wrapper });
    expect(screen.getByTestId('role').textContent).toBe('true');
  });

  it('should return false for non-matching role', () => {
    vi.mocked(useCurrentUser).mockReturnValue({ data: { role: 'PATIENT' } } as any);
    vi.mocked(usePermissions).mockReturnValue({ data: [] } as any);

    render(<TestConsumer roles={['MEDECIN']} />, { wrapper });
    expect(screen.getByTestId('role').textContent).toBe('false');
  });
});

describe('useCan', () => {
  function wrapper({ children }: { children: React.ReactNode }) {
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

  it('should return false when not authenticated', () => {
    vi.mocked(useCurrentUser).mockReturnValue({ data: null } as any);
    vi.mocked(usePermissions).mockReturnValue({ data: ['patients:READ'] } as any);

    render(<TestConsumer permission="patients:READ" />, { wrapper });
    expect(screen.getByTestId('can').textContent).toBe('false');
  });
});
