import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { medicalRecordsExtendedApi } from '@/services/api.service';

export function useSignesVitaux(patientId: string) {
  return useQuery({
    queryKey: ['medical-records', 'signes-vitaux', patientId],
    queryFn: () => medicalRecordsExtendedApi.getSignesVitaux(patientId),
    enabled: !!patientId,
  });
}

export function useSignesVitauxByHospitalisation(hospitalisationId: string) {
  return useQuery({
    queryKey: ['medical-records', 'signes-vitaux', 'hospitalisation', hospitalisationId],
    queryFn: () => medicalRecordsExtendedApi.getSignesVitauxByHospitalisation(hospitalisationId),
    enabled: !!hospitalisationId,
  });
}

export function useCreateSignesVitaux() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: any) => medicalRecordsExtendedApi.createSignesVitaux(data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['medical-records', 'signes-vitaux'] });
      if (variables.patientId) {
        queryClient.invalidateQueries({ queryKey: ['medical-records', 'signes-vitaux', variables.patientId] });
      }
    },
  });
}

export function useDeleteSignesVitaux() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => medicalRecordsExtendedApi.deleteSignesVitaux(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['medical-records', 'signes-vitaux'] });
    },
  });
}

export function usePlansSoins(patientId: string) {
  return useQuery({
    queryKey: ['medical-records', 'plans-soins', patientId],
    queryFn: () => medicalRecordsExtendedApi.getPlansSoins(patientId),
    enabled: !!patientId,
  });
}

export function usePlansSoinsByHospitalisation(hospitalisationId: string) {
  return useQuery({
    queryKey: ['medical-records', 'plans-soins', 'hospitalisation', hospitalisationId],
    queryFn: () => medicalRecordsExtendedApi.getPlansSoinsByHospitalisation(hospitalisationId),
    enabled: !!hospitalisationId,
  });
}

export function useCreatePlanSoin() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: any) => medicalRecordsExtendedApi.createPlanSoin(data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['medical-records', 'plans-soins'] });
      if (variables.patientId) {
        queryClient.invalidateQueries({ queryKey: ['medical-records', 'plans-soins', variables.patientId] });
      }
    },
  });
}

export function useMarquerSoinRealise() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, notes }: { id: string; notes?: string }) =>
      medicalRecordsExtendedApi.marquerSoinRealise(id, notes),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['medical-records', 'plans-soins'] });
    },
  });
}

export function useDeletePlanSoin() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => medicalRecordsExtendedApi.deletePlanSoin(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['medical-records', 'plans-soins'] });
    },
  });
}

export function useAdministrationsMedicaments(patientId: string) {
  return useQuery({
    queryKey: ['medical-records', 'administrations-medicaments', patientId],
    queryFn: () => medicalRecordsExtendedApi.getAdministrationsMedicaments(patientId),
    enabled: !!patientId,
  });
}

export function useCreateAdministrationMedicament() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: any) => medicalRecordsExtendedApi.createAdministrationMedicament(data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['medical-records', 'administrations-medicaments'] });
      if (variables.patientId) {
        queryClient.invalidateQueries({ queryKey: ['medical-records', 'administrations-medicaments', variables.patientId] });
      }
    },
  });
}

export function useMarquerAdministre() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => medicalRecordsExtendedApi.marquerAdministre(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['medical-records', 'administrations-medicaments'] });
    },
  });
}