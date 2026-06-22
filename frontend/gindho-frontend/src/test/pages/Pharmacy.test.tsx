import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { PharmacyPage } from '@/pages/pharmacy/Pharmacy';

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

describe('Pharmacy Page', () => {
  it('should render page title', () => {
    render(<PharmacyPage />, { wrapper });
    expect(screen.getByText('Pharmacie')).toBeInTheDocument();
  });

  it('should render new prescription button', () => {
    render(<PharmacyPage />, { wrapper });
    expect(screen.getByText('Nouvelle prescription')).toBeInTheDocument();
  });
});