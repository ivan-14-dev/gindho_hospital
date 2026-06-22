import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { assurancesApi } from '@/services/api.service';

export function useAssurancesByPatient(patientId: string) {
  return useQuery({
    queryKey: ['insurance', 'assurances', 'patient', patientId],
    queryFn: () => assurancesApi.getAssurancesByPatient(patientId),
    enabled: !!patientId,
  });
}

export function useAssurancesActivesByPatient(patientId: string) {
  return useQuery({
    queryKey: ['insurance', 'assurances', 'patient', patientId, 'actives'],
    queryFn: () => assurancesApi.getAssurancesActivesByPatient(patientId),
    enabled: !!patientId,
  });
}

export function useAssurance(id: string) {
  return useQuery({
    queryKey: ['insurance', 'assurances', id],
    queryFn: () => assurancesApi.getAssurance(id),
    enabled: !!id,
  });
}

export function useCreateAssurance() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: any) => assurancesApi.createAssurance(data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['insurance', 'assurances'] });
      if (variables.patientId) {
        queryClient.invalidateQueries({ queryKey: ['insurance', 'assurances', 'patient', variables.patientId] });
      }
    },
  });
}

export function useUpdateAssurance() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: any }) =>
      assurancesApi.updateAssurance(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['insurance', 'assurances'] });
    },
  });
}

export function useDeleteAssurance() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => assurancesApi.deleteAssurance(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['insurance', 'assurances'] });
    },
  });
}

export function useSearchAssurances(compagnie: string) {
  return useQuery({
    queryKey: ['insurance', 'assurances', 'search', compagnie],
    queryFn: () => assurancesApi.searchAssurances(compagnie),
    enabled: !!compagnie && compagnie.length > 0,
  });
}