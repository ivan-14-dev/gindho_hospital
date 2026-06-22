import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { eventsApi, surgeryApi } from '@/services/api.service';

// ========== EVENTS ==========
export function useEvenements() {
  return useQuery({
    queryKey: ['events', 'evenements'],
    queryFn: () => eventsApi.getEvenements(),
  });
}

export function useCreateEvenement() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: any) => eventsApi.createEvenement(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['events', 'evenements'] });
    },
  });
}

export function useValiderEvenement() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => eventsApi.validerEvenement(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['events', 'evenements'] });
    },
  });
}

// ========== ROUNDS ==========
export function useCreateRonde() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: any) => eventsApi.createRonde(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['events', 'rondes'] });
    },
  });
}

export function useValiderRonde() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, compteRendu }: { id: string; compteRendu?: string }) =>
      eventsApi.validerRonde(id, compteRendu),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['events', 'rondes'] });
    },
  });
}

// ========== SURGERY ==========
export function useCreateProgrammeOperatoire() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: any) => surgeryApi.createProgrammeOperatoire(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['surgery', 'bloc'] });
    },
  });
}

export function useUpdateStatutProgramme() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, statut }: { id: string; statut: string }) =>
      surgeryApi.updateStatutProgramme(id, statut),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['surgery', 'bloc'] });
    },
  });
}