import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useCallback } from 'react';
import * as api from '@/services/api.service';
import type {
  Patient,
} from '@/types';

// ===== CONSTANTS =====
const QUERY_KEYS = {
  // Auth
  AUTH: ['auth'],
  AUTH_USER: ['auth', 'user'],
  AUTH_PERMISSIONS: ['auth', 'permissions'],
  
  // Patients
  PATIENTS: ['patients'],
  PATIENT: (id: string) => ['patient', id],
  
  // Appointments
  APPOINTMENTS: ['appointments'],
  APPOINTMENT: (id: string) => ['appointment', id],
  
  // Medical Records
  MEDICAL_RECORDS: ['medical-records'],
  MEDICAL_RECORD: (id: string) => ['medical-record', id],
  CONSULTATIONS: ['consultations'],
  
  // Laboratory
  LABORATORY_TESTS: ['laboratory-tests'],
  LABORATORY_TEST: (id: string) => ['laboratory-test', id],
  
  // Imaging
  IMAGING_REQUESTS: ['imaging-requests'],
  IMAGING_REQUEST: (id: string) => ['imaging-request', id],
  
  // Pharmacy
  PHARMACY_INVENTORY: ['pharmacy-inventory'],
  PHARMACY_ITEM: (id: string) => ['pharmacy-item', id],
  PRESCRIPTIONS: ['prescriptions'],
  
  // Admissions
  ADMISSIONS: ['admissions'],
  ADMISSION: (id: string) => ['admission', id],
  
  // Discharges
  DISCHARGES: ['discharges'],
  DISCHARGE: (id: string) => ['discharge', id],
  
  // Wards
  WARDS: ['wards'],
  WARD: (id: string) => ['ward', id],
  
  // Beds
  BEDS: ['beds'],
  BED: (id: string) => ['bed', id],
  
  // Surgery
  SURGERIES: ['surgeries'],
  SURGERY: (id: string) => ['surgery', id],
  
  // Billing
  INVOICES: ['invoices'],
  INVOICE: (id: string) => ['invoice', id],
  PAYMENTS: ['payments'],
  
  // Inventory
  INVENTORY_ITEMS: ['inventory-items'],
  INVENTORY_ITEM: (id: string) => ['inventory-item', id],
  PROCUREMENT_ORDERS: ['procurement-orders'],
  
  // HR
  EMPLOYEES: ['employees'],
  EMPLOYEE: (id: string) => ['employee', id],
  SCHEDULES: ['schedules'],
  LEAVES: ['leaves'],
  
  // Ambulance
  AMBULANCE_REQUESTS: ['ambulance-requests'],
  AMBULANCE_REQUEST: (id: string) => ['ambulance-request', id],
  
  // Notifications
  NOTIFICATIONS: ['notifications'],
  
  // Audit
  AUDIT_LOGS: ['audit-logs'],
  
  // Rounds
  MEDICAL_ROUNDS: ['medical-rounds'],
  
  // Quality
  QUALITY_INCIDENTS: ['quality-incidents'],
  
  // Dashboard
  DASHBOARD_STATS: ['dashboard', 'stats'],
} as const;

// ===== AUTH HOOKS =====
export const useAuth = () => {
  return useQuery({
    queryKey: QUERY_KEYS.AUTH_USER,
    queryFn: () => api.authApi.getCurrentUser(),
    staleTime: 1000 * 60 * 30, // 30 minutes
  });
};

export const usePermissions = () => {
  return useQuery({
    queryKey: QUERY_KEYS.AUTH_PERMISSIONS,
    queryFn: () => api.authApi.getPermissions(),
    staleTime: 1000 * 60 * 30,
  });
};

export const useLogin = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: ({ email, password }: { email: string; password: string }) =>
      api.authApi.login(email, password),
    onSuccess: (data) => {
      localStorage.setItem('token', data.token);
      queryClient.setQueryData(QUERY_KEYS.AUTH_USER, data.user);
    },
  });
};

export const useLogout = () => {
  const queryClient = useQueryClient();
  
  return useCallback(() => {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    queryClient.clear();
  }, [queryClient]);
};

// ===== PATIENTS HOOKS =====
export const usePatients = (params?: { search?: string; page?: number; size?: number }) => {
  return useQuery({
    queryKey: [QUERY_KEYS.PATIENTS, params],
    queryFn: () => api.patientsApi.getPatients(params),
    staleTime: 1000 * 60 * 5,
  });
};

export const usePatient = (id: string | null) => {
  return useQuery({
    queryKey: QUERY_KEYS.PATIENT(id || ''),
    queryFn: () => api.patientsApi.getPatient(id!),
    enabled: !!id,
    staleTime: 1000 * 60 * 10,
  });
};

export const useCreatePatient = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (data: Partial<Patient>) => api.patientsApi.createPatient(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.PATIENTS });
    },
  });
};

