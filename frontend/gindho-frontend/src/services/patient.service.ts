import { apiClient } from '@/lib/api-client';
import type { Patient } from '@/types';

function unwrap<T>(response: { data?: T } | T): T {
  if (response && typeof response === 'object' && 'data' in response) {
    return (response as { data: T }).data;
  }
  return response as T;
}

export const patientService = {
  async getPatients(params?: { search?: string; page?: number; size?: number }): Promise<{ content: Patient[]; totalElements: number }> {
    const query = new URLSearchParams();
    if (params?.search) query.set('search', params.search);
    if (params?.page) query.set('page', params.page.toString());
    if (params?.size) query.set('size', params.size.toString());
    const qs = query.toString();
    const response = await apiClient.get<{ data: { content: Patient[]; totalElements: number } }>(`/api/patients${qs ? `?${qs}` : ''}`);
    return unwrap(response);
  },

  async getPatient(id: string): Promise<Patient> {
    const response = await apiClient.get<{ data: Patient }>(`/api/patients/${id}`);
    return unwrap(response);
  },

  async createPatient(data: Partial<Patient>): Promise<Patient> {
    const response = await apiClient.post<{ data: Patient }>('/api/patients', data);
    return unwrap(response);
  },

  async updatePatient(id: string, data: Partial<Patient>): Promise<Patient> {
    const response = await apiClient.put<{ data: Patient }>(`/api/patients/${id}`, data);
    return unwrap(response);
  },

  async deletePatient(id: string): Promise<void> {
    await apiClient.delete(`/api/patients/${id}`);
  },
};