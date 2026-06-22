import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import LanguageDetector from 'i18next-browser-languagedetector';

const resources = {
  fr: {
    translation: {
      nav: {
        dashboard: 'Dashboard',
        patients: 'Patients',
        appointments: 'Rendez-vous',
        medicalRecords: 'Dossier Médical',
        laboratory: 'Laboratoire',
        pharmacy: 'Pharmacie',
        payments: 'Facturation',
        analytics: 'Analytics',
        aiAssistant: 'Assistant IA',
        settings: 'Paramètres',
        hospitalization: 'Hospitalisation',
      },
      common: {
        loading: 'Chargement...',
        error: 'Une erreur est survenue',
        save: 'Enregistrer',
        cancel: 'Annuler',
        delete: 'Supprimer',
        edit: 'Modifier',
        search: 'Rechercher',
        new: 'Nouveau',
        submit: 'Soumettre',
        noData: 'Aucune donnée disponible',
      },
      auth: {
        login: 'Connexion',
        register: 'Inscription',
        logout: 'Déconnexion',
        email: 'Email',
        password: 'Mot de passe',
        firstName: 'Prénom',
        lastName: 'Nom',
        signIn: 'Se connecter',
        signUp: 'S\'inscrire',
      },
      patients: {
        title: 'Patients',
        searchPlaceholder: 'Rechercher un patient...',
        newPatient: 'Nouveau patient',
        name: 'Nom',
        firstName: 'Prénom',
        birthDate: 'Date de naissance',
        gender: 'Sexe',
        male: 'Masculin',
        female: 'Féminin',
        other: 'Autre',
        phone: 'Téléphone',
        address: 'Adresse',
        noPatients: 'Aucun patient trouvé',
      },
      appointments: {
        title: 'Rendez-vous',
        newAppointment: 'Nouveau rendez-vous',
        patient: 'Patient',
        doctor: 'Médecin',
        date: 'Date',
        time: 'Heure',
        reason: 'Motif',
        notes: 'Notes',
      },
      medicalRecords: {
        title: 'Dossier Médical',
        consultations: 'Consultations',
        analyses: 'Analyses',
        prescriptions: 'Prescriptions',
      },
      ui: {
        status: {
          planned: 'Planifié',
          confirmed: 'Confirmé',
          cancelled: 'Annulé',
          completed: 'Terminé',
        },
      },
    },
  },
  en: {
    translation: {
      nav: {
        dashboard: 'Dashboard',
        patients: 'Patients',
        appointments: 'Appointments',
        medicalRecords: 'Medical Records',
        laboratory: 'Laboratory',
        pharmacy: 'Pharmacy',
        payments: 'Billing',
        analytics: 'Analytics',
        aiAssistant: 'AI Assistant',
        settings: 'Settings',
        hospitalization: 'Hospitalization',
      },
      common: {
        loading: 'Loading...',
        error: 'An error occurred',
        save: 'Save',
        cancel: 'Cancel',
        delete: 'Delete',
        edit: 'Edit',
        search: 'Search',
        new: 'New',
        submit: 'Submit',
        noData: 'No data available',
      },
      auth: {
        login: 'Login',
        register: 'Register',
        logout: 'Logout',
        email: 'Email',
        password: 'Password',
        firstName: 'First Name',
        lastName: 'Last Name',
        signIn: 'Sign In',
        signUp: 'Sign Up',
      },
      patients: {
        title: 'Patients',
        searchPlaceholder: 'Search for a patient...',
        newPatient: 'New patient',
        name: 'Last Name',
        firstName: 'First Name',
        birthDate: 'Birth Date',
        gender: 'Gender',
        male: 'Male',
        female: 'Female',
        other: 'Other',
        phone: 'Phone',
        address: 'Address',
        noPatients: 'No patients found',
      },
      appointments: {
        title: 'Appointments',
        newAppointment: 'New appointment',
        patient: 'Patient',
        doctor: 'Doctor',
        date: 'Date',
        time: 'Time',
        reason: 'Reason',
        notes: 'Notes',
      },
      medicalRecords: {
        title: 'Medical Records',
        consultations: 'Consultations',
        analyses: 'Analyses',
        prescriptions: 'Prescriptions',
      },
      ui: {
        status: {
          planned: 'Planned',
          confirmed: 'Confirmed',
          cancelled: 'Cancelled',
          completed: 'Completed',
        },
      },
    },
  },
};

i18n
  .use(LanguageDetector)
  .use(initReactI18next)
  .init({
    resources,
    fallbackLng: 'fr',
    supportedLngs: ['fr', 'en'],
    interpolation: {
      escapeValue: false,
    },
  });

export default i18n;