// ===== AUTHENTICATION & USERS =====
export type UserRole = 'ADMIN' | 'DOCTOR' | 'NURSE' | 'PATIENT' | 'HR' | 'PHARMACIST' | 'RADIOLOGIST' | 'RECEPTIONIST' | 'ACCOUNTANT' | 'LABORATORY_TECHNICIAN';

export interface Permission {
  id: string;
  name: string;
  description?: string;
  ressource?: string;
  action?: string;
  validFrom?: string;
  validTo?: string;
}

export interface User {
  id: string;
  email: string;
  nom: string;
  prenom: string;
  dateNaissance?: string;
  role: UserRole;
  department?: string;
  telephone?: string;
  avatar?: string;
  specialite?: string;
  numeroLicence?: string;
  status: 'active' | 'inactive' | 'suspended';
  createdAt: string;
  updatedAt: string;
  permissions: Permission[];
  actif?: boolean;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken?: string;
  token?: string;
  user: User;
  expiresIn?: number;
}

export interface AuthCredentials {
  email: string;
  password: string;
}

// ===== PATIENTS =====
export interface Patient {
  id: string;
  numeroMedical?: string;
  nom: string;
  prenom: string;
  dateNaissance: string;
  sexe: 'M' | 'F' | 'Autre';
  genre?: 'M' | 'F' | 'OTHER';
  telephone?: string;
  email?: string;
  adresse?: string;
  codePostal?: string;
  ville?: string;
  pays?: string;
  groupeSanguin?: 'O+' | 'O-' | 'A+' | 'A-' | 'B+' | 'B-' | 'AB+' | 'AB-';
  allergie?: string[];
  allergies?: string[];
  contactUrgence?: {
    nom: string;
    telephone: string;
    relation?: string;
  };
  assurance?: InsuranceInfo;
  status?: 'active' | 'inactive' | 'discharged';
  photo?: string;
  historiqueMedical?: string;
  createdAt: string;
  updatedAt: string;
}

export interface InsuranceInfo {
  id: string;
  numeroPolice: string;
  compagnie: string;
  dateDebut: string;
  dateFin: string;
  couverturePourcentage?: number;
  status: 'active' | 'expired' | 'cancelled';
}

// ===== APPOINTMENTS (RENDEZ-VOUS) =====
export type AppointmentStatus = 'PLANIFIE' | 'CONFIRME' | 'ANNULE' | 'TERMINE' | 'SCHEDULED' | 'CONFIRMED' | 'COMPLETED' | 'CANCELLED' | 'NO_SHOW' | 'RESCHEDULED';
export type AppointmentType = 'CONSULTATION' | 'FOLLOW_UP' | 'PROCEDURE' | 'EMERGENCY' | 'ROUTINE';

export interface Appointment {
  id: string;
  patientId: string;
  medecinId: string;
  dateDebut?: string;
  dateFin?: string;
  dateHeure?: string;
  type?: AppointmentType;
  raison?: string;
  motif?: string;
  notes?: string;
  status?: 'active' | 'inactive' | 'discharged';
  resultat?: string;
  dureeMinutes?: number;
  departement?: string;
  salle?: string;
  statut?: AppointmentStatus;
  createdAt: string;
  updatedAt?: string;
}

export interface RendezVous extends Appointment {
  statut: 'PLANIFIE' | 'CONFIRME' | 'ANNULE' | 'TERMINE';
  dateFin?: string;
}

// ===== MEDICAL RECORDS (DOSSIER PATIENT) =====
export interface MedicalRecord {
  id: string;
  patientId: string;
  medecinId: string;
  dateConsultation: string;
  symptomes?: string;
  diagnostic?: string;
  traitement?: string;
  observations?: string;
  prescriptions?: Prescription[];
  examensCliniques?: string;
  resultatsTests?: string;
  status: 'draft' | 'finalized' | 'archived';
  createdAt: string;
  updatedAt: string;
}

