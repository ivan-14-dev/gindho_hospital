import { apiClient } from '@/lib/api-client';
import { buildApiUrl } from '@/lib/config';
import type { Patient, AuthResponse, User } from '@/types';

function unwrap<T>(response: { data?: T } | T): T {
  if (response && typeof response === 'object' && 'data' in response) {
    return (response as { data: T }).data;
  }
  return response as T;
}

function buildUserFromIdentity(data: { userId: number; email: string; role: string }): User {
  const [prenom = 'Utilisateur', nom = ''] = data.email.split('@')[0].split('.');
  return {
    id: String(data.userId),
    email: data.email,
    nom,
    prenom,
    role: data.role as User['role'],
    status: 'active',
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
    permissions: [],
    actif: true,
  };
}

function buildAuthResponse(data: { token: string; email: string; role: string; userId: number }): AuthResponse {
  return {
    accessToken: data.token,
    token: data.token,
    user: buildUserFromIdentity(data),
  };
}

/**
 * Service API générique pour communiquer avec les microservices Kubernetes
 * via Kong API Gateway
 */

// ========== AUTHENTICATION ==========
export const authApi = {
  async login(email: string, password: string): Promise<AuthResponse> {
    const response = await apiClient.post<{ data: { token: string; email: string; role: string; userId: number } }>(buildApiUrl('AUTH', '/login'), { email, password });
    const data = unwrap(response);
    return buildAuthResponse(data);
  },

  async register(data: {
    email: string;
    password: string;
    nom: string;
    prenom: string;
    role?: string;
  }): Promise<AuthResponse> {
    const response = await apiClient.post<{ data: { token: string; email: string; role: string; userId: number } }>(buildApiUrl('AUTH', '/register'), data);
    const identity = unwrap(response);
    return buildAuthResponse(identity);
  },

  async forgotPassword(email: string): Promise<void> {
    await apiClient.post(buildApiUrl('AUTH', '/forgot-password'), { email });
  },

  async resetPassword(email: string, code: string, newPassword: string): Promise<void> {
    await apiClient.post(buildApiUrl('AUTH', '/reset-password'), {
      email,
      code,
      newPassword,
    });
  },

  async getCurrentUser(): Promise<User> {
    const response = await apiClient.get<{ data: { userId: number; email: string; role: string; authorities: string[] } }>(buildApiUrl('AUTH', '/me'));
    const data = unwrap(response);
    return buildUserFromIdentity(data);
  },

  async getPermissions(): Promise<string[]> {
    const response = await apiClient.get<{ data: { authorities: string[] } }>(buildApiUrl('AUTH', '/me-authorities'));
    const data = unwrap(response);
    return data.authorities;
  },
};

// ========== PATIENTS ==========
export const patientsApi = {
  async getPatients(params?: {
    search?: string;
    page?: number;
    size?: number;
  }): Promise<{ content: Patient[]; totalElements: number }> {
    const query = new URLSearchParams();
    if (params?.search) query.set('search', params.search);
    if (params?.page) query.set('page', params.page.toString());
    if (params?.size) query.set('size', params.size.toString());
    const qs = query.toString();
    const response = await apiClient.get<{ data: { content: Patient[]; totalElements: number } }>(buildApiUrl('PATIENTS', `/${qs ? `?${qs}` : ''}`));
    return unwrap(response);
  },

  async getPatient(id: string): Promise<Patient> {
    const response = await apiClient.get<{ data: Patient }>(buildApiUrl('PATIENTS', `/${id}`));
    return unwrap(response);
  },

  async createPatient(data: Partial<Patient>): Promise<Patient> {
    const response = await apiClient.post<{ data: Patient }>(buildApiUrl('PATIENTS', '/'), data);
    return unwrap(response);
  },

  async updatePatient(id: string, data: Partial<Patient>): Promise<Patient> {
    const response = await apiClient.put<{ data: Patient }>(buildApiUrl('PATIENTS', `/${id}`), data);
    return unwrap(response);
  },

  async deletePatient(id: string): Promise<void> {
    await apiClient.delete(buildApiUrl('PATIENTS', `/${id}`));
  },
};

