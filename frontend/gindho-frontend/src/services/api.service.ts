import { apiClient } from '@/lib/api-client';
import { buildApiUrl } from '@/lib/config';
import type { Patient, AuthResponse, User } from '@/types';

/**
 * Service API générique pour communiquer avec les microservices Kubernetes
 * via Kong API Gateway
 */

// ========== AUTHENTICATION ==========
export const authApi = {
  async login(email: string, password: string): Promise<AuthResponse> {
    return apiClient.post(buildApiUrl('AUTH', '/login'), { email, password });
  },

  async register(data: {
    email: string;
    password: string;
    nom: string;
    prenom: string;
    role?: string;
  }): Promise<AuthResponse> {
    return apiClient.post(buildApiUrl('AUTH', '/register'), data);
  },

  async forgotPassword(email: string): Promise<void> {
    return apiClient.post(buildApiUrl('AUTH', '/forgot-password'), { email });
  },

  async resetPassword(email: string, code: string, newPassword: string): Promise<void> {
    return apiClient.post(buildApiUrl('AUTH', '/reset-password'), {
      email,
      code,
      newPassword,
    });
  },

  async getCurrentUser(): Promise<User> {
    return apiClient.get(buildApiUrl('AUTH', '/me'));
  },

  async getPermissions(): Promise<string[]> {
    return apiClient.get(buildApiUrl('AUTH', '/me-authorities'));
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
    return apiClient.get(buildApiUrl('PATIENTS', `/${qs ? `?${qs}` : ''}`));
  },

  async getPatient(id: string): Promise<Patient> {
    return apiClient.get(buildApiUrl('PATIENTS', `/${id}`));
  },

  async createPatient(data: Partial<Patient>): Promise<Patient> {
    return apiClient.post(buildApiUrl('PATIENTS', '/'), data);
  },

  async updatePatient(id: string, data: Partial<Patient>): Promise<Patient> {
    return apiClient.put(buildApiUrl('PATIENTS', `/${id}`), data);
  },

  async deletePatient(id: string): Promise<void> {
    return apiClient.delete(buildApiUrl('PATIENTS', `/${id}`));
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
    return apiClient.get(buildApiUrl('APPOINTMENTS', `/${qs ? `?${qs}` : ''}`));
  },

  async createAppointment(data: any): Promise<any> {
    return apiClient.post(buildApiUrl('APPOINTMENTS', '/'), data);
  },

  async updateAppointment(id: string, data: any): Promise<any> {
    return apiClient.put(buildApiUrl('APPOINTMENTS', `/${id}`), data);
  },

  async deleteAppointment(id: string): Promise<void> {
    return apiClient.delete(buildApiUrl('APPOINTMENTS', `/${id}`));
  },
};

// ========== MEDICAL RECORDS ==========
export const medicalRecordsApi = {
  async getConsultations(patientId: string): Promise<any[]> {
    return apiClient.get(buildApiUrl('MEDICAL_RECORDS', `/consultations?patientId=${patientId}`));
  },

  async createConsultation(data: any): Promise<any> {
    return apiClient.post(buildApiUrl('MEDICAL_RECORDS', '/consultations'), data);
  },

  async getAnalyses(patientId: string): Promise<any[]> {
    return apiClient.get(buildApiUrl('MEDICAL_RECORDS', `/analyses?patientId=${patientId}`));
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
    return apiClient.get(buildApiUrl('LABORATORY', `/analyses${qs ? `?${qs}` : ''}`));
  },

  async createAnalyse(data: any): Promise<any> {
    return apiClient.post(buildApiUrl('LABORATORY', '/analyses'), data);
  },

  async updateAnalyseResult(id: string, resultat: string): Promise<any> {
    return apiClient.patch(buildApiUrl('LABORATORY', `/analyses/${id}/resultat`), { resultat });
  },
};

// ========== PHARMACY ==========
export const pharmacyApi = {
  async getPrescriptions(patientId: string): Promise<any[]> {
    return apiClient.get(buildApiUrl('PHARMACY', `/prescriptions?patientId=${patientId}`));
  },

  async createPrescription(data: any): Promise<any> {
    return apiClient.post(buildApiUrl('PHARMACY', '/prescriptions'), data);
  },

  async getMedications(): Promise<any[]> {
    return apiClient.get(buildApiUrl('PHARMACY', '/medications'));
  },
};

