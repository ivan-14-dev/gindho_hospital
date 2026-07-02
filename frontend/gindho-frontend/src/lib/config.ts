// Configuration pour microservices Kubernetes

export const API_CONFIG = {
  // Kong API Gateway (point d'entrée unique)
  GATEWAY_URL: import.meta.env.VITE_GATEWAY_URL || 'http://localhost:8000',
  
  // Services endpoints (via Kong)
  SERVICES: {
    AUTH: '/api/auth',
    PATIENTS: '/api/patients',
    APPOINTMENTS: '/api/appointments',
    MEDICAL_RECORDS: '/api/consultations',
    LABORATORY: '/api/analyses',
    IMAGING: '/api/imaging',
    PHARMACY: '/api/pharmacy',
    BILLING: '/api/invoices',
    PAYMENTS: '/api/invoices',
    NOTIFICATIONS: '/api/notifications',
    EMERGENCY: '/api/emergency',
    HR: '/api/hr',
    INVENTORY: '/api/stocks',
    AUDIT: '/api/audit',
    ADMISSIONS: '/api/admissions',
    AMBULANCE: '/api/ambulances',
    BEDS: '/api/beds',
    WARDS: '/api/wards',
    SURGERY: '/api/surgeries',
    PRESCRIPTIONS: '/api/prescriptions',
    INSURANCE: '/api/assurances',
    REPORTING: '/api/dashboard',
    ROUNDS: '/api/rondes',
    EVENTS: '/api/evenements',
    ASSETS: '/api/equipements',
    PROCUREMENT: '/api/procurement',
  },

  // Timeouts
  TIMEOUT: 30000, // 30 secondes

  // Retry configuration
  RETRY_ATTEMPTS: 3,
  RETRY_DELAY: 1000, // 1 seconde
} as const;

// Types pour la configuration
export type ServiceName = keyof typeof API_CONFIG.SERVICES;

// Helper pour construire les URLs
export function buildApiUrl(service: ServiceName, endpoint: string): string {
  const baseUrl = API_CONFIG.SERVICES[service];
  return `${baseUrl}${endpoint}`;
}