// ========== APPOINTMENTS ==========
export const appointmentsApi = {
  async getAppointments(params?: {
    patientId?: string;
    medecinId?: string;
    dateDebut?: string;
    dateFin?: string;
    statut?: string;
  }): Promise<any[]> {
    const query = new URLSearchParams();
    Object.entries(params || {}).forEach(([key, value]) => {
      if (value) query.set(key, value);
    });
    const qs = query.toString();
    const response = await apiClient.get<{ data: any[] }>(buildApiUrl('APPOINTMENTS', `/${qs ? `?${qs}` : ''}`));
    return unwrap(response);
  },

  async createAppointment(data: any): Promise<any> {
    const response = await apiClient.post<{ data: any }>(buildApiUrl('APPOINTMENTS', '/'), data);
    return unwrap(response);
  },

  async updateAppointment(id: string, data: any): Promise<any> {
    const response = await apiClient.put<{ data: any }>(buildApiUrl('APPOINTMENTS', `/${id}`), data);
    return unwrap(response);
  },

  async deleteAppointment(id: string): Promise<void> {
    await apiClient.delete(buildApiUrl('APPOINTMENTS', `/${id}`));
  },
};

// ========== MEDICAL RECORDS ==========
export const medicalRecordsApi = {
  async getConsultations(patientId: string): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>(`/api/consultations?patientId=${patientId}`);
    return unwrap(response);
  },

  async createConsultation(data: any): Promise<any> {
    const response = await apiClient.post<{ data: any }>('/api/consultations', data);
    return unwrap(response);
  },

  async getAnalyses(patientId: string): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>(`/api/analyses?patientId=${patientId}`);
    return unwrap(response);
  },
};

// ========== LABORATORY ==========
export const laboratoryApi = {
  async getAnalyses(params?: { patientId?: string; statut?: string }): Promise<any[]> {
    const query = new URLSearchParams();
    Object.entries(params || {}).forEach(([key, value]) => {
      if (value) query.set(key, value);
    });
    const qs = query.toString();
    const response = await apiClient.get<{ data: any[] }>(`/api/analyses${qs ? `?${qs}` : ''}`);
    return unwrap(response);
  },

  async createAnalyse(data: any): Promise<any> {
    const response = await apiClient.post<{ data: any }>('/api/analyses', data);
    return unwrap(response);
  },

  async updateAnalyseResult(id: string, resultat: string): Promise<any> {
    const response = await apiClient.put<{ data: any }>(`/api/analyses/${id}/result`, { result: resultat });
    return unwrap(response);
  },
};

// ========== PHARMACY ==========
export const pharmacyApi = {
  async getPrescriptions(patientId: string): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>(`/api/prescriptions?patientId=${patientId}`);
    return unwrap(response);
  },

  async createPrescription(data: any): Promise<any> {
    const response = await apiClient.post<{ data: any }>('/api/prescriptions', data);
    return unwrap(response);
  },

  async getMedications(): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>('/api/pharmacy/medicaments');
    return unwrap(response);
  },
};

// ========== BILLING ==========
export const billingApi = {
  async getInvoices(patientId?: string): Promise<any[]> {
    const qs = patientId ? `?patientId=${patientId}` : '';
    const response = await apiClient.get<{ data: any[] }>(`/api/invoices${qs}`);
    return unwrap(response);
  },

  async createInvoice(data: any): Promise<any> {
    const response = await apiClient.post<{ data: any }>('/api/invoices', data);
    return unwrap(response);
  },

  async payInvoice(id: string, data: any): Promise<any> {
    const response = await apiClient.post<{ data: any }>(`/api/invoices/${id}/pay`, data);
    return unwrap(response);
  },
};

