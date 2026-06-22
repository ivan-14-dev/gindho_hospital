import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { dashboardApi } from '@/services/api.service';

export function useAdminStats() {
  return useQuery({
    queryKey: ['dashboard', 'admin', 'stats'],
    queryFn: () => dashboardApi.getAdminStats(),
  });
}

export function useAdminMetricSeries(metric: string, from?: string, to?: string) {
  return useQuery({
    queryKey: ['dashboard', 'admin', 'metric', metric, from, to],
    queryFn: () => dashboardApi.getAdminMetricSeries(metric, from, to),
    enabled: !!metric,
  });
}

export function useQueryAdminStats() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: { metric: string; from?: string; to?: string }) =>
      dashboardApi.queryAdminStats(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
    },
  });
}

export function usePatientDashboard(patientId: string) {
  return useQuery({
    queryKey: ['dashboard', 'patient', patientId],
    queryFn: () => dashboardApi.getPatientDashboard(patientId),
    enabled: !!patientId,
  });
}

export function useMedecinDashboard(medecinId: string) {
  return useQuery({
    queryKey: ['dashboard', 'medecin', medecinId],
    queryFn: () => dashboardApi.getMedecinDashboard(medecinId),
    enabled: !!medecinId,
  });
}

export function usePatientsByMedecin(medecinId: string) {
  return useQuery({
    queryKey: ['dashboard', 'medecin', medecinId, 'patients'],
    queryFn: () => dashboardApi.getPatientsByMedecin(medecinId),
    enabled: !!medecinId,
  });
}