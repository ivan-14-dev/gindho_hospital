import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { PatientsPage } from '@/pages/patients/Patients';

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

describe('Patients Page', () => {
  it('should render page title', () => {
    render(<PatientsPage />, { wrapper });
    expect(screen.getByText('Patients')).toBeInTheDocument();
    expect(screen.getByText('Gestion des patients')).toBeInTheDocument();
  });

  it('should render new patient button', () => {
    render(<PatientsPage />, { wrapper });
    expect(screen.getByText('Nouveau patient')).toBeInTheDocument();
  });

  it('should render search input', () => {
    render(<PatientsPage />, { wrapper });
    expect(screen.getByPlaceholderText('Rechercher un patient...')).toBeInTheDocument();
  });

  it('should render export PDF button', () => {
    render(<PatientsPage />, { wrapper });
    expect(screen.getByText('Exporter PDF')).toBeInTheDocument();
  });
});