import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { imagingApi, telemedicineApi } from '@/services/api.service';

// ========== IMAGERIE ==========
export function useExamensByPatient(patientId: string) {
  return useQuery({
    queryKey: ['imaging', 'examens', patientId],
    queryFn: () => imagingApi.getExamensByPatient(patientId),
    enabled: !!patientId,
  });
}

export function useCreateExamen() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: any) => imagingApi.createExamen(data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['imaging', 'examens'] });
      if (variables.patientId) {
        queryClient.invalidateQueries({ queryKey: ['imaging', 'examens', variables.patientId] });
      }
    },
  });
}

// ========== TÉLÉCONSULTATION ==========
export function useCreateTeleconsultation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: any) => telemedicineApi.createTeleconsultation(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['telemedicine', 'consultations'] });
    },
  });
}

export function useUpdateStatutTeleconsultation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, statut }: { id: string; statut: string }) =>
      telemedicineApi.updateStatutTeleconsultation(id, statut),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['telemedicine', 'consultations'] });
    },
  });
}