// ========== BILLING ==========
export const billingApi = {
  async getInvoices(patientId?: string): Promise<any[]> {
    const qs = patientId ? `?patientId=${patientId}` : '';
    return apiClient.get(buildApiUrl('BILLING', `/invoices${qs}`));
  },

  async createInvoice(data: any): Promise<any> {
    return apiClient.post(buildApiUrl('BILLING', '/invoices'), data);
  },

  async payInvoice(id: string, data: any): Promise<any> {
    return apiClient.post(buildApiUrl('PAYMENTS', `/${id}/pay`), data);
  },
};

// ========== NOTIFICATIONS ==========
export const notificationsApi = {
  async getNotifications(): Promise<any[]> {
    return apiClient.get(buildApiUrl('NOTIFICATIONS', '/'));
  },

  async markAsRead(id: string): Promise<void> {
    return apiClient.put(buildApiUrl('NOTIFICATIONS', `/${id}/read`), {});
  },
};

// ========== EMERGENCY ==========
export const emergencyApi = {
  async getUrgencies(): Promise<any[]> {
    return apiClient.get(buildApiUrl('EMERGENCY', '/'));
  },

  async createUrgency(data: any): Promise<any> {
    return apiClient.post(buildApiUrl('EMERGENCY', '/'), data);
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
    return apiClient.get(buildApiUrl('HR', `/medecins${qs ? `?${qs}` : ''}`));
  },

  async getMedecin(id: string): Promise<any> {
    return apiClient.get(buildApiUrl('HR', `/medecins/${id}`));
  },

  async getMedecinByUserId(userId: string): Promise<any> {
    return apiClient.get(buildApiUrl('HR', `/medecins/by-user/${userId}`));
  },

  async createMedecin(data: any): Promise<any> {
    return apiClient.post(buildApiUrl('HR', '/medecins'), data);
  },

  async updateMedecin(id: string, data: any): Promise<any> {
    return apiClient.put(buildApiUrl('HR', `/medecins/${id}`), data);
  },

  async deleteMedecin(id: string): Promise<void> {
    return apiClient.delete(buildApiUrl('HR', `/medecins/${id}`));
  },

  async getSchedules(medecinId: string, date: string): Promise<any[]> {
    return apiClient.get(buildApiUrl('HR', `/schedules?medecinId=${medecinId}&date=${date}`));
  },

  async getPersonnel(): Promise<any[]> {
    return apiClient.get(buildApiUrl('HR', '/personnel'));
  },

  async createPersonnel(data: any): Promise<any> {
    return apiClient.post(buildApiUrl('HR', '/personnel'), data);
  },

  async deletePersonnel(id: string): Promise<void> {
    return apiClient.delete(buildApiUrl('HR', `/personnel/${id}`));
  },

  async pointerPresence(personnelId: string): Promise<any> {
    return apiClient.post(buildApiUrl('HR', `/presence/pointer/${personnelId}`), {});
  },

  async getPresences(personnelId: string): Promise<any[]> {
    return apiClient.get(buildApiUrl('HR', `/presence/${personnelId}`));
  },

  async createConge(data: any): Promise<any> {
    return apiClient.post(buildApiUrl('HR', '/conges'), data);
  },

  async validerConge(id: string): Promise<any> {
    return apiClient.patch(buildApiUrl('HR', `/conges/${id}/valider`), {});
  },
};

