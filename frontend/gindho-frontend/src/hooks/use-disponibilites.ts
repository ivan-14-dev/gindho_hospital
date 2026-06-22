import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { disponibilitesApi } from '@/services/api.service';

export function useDisponibilites(medecinId: string) {
  return useQuery({
    queryKey: ['appointments', 'disponibilites', medecinId],
    queryFn: () => disponibilitesApi.getDisponibilites(medecinId),
    enabled: !!medecinId,
  });
}

export function useCreateDisponibilite() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: any) => disponibilitesApi.createDisponibilite(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['appointments', 'disponibilites'] });
    },
  });
}

export function useUpdateDisponibilite() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: any }) =>
      disponibilitesApi.updateDisponibilite(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['appointments', 'disponibilites'] });
    },
  });
}

export function useDeleteDisponibilite() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => disponibilitesApi.deleteDisponibilite(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['appointments', 'disponibilites'] });
    },
  });
}