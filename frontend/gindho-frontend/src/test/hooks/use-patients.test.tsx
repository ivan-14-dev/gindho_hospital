import { describe, it, expect, vi } from 'vitest';
import { renderHook, waitFor } from '@testing-library/react';
import { usePatients, useCreatePatient, useUpdatePatient, useDeletePatient } from '@/hooks/use-patients';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { patientsApi } from '@/services/api.service';
import type { Patient } from '@/types';

// Mock du service API
vi.mock('@/services/api.service', () => ({
  patientsApi: {
    getPatients: vi.fn(),
    getPatient: vi.fn(),
    createPatient: vi.fn(),
    updatePatient: vi.fn(),
    deletePatient: vi.fn(),
  },
}));

const createWrapper = () => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
      mutations: { retry: false },
    },
  });
  return ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  );
};

describe('usePatients Hook', () => {
  it('fetches patients successfully', async () => {
    const mockPatients = {
      content: [{ id: '1', nom: 'Dupont', prenom: 'Jean', dateNaissance: '1990-01-01', sexe: 'M', createdAt: '2024-01-01', updatedAt: '2024-01-01' }] as Patient[],
      totalElements: 1,
    };
    vi.mocked(patientsApi.getPatients).mockResolvedValue(mockPatients);

    const { result } = renderHook(() => usePatients(), { wrapper: createWrapper() });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));
    expect(result.current.data).toEqual(mockPatients);
  });

  it('handles search params', async () => {
    const mockPatients = {
      content: [{ id: '1', nom: 'Dupont', prenom: 'Jean', dateNaissance: '1990-01-01', sexe: 'M', createdAt: '2024-01-01', updatedAt: '2024-01-01' }] as Patient[],
      totalElements: 1,
    };
    vi.mocked(patientsApi.getPatients).mockResolvedValue(mockPatients);

    const { result } = renderHook(() => usePatients({ search: 'Dupont' }), {
      wrapper: createWrapper(),
    });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));
    expect(patientsApi.getPatients).toHaveBeenCalledWith({ search: 'Dupont' });
  });
});

describe('useCreatePatient', () => {
  it('creates patient successfully', async () => {
    const newPatient = { id: '2', nom: 'Martin', prenom: 'Marie', dateNaissance: '1995-05-15', sexe: 'F', createdAt: '2024-01-01', updatedAt: '2024-01-01' } as Patient;
    vi.mocked(patientsApi.createPatient).mockResolvedValue(newPatient);

    const { result } = renderHook(() => useCreatePatient(), { wrapper: createWrapper() });

    await result.current.mutateAsync({ nom: 'Martin', prenom: 'Marie' });

    expect(patientsApi.createPatient).toHaveBeenCalledWith({ nom: 'Martin', prenom: 'Marie' });
  });
});

describe('useUpdatePatient', () => {
  it('updates patient successfully', async () => {
    const updatedPatient = { id: '1', nom: 'Dupont', prenom: 'Jean', dateNaissance: '1990-01-01', sexe: 'M', createdAt: '2024-01-01', updatedAt: '2024-01-01' } as Patient;
    vi.mocked(patientsApi.updatePatient).mockResolvedValue(updatedPatient);

    const { result } = renderHook(() => useUpdatePatient(), { wrapper: createWrapper() });

    await result.current.mutateAsync({ id: '1', data: { nom: 'Dupont' } });

    expect(patientsApi.updatePatient).toHaveBeenCalledWith('1', { nom: 'Dupont' });
  });
});

describe('useDeletePatient', () => {
  it('deletes patient successfully', async () => {
    vi.mocked(patientsApi.deletePatient).mockResolvedValue(undefined);

    const { result } = renderHook(() => useDeletePatient(), { wrapper: createWrapper() });

    await result.current.mutateAsync('1');

    expect(patientsApi.deletePatient).toHaveBeenCalledWith('1');
  });
});