// ========== NOTIFICATIONS ==========
export const notificationsApi = {
  async getNotifications(): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>('/api/notifications/user/me');
    return unwrap(response);
  },

  async markAsRead(id: string): Promise<void> {
    await apiClient.put(`/api/notifications/${id}/read`, {});
  },
};

// ========== EMERGENCY ==========
export const emergencyApi = {
  async getUrgencies(): Promise<any[]> {
    throw new Error('Endpoint GET /api/emergency/ non supporté côté backend. Utiliser POST /api/emergency/triage.');
  },

  async createUrgency(data: any): Promise<any> {
    const response = await apiClient.post<{ data: any }>('/api/emergency/triage', data);
    return unwrap(response);
  },
};

// ========== HR ==========
export const hrApi = {
  async getMedecins(params?: { search?: string; page?: number; size?: number }): Promise<any> {
    const query = new URLSearchParams();
    if (params?.search) query.set('search', params.search);
    if (params?.page) query.set('page', params.page.toString());
    if (params?.size) query.set('size', params.size.toString());
    const qs = query.toString();
    const response = await apiClient.get<{ data: any }>(`/api/hr/doctors${qs ? `?${qs}` : ''}`);
    return unwrap(response);
  },

  async getMedecin(id: string): Promise<any> {
    const response = await apiClient.get<{ data: any }>(`/api/hr/doctors/${id}`);
    return unwrap(response);
  },

  async getMedecinByUserId(userId: string): Promise<any> {
    const response = await apiClient.get<{ data: any }>(`/api/hr/doctors/by-user/${userId}`);
    return unwrap(response);
  },

  async createMedecin(data: any): Promise<any> {
    const response = await apiClient.post<{ data: any }>('/api/hr/employees', data);
    return unwrap(response);
  },

  async updateMedecin(id: string, data: any): Promise<any> {
    const response = await apiClient.put<{ data: any }>(`/api/hr/employees/${id}`, data);
    return unwrap(response);
  },

  async deleteMedecin(id: string): Promise<void> {
    await apiClient.delete(`/api/hr/employees/${id}`);
  },

  async getSchedules(medecinId: string, date: string): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>(`/api/schedules?medecinId=${medecinId}&date=${date}`);
    return unwrap(response);
  },

  async getPersonnel(): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>('/api/hr/employees');
    return unwrap(response);
  },

  async createPersonnel(data: any): Promise<any> {
    const response = await apiClient.post<{ data: any }>('/api/hr/employees', data);
    return unwrap(response);
  },

  async deletePersonnel(id: string): Promise<void> {
    await apiClient.delete(`/api/hr/employees/${id}`);
  },

  async pointerPresence(personnelId: string): Promise<any> {
    const response = await apiClient.post<{ data: any }>(`/api/hr/presence/pointer/${personnelId}`, {});
    return unwrap(response);
  },

  async getPresences(personnelId: string): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>(`/api/hr/presence/${personnelId}`);
    return unwrap(response);
  },

  async createConge(data: any): Promise<any> {
    const response = await apiClient.post<{ data: any }>('/api/hr/conges', data);
    return unwrap(response);
  },

  async validerConge(id: string): Promise<any> {
    const response = await apiClient.post<{ data: any }>(`/api/hr/conges/${id}/valider`, {});
    return unwrap(response);
  },
};