export const useUpdatePatient = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: Partial<Patient> }) =>
      api.patientsApi.updatePatient(id, data),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.PATIENTS });
      queryClient.setQueryData(QUERY_KEYS.PATIENT(data.id), data);
    },
  });
};

export const useDeletePatient = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (id: string) => api.patientsApi.deletePatient(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.PATIENTS });
    },
  });
};

// ===== APPOINTMENTS HOOKS =====
export const useAppointments = (params?: any) => {
  return useQuery({
    queryKey: [QUERY_KEYS.APPOINTMENTS, params],
    queryFn: () => api.appointmentsApi.getAppointments(params),
    staleTime: 1000 * 60 * 2,
  });
};

export const useCreateAppointment = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (data: any) => api.appointmentsApi.createAppointment(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.APPOINTMENTS });
    },
  });
};

export const useUpdateAppointment = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: any }) =>
      api.appointmentsApi.updateAppointment(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.APPOINTMENTS });
    },
  });
};

export const useCancelAppointment = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (id: string) => api.appointmentsApi.deleteAppointment(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.APPOINTMENTS });
    },
  });
};

// ===== MEDICAL RECORDS HOOKS =====
export const useMedicalRecords = (patientId: string | null) => {
  return useQuery({
    queryKey: [QUERY_KEYS.MEDICAL_RECORDS, patientId],
    queryFn: () => api.medicalRecordsApi.getConsultations(patientId!),
    enabled: !!patientId,
    staleTime: 1000 * 60 * 10,
  });
};

export const useCreateConsultation = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (data: any) => api.medicalRecordsApi.createConsultation(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.MEDICAL_RECORDS });
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.CONSULTATIONS });
    },
  });
};

// ===== LABORATORY HOOKS =====
export const useLaboratoryTests = (params?: any) => {
  return useQuery({
    queryKey: [QUERY_KEYS.LABORATORY_TESTS, params],
    queryFn: () => api.laboratoryApi.getAnalyses(params),
    staleTime: 1000 * 60 * 5,
  });
};

export const useCreateLaboratoryTest = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (data: any) => api.laboratoryApi.createAnalyse(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.LABORATORY_TESTS });
    },
  });
};

export const useUpdateLabTestResult = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: ({ id, resultat }: { id: string; resultat: string }) =>
      api.laboratoryApi.updateAnalyseResult(id, resultat),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.LABORATORY_TESTS });
    },
  });
};

// ===== PHARMACY HOOKS =====
export const usePharmacyInventory = () => {
  return useQuery({
    queryKey: QUERY_KEYS.PHARMACY_INVENTORY,
    queryFn: () => api.inventoryApi.getPharmacieStock(),
    staleTime: 1000 * 60 * 5,
  });
};

export const usePrescriptions = (patientId: string | null) => {
  return useQuery({
    queryKey: [QUERY_KEYS.PRESCRIPTIONS, patientId],
    queryFn: () => api.pharmacyApi.getPrescriptions(patientId!),
    enabled: !!patientId,
    staleTime: 1000 * 60 * 5,
  });
};

export const useCreatePrescription = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (data: any) => api.pharmacyApi.createPrescription(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.PRESCRIPTIONS });
    },
  });
};

// ===== ADMISSIONS HOOKS =====
export const useAdmissions = () => {
  return useQuery({
    queryKey: QUERY_KEYS.ADMISSIONS,
    queryFn: () => api.hospitalisationApi.getAdmissions(),
    staleTime: 1000 * 60 * 5,
  });
};

export const useActiveAdmissions = () => {
  return useQuery({
    queryKey: [QUERY_KEYS.ADMISSIONS, 'active'],
    queryFn: () => api.hospitalisationApi.getAdmissionsEnCours(),
    staleTime: 1000 * 60 * 2,
  });
};

export const useCreateAdmission = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (data: any) => api.hospitalisationApi.createAdmission(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.ADMISSIONS });
    },
  });
};

export const useDischargePatient = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (id: string) => api.hospitalisationApi.dischargePatient(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.ADMISSIONS });
    },
  });
};

// ===== WARDS & BEDS HOOKS =====
export const useWards = () => {
  return useQuery({
    queryKey: QUERY_KEYS.WARDS,
    queryFn: () => api.hospitalisationApi.getChambres(),
    staleTime: 1000 * 60 * 10,
  });
};

export const useBeds = () => {
  return useQuery({
    queryKey: QUERY_KEYS.BEDS,
    queryFn: () => api.hospitalisationApi.getLits(),
    staleTime: 1000 * 60 * 5,
  });
};

export const useBedsByWard = (wardId: string | null) => {
  return useQuery({
    queryKey: [QUERY_KEYS.BEDS, wardId],
    queryFn: () => api.hospitalisationApi.getLitsByChambre(wardId!),
    enabled: !!wardId,
    staleTime: 1000 * 60 * 5,
  });
};

