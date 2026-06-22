import { z } from 'zod';

// Schéma de connexion
export const loginSchema = z.object({
  email: z.string().email('Email invalide'),
  password: z.string().min(6, 'Le mot de passe doit contenir au moins 6 caractères'),
});

// Schéma d'inscription
export const registerSchema = z.object({
  email: z.string().email('Email invalide'),
  password: z.string().min(6, 'Le mot de passe doit contenir au moins 6 caractères'),
  confirmPassword: z.string(),
  nom: z.string().min(2, 'Le nom doit contenir au moins 2 caractères'),
  prenom: z.string().min(2, 'Le prénom doit contenir au moins 2 caractères'),
}).refine((data) => data.password === data.confirmPassword, {
  message: 'Les mots de passe ne correspondent pas',
  path: ['confirmPassword'],
});

// Schéma de patient
export const patientSchema = z.object({
  nom: z.string().min(2, 'Le nom doit contenir au moins 2 caractères'),
  prenom: z.string().min(2, 'Le prénom doit contenir au moins 2 caractères'),
  dateNaissance: z.string().min(1, 'La date de naissance est requise'),
  sexe: z.enum(['M', 'F', 'Autre']),
  email: z.string().email('Email invalide').optional().or(z.literal('')),
  telephone: z.string().optional().or(z.literal('')),
  adresse: z.string().optional().or(z.literal('')),
});

// Schéma de rendez-vous
export const rendezVousSchema = z.object({
  patientId: z.string().min(1, 'Le patient est requis'),
  medecinId: z.string().min(1, 'Le médecin est requis'),
  dateDebut: z.string().min(1, 'La date de début est requise'),
  dateFin: z.string().min(1, 'La date de fin est requise'),
  motif: z.string().optional().or(z.literal('')),
  notes: z.string().optional().or(z.literal('')),
});

// Types dérivés des schémas
export type LoginFormData = z.infer<typeof loginSchema>;
export type RegisterFormData = z.infer<typeof registerSchema>;
export type PatientFormData = z.infer<typeof patientSchema>;
export type RendezVousFormData = z.infer<typeof rendezVousSchema>;