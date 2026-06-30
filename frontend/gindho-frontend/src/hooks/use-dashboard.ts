import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { dashboardApi } from '@/services/api.service';
import { apiClient } from '@/lib/api-client';
import type { DashboardStats, Role } from '@/types';

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

const ROLE_DASHBOARD_ACCESS: Record<string, (keyof DashboardStats)[]> = {
  ADMIN: ['totalPatients', 'totalRendezVous', 'totalConsultations', 'totalHospitalisations', 'rendezVousAujourdhui'],
  SUPER_ADMIN: ['totalPatients', 'totalRendezVous', 'totalConsultations', 'totalHospitalisations', 'rendezVousAujourdhui'],
  MEDECIN: ['totalPatients', 'totalRendezVous', 'totalConsultations', 'rendezVousAujourdhui'],
  DOCTOR: ['totalPatients', 'totalRendezVous', 'totalConsultations', 'rendezVousAujourdhui'],
  NURSE: ['totalPatients', 'totalRendezVous', 'totalConsultations', 'totalHospitalisations'],
  RECEPTION: ['totalRendezVous', 'rendezVousAujourdhui'],
  RECEPTIONIST: ['totalRendezVous', 'rendezVousAujourdhui'],
  PHARMACIST: [],
  LABORATORY: [],
  LABORATORY_TECHNICIAN: [],
  ACCOUNTING: ['totalRendezVous'],
  ACCOUNTANT: ['totalRendezVous'],
  URGENCY: ['totalPatients'],
  HOSPITALIZATION_SERVICE: ['totalHospitalisations'],
  PATIENT: [],
  UTILISATEUR_SECONDAIRE: [],
};

export function useDashboardStats(userRole: Role) {
  return useQuery({
    queryKey: ['dashboard', 'stats', userRole],
    queryFn: async (): Promise<Partial<DashboardStats>> => {
      const response = await apiClient.get<DashboardStats>('/api/dashboard/stats');
      const allowedKeys = ROLE_DASHBOARD_ACCESS[userRole] ?? [];
      return Object.fromEntries(
        allowedKeys.map((key) => [key, response[key]]),
      ) as Partial<DashboardStats>;
    },
    enabled: (ROLE_DASHBOARD_ACCESS[userRole]?.length ?? 0) > 0,
  });
}
