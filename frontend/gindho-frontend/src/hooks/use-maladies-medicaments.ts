import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { maladiesApi } from '@/services/api.service';

// ========== MALADIES ==========
export function useMaladies() {
  return useQuery({
    queryKey: ['pharmacy', 'maladies'],
    queryFn: () => maladiesApi.getMaladies(),
  });
}

export function useCreateMaladie() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: any) => maladiesApi.createMaladie(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['pharmacy', 'maladies'] });
    },
  });
}

export function useUpdateMaladie() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: any }) =>
      maladiesApi.updateMaladie(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['pharmacy', 'maladies'] });
    },
  });
}

export function useDeleteMaladie() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => maladiesApi.deleteMaladie(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['pharmacy', 'maladies'] });
    },
  });
}

export function useMaladiesByPatient(patientId: string) {
  return useQuery({
    queryKey: ['pharmacy', 'maladies', 'patient', patientId],
    queryFn: () => maladiesApi.getMaladiesByPatient(patientId),
    enabled: !!patientId,
  });
}

// ========== MÉDICAMENTS ==========
export function useMedicaments() {
  return useQuery({
    queryKey: ['pharmacy', 'medicaments'],
    queryFn: () => maladiesApi.getMedicaments(),
  });
}

export function useCreateMedicament() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: any) => maladiesApi.createMedicament(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['pharmacy', 'medicaments'] });
    },
  });
}

export function useUpdateMedicament() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: any }) =>
      maladiesApi.updateMedicament(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['pharmacy', 'medicaments'] });
    },
  });
}

export function useDeleteMedicament() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => maladiesApi.deleteMedicament(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['pharmacy', 'medicaments'] });
    },
  });
}