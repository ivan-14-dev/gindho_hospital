import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { qualityApi } from '@/services/api.service';

export function useAudits() {
  return useQuery({
    queryKey: ['quality', 'audits'],
    queryFn: () => qualityApi.getAudits(),
  });
}

export function useCreateAudit() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: any) => qualityApi.createAudit(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['quality', 'audits'] });
    },
  });
}

export function useIncidentsNonResolus() {
  return useQuery({
    queryKey: ['quality', 'incidents', 'non-resolus'],
    queryFn: () => qualityApi.getIncidentsNonResolus(),
  });
}

export function useCreateIncident() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: any) => qualityApi.createIncident(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['quality', 'incidents'] });
    },
  });
}

export function useResoudreIncident() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, action }: { id: string; action?: string }) =>
      qualityApi.resoudreIncident(id, action),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['quality', 'incidents'] });
    },
  });
}