import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { patientsApi } from '@/services/api.service';
import type { Patient } from '@/types';

/**
 * Hook pour récupérer la liste des patients avec recherche et pagination
 * @param params - Paramètres de recherche et pagination
 * @returns Données patients + fonctions de création/modification
 */
export function usePatients(params?: { search?: string; page?: number; size?: number }) {
  const createMutation = useCreatePatient();
  const updateMutation = useUpdatePatient();
  
  return {
    ...useQuery({
      queryKey: ['patients', params],
      queryFn: () => patientsApi.getPatients(params),
    }),
    createPatient: createMutation.mutateAsync,
    updatePatient: updateMutation.mutateAsync,
  };
}

export function usePatient(id: string) {
  return useQuery({
    queryKey: ['patients', id],
    queryFn: () => patientsApi.getPatient(id),
    enabled: !!id,
  });
}

export function useCreatePatient() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: Partial<Patient>) => patientsApi.createPatient(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['patients'] });
    },
  });
}

export function useUpdatePatient() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: Partial<Patient> }) =>
      patientsApi.updatePatient(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['patients'] });
    },
  });
}

export function useDeletePatient() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => patientsApi.deletePatient(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['patients'] });
    },
  });
}