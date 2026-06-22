import { apiClient } from '@/lib/api-client';
import type { Patient } from '@/types';

export const patientService = {
  async getPatients(params?: { search?: string; page?: number; size?: number }): Promise<{ content: Patient[]; totalElements: number }> {
    const query = new URLSearchParams();
    if (params?.search) query.set('search', params.search);
    if (params?.page) query.set('page', params.page.toString());
    if (params?.size) query.set('size', params.size.toString());
    const qs = query.toString();
    return apiClient.get(`/api/patients${qs ? `?${qs}` : ''}`);
  },

  async getPatient(id: string): Promise<Patient> {
    return apiClient.get(`/api/patients/${id}`);
  },

  async createPatient(data: Partial<Patient>): Promise<Patient> {
    return apiClient.post('/api/patients', data);
  },

  async updatePatient(id: string, data: Partial<Patient>): Promise<Patient> {
    return apiClient.put(`/api/patients/${id}`, data);
  },

  async deletePatient(id: string): Promise<void> {
    return apiClient.delete(`/api/patients/${id}`);
  },
};