// ========== DASHBOARD & ANALYTICS ==========
export const dashboardApi = {
  async getAdminStats(): Promise<any> {
    const response = await apiClient.get<{ data: any }>('/api/dashboard/admin/stats');
    return unwrap(response);
  },

  async getAdminMetricSeries(metric: string, from?: string, to?: string): Promise<any> {
    const params = new URLSearchParams();
    if (from) params.set('from', from);
    if (to) params.set('to', to);
    const qs = params.toString();
    const response = await apiClient.get<{ data: any }>(`/api/dashboard/admin/stats/${metric}${qs ? `?${qs}` : ''}`);
    return unwrap(response);
  },

  async queryAdminStats(data: { metric: string; from?: string; to?: string }): Promise<any> {
    const response = await apiClient.post<{ data: any }>('/api/dashboard/admin/stats/query', data);
    return unwrap(response);
  },

  async getPatientDashboard(patientId: string): Promise<any> {
    const response = await apiClient.get<{ data: any }>(`/api/dashboard/patient/${patientId}`);
    return unwrap(response);
  },

  async getMedecinDashboard(medecinId: string): Promise<any> {
    const response = await apiClient.get<{ data: any }>(`/api/dashboard/medecin/${medecinId}`);
    return unwrap(response);
  },

  async getPatientsByMedecin(medecinId: string): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>(`/api/dashboard/medecin/${medecinId}/patients`);
    return unwrap(response);
  },
};

// ========== MEDICAL RECORDS (Soins infirmiers) ==========
export const medicalRecordsExtendedApi = {
  async getSignesVitaux(patientId: string): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>(`/api/signes-vitaux/patient/${patientId}`);
    return unwrap(response);
  },

  async getSignesVitauxByHospitalisation(hospitalisationId: string): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>(`/api/signes-vitaux/hospitalisation/${hospitalisationId}`);
    return unwrap(response);
  },

  async createSignesVitaux(data: any): Promise<any> {
    const response = await apiClient.post<{ data: any }>('/api/signes-vitaux', data);
    return unwrap(response);
  },

  async deleteSignesVitaux(id: string): Promise<void> {
    await apiClient.delete(`/api/signes-vitaux/${id}`);
  },

  async getPlansSoins(patientId: string): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>(`/api/plans-soins/patient/${patientId}`);
    return unwrap(response);
  },

  async getPlansSoinsByHospitalisation(hospitalisationId: string): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>(`/api/plans-soins/hospitalisation/${hospitalisationId}`);
    return unwrap(response);
  },

  async createPlanSoin(data: any): Promise<any> {
    const response = await apiClient.post<{ data: any }>('/api/plans-soins', data);
    return unwrap(response);
  },

  async marquerSoinRealise(id: string, notes?: string): Promise<any> {
    const params = notes ? `?notes=${encodeURIComponent(notes)}` : '';
    const response = await apiClient.patch<{ data: any }>(`/api/plans-soins/${id}/realiser${params}`, {});
    return unwrap(response);
  },

  async deletePlanSoin(id: string): Promise<void> {
    await apiClient.delete(`/api/plans-soins/${id}`);
  },

  async getAdministrationsMedicaments(patientId: string): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>(`/api/administrations-medicaments/patient/${patientId}`);
    return unwrap(response);
  },

  async createAdministrationMedicament(data: any): Promise<any> {
    const response = await apiClient.post<{ data: any }>('/api/administrations-medicaments', data);
    return unwrap(response);
  },

  async marquerAdministre(id: string): Promise<any> {
    const response = await apiClient.patch<{ data: any }>(`/api/administrations-medicaments/${id}/administrer`, {});
    return unwrap(response);
  },
};

