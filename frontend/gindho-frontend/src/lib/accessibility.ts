/**
 * Utilitaires d'accessibilité WCAG 2.1 AA
 */

export const ARIA_LABELS = {
  // Navigation
  MAIN_NAVIGATION: 'Navigation principale',
  SKIP_TO_CONTENT: 'Aller au contenu principal',
  MOBILE_MENU: 'Menu mobile',
  CLOSE_MENU: 'Fermer le menu',

  // Actions
  SEARCH: 'Rechercher',
  FILTER: 'Filtrer',
  SORT: 'Trier',
  ADD_NEW: 'Ajouter',
  EDIT: 'Modifier',
  DELETE: 'Supprimer',
  SAVE: 'Enregistrer',
  CANCEL: 'Annuler',
  CONFIRM: 'Confirmer',

  // Patients
  PATIENT_LIST: 'Liste des patients',
  PATIENT_DETAILS: 'Détails du patient',
  PATIENT_SEARCH: 'Rechercher un patient',

  // Rendez-vous
  APPOINTMENT_LIST: 'Liste des rendez-vous',
  APPOINTMENT_DETAILS: 'Détails du rendez-vous',
  APPOINTMENT_DATE: 'Date du rendez-vous',
  APPOINTMENT_TIME: 'Heure du rendez-vous',

  // Formulaires
  FORM_ERROR: 'Erreur de formulaire',
  REQUIRED_FIELD: 'Champ obligatoire',
  INVALID_EMAIL: 'Adresse email invalide',
  PASSWORD_MIN_LENGTH: 'Le mot de passe doit contenir au moins 8 caractères',

  // États
  LOADING: 'Chargement en cours',
  ERROR: 'Une erreur est survenue',
  SUCCESS: 'Opération réussie',
  EMPTY_STATE: 'Aucune donnée disponible',
} as const;

export const getAriaLabel = (key: keyof typeof ARIA_LABELS): string => {
  return ARIA_LABELS[key];
};

export const announceToScreenReader = (message: string) => {
  const announcement = document.createElement('div');
  announcement.setAttribute('aria-live', 'polite');
  announcement.setAttribute('aria-atomic', 'true');
  announcement.className = 'sr-only';
  announcement.textContent = message;
  document.body.appendChild(announcement);
  setTimeout(() => document.body.removeChild(announcement), 1000);
};

export const trapFocus = (element: HTMLElement) => {
  const focusableElements = element.querySelectorAll(
    'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
  );
  const firstElement = focusableElements[0] as HTMLElement;
  const lastElement = focusableElements[focusableElements.length - 1] as HTMLElement;

  element.addEventListener('keydown', (e) => {
    if (e.key === 'Tab') {
      if (e.shiftKey) {
        if (document.activeElement === firstElement) {
          lastElement.focus();
          e.preventDefault();
        }
      } else {
        if (document.activeElement === lastElement) {
          firstElement.focus();
          e.preventDefault();
        }
      }
    }
  });
};

export const checkColorContrast = (foreground: string, background: string): boolean => {
  const getLuminance = (color: string): number => {
    const rgb = color.match(/\d+/g);
    if (!rgb) return 0;
    const [r, g, b] = rgb.map(Number);
    const [rs, gs, bs] = [r, g, b].map((c) => {
      c = c / 255;
      return c <= 0.03928 ? c / 12.92 : Math.pow((c + 0.055) / 1.055, 2.4);
    });
    return 0.2126 * rs + 0.7152 * gs + 0.0722 * bs;
  };

  const l1 = getLuminance(foreground);
  const l2 = getLuminance(background);
  const ratio = (Math.max(l1, l2) + 0.05) / (Math.min(l1, l2) + 0.05);

  return ratio >= 4.5; // WCAG AA standard
};