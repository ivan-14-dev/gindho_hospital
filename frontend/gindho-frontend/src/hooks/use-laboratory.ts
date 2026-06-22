import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { laboratoryApi } from '@/services/api.service';

export interface Analyse {
  id: string;
  patientId: string;
  patientNom: string;
  type: string;
  date: string;
  statut: 'EN_COURS' | 'TERMINE';
  resultat?: string;
}

export function useLaboratory(params?: { patientId?: string; statut?: string }) {
  return useQuery({
    queryKey: ['laboratory', params],
    queryFn: () => laboratoryApi.getAnalyses(params),
  });
}

export function useCreateAnalyse() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: Partial<Analyse>) => laboratoryApi.createAnalyse(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['laboratory'] });
    },
  });
}

export function useUpdateAnalyseResult() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, resultat }: { id: string; resultat: string }) =>
      laboratoryApi.updateAnalyseResult(id, resultat),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['laboratory'] });
    },
  });
}