// ========== HOSPITALISATION ==========
export const hospitalisationApi = {
  async getChambres(): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>('/api/chambres');
    return unwrap(response);
  },

  async createChambre(data: any): Promise<any> {
    const response = await apiClient.post<{ data: any }>('/api/chambres', data);
    return unwrap(response);
  },

  async updateChambre(id: string, data: any): Promise<any> {
    const response = await apiClient.put<{ data: any }>(`/api/chambres/${id}`, data);
    return unwrap(response);
  },

  async deleteChambre(id: string): Promise<void> {
    await apiClient.delete(`/api/chambres/${id}`);
  },

  async getLits(): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>('/api/lits');
    return unwrap(response);
  },

  async getLitsByChambre(chambreId: string): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>(`/api/lits/chambre/${chambreId}`);
    return unwrap(response);
  },

  async createLit(data: any): Promise<any> {
    const response = await apiClient.post<{ data: any }>('/api/lits', data);
    return unwrap(response);
  },

  async updateLit(id: string, data: any): Promise<any> {
    const response = await apiClient.put<{ data: any }>(`/api/lits/${id}`, data);
    return unwrap(response);
  },

  async deleteLit(id: string): Promise<void> {
    await apiClient.delete(`/api/lits/${id}`);
  },

  async getAdmissions(): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>('/api/admissions');
    return unwrap(response);
  },

  async createAdmission(data: any): Promise<any> {
    const response = await apiClient.post<{ data: any }>('/api/admissions', data);
    return unwrap(response);
  },

  async getAdmissionsEnCours(): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>('/api/admissions/en-cours');
    return unwrap(response);
  },

  async dischargePatient(id: string): Promise<any> {
    const response = await apiClient.put<{ data: any }>(`/api/admissions/${id}/discharge`, {});
    return unwrap(response);
  },
};

// ========== INVENTORY & STOCKS ==========
export const inventoryApi = {
  async getStocks(): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>('/api/stocks');
    return unwrap(response);
  },

  async createStock(data: any): Promise<any> {
    const response = await apiClient.post<{ data: any }>('/api/stocks', data);
    return unwrap(response);
  },

  async getStockAlertesRupture(): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>('/api/stocks/alertes/rupture');
    return unwrap(response);
  },

  async getStockAlertesPeremption(): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>('/api/stocks/alertes/peremption');
    return unwrap(response);
  },

  async getPharmacieStock(): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>('/api/pharmacie');
    return unwrap(response);
  },

  async createPharmacieStock(data: any): Promise<any> {
    const response = await apiClient.post<{ data: any }>('/api/pharmacie', data);
    return unwrap(response);
  },

  async updatePharmacieQuantite(id: string, quantite: number): Promise<any> {
    const response = await apiClient.patch<{ data: any }>(`/api/pharmacie/${id}/quantite`, { quantite });
    return unwrap(response);
  },

  async searchPharmacie(medicament: string): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>(`/api/pharmacie/recherche?medicament=${encodeURIComponent(medicament)}`);
    return unwrap(response);
  },
};

// ========== EVENTS & ROUNDS ==========
export const eventsApi = {
  async getEvenements(): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>('/api/evenements');
    return unwrap(response);
  },

  async createEvenement(data: any): Promise<any> {
    const response = await apiClient.post<{ data: any }>('/api/evenements', data);
    return unwrap(response);
  },

  async validerEvenement(id: string): Promise<any> {
    const response = await apiClient.patch<{ data: any }>(`/api/evenements/${id}/valider`, {});
    return unwrap(response);
  },

  async createRonde(data: any): Promise<any> {
    const response = await apiClient.post<{ data: any }>('/api/rondes', data);
    return unwrap(response);
  },

  async validerRonde(id: string, compteRendu?: string): Promise<any> {
    const params = compteRendu ? `?compteRendu=${encodeURIComponent(compteRendu)}` : '';
    const response = await apiClient.patch<{ data: any }>(`/api/rondes/${id}/valider${params}`, {});
    return unwrap(response);
  },
};

// ========== SURGERY & OPERATIONS ==========
export const surgeryApi = {
  async createProgrammeOperatoire(data: any): Promise<any> {
    const response = await apiClient.post<{ data: any }>('/api/bloc', data);
    return unwrap(response);
  },

  async updateStatutProgramme(id: string, statut: string): Promise<any> {
    const response = await apiClient.patch<{ data: any }>(`/api/bloc/${id}/statut`, { statut });
    return unwrap(response);
  },
};

