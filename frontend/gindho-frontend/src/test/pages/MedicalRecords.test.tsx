import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { BrowserRouter } from 'react-router-dom';
import { MedicalRecordsPage } from '@/pages/medical-records/MedicalRecords';

vi.mock('@/hooks/use-medical-records');

import { useMedicalRecords } from '@/hooks/use-medical-records';

const mockUseMedicalRecords = useMedicalRecords as ReturnType<typeof vi.fn>;

function wrapper({ children }: { children: React.ReactNode }) {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
    },
  });
  return (
    <BrowserRouter>
      <QueryClientProvider client={queryClient}>
        {children}
      </QueryClientProvider>
    </BrowserRouter>
  );
}

describe('MedicalRecords Page', () => {
  it('should render page title', () => {
    mockUseMedicalRecords.mockReturnValue({
      data: [],
      isLoading: false,
    });
    render(<MedicalRecordsPage />, { wrapper });
    expect(screen.getByText('Dossier Médical')).toBeInTheDocument();
    expect(screen.getByText('Consultations et historique médical')).toBeInTheDocument();
  });

  it('should render new consultation button', () => {
    mockUseMedicalRecords.mockReturnValue({
      data: [],
      isLoading: false,
    });
    render(<MedicalRecordsPage />, { wrapper });
    expect(screen.getByText('Nouvelle consultation')).toBeInTheDocument();
  });

  it('should render search input', () => {
    mockUseMedicalRecords.mockReturnValue({
      data: [],
      isLoading: false,
    });
    render(<MedicalRecordsPage />, { wrapper });
    expect(screen.getByPlaceholderText('Rechercher un patient...')).toBeInTheDocument();
  });

  it('should display consultations', () => {
    mockUseMedicalRecords.mockReturnValue({
      data: [
        {
          id: '1',
          patientNom: 'Dupont Jean',
          date: '2024-01-15',
          diagnostic: 'Grippe saisonnière',
          notes: 'Repos 3 jours',
          medecin: 'Dr. Martin',
        },
      ],
      isLoading: false,
    });
    render(<MedicalRecordsPage />, { wrapper });
    expect(screen.getByText('Dupont Jean')).toBeInTheDocument();
    expect(screen.getByText('Grippe saisonnière')).toBeInTheDocument();
  });

  it('should render export PDF button', () => {
    mockUseMedicalRecords.mockReturnValue({
      data: [],
      isLoading: false,
    });
    render(<MedicalRecordsPage />, { wrapper });
    expect(screen.getByText('Exporter PDF')).toBeInTheDocument();
  });

  it('should render details and documents buttons', () => {
    mockUseMedicalRecords.mockReturnValue({
      data: [
        {
          id: '1',
          patientNom: 'Dupont Jean',
          date: '2024-01-15',
          diagnostic: 'Grippe saisonnière',
          notes: 'Repos 3 jours',
          medecin: 'Dr. Martin',
        },
      ],
      isLoading: false,
    });
    render(<MedicalRecordsPage />, { wrapper });
    const detailsButtons = screen.getAllByText('Détails');
    const documentsButtons = screen.getAllByText('Documents');
    expect(detailsButtons.length).toBeGreaterThan(0);
    expect(documentsButtons.length).toBeGreaterThan(0);
  });
});
