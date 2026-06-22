import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { revenusApi } from '@/services/api.service';

export function useRevenus(params?: { page?: number; size?: number }) {
  return useQuery({
    queryKey: ['billing', 'revenus', params],
    queryFn: () => revenusApi.getRevenus(params),
  });
}

export function useRevenusByPatient(patientId: string, params?: { page?: number; size?: number }) {
  return useQuery({
    queryKey: ['billing', 'revenus', 'patient', patientId, params],
    queryFn: () => revenusApi.getRevenusByPatient(patientId, params),
    enabled: !!patientId,
  });
}

export function useRevenusByMedecin(medecinId: string, params?: { page?: number; size?: number }) {
  return useQuery({
    queryKey: ['billing', 'revenus', 'medecin', medecinId, params],
    queryFn: () => revenusApi.getRevenusByMedecin(medecinId, params),
    enabled: !!medecinId,
  });
}

export function useTotalRevenus(start: string, end: string) {
  return useQuery({
    queryKey: ['billing', 'revenus', 'total', start, end],
    queryFn: () => revenusApi.getTotalRevenus(start, end),
    enabled: !!start && !!end,
  });
}

export function useRevenu(id: string) {
  return useQuery({
    queryKey: ['billing', 'revenus', id],
    queryFn: () => revenusApi.getRevenu(id),
    enabled: !!id,
  });
}

export function useCreateRevenu() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: any) => revenusApi.createRevenu(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['billing', 'revenus'] });
    },
  });
}

export function useUpdateRevenu() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: any }) =>
      revenusApi.updateRevenu(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['billing', 'revenus'] });
    },
  });
}

export function useDeleteRevenu() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => revenusApi.deleteRevenu(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['billing', 'revenus'] });
    },
  });
}