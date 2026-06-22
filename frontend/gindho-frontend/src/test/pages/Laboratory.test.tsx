import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { LaboratoryPage } from '@/pages/laboratory/Laboratory';

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

describe('Laboratory Page', () => {
  it('should render page title', () => {
    render(<LaboratoryPage />, { wrapper });
    expect(screen.getByText('Laboratoire')).toBeInTheDocument();
  });

  it('should render new analysis button', () => {
    render(<LaboratoryPage />, { wrapper });
    expect(screen.getByText('Nouvelle analyse')).toBeInTheDocument();
  });
});