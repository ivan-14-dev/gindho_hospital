import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { hrApi } from '@/services/api.service';

export function usePersonnel() {
  return useQuery({
    queryKey: ['hr', 'personnel'],
    queryFn: () => hrApi.getPersonnel(),
  });
}

export function useCreatePersonnel() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: any) => hrApi.createPersonnel(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['hr', 'personnel'] });
    },
  });
}

export function useDeletePersonnel() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => hrApi.deletePersonnel(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['hr', 'personnel'] });
    },
  });
}

export function usePointerPresence() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (personnelId: string) => hrApi.pointerPresence(personnelId),
    onSuccess: (_, personnelId) => {
      queryClient.invalidateQueries({ queryKey: ['hr', 'presence', personnelId] });
    },
  });
}

export function usePresences(personnelId: string) {
  return useQuery({
    queryKey: ['hr', 'presence', personnelId],
    queryFn: () => hrApi.getPresences(personnelId),
    enabled: !!personnelId,
  });
}

export function useCreateConge() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: any) => hrApi.createConge(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['hr'] });
    },
  });
}

export function useValiderConge() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => hrApi.validerConge(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['hr'] });
    },
  });
}

export function useGardes(medecinId: string) {
  return useQuery({
    queryKey: ['hr', 'gardes', medecinId],
    queryFn: () => hrApi.getSchedules(medecinId, new Date().toISOString()),
    enabled: !!medecinId,
  });
}