// ========== QUALITY & INCIDENTS ==========
export const qualityApi = {
  async getAudits(): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>('/api/qualite/audits');
    return unwrap(response);
  },

  async createAudit(data: any): Promise<any> {
    const response = await apiClient.post<{ data: any }>('/api/qualite/audits', data);
    return unwrap(response);
  },

  async getIncidentsNonResolus(): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>('/api/incidents/non-resolus');
    return unwrap(response);
  },

  async createIncident(data: any): Promise<any> {
    const response = await apiClient.post<{ data: any }>('/api/incidents', data);
    return unwrap(response);
  },

  async resoudreIncident(id: string, action?: string): Promise<any> {
    const params = action ? `?action=${encodeURIComponent(action)}` : '';
    const response = await apiClient.patch<{ data: any }>(`/api/incidents/${id}/resoudre${params}`, {});
    return unwrap(response);
  },
};

// ========== ASSETS & EQUIPMENT ==========
export const assetsApi = {
  async getEquipements(): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>('/api/equipements');
    return unwrap(response);
  },

  async createEquipement(data: any): Promise<any> {
    const response = await apiClient.post<{ data: any }>('/api/equipements', data);
    return unwrap(response);
  },

  async getAmbulances(): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>('/api/ambulances');
    return unwrap(response);
  },

  async createAmbulance(data: any): Promise<any> {
    const response = await apiClient.post<{ data: any }>('/api/ambulances', data);
    return unwrap(response);
  },

  async updateAmbulancePosition(id: string, lat: number, lng: number): Promise<any> {
    const response = await apiClient.patch<{ data: any }>(`/api/ambulances/${id}/position`, { lat, lng });
    return unwrap(response);
  },
};

// ========== IMAGING ==========
export const imagingApi = {
  async createExamen(data: any): Promise<any> {
    const response = await apiClient.post<{ data: any }>('/api/imagerie', data);
    return unwrap(response);
  },

  async getExamensByPatient(patientId: string): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>(`/api/imaging/patient/${patientId}`);
    return unwrap(response);
  },
};

// ========== TELEMEDICINE ==========
export const telemedicineApi = {
  async createTeleconsultation(data: any): Promise<any> {
    const response = await apiClient.post<{ data: any }>('/api/teleconsultations', data);
    return unwrap(response);
  },

  async updateStatutTeleconsultation(id: string, statut: string): Promise<any> {
    const response = await apiClient.patch<{ data: any }>(`/api/teleconsultations/${id}/statut`, { statut });
    return unwrap(response);
  },
};

// ========== DISPONIBILITES ==========
export const disponibilitesApi = {
  async getDisponibilites(medecinId: string): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>(`/api/appointments/disponibilites?medecinId=${medecinId}`);
    return unwrap(response);
  },

  async createDisponibilite(data: any): Promise<any> {
    const response = await apiClient.post<{ data: any }>('/api/appointments/disponibilites', data);
    return unwrap(response);
  },

  async updateDisponibilite(id: string, data: any): Promise<any> {
    const response = await apiClient.put<{ data: any }>(`/api/appointments/disponibilites/${id}`, data);
    return unwrap(response);
  },

  async deleteDisponibilite(id: string): Promise<void> {
    await apiClient.delete(`/api/appointments/disponibilites/${id}`);
  },
};

// ========== MALADIES & MEDICAMENTS ==========
export const maladiesApi = {
  async getMaladies(): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>('/api/maladies');
    return unwrap(response);
  },

  async createMaladie(data: any): Promise<any> {
    const response = await apiClient.post<{ data: any }>('/api/maladies', data);
    return unwrap(response);
  },

  async updateMaladie(id: string, data: any): Promise<any> {
    const response = await apiClient.put<{ data: any }>(`/api/maladies/${id}`, data);
    return unwrap(response);
  },

  async deleteMaladie(id: string): Promise<void> {
    await apiClient.delete(`/api/maladies/${id}`);
  },

  async getMaladiesByPatient(patientId: string): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>(`/api/maladies/patient/${patientId}`);
    return unwrap(response);
  },

  async getMedicaments(): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>('/api/pharmacy/medicaments');
    return unwrap(response);
  },

  async createMedicament(data: any): Promise<any> {
    const response = await apiClient.post<{ data: any }>('/api/pharmacy/medicaments', data);
    return unwrap(response);
  },

  async updateMedicament(id: string, data: any): Promise<any> {
    const response = await apiClient.put<{ data: any }>(`/api/pharmacy/medicaments/${id}`, data);
    return unwrap(response);
  },

  async deleteMedicament(id: string): Promise<void> {
    await apiClient.delete(`/api/pharmacy/medicaments/${id}`);
  },
};

