import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { pharmacyApi } from '@/services/api.service';

export interface Prescription {
  id: string;
  patientId: string;
  patientNom: string;
  medicaments: string;
  date: string;
  statut: 'ACTIVE' | 'TERMINEE';
}

export interface Medication {
  id: string;
  nom: string;
  dosage: string;
  stock: number;
}

export function usePharmacy(patientId?: string) {
  return useQuery({
    queryKey: ['pharmacy', patientId],
    queryFn: () => pharmacyApi.getPrescriptions(patientId || ''),
    enabled: !!patientId,
  });
}

export function useCreatePrescription() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: Partial<Prescription>) => pharmacyApi.createPrescription(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['pharmacy'] });
    },
  });
}

export function useMedications() {
  return useQuery({
    queryKey: ['medications'],
    queryFn: () => pharmacyApi.getMedications(),
  });
}