export interface Consultation extends MedicalRecord {
  date?: string;
}

// ===== PRESCRIPTION =====
export interface Prescription {
  id: string;
  patientId?: string;
  medecinId?: string;
  medicamentId?: string;
  medicaments?: MedicamentPrescrit[];
  nomMedicament?: string;
  dosage?: string;
  frequence?: string;
  duree?: string;
  instructions?: string;
  dateDebut?: string;
  dateFin?: string;
  status?: 'ACTIVE' | 'TERMINEE' | 'ANNULEE' | 'active' | 'completed' | 'cancelled';
  date?: string;
  statut?: 'ACTIVE' | 'TERMINEE' | 'ANNULEE';
  createdAt?: string;
  updatedAt?: string;
}

export interface MedicamentPrescrit {
  id?: string;
  nom?: string;
  posologie?: string;
  voie?: string;
  duree?: string;
  dosage?: string;
  frequence?: string;
}

// ===== LABORATORY =====
export interface LaboratoryTest {
  id: string;
  patientId: string;
  type: string;
  nom?: string;
  description?: string;
  dateCommande: string;
  datePrevueResultat?: string;
  dateResultat?: string;
  resultat?: TestResult[];
  status: 'DEMANDE' | 'EN_COURS' | 'TERMINE' | 'ordered' | 'in_progress' | 'completed' | 'cancelled';
  technicienId?: string;
  medecinId?: string;
  dateDemande?: string;
  notes?: string;
  createdAt: string;
  updatedAt?: string;
  statut?: 'DEMANDE' | 'EN_COURS' | 'TERMINE';
}

export interface Analyse extends LaboratoryTest {
  statut: 'DEMANDE' | 'EN_COURS' | 'TERMINE';
  dateDemande: string;
  dateResultat?: string;
}

export interface TestResult {
  nom: string;
  valeur: string;
  unite?: string;
  valeurNormale?: string;
  interpretation?: 'normal' | 'low' | 'high' | 'critical';
}

// ===== IMAGING =====
export type ImagingType = 'XRAY' | 'CT' | 'MRI' | 'ULTRASOUND' | 'MAMMOGRAPHY' | 'FLUOROSCOPY';

export interface ImagingRequest {
  id: string;
  patientId: string;
  medecinId: string;
  type: ImagingType;
  zone?: string;
  description?: string;
  urgence?: 'routine' | 'urgent' | 'emergency';
  dateCommande: string;
  dateExamen?: string;
  dateResultat?: string;
  resultat?: {
    images?: string[];
    rapport?: string;
    radiologueId?: string;
  };
  status: 'pending' | 'scheduled' | 'completed' | 'cancelled';
  createdAt: string;
  updatedAt: string;
}

// ===== PHARMACY =====
export interface PharmacyInventory {
  id: string;
  medicamentId?: string;
  nom: string;
  description?: string;
  quantite: number;
  quantiteMinimale?: number;
  prix: number;
  prixGros?: number;
  dateExpiration?: string;
  fournisseur?: string;
  emplacementRayonnage?: string;
  formes?: string;
  dosage?: string;
  status: 'in_stock' | 'low_stock' | 'out_of_stock' | 'expired';
  createdAt: string;
  updatedAt: string;
}

export interface Dispensation {
  id: string;
  prescriptionId: string;
  patientId: string;
  medicamentId: string;
  quantiteDispensee: number;
  pharmacienId?: string;
  dateDispensation: string;
  prixTotal?: number;
  notes?: string;
  status: 'dispensed' | 'returned' | 'cancelled';
}

// ===== ADMISSIONS & DISCHARGES =====
export type AdmissionType = 'EMERGENCY' | 'PLANNED' | 'TRANSFER';
export type DischargeMotivation = 'HEALED' | 'IMPROVED' | 'UNCHANGED' | 'WORSENED' | 'DECEASED' | 'AGAINST_ADVICE' | 'TRANSFER';

