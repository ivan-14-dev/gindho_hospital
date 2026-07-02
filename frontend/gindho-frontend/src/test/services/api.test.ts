import { describe, it, expect, vi, beforeEach } from 'vitest';
import { authApi, patientsApi, appointmentsApi } from '@/services/api.service';

// Mock fetch
(globalThis as any).fetch = vi.fn();

describe('API Services', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('authApi', () => {
    it('login calls correct endpoint', async () => {
      const mockResponse = { token: 'test-token', email: 'test@test.com', role: 'ADMIN', userId: 1 };
      (globalThis as any).fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => ({ data: mockResponse }),
      });

      const result = await authApi.login('test@test.com', 'password');
      expect(result.token).toBe('test-token');
      expect(result.user.email).toBe('test@test.com');
      expect((globalThis as any).fetch).toHaveBeenCalledWith(
        expect.stringContaining('/api/auth/login'),
        expect.objectContaining({
          method: 'POST',
          body: JSON.stringify({ email: 'test@test.com', password: 'password' }),
        })
      );
    });

    it('getCurrentUser calls correct endpoint', async () => {
      const mockUser = { userId: 1, email: 'test@test.com', role: 'ADMIN', authorities: ['ROLE_ADMIN'] };
      (globalThis as any).fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => ({ data: mockUser }),
      });

      const result = await authApi.getCurrentUser();
      expect(result.email).toBe('test@test.com');
      expect(result.role).toBe('ADMIN');
    });
  });

  describe('patientsApi', () => {
    it('getPatients calls correct endpoint', async () => {
      const mockPatients = {
        content: [{ id: '1', nom: 'Dupont', prenom: 'Jean' }],
        totalElements: 1,
      };
      (globalThis as any).fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockPatients,
      });

      const result = await patientsApi.getPatients({ search: 'Dupont' });
      expect(result).toEqual(mockPatients);
      expect((globalThis as any).fetch).toHaveBeenCalledWith(
        expect.stringContaining('/api/patients/'),
        expect.objectContaining({
          method: 'GET',
          headers: { 'Content-Type': 'application/json' },
        })
      );
    });

    it('createPatient calls correct endpoint', async () => {
      const newPatient = { id: '2', nom: 'Martin', prenom: 'Marie' };
      (globalThis as any).fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => newPatient,
      });

      const result = await patientsApi.createPatient({ nom: 'Martin', prenom: 'Marie' });
      expect(result).toEqual(newPatient);
    });
  });

  describe('appointmentsApi', () => {
    it('getAppointments calls correct endpoint', async () => {
      const mockAppointments = [
        { id: '1', patientId: '1', dateDebut: '2024-01-01', statut: 'PLANIFIE' },
      ];
      (globalThis as any).fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockAppointments,
      });

      const result = await appointmentsApi.getAppointments({ patientId: '1' });
      expect(result).toEqual(mockAppointments);
    });
  });
});