// ===== SURGERY HOOKS =====
export const useScheduleSurgery = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (data: any) => api.surgeryApi.createProgrammeOperatoire(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.SURGERIES });
    },
  });
};

export const useUpdateSurgeryStatus = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: ({ id, statut }: { id: string; statut: string }) =>
      api.surgeryApi.updateStatutProgramme(id, statut),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.SURGERIES });
    },
  });
};

// ===== BILLING HOOKS =====
export const useInvoices = (patientId?: string) => {
  return useQuery({
    queryKey: [QUERY_KEYS.INVOICES, patientId],
    queryFn: () => api.billingApi.getInvoices(patientId),
    staleTime: 1000 * 60 * 5,
  });
};

export const useCreateInvoice = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (data: any) => api.billingApi.createInvoice(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.INVOICES });
    },
  });
};

export const usePayInvoice = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: any }) =>
      api.billingApi.payInvoice(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.INVOICES });
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.PAYMENTS });
    },
  });
};

// ===== INVENTORY HOOKS =====
export const useInventoryItems = () => {
  return useQuery({
    queryKey: QUERY_KEYS.INVENTORY_ITEMS,
    queryFn: () => api.inventoryApi.getStocks(),
    staleTime: 1000 * 60 * 10,
  });
};

export const useInventoryAlerts = () => {
  return useQuery({
    queryKey: [QUERY_KEYS.INVENTORY_ITEMS, 'alerts'],
    queryFn: async () => {
      const [rupture, peremption] = await Promise.all([
        api.inventoryApi.getStockAlertesRupture(),
        api.inventoryApi.getStockAlertesPeremption(),
      ]);
      return { rupture, peremption };
    },
    staleTime: 1000 * 60 * 5,
  });
};

// ===== HR HOOKS =====
export const useDoctors = (params?: any) => {
  return useQuery({
    queryKey: [['doctors'], params],
    queryFn: () => api.hrApi.getMedecins(params),
    staleTime: 1000 * 60 * 10,
  });
};

export const useEmployees = () => {
  return useQuery({
    queryKey: QUERY_KEYS.EMPLOYEES,
    queryFn: () => api.hrApi.getPersonnel(),
    staleTime: 1000 * 60 * 10,
  });
};

export const useCreateEmployee = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (data: any) => api.hrApi.createPersonnel(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.EMPLOYEES });
    },
  });
};

export const useCheckIn = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (personnelId: string) => api.hrApi.pointerPresence(personnelId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.EMPLOYEES });
    },
  });
};

// ===== NOTIFICATIONS HOOKS =====
export const useNotifications = () => {
  return useQuery({
    queryKey: QUERY_KEYS.NOTIFICATIONS,
    queryFn: () => api.notificationsApi.getNotifications(),
    staleTime: 1000 * 30, // 30 seconds
  });
};

export const useMarkNotificationAsRead = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (id: string) => api.notificationsApi.markAsRead(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.NOTIFICATIONS });
    },
  });
};

// ===== QUALITY HOOKS =====
export const useQualityIncidents = () => {
  return useQuery({
    queryKey: QUERY_KEYS.QUALITY_INCIDENTS,
    queryFn: () => api.qualityApi.getIncidentsNonResolus(),
    staleTime: 1000 * 60 * 5,
  });
};

export const useReportIncident = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (data: any) => api.qualityApi.createIncident(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.QUALITY_INCIDENTS });
    },
  });
};

// ===== DASHBOARD HOOKS =====
export const useDashboardStats = () => {
  return useQuery({
    queryKey: QUERY_KEYS.DASHBOARD_STATS,
    queryFn: () => api.dashboardApi.getAdminStats(),
    staleTime: 1000 * 60 * 2,
  });
};

export const useMedecinDashboard = (medecinId: string | null) => {
  return useQuery({
    queryKey: [QUERY_KEYS.DASHBOARD_STATS, 'medecin', medecinId],
    queryFn: () => api.dashboardApi.getMedecinDashboard(medecinId!),
    enabled: !!medecinId,
    staleTime: 1000 * 60 * 2,
  });
};

export const usePatientDashboard = (patientId: string | null) => {
  return useQuery({
    queryKey: [QUERY_KEYS.DASHBOARD_STATS, 'patient', patientId],
    queryFn: () => api.dashboardApi.getPatientDashboard(patientId!),
    enabled: !!patientId,
    staleTime: 1000 * 60 * 5,
  });
};

// ===== UTILITY HOOK FOR INVALIDATING ALL DATA =====
export const useInvalidateAllData = () => {
  const queryClient = useQueryClient();
  
  return useCallback(() => {
    queryClient.invalidateQueries();
  }, [queryClient]);
};
