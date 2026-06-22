import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { PaymentsPage } from '@/pages/payments/Payments';

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

describe('Payments Page', () => {
  it('should render page title', () => {
    render(<PaymentsPage />, { wrapper });
    expect(screen.getByText('Facturation')).toBeInTheDocument();
  });

  it('should render new payment button', () => {
    render(<PaymentsPage />, { wrapper });
    expect(screen.getByText('Nouvelle facture')).toBeInTheDocument();
  });
});