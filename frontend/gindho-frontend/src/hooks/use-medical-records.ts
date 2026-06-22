import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { medicalRecordsApi } from '@/services/api.service';

export interface Consultation {
  id: string;
  patientId: string;
  patientNom: string;
  date: string;
  diagnostic: string;
  notes: string;
  medecin: string;
}

export function useMedicalRecords(patientId?: string) {
  return useQuery({
    queryKey: ['medical-records', patientId],
    queryFn: () => medicalRecordsApi.getConsultations(patientId || ''),
    enabled: !!patientId,
  });
}

export function useCreateConsultation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: Partial<Consultation>) => medicalRecordsApi.createConsultation(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['medical-records'] });
    },
  });
}

export function useAnalyses(patientId?: string) {
  return useQuery({
    queryKey: ['analyses', patientId],
    queryFn: () => medicalRecordsApi.getAnalyses(patientId || ''),
    enabled: !!patientId,
  });
}