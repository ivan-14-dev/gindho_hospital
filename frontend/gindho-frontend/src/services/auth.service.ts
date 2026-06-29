import { apiClient } from '@/lib/api-client';
import type { AuthResponse, User } from '@/types';

export const authService = {
  async login(email: string, password: string): Promise<AuthResponse> {
    const response = await apiClient.post<any>('/api/auth/login', { email, password });
    return {
      token: response.data.token,
      refreshToken: response.data.refreshToken,
      user: {
        id: response.data.userId,
        email: response.data.email,
        nom: response.data.nom || '',
        prenom: response.data.prenom || '',
        role: response.data.role || 'PATIENT',
        actif: response.data.actif || true,
        permissions: response.data.permissions || [],
        dateCreation: response.data.dateCreation,
        dateModification: response.data.dateModification
      }
    } as AuthResponse;
  },

  async register(data: { email: string; password: string; nom: string; prenom: string; role?: string }): Promise<AuthResponse> {
    const response = await apiClient.post<any>('/api/auth/register', data);
    return {
      token: response.data.token,
      refreshToken: response.data.refreshToken,
      user: {
        id: response.data.userId,
        email: response.data.email,
        nom: data.nom,
        prenom: data.prenom,
        role: data.role || 'PATIENT',
        actif: true,
        permissions: [],
        dateCreation: null,
        dateModification: null
      }
    } as AuthResponse;
  },

  async forgotPassword(email: string): Promise<void> {
    return apiClient.post('/api/auth/forgot-password', { email });
  },

  async resetPassword(email: string, code: string, newPassword: string): Promise<void> {
    return apiClient.post('/api/auth/reset-password', { email, code, newPassword });
  },

  async getCurrentUser(): Promise<User> {
    const response = await apiClient.get<any>('/api/auth/me');
    return {
      id: response.data.userId,
      email: response.data.email,
      nom: response.data.nom,
      prenom: response.data.prenom,
      role: response.data.role,
      actif: response.data.actif,
      permissions: response.data.permissions,
      dateCreation: response.data.dateCreation,
      dateModification: response.data.dateModification
    };
  },

  async getPermissions(): Promise<string[]> {
    const response = await apiClient.get<any>('/api/auth/me-authorities');
    return response.data.authorities || [];
  },
};
  },

  async getPermissions(): Promise<string[]> {
    const response = await apiClient.get<any>('/api/auth/me-authorities');
    return response.data.authorities || [];
  },
};