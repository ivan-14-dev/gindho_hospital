import { apiClient } from '@/lib/api-client';
import type { AuthResponse, User } from '@/types';

function buildUserFromIdentity(data: { userId: number; email: string; role: string }): User {
  return {
    id: String(data.userId),
    email: data.email,
    nom: '',
    prenom: '',
    role: data.role as User['role'],
    status: 'active',
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
    permissions: [],
    actif: true,
  };
}

export const authService = {
  async login(email: string, password: string): Promise<AuthResponse> {
    const response = await apiClient.post<{ data: { token: string; email: string; role: string; userId: number } }>('/api/auth/login', { email, password });
    const data = response.data;
    const token = data.token;
    const user = buildUserFromIdentity(data);
    return { accessToken: token, token, user };
  },

  async register(data: {
    email: string;
    password: string;
    nom: string;
    prenom: string;
    role?: string;
  }): Promise<AuthResponse> {
    const response = await apiClient.post<{ data: { token: string; email: string; role: string; userId: number } }>('/api/auth/register', data);
    const identity = response.data;
    const token = identity.token;
    const user = buildUserFromIdentity(identity);
    return { accessToken: token, token, user };
  },

  async forgotPassword(email: string): Promise<void> {
    return apiClient.post('/api/auth/forgot-password', { email });
  },

  async resetPassword(email: string, code: string, newPassword: string): Promise<void> {
    return apiClient.post('/api/auth/reset-password', { email, code, newPassword });
  },

  async getCurrentUser(): Promise<User> {
    const response = await apiClient.get<{ data: { userId: number; email: string; role: string; authorities: string[] } }>('/api/auth/me');
    const data = response.data;
    return buildUserFromIdentity({ userId: data.userId, email: data.email, role: data.role });
  },

  async getPermissions(): Promise<string[]> {
    const response = await apiClient.get<{ data: { authorities: string[] } }>('/api/auth/me-authorities');
    return response.data.authorities;
  },
};