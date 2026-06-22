import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { appointmentsApi } from '@/services/api.service';

export interface Appointment {
  id: string;
  patientId: string;
  patientNom?: string;
  medecinId: string;
  dateDebut: string;
  dateFin: string;
  statut: 'PLANIFIE' | 'CONFIRME' | 'ANNULE' | 'TERMINE';
  motif?: string;
  notes?: string;
  createdAt: string;
}

export function useAppointments(params?: { patientId?: string; medecinId?: string }) {
  const createMutation = useCreateAppointment();
  const updateMutation = useUpdateAppointment();
  
  return {
    ...useQuery({
      queryKey: ['appointments', params],
      queryFn: () => appointmentsApi.getAppointments(params),
    }),
    createAppointment: createMutation.mutateAsync,
    updateAppointment: updateMutation.mutateAsync,
  };
}

export function useAppointment(id: string) {
  return useQuery({
    queryKey: ['appointments', id],
    queryFn: () => appointmentsApi.getAppointments({ patientId: id }),
    enabled: !!id,
  });
}

export function useCreateAppointment() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: Partial<Appointment>) => appointmentsApi.createAppointment(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['appointments'] });
    },
  });
}

export function useUpdateAppointment() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: Partial<Appointment> }) =>
      appointmentsApi.updateAppointment(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['appointments'] });
    },
  });
}

export function useDeleteAppointment() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => appointmentsApi.deleteAppointment(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['appointments'] });
    },
  });
}