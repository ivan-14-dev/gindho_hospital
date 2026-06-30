export const authService = {
  async login(email: string, password: string): Promise<AuthResponse> {
    const response = await apiClient.post<{ data: AuthResponse }>('/api/auth/login', { email, password });
    return response.data;
  },

  async register(data: { email: string; password: string; nom: string; prenom: string; role?: string }): Promise<AuthResponse> {
    const response = await apiClient.post<{ data: AuthResponse }>('/api/auth/register', data);
    return response.data;
  },

  async forgotPassword(email: string): Promise<void> {
    return apiClient.post('/api/auth/forgot-password', { email });
  },

  async resetPassword(email: string, code: string, newPassword: string): Promise<void> {
    return apiClient.post('/api/auth/reset-password', { email, code, newPassword });
  },

  async getCurrentUser(): Promise<User> {
    const response = await apiClient.get<{ data: User }>('/api/auth/me');
    return response.data;
  },

  async getPermissions(): Promise<string[]> {
    const response = await apiClient.get<{ data: string[] }>('/api/auth/me-authorities');
    return response.data;
  },
};