// ========== DASHBOARD & ANALYTICS ==========
export const dashboardApi = {
  async getAdminStats(): Promise<any> {
    return apiClient.get(buildApiUrl('REPORTING', '/dashboard/admin/stats'));
  },

  async getAdminMetricSeries(metric: string, from?: string, to?: string): Promise<any> {
    const params = new URLSearchParams();
    if (from) params.set('from', from);
    if (to) params.set('to', to);
    const qs = params.toString();
    return apiClient.get(buildApiUrl('REPORTING', `/dashboard/admin/stats/${metric}${qs ? `?${qs}` : ''}`));
  },

  async queryAdminStats(data: { metric: string; from?: string; to?: string }): Promise<any> {
    return apiClient.post(buildApiUrl('REPORTING', '/dashboard/admin/stats/query'), data);
  },

  async getPatientDashboard(patientId: string): Promise<any> {
    return apiClient.get(buildApiUrl('REPORTING', `/dashboard/patient/${patientId}`));
  },

  async getMedecinDashboard(medecinId: string): Promise<any> {
    return apiClient.get(buildApiUrl('REPORTING', `/dashboard/medecin/${medecinId}`));
  },

  async getPatientsByMedecin(medecinId: string): Promise<any[]> {
    return apiClient.get(buildApiUrl('REPORTING', `/dashboard/medecin/${medecinId}/patients`));
  },
};

// ========== MEDICAL RECORDS (Soins infirmiers) ==========
export const medicalRecordsExtendedApi = {
  async getSignesVitaux(patientId: string): Promise<any[]> {
    return apiClient.get(buildApiUrl('MEDICAL_RECORDS', `/signes-vitaux/patient/${patientId}`));
  },

  async getSignesVitauxByHospitalisation(hospitalisationId: string): Promise<any[]> {
    return apiClient.get(buildApiUrl('MEDICAL_RECORDS', `/signes-vitaux/hospitalisation/${hospitalisationId}`));
  },

  async createSignesVitaux(data: any): Promise<any> {
    return apiClient.post(buildApiUrl('MEDICAL_RECORDS', '/signes-vitaux'), data);
  },

  async deleteSignesVitaux(id: string): Promise<void> {
    return apiClient.delete(buildApiUrl('MEDICAL_RECORDS', `/signes-vitaux/${id}`));
  },

  async getPlansSoins(patientId: string): Promise<any[]> {
    return apiClient.get(buildApiUrl('MEDICAL_RECORDS', `/plans-soins/patient/${patientId}`));
  },

  async getPlansSoinsByHospitalisation(hospitalisationId: string): Promise<any[]> {
    return apiClient.get(buildApiUrl('MEDICAL_RECORDS', `/plans-soins/hospitalisation/${hospitalisationId}`));
  },

  async createPlanSoin(data: any): Promise<any> {
    return apiClient.post(buildApiUrl('MEDICAL_RECORDS', '/plans-soins'), data);
  },

  async marquerSoinRealise(id: string, notes?: string): Promise<any> {
    const params = notes ? `?notes=${encodeURIComponent(notes)}` : '';
    return apiClient.patch(buildApiUrl('MEDICAL_RECORDS', `/plans-soins/${id}/realiser${params}`), {});
  },

  async deletePlanSoin(id: string): Promise<void> {
    return apiClient.delete(buildApiUrl('MEDICAL_RECORDS', `/plans-soins/${id}`));
  },

  async getAdministrationsMedicaments(patientId: string): Promise<any[]> {
    return apiClient.get(buildApiUrl('MEDICAL_RECORDS', `/administrations-medicaments/patient/${patientId}`));
  },

  async createAdministrationMedicament(data: any): Promise<any> {
    return apiClient.post(buildApiUrl('MEDICAL_RECORDS', '/administrations-medicaments'), data);
  },

  async marquerAdministre(id: string): Promise<any> {
    return apiClient.patch(buildApiUrl('MEDICAL_RECORDS', `/administrations-medicaments/${id}/administrer`), {});
  },
};

