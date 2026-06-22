import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AppointmentsPage } from '@/pages/appointments/Appointments';

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

describe('Appointments Page', () => {
  it('should render page title', () => {
    render(<AppointmentsPage />, { wrapper });
    expect(screen.getByText('Rendez-vous')).toBeInTheDocument();
  });

  it('should render new appointment button', () => {
    render(<AppointmentsPage />, { wrapper });
    expect(screen.getByText('Nouveau rendez-vous')).toBeInTheDocument();
  });

  it('should render filter buttons', () => {
    render(<AppointmentsPage />, { wrapper });
    expect(screen.getByText('Tous')).toBeInTheDocument();
    expect(screen.getByText("Aujourd'hui")).toBeInTheDocument();
    expect(screen.getByText('À venir')).toBeInTheDocument();
    expect(screen.getByText('Passés')).toBeInTheDocument();
  });
});