export interface Admission {
  id: string;
  patientId: string;
  medecinAdmettantId?: string;
  litId?: string;
  dateAdmission: string;
  heureAdmission?: string;
  typeAdmission?: AdmissionType;
  raison?: string;
  diagnosticPrincipal?: string;
  diagnosticSecondaire?: string[];
  priorite?: 'urgent' | 'non-urgent' | 'planifiee';
  accompagnant?: string;
  teleAccompagnant?: string;
  status: 'admitted' | 'hospitalized' | 'discharged' | 'transferred' | 'deceased';
  service?: string;
  dateSortie?: string;
  motif?: string;
  createdAt: string;
  updatedAt: string;
}

export interface Hospitalisation extends Admission {
  litId?: string;
  dateSortie?: string;
  service: string;
  motif?: string;
}

export interface Discharge {
  id: string;
  admissionId: string;
  patientId: string;
  medecinDechargerId?: string;
  dateDecharge: string;
  heureDecharge?: string;
  etatSortie?: string;
  prescriptionsFinales?: Prescription[];
  consignesFollowUp?: string;
  certificatMedical?: string;
  rendezVousSuivi?: string;
  motivation?: DischargeMotivation;
  destinationSortie?: string;
  createdAt: string;
  updatedAt: string;
}

// ===== BEDS & WARDS =====
export type BedType = 'STANDARD' | 'ICU' | 'PEDIATRIC' | 'PSYCHIATRIC' | 'ISOLATION' | 'RECOVERY';
export type BedStatus = 'available' | 'occupied' | 'maintenance' | 'cleaning' | 'reserved' | 'out_of_service';

export interface Ward {
  id: string;
  nom: string;
  description?: string;
  departement?: string;
  nombreLits?: number;
  typeLits?: string;
  chefServiceId?: string;
  infirmierChefId?: string;
  capaciteActuelle?: number;
  status: 'active' | 'inactive' | 'maintenance';
  numero?: string;
  service?: string;
  etage?: number;
  createdAt: string;
  updatedAt: string;
}