// ========== HOSPITALISATION ==========
export const hospitalisationApi = {
  async getChambres(): Promise<any[]> {
    return apiClient.get(buildApiUrl('ADMISSIONS', '/chambres'));
  },

  async createChambre(data: any): Promise<any> {
    return apiClient.post(buildApiUrl('ADMISSIONS', '/chambres'), data);
  },

  async updateChambre(id: string, data: any): Promise<any> {
    return apiClient.put(buildApiUrl('ADMISSIONS', `/chambres/${id}`), data);
  },

  async deleteChambre(id: string): Promise<void> {
    return apiClient.delete(buildApiUrl('ADMISSIONS', `/chambres/${id}`));
  },

  async getLits(): Promise<any[]> {
    return apiClient.get(buildApiUrl('BEDS', '/lits'));
  },

  async getLitsByChambre(chambreId: string): Promise<any[]> {
    return apiClient.get(buildApiUrl('BEDS', `/lits/chambre/${chambreId}`));
  },

  async createLit(data: any): Promise<any> {
    return apiClient.post(buildApiUrl('BEDS', '/lits'), data);
  },

  async updateLit(id: string, data: any): Promise<any> {
    return apiClient.put(buildApiUrl('BEDS', `/lits/${id}`), data);
  },

  async deleteLit(id: string): Promise<void> {
    return apiClient.delete(buildApiUrl('BEDS', `/lits/${id}`));
  },

  async getAdmissions(): Promise<any[]> {
    return apiClient.get(buildApiUrl('ADMISSIONS', '/'));
  },

  async createAdmission(data: any): Promise<any> {
    return apiClient.post(buildApiUrl('ADMISSIONS', '/'), data);
  },

  async getAdmissionsEnCours(): Promise<any[]> {
    return apiClient.get(buildApiUrl('ADMISSIONS', '/en-cours'));
  },

  async dischargePatient(id: string): Promise<any> {
    return apiClient.put(buildApiUrl('ADMISSIONS', `/${id}/discharge`), {});
  },
};

// ========== INVENTORY & STOCKS ==========
export const inventoryApi = {
  async getStocks(): Promise<any[]> {
    return apiClient.get(buildApiUrl('INVENTORY', '/stocks'));
  },

  async createStock(data: any): Promise<any> {
    return apiClient.post(buildApiUrl('INVENTORY', '/stocks'), data);
  },

  async getStockAlertesRupture(): Promise<any[]> {
    return apiClient.get(buildApiUrl('INVENTORY', '/stocks/alertes/rupture'));
  },

  async getStockAlertesPeremption(): Promise<any[]> {
    return apiClient.get(buildApiUrl('INVENTORY', '/stocks/alertes/peremption'));
  },

  async getPharmacieStock(): Promise<any[]> {
    return apiClient.get(buildApiUrl('PHARMACY', '/pharmacie'));
  },

  async createPharmacieStock(data: any): Promise<any> {
    return apiClient.post(buildApiUrl('PHARMACY', '/pharmacie'), data);
  },

  async updatePharmacieQuantite(id: string, quantite: number): Promise<any> {
    return apiClient.patch(buildApiUrl('PHARMACY', `/pharmacie/${id}/quantite`), { quantite });
  },

  async searchPharmacie(medicament: string): Promise<any[]> {
    return apiClient.get(buildApiUrl('PHARMACY', `/pharmacie/recherche?medicament=${encodeURIComponent(medicament)}`));
  },
};

// ========== EVENTS & ROUNDS ==========
export const eventsApi = {
  async getEvenements(): Promise<any[]> {
    return apiClient.get(buildApiUrl('EVENTS', '/evenements'));
  },

  async createEvenement(data: any): Promise<any> {
    return apiClient.post(buildApiUrl('EVENTS', '/evenements'), data);
  },

  async validerEvenement(id: string): Promise<any> {
    return apiClient.patch(buildApiUrl('EVENTS', `/evenements/${id}/valider`), {});
  },

  async createRonde(data: any): Promise<any> {
    return apiClient.post(buildApiUrl('ROUNDS', '/rondes'), data);
  },

  async validerRonde(id: string, compteRendu?: string): Promise<any> {
    const params = compteRendu ? `?compteRendu=${encodeURIComponent(compteRendu)}` : '';
    return apiClient.patch(buildApiUrl('ROUNDS', `/rondes/${id}/valider${params}`), {});
  },
};

// ========== SURGERY & OPERATIONS ==========
export const surgeryApi = {
  async createProgrammeOperatoire(data: any): Promise<any> {
    return apiClient.post(buildApiUrl('SURGERY', '/bloc'), data);
  },

  async updateStatutProgramme(id: string, statut: string): Promise<any> {
    return apiClient.patch(buildApiUrl('SURGERY', `/bloc/${id}/statut`), { statut });
  },
};

