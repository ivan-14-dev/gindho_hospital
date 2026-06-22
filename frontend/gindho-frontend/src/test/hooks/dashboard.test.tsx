import { describe, it, expect } from 'vitest';
import { renderHook } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

function wrapper({ children }: { children: React.ReactNode }) {
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  });
  return (
    <QueryClientProvider client={queryClient}>
      {children}
    </QueryClientProvider>
  );
}

describe('useDashboardStats hook', () => {
  it('should be defined', async () => {
    const { useDashboardStats } = await import('@/hooks/use-dashboard');
    const { result } = renderHook(() => useDashboardStats('ADMIN'), { wrapper });
    expect(result.current).toBeDefined();
  });
});