export interface Bed {
  id: string;
  wardId?: string;
  chambreId?: string;
  numero: string;
  type?: BedType;
  status: BedStatus | 'AVAILABLE' | 'OCCUPIED' | 'MAINTENANCE' | 'CLEANING';
  patientActuelId?: string;
  patientId?: string;
  dateOccupation?: string;
  dateLiberation?: string;
  notes?: string;
  niveauHygiene?: 'excellent' | 'good' | 'satisfactory' | 'poor';
  maintenancePrevueDate?: string;
  occupe?: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Lit extends Bed {
  numero: string;
  occupe: boolean;
}

export interface Chambre extends Ward {
  numero: string;
  service: string;
  etage: number;
  lits?: Lit[];
}

// ===== SURGERY =====
export interface SurgerySchedule {
  id: string;
  patientId: string;
  chirurgienId: string;
  infirmierSalleId?: string;
  anesthesianId?: string;
  typeIntervention: string;
  description?: string;
  datePrevu: string;
  heureDebut: string;
  heureFin?: string;
  saleOperatoireId?: string;
  typeAnesthesie?: string;
  dureePrevisionnelle?: number;
  priorite?: 'urgent' | 'routine' | 'electif';
  examensPreoperatoires?: string[];
  status: 'scheduled' | 'confirmed' | 'in_progress' | 'completed' | 'cancelled';
  rapport?: SurgeryReport;
  createdAt: string;
  updatedAt: string;
}

export interface SurgeryReport {
  id: string;
  chirurgienId: string;
  descriptionIntervention: string;
  complicationsPeroperatoires?: string;
  dureeActuelle?: number;
  quantiteSangPedue?: number;
  materiausUtilises?: string[];
  observations?: string;
  dateRedaction: string;
}

export interface OperatingRoom {
  id: string;
  numero: string;
  nom?: string;
  capacite?: number;
  equipements?: string[];
  derniereMaintenanceDate?: string;
  prochainMaintenanceDate?: string;
  status: 'available' | 'occupied' | 'maintenance' | 'cleaning';
  createdAt: string;
  updatedAt: string;
}

// ===== BILLING & PAYMENTS =====
export interface Invoice {
  id: string;
  patientId: string;
  numeroFacture?: string;
  dateEmission: string;
  dateDue?: string;
  montantTotal: number;
  montantAyantDroit?: number;
  montantPatient?: number;
  montantAssurance?: number;
  montantPaye?: number;
  devise?: string;
  status: 'EN_ATTENTE' | 'PAYEE' | 'ANNULEE' | 'draft' | 'sent' | 'partial' | 'paid' | 'overdue' | 'cancelled';
  statut?: 'EN_ATTENTE' | 'PAYEE' | 'ANNULEE';
  details?: LineItem[];
  items?: FactureItem[];
  notes?: string;
  createdAt: string;
  updatedAt: string;
}

export interface Facture extends Invoice {
  statut: 'EN_ATTENTE' | 'PAYEE' | 'ANNULEE';
  items: FactureItem[];
}

export interface LineItem {
  id?: string;
  description: string;
  quantite: number;
  prixUnitaire: number;
  montant?: number;
  montantTotal?: number;
  type?: 'consultation' | 'procedure' | 'medication' | 'bed' | 'test' | 'imaging' | 'other';
  referenceServiceId?: string;
}

export interface FactureItem extends LineItem {
  montant: number;
}

export interface Payment {
  id: string;
  factureId?: string;
  invoiceId?: string;
  montant: number;
  methode?: PaymentMethod;
  datePayment: string;
  datePaiement?: string;
  referencePayment?: string;
  status: 'pending' | 'success' | 'failed' | 'refunded';
  beneficiaire?: string;
  notes?: string;
  createdAt: string;
  updatedAt: string;
}

export type PaymentMethod = 'CASH' | 'CARD' | 'CHECK' | 'TRANSFER' | 'INSURANCE' | 'OTHER';

// ===== INVENTORY & PROCUREMENT =====
export interface InventoryItem {
  id: string;
  code?: string;
  nom: string;
  description?: string;
  categorie?: string;
  quantite: number;
  quantiteMinimale?: number;
  quantiteMaximale?: number;
  unite?: string;
  prix: number;
  fournisseurId?: string;
  dateAcquisition?: string;
  dateExpiration?: string;
  emplacementRayonnage?: string;
  status: 'in_stock' | 'low_stock' | 'out_of_stock' | 'expired' | 'discontinued';
  createdAt: string;
  updatedAt: string;
}

export interface ProcurementOrder {
  id: string;
  numero?: string;
  fournisseurId: string;
  dateCommande: string;
  dateLivraisonPrevue?: string;
  dateLivraisonActuelle?: string;
  montantTotal: number;
  status: 'draft' | 'submitted' | 'confirmed' | 'partial_delivery' | 'completed' | 'cancelled';
  items: OrderItem[];
  notes?: string;
  createdAt: string;
  updatedAt: string;
}

export interface OrderItem {
  id?: string;
  inventoryItemId?: string;
  quantite: number;
  prixUnitaire: number;
  montant?: number;
  montantTotal?: number;
  quantiteRecue?: number;
  dateReception?: string;
}

export interface Supplier {
  id: string;
  nom: string;
  contactPrincipal?: string;
  email?: string;
  telephone?: string;
  adresse?: string;
  codePostal?: string;
  ville?: string;
  pays?: string;
  delaivraison?: number;
  conditionsPayment?: string;
  status: 'active' | 'inactive' | 'suspended';
  ratingQualite?: number;
  createdAt: string;
  updatedAt: string;
}

// ===== HR & SCHEDULING =====
export type LeaveType = 'VACATION' | 'SICK' | 'MATERNITY' | 'PATERNITY' | 'UNPAID' | 'OTHER';
export type ScheduleType = 'MORNING' | 'AFTERNOON' | 'NIGHT' | 'FULL_DAY' | 'FLEXIBLE';

export interface Employee {
  id: string;
  numeroEmploye?: string;
  nom: string;
  prenom: string;
  dateNaissance?: string;
  genre?: 'M' | 'F' | 'OTHER';
  poste?: string;
  departement?: string;
  dateEmbauche?: string;
  salaire?: number;
  typeContrat?: 'CDI' | 'CDD' | 'FREELANCE' | 'STAGE';
  telephone?: string;
  email?: string;
  adresse?: string;
  numeroSecu?: string;
  qualifications?: string[];
  certifications?: string[];
  status: 'active' | 'on_leave' | 'inactive' | 'retired';
  supervisorId?: string;
  createdAt: string;
  updatedAt: string;
}

export interface Schedule {
  id: string;
  employeeId: string;
  dateDebut: string;
  dateFin: string;
  typeHoraire?: ScheduleType;
  heureDebut: string;
  heureFin: string;
  pauses?: Break[];
  status: 'scheduled' | 'confirmed' | 'completed' | 'cancelled';
  createdAt: string;
  updatedAt: string;
}

export interface Break {
  debut: string;
  fin: string;
  type?: 'LUNCH' | 'COFFEE' | 'OTHER';
}

export interface Leave {
  id: string;
  employeeId: string;
  type?: LeaveType;
  dateDebut: string;
  dateFin: string;
  nombreJours?: number;
  raison?: string;
  status: 'requested' | 'approved' | 'rejected' | 'cancelled';
  approverIds?: string[];
  createdAt: string;
  updatedAt: string;
}

// ===== AMBULANCE =====
export interface AmbulanceRequest {
  id: string;
  patientId: string;
  pointDepart?: {
    adresse: string;
    lat?: number;
    lng?: number;
  };
  pointDestination?: {
    adresse: string;
    lat?: number;
    lng?: number;
  };
  dateHeureCommande: string;
  typeTransport?: 'EMERGENCY' | 'ROUTINE' | 'INTER_HOSPITAL';
  conducteurId?: string;
  ambulanceId?: string;
  priorite?: 'urgent' | 'non-urgent';
  notes?: string;
  status: 'requested' | 'assigned' | 'in_transit' | 'arrived' | 'completed' | 'cancelled';
  dateHeureArriveeEstimee?: string;
  dateHeureArriveActuelle?: string;
  createdAt: string;
  updatedAt: string;
}

export interface Ambulance {
  id: string;
  numeroImmatriculation: string;
  marque?: string;
  modele?: string;
  capacite?: number;
  equipements?: string[];
  derniereRevision?: string;
  prochainRevision?: string;
  conducteur1Id?: string;
  conducteur2Id?: string;
  status: 'available' | 'in_transit' | 'maintenance' | 'inactive';
  localisationActuelle?: {
    lat: number;
    lng: number;
  };
  createdAt: string;
  updatedAt: string;
}

// ===== NOTIFICATIONS =====
export type NotificationType = 'INFO' | 'WARNING' | 'ERROR' | 'SUCCESS' | 'ALERT' | 'REMINDER' | 'APPOINTMENT' | 'RESULT';

export interface Notification {
  id: string;
  userId: string;
  titre: string;
  message: string;
  type: NotificationType;
  priorite?: 'low' | 'medium' | 'high' | 'critical';
  donnees?: Record<string, any>;
  lu?: boolean;
  dateCreation: string;
  dateExpiration?: string;
  actions?: NotificationAction[];
}

export interface NotificationAction {
  label: string;
  url: string;
  type?: 'link' | 'action';
}

// ===== AUDIT LOGS =====
export interface AuditLog {
  id: string;
  userId: string;
  action: string;
  ressource: string;
  ressourceId: string;
  anciennesValeurs?: Record<string, any>;
  nouvellesValeurs?: Record<string, any>;
  adresseIP?: string;
  userAgent?: string;
  status: 'success' | 'failure';
  erreur?: string;
  timestamp: string;
}

// ===== EVENTS & ROUNDS =====
export interface MedicalRound {
  id: string;
  wardId: string;
  medecinId: string;
  dateHeure: string;
  patients?: string[];
  observations?: string;
  status: 'scheduled' | 'in_progress' | 'completed' | 'cancelled';
  dureeMinutes?: number;
  createdAt: string;
  updatedAt: string;
}

export interface Event {
  id: string;
  titre: string;
  description?: string;
  type?: string;
  dateHeure: string;
  lieu?: string;
  organisateurId: string;
  participants?: string[];
  statut?: 'planned' | 'ongoing' | 'completed' | 'cancelled';
  createdAt: string;
  updatedAt: string;
}

// ===== QUALITY & INCIDENTS =====
export type IncidentType = 'PATIENT_SAFETY' | 'INFECTION' | 'MEDICATION_ERROR' | 'EQUIPMENT_FAILURE' | 'STAFFING' | 'COMMUNICATION' | 'OTHER';

export interface QualityIncident {
  id: string;
  titre: string;
  description: string;
  type: IncidentType;
  severite?: 'low' | 'medium' | 'high' | 'critical';
  dateIncident: string;
  departement?: string;
  personnesImpliquees?: string[];
  actionsCorrectives?: string;
  status: 'open' | 'in_investigation' | 'resolved' | 'closed';
  dateResolution?: string;
  createdAt: string;
  updatedAt: string;
}

// ===== PAGINATION & FILTERS =====
export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages?: number;
  currentPage?: number;
  pageSize?: number;
  hasNextPage?: boolean;
  hasPreviousPage?: boolean;
}