// ========== QUALITY & INCIDENTS ==========
export const qualityApi = {
  async getAudits(): Promise<any[]> {
    return apiClient.get(buildApiUrl('AUDIT', '/qualite/audits'));
  },

  async createAudit(data: any): Promise<any> {
    return apiClient.post(buildApiUrl('AUDIT', '/qualite/audits'), data);
  },

  async getIncidentsNonResolus(): Promise<any[]> {
    return apiClient.get(buildApiUrl('AUDIT', '/incidents/non-resolus'));
  },

  async createIncident(data: any): Promise<any> {
    return apiClient.post(buildApiUrl('AUDIT', '/incidents'), data);
  },

  async resoudreIncident(id: string, action?: string): Promise<any> {
    const params = action ? `?action=${encodeURIComponent(action)}` : '';
    return apiClient.patch(buildApiUrl('AUDIT', `/incidents/${id}/resoudre${params}`), {});
  },
};

// ========== ASSETS & EQUIPMENT ==========
export const assetsApi = {
  async getEquipements(): Promise<any[]> {
    return apiClient.get(buildApiUrl('ASSETS', '/equipements'));
  },

  async createEquipement(data: any): Promise<any> {
    return apiClient.post(buildApiUrl('ASSETS', '/equipements'), data);
  },

  async getAmbulances(): Promise<any[]> {
    return apiClient.get(buildApiUrl('AMBULANCE', '/ambulances'));
  },

  async createAmbulance(data: any): Promise<any> {
    return apiClient.post(buildApiUrl('AMBULANCE', '/ambulances'), data);
  },

  async updateAmbulancePosition(id: string, lat: number, lng: number): Promise<any> {
    return apiClient.patch(buildApiUrl('AMBULANCE', `/ambulances/${id}/position`), { lat, lng });
  },
};

// ========== IMAGING ==========
export const imagingApi = {
  async createExamen(data: any): Promise<any> {
    return apiClient.post(buildApiUrl('IMAGING', '/imagerie'), data);
  },

  async getExamensByPatient(patientId: string): Promise<any[]> {
    return apiClient.get(buildApiUrl('IMAGING', `/imagerie/patient/${patientId}`));
  },
};

// ========== TELEMEDICINE ==========
export const telemedicineApi = {
  async createTeleconsultation(data: any): Promise<any> {
    return apiClient.post(buildApiUrl('IMAGING', '/teleconsultations'), data);
  },

  async updateStatutTeleconsultation(id: string, statut: string): Promise<any> {
    return apiClient.patch(buildApiUrl('IMAGING', `/teleconsultations/${id}/statut`), { statut });
  },
};

// ========== DISPONIBILITES ==========
export const disponibilitesApi = {
  async getDisponibilites(medecinId: string): Promise<any[]> {
    return apiClient.get(buildApiUrl('APPOINTMENTS', `/disponibilites?medecinId=${medecinId}`));
  },

  async createDisponibilite(data: any): Promise<any> {
    return apiClient.post(buildApiUrl('APPOINTMENTS', '/disponibilites'), data);
  },

  async updateDisponibilite(id: string, data: any): Promise<any> {
    return apiClient.put(buildApiUrl('APPOINTMENTS', `/disponibilites/${id}`), data);
  },

  async deleteDisponibilite(id: string): Promise<void> {
    return apiClient.delete(buildApiUrl('APPOINTMENTS', `/disponibilites/${id}`));
  },
};

