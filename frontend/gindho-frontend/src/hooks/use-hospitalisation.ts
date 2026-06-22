import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { hospitalisationApi } from '@/services/api.service';

export function useChambres() {
  return useQuery({
    queryKey: ['hospitalisation', 'chambres'],
    queryFn: () => hospitalisationApi.getChambres(),
  });
}

export function useCreateChambre() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: any) => hospitalisationApi.createChambre(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['hospitalisation', 'chambres'] });
    },
  });
}

export function useUpdateChambre() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: any }) =>
      hospitalisationApi.updateChambre(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['hospitalisation', 'chambres'] });
    },
  });
}

export function useDeleteChambre() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => hospitalisationApi.deleteChambre(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['hospitalisation', 'chambres'] });
    },
  });
}

export function useLits() {
  return useQuery({
    queryKey: ['hospitalisation', 'lits'],
    queryFn: () => hospitalisationApi.getLits(),
  });
}

export function useLitsByChambre(chambreId: string) {
  return useQuery({
    queryKey: ['hospitalisation', 'lits', 'chambre', chambreId],
    queryFn: () => hospitalisationApi.getLitsByChambre(chambreId),
    enabled: !!chambreId,
  });
}

export function useCreateLit() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: any) => hospitalisationApi.createLit(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['hospitalisation', 'lits'] });
    },
  });
}

export function useUpdateLit() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: any }) =>
      hospitalisationApi.updateLit(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['hospitalisation', 'lits'] });
    },
  });
}

export function useDeleteLit() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => hospitalisationApi.deleteLit(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['hospitalisation', 'lits'] });
    },
  });
}

export function useAdmissions() {
  return useQuery({
    queryKey: ['hospitalisation', 'admissions'],
    queryFn: () => hospitalisationApi.getAdmissions(),
  });
}

export function useCreateAdmission() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: any) => hospitalisationApi.createAdmission(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['hospitalisation', 'admissions'] });
    },
  });
}

export function useAdmissionsEnCours() {
  return useQuery({
    queryKey: ['hospitalisation', 'admissions', 'en-cours'],
    queryFn: () => hospitalisationApi.getAdmissionsEnCours(),
  });
}

export function useDischargePatient() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => hospitalisationApi.dischargePatient(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['hospitalisation', 'admissions'] });
      queryClient.invalidateQueries({ queryKey: ['hospitalisation', 'admissions', 'en-cours'] });
    },
  });
}