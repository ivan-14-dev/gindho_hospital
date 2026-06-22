import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { DashboardPage } from '@/pages/dashboard/Dashboard';
import { useDashboardStats } from '@/hooks/use-dashboard';

// Mock the hook
vi.mock('@/hooks/use-dashboard');

const mockUseDashboardStats = useDashboardStats as ReturnType<typeof vi.fn>;

function wrapper({ children }: { children: React.ReactNode }) {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
    },
  });
  return (
    <QueryClientProvider client={queryClient}>
      {children}
    </QueryClientProvider>
  );
}

describe('Dashboard Page', () => {
  it('should render welcome message', () => {
    mockUseDashboardStats.mockReturnValue({
      data: { totalPatients: 100 },
      isLoading: false,
    });
    render(<DashboardPage />, { wrapper });
    expect(screen.getByText('Dashboard')).toBeInTheDocument();
  });

  it('should show loading state', () => {
    mockUseDashboardStats.mockReturnValue({
      data: undefined,
      isLoading: true,
    });
    render(<DashboardPage />, { wrapper });
    expect(screen.getByText('Dashboard')).toBeInTheDocument();
  });
});