import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { assetsApi } from '@/services/api.service';

// ========== EQUIPEMENTS ==========
export function useEquipements() {
  return useQuery({
    queryKey: ['assets', 'equipements'],
    queryFn: () => assetsApi.getEquipements(),
  });
}

export function useCreateEquipement() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: any) => assetsApi.createEquipement(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['assets', 'equipements'] });
    },
  });
}

// ========== AMBULANCES ==========
export function useAmbulances() {
  return useQuery({
    queryKey: ['ambulance', 'ambulances'],
    queryFn: () => assetsApi.getAmbulances(),
  });
}

export function useCreateAmbulance() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: any) => assetsApi.createAmbulance(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ambulance', 'ambulances'] });
    },
  });
}

export function useUpdateAmbulancePosition() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, lat, lng }: { id: string; lat: number; lng: number }) =>
      assetsApi.updateAmbulancePosition(id, lat, lng),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ambulance', 'ambulances'] });
    },
  });
}