// ========== MALADIES & MEDICAMENTS ==========
export const maladiesApi = {
  async getMaladies(): Promise<any[]> {
    return apiClient.get(buildApiUrl('PHARMACY', '/maladies'));
  },

  async createMaladie(data: any): Promise<any> {
    return apiClient.post(buildApiUrl('PHARMACY', '/maladies'), data);
  },

  async updateMaladie(id: string, data: any): Promise<any> {
    return apiClient.put(buildApiUrl('PHARMACY', `/maladies/${id}`), data);
  },

  async deleteMaladie(id: string): Promise<void> {
    return apiClient.delete(buildApiUrl('PHARMACY', `/maladies/${id}`));
  },

  async getMaladiesByPatient(patientId: string): Promise<any[]> {
    return apiClient.get(buildApiUrl('PHARMACY', `/maladies/patient/${patientId}`));
  },

  async getMedicaments(): Promise<any[]> {
    return apiClient.get(buildApiUrl('PHARMACY', '/medicaments'));
  },

  async createMedicament(data: any): Promise<any> {
    return apiClient.post(buildApiUrl('PHARMACY', '/medicaments'), data);
  },

  async updateMedicament(id: string, data: any): Promise<any> {
    return apiClient.put(buildApiUrl('PHARMACY', `/medicaments/${id}`), data);
  },

  async deleteMedicament(id: string): Promise<void> {
    return apiClient.delete(buildApiUrl('PHARMACY', `/medicaments/${id}`));
  },
};

// ========== ASSURANCES ==========
export const assurancesApi = {
  async getAssurancesByPatient(patientId: string): Promise<any[]> {
    return apiClient.get(buildApiUrl('INSURANCE', `/assurances/patient/${patientId}`));
  },

  async getAssurancesActivesByPatient(patientId: string): Promise<any[]> {
    return apiClient.get(buildApiUrl('INSURANCE', `/assurances/patient/${patientId}/actives`));
  },

  async getAssurance(id: string): Promise<any> {
    return apiClient.get(buildApiUrl('INSURANCE', `/assurances/${id}`));
  },

  async createAssurance(data: any): Promise<any> {
    return apiClient.post(buildApiUrl('INSURANCE', '/assurances'), data);
  },

  async updateAssurance(id: string, data: any): Promise<any> {
    return apiClient.put(buildApiUrl('INSURANCE', `/assurances/${id}`), data);
  },

  async deleteAssurance(id: string): Promise<void> {
    return apiClient.delete(buildApiUrl('INSURANCE', `/assurances/${id}`));
  },

  async searchAssurances(compagnie: string): Promise<any[]> {
    return apiClient.get(buildApiUrl('INSURANCE', `/assurances/recherche?compagnie=${encodeURIComponent(compagnie)}`));
  },
};

// ========== REVENUS ==========
export const revenusApi = {
  async getRevenus(params?: { page?: number; size?: number }): Promise<any> {
    const query = new URLSearchParams();
    if (params?.page) query.set('page', params.page.toString());
    if (params?.size) query.set('size', params.size.toString());
    const qs = query.toString();
    return apiClient.get(buildApiUrl('BILLING', `/revenus${qs ? `?${qs}` : ''}`));
  },

  async getRevenusByPatient(patientId: string, params?: { page?: number; size?: number }): Promise<any> {
    const query = new URLSearchParams();
    if (params?.page) query.set('page', params.page.toString());
    if (params?.size) query.set('size', params.size.toString());
    const qs = query.toString();
    return apiClient.get(buildApiUrl('BILLING', `/revenus/patient/${patientId}${qs ? `?${qs}` : ''}`));
  },

  async getRevenusByMedecin(medecinId: string, params?: { page?: number; size?: number }): Promise<any> {
    const query = new URLSearchParams();
    if (params?.page) query.set('page', params.page.toString());
    if (params?.size) query.set('size', params.size.toString());
    const qs = query.toString();
    return apiClient.get(buildApiUrl('BILLING', `/revenus/medecin/${medecinId}${qs ? `?${qs}` : ''}`));
  },

  async getTotalRevenus(start: string, end: string): Promise<number> {
    return apiClient.get(buildApiUrl('BILLING', `/revenus/total?start=${start}&end=${end}`));
  },

  async getRevenu(id: string): Promise<any> {
    return apiClient.get(buildApiUrl('BILLING', `/revenus/${id}`));
  },

  async createRevenu(data: any): Promise<any> {
    return apiClient.post(buildApiUrl('BILLING', '/revenus'), data);
  },

  async updateRevenu(id: string, data: any): Promise<any> {
    return apiClient.put(buildApiUrl('BILLING', `/revenus/${id}`), data);
  },

  async deleteRevenu(id: string): Promise<void> {
    return apiClient.delete(buildApiUrl('BILLING', `/revenus/${id}`));
  },
};