export interface ApiError {
  code?: string;
  message: string;
  details?: Record<string, any>;
  timestamp?: string;
  error?: string;
}

export interface ApiResponse<T = any> {
  success: boolean;
  data?: T;
  error?: ApiError;
  meta?: {
    timestamp: string;
    path: string;
    status: number;
  };
  message?: string;
}

// ===== REPORTING & DASHBOARDS =====
export type ReportType = 'PATIENT_SUMMARY' | 'ADMISSION_DISCHARGE' | 'FINANCIAL' | 'HR' | 'INVENTORY' | 'OCCUPANCY' | 'CLINICAL' | 'QUALITY';
export type WidgetType = 'CHART' | 'TABLE' | 'KPI' | 'GAUGE' | 'TIMELINE' | 'MAP' | 'CALENDAR' | 'ALERT';

export interface Report {
  id: string;
  titre: string;
  type: ReportType;
  dateGeneration: string;
  dateDebut?: string;
  dateFin?: string;
  parametres?: Record<string, any>;
  donnees: any;
  format?: 'pdf' | 'excel' | 'csv' | 'json';
  generePar: string;
  url?: string;
  status: 'generating' | 'ready' | 'expired';
}

export interface DashboardWidget {
  id: string;
  type: WidgetType;
  titre: string;
  donnees?: any;
  configuration?: Record<string, any>;
  position?: { x: number; y: number };
}

export interface Dashboard {
  id: string;
  titre: string;
  role?: UserRole;
  widgets: DashboardWidget[];
  theme?: 'light' | 'dark' | 'auto';
  refreshInterval?: number;
}

export interface DashboardStats {
  totalPatients: number;
  totalAppointments?: number;
  totalRendezVous?: number;
  totalConsultations?: number;
  totalHospitalisations?: number;
  rendezVousAujourdhui?: number;
  patientsParMois?: { mois: string; count: number }[];
  rendezVousParStatut?: { statut: string; count: number }[];
  occupancyRate?: number;
  revenue?: number;
  pendingInvoices?: number;
}
