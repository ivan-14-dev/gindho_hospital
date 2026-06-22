import { apiClient } from '@/lib/api-client';
import type { AuthResponse, User } from '@/types';

export const authService = {
  async login(email: string, password: string): Promise<AuthResponse> {
    return apiClient.post<AuthResponse>('/api/auth/login', { email, password });
  },

  async register(data: { email: string; password: string; nom: string; prenom: string; role?: string }): Promise<AuthResponse> {
    return apiClient.post<AuthResponse>('/api/auth/register', data);
  },

  async forgotPassword(email: string): Promise<void> {
    return apiClient.post('/api/auth/forgot-password', { email });
  },

  async resetPassword(email: string, code: string, newPassword: string): Promise<void> {
    return apiClient.post('/api/auth/reset-password', { email, code, newPassword });
  },

  async getCurrentUser(): Promise<User> {
    return apiClient.get<User>('/api/auth/me');
  },

  async getPermissions(): Promise<string[]> {
    return apiClient.get<string[]>('/api/auth/me-authorities');
  },
};