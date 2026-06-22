import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { billingApi } from '@/services/api.service';

export interface Invoice {
  id: string;
  patientId: string;
  patientNom: string;
  montant: number;
  date: string;
  statut: 'PAYEE' | 'EN_ATTENTE' | 'ANNULEE';
}

export function usePayments(patientId?: string) {
  return useQuery({
    queryKey: ['payments', patientId],
    queryFn: () => billingApi.getInvoices(patientId),
  });
}

export function useCreateInvoice() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: Partial<Invoice>) => billingApi.createInvoice(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['payments'] });
    },
  });
}

export function usePayInvoice() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: any }) => billingApi.payInvoice(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['payments'] });
    },
  });
}