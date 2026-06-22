import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { inventoryApi } from '@/services/api.service';

export function useStocks() {
  return useQuery({
    queryKey: ['inventory', 'stocks'],
    queryFn: () => inventoryApi.getStocks(),
  });
}

export function useCreateStock() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: any) => inventoryApi.createStock(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['inventory', 'stocks'] });
    },
  });
}

export function useStockAlertesRupture() {
  return useQuery({
    queryKey: ['inventory', 'stocks', 'alertes', 'rupture'],
    queryFn: () => inventoryApi.getStockAlertesRupture(),
  });
}

export function useStockAlertesPeremption() {
  return useQuery({
    queryKey: ['inventory', 'stocks', 'alertes', 'peremption'],
    queryFn: () => inventoryApi.getStockAlertesPeremption(),
  });
}

export function usePharmacieStock() {
  return useQuery({
    queryKey: ['pharmacy', 'stock'],
    queryFn: () => inventoryApi.getPharmacieStock(),
  });
}

export function useCreatePharmacieStock() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: any) => inventoryApi.createPharmacieStock(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['pharmacy', 'stock'] });
    },
  });
}

export function useUpdatePharmacieQuantite() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, quantite }: { id: string; quantite: number }) =>
      inventoryApi.updatePharmacieQuantite(id, quantite),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['pharmacy', 'stock'] });
    },
  });
}

export function useSearchPharmacie(medicament: string) {
  return useQuery({
    queryKey: ['pharmacy', 'stock', 'search', medicament],
    queryFn: () => inventoryApi.searchPharmacie(medicament),
    enabled: !!medicament && medicament.length > 0,
  });
}