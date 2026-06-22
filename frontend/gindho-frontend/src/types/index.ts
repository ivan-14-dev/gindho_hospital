// Types basés sur les modèles backend GinDHO

export type Role = 
  | 'ADMIN'
  | 'SUPER_ADMIN'
  | 'MEDECIN'
  | 'PATIENT'
  | 'NURSE'
  | 'RECEPTION'
  | 'PHARMACIST'
  | 'LABORATORY'
  | 'HOSPITALIZATION_SERVICE'
  | 'ACCOUNTING'
  | 'URGENCY'
  | 'UTILISATEUR_SECONDAIRE';

export interface User {
  id: string;
  email: string;
  nom: string;
  prenom: string;
  role: Role;
  actif: boolean;
  permissions: Permission[];
}

export interface Permission {
  id: string;
  permission?: string;
  ressource?: string;
  action?: string;
  validFrom?: string;
  validTo?: string;
}

export interface AuthResponse {
  token: string;
  refreshToken?: string;
  user: User;
}

export interface Patient {
  id: string;
  nom: string;
  prenom: string;
  dateNaissance: string;
  sexe: 'M' | 'F' | 'Autre';
  email?: string;
  telephone?: string;
  adresse?: string;
  createdAt: string;
  updatedAt: string;
}

export interface RendezVous {
  id: string;
  patientId: string;
  medecinId: string;
  dateDebut: string;
  dateFin: string;
  statut: 'PLANIFIE' | 'CONFIRME' | 'ANNULE' | 'TERMINE';
  motif?: string;
  notes?: string;
  createdAt: string;
}

export interface Consultation {
  id: string;
  patientId: string;
  medecinId: string;
  date: string;
  diagnostic?: string;
  notes?: string;
  createdAt: string;
}

export interface Analyse {
  id: string;
  patientId: string;
  type: string;
  dateDemande: string;
  dateResultat?: string;
  resultat?: string;
  statut: 'DEMANDE' | 'EN_COURS' | 'TERMINE';
  medecinId: string;
}

export interface Prescription {
  id: string;
  patientId: string;
  medecinId: string;
  date: string;
  medicaments: MedicamentPrescrit[];
  statut: 'ACTIVE' | 'TERMINEE' | 'ANNULEE';
}

export interface MedicamentPrescrit {
  id: string;
  nom: string;
  posologie: string;
  voie: string;
  duree: string;
}

export interface Facture {
  id: string;
  patientId: string;
  montant: number;
  dateEmission: string;
  datePaiement?: string;
  statut: 'EN_ATTENTE' | 'PAYEE' | 'ANNULEE';
  items: FactureItem[];
}

export interface FactureItem {
  id: string;
  description: string;
  quantite: number;
  prixUnitaire: number;
  montant: number;
}

export interface Hospitalisation {
  id: string;
  patientId: string;
  litId?: string;
  dateAdmission: string;
  dateSortie?: string;
  motif?: string;
  service: string;
}

export interface Lit {
  id: string;
  numero: string;
  chambreId: string;
  occupe: boolean;
  patientId?: string;
}

export interface Chambre {
  id: string;
  numero: string;
  service: string;
  etage: number;
  lits: Lit[];
}

export interface DashboardStats {
  totalPatients: number;
  totalRendezVous: number;
  totalConsultations: number;
  totalHospitalisations: number;
  rendezVousAujourdhui: number;
  patientsParMois: { mois: string; count: number }[];
  rendezVousParStatut: { statut: string; count: number }[];
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data?: T;
  error?: string;
}