// ========== ASSURANCES ==========
export const assurancesApi = {
  async getAssurancesByPatient(patientId: string): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>(`/api/assurances/patient/${patientId}`);
    return unwrap(response);
  },

  async getAssurancesActivesByPatient(patientId: string): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>(`/api/assurances/patient/${patientId}/actives`);
    return unwrap(response);
  },

  async getAssurance(id: string): Promise<any> {
    const response = await apiClient.get<{ data: any }>(`/api/assurances/${id}`);
    return unwrap(response);
  },

  async createAssurance(data: any): Promise<any> {
    const response = await apiClient.post<{ data: any }>('/api/assurances', data);
    return unwrap(response);
  },

  async updateAssurance(id: string, data: any): Promise<any> {
    const response = await apiClient.put<{ data: any }>(`/api/assurances/${id}`, data);
    return unwrap(response);
  },

  async deleteAssurance(id: string): Promise<void> {
    await apiClient.delete(`/api/assurances/${id}`);
  },

  async searchAssurances(compagnie: string): Promise<any[]> {
    const response = await apiClient.get<{ data: any[] }>(`/api/assurances/recherche?compagnie=${encodeURIComponent(compagnie)}`);
    return unwrap(response);
  },
};

// ========== REVENUS ==========
export const revenusApi = {
  async getRevenus(params?: { page?: number; size?: number }): Promise<any> {
    const query = new URLSearchParams();
    if (params?.page) query.set('page', params.page.toString());
    if (params?.size) query.set('size', params.size.toString());
    const qs = query.toString();
    const response = await apiClient.get<{ data: any }>(`/api/revenus${qs ? `?${qs}` : ''}`);
    return unwrap(response);
  },

  async getRevenusByPatient(patientId: string, params?: { page?: number; size?: number }): Promise<any> {
    const query = new URLSearchParams();
    if (params?.page) query.set('page', params.page.toString());
    if (params?.size) query.set('size', params.size.toString());
    const qs = query.toString();
    const response = await apiClient.get<{ data: any }>(`/api/revenus/patient/${patientId}${qs ? `?${qs}` : ''}`);
    return unwrap(response);
  },

  async getRevenusByMedecin(medecinId: string, params?: { page?: number; size?: number }): Promise<any> {
    const query = new URLSearchParams();
    if (params?.page) query.set('page', params.page.toString());
    if (params?.size) query.set('size', params.size.toString());
    const qs = query.toString();
    const response = await apiClient.get<{ data: any }>(`/api/revenus/medecin/${medecinId}${qs ? `?${qs}` : ''}`);
    return unwrap(response);
  },

  async getTotalRevenus(start: string, end: string): Promise<number> {
    const response = await apiClient.get<{ data: number }>(`/api/revenus/total?start=${start}&end=${end}`);
    return unwrap(response);
  },

  async getRevenu(id: string): Promise<any> {
    const response = await apiClient.get<{ data: any }>(`/api/revenus/${id}`);
    return unwrap(response);
  },

  async createRevenu(data: any): Promise<any> {
    const response = await apiClient.post<{ data: any }>('/api/revenus', data);
    return unwrap(response);
  },

  async updateRevenu(id: string, data: any): Promise<any> {
    const response = await apiClient.put<{ data: any }>(`/api/revenus/${id}`, data);
    return unwrap(response);
  },

  async deleteRevenu(id: string): Promise<void> {
    await apiClient.delete(`/api/revenus/${id}`);
  },
};
