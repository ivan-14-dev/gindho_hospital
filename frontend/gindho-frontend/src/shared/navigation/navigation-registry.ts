import {
  LayoutDashboard,
  Users,
  Calendar,
  FileText,
  FlaskRound,
  Pill,
  DollarSign,
  Bed,
  Settings,
  BarChart3,
  Bot,
  Siren,
  Truck,
  Video,
  Activity,
  ShoppingCart,
  Package,
  ShieldAlert,
  CalendarDays,
  Archive,
  AlertTriangle,
  Scissors,
  Bell,
  FileBarChart,
  Stethoscope
} from 'lucide-react';

type Role = string;

export type NavigationPermission = `${string}:${'READ' | 'WRITE' | 'DELETE' | 'ADMIN'}`;

export interface NavigationItem {
  id: string;
  label: string;
  path: string;
  icon: React.ComponentType<{ className?: string }>;
  roles?: Role[];
  permissions?: NavigationPermission[];
  visible?: (user: { role: Role; permissions: NavigationPermission[] }) => boolean;
  children?: NavigationItem[];
}

export const NAVIGATION_REGISTRY: NavigationItem[] = [
  {
    id: 'dashboard',
    label: 'Dashboard',
    path: '/',
    icon: LayoutDashboard,
    roles: ['ADMIN', 'SUPER_ADMIN', 'MEDECIN', 'NURSE', 'RECEPTION', 'PHARMACIST', 'LABORATORY', 'ACCOUNTING', 'URGENCY', 'HOSPITALIZATION_SERVICE', 'PATIENT'],
  },
  {
    id: 'patients',
    label: 'Patients',
    path: '/patients',
    icon: Users,
    roles: ['ADMIN', 'SUPER_ADMIN', 'MEDECIN', 'NURSE', 'RECEPTION'],
    permissions: ['patients:READ'],
  },
  {
    id: 'appointments',
    label: 'Rendez-vous',
    path: '/appointments',
    icon: Calendar,
    roles: ['ADMIN', 'SUPER_ADMIN', 'MEDECIN', 'RECEPTION'],
    permissions: ['appointments:READ'],
  },
  {
    id: 'medical-records',
    label: 'Dossier Médical',
    path: '/medical-records',
    icon: FileText,
    roles: ['ADMIN', 'SUPER_ADMIN', 'MEDECIN', 'NURSE'],
    permissions: ['medical_records:READ'],
  },
  {
    id: 'laboratory',
    label: 'Laboratoire',
    path: '/lab',
    icon: FlaskRound,
    roles: ['ADMIN', 'SUPER_ADMIN', 'MEDECIN', 'LABORATORY', 'NURSE'],
    permissions: ['laboratory:READ'],
  },
  {
    id: 'pharmacy',
    label: 'Pharmacie',
    path: '/medications',
    icon: Pill,
    roles: ['ADMIN', 'SUPER_ADMIN', 'MEDECIN', 'PHARMACIST', 'NURSE'],
    permissions: ['pharmacy:READ'],
  },
  {
    id: 'payments',
    label: 'Paiements',
    path: '/payments',
    icon: DollarSign,
    roles: ['ADMIN', 'SUPER_ADMIN', 'ACCOUNTING'],
    permissions: ['payments:READ'],
  },
  {
    id: 'hospitalization',
    label: 'Hospitalisation (Admissions)',
    path: '/hospitalization',
    icon: Bed,
    roles: ['ADMIN', 'SUPER_ADMIN', 'NURSE', 'HOSPITALIZATION_SERVICE'],
    permissions: ['hospitalization:READ'],
  },
  {
    id: 'wards',
    label: 'Lits & Chambres',
    path: '/wards',
    icon: Bed,
    roles: ['ADMIN', 'SUPER_ADMIN', 'NURSE', 'HOSPITALIZATION_SERVICE', 'MEDECIN'],
    permissions: ['wards:READ'],
  },
  {
    id: 'nursing',
    label: 'Soins Infirmiers',
    path: '/nursing',
    icon: Activity,
    roles: ['ADMIN', 'SUPER_ADMIN', 'NURSE', 'MEDECIN'],
    permissions: ['nursing:READ'],
  },
  {
    id: 'rounds',
    label: 'Visites Médicales',
    path: '/rounds',
    icon: Stethoscope,
    roles: ['ADMIN', 'SUPER_ADMIN', 'MEDECIN', 'NURSE'],
    permissions: ['rounds:READ'],
  },
  {
    id: 'ambulance',
    label: 'Ambulance',
    path: '/ambulance',
    icon: Truck,
    roles: ['ADMIN', 'SUPER_ADMIN', 'URGENCY', 'RECEPTION'],
    permissions: ['ambulance:READ'],
  },
  {
    id: 'teleconsultation',
    label: 'Téléconsultations',
    path: '/teleconsultation',
    icon: Video,
    roles: ['ADMIN', 'SUPER_ADMIN', 'MEDECIN', 'NURSE', 'PATIENT'],
    permissions: ['teleconsultation:READ'],
  },
  {
    id: 'procurement',
    label: 'Approvisionnements',
    path: '/procurement',
    icon: ShoppingCart,
    roles: ['ADMIN', 'SUPER_ADMIN', 'ACCOUNTING'],
    permissions: ['procurement:READ'],
  },
  {
    id: 'assets',
    label: 'Équipements',
    path: '/assets',
    icon: Package,
    roles: ['ADMIN', 'SUPER_ADMIN'],
    permissions: ['assets:READ'],
  },
  {
    id: 'audit',
    label: 'Logs d\'Audit',
    path: '/audit',
    icon: ShieldAlert,
    roles: ['ADMIN', 'SUPER_ADMIN'],
    permissions: ['audit:READ'],
  },
  {
    id: 'events',
    label: 'Événements',
    path: '/events',
    icon: CalendarDays,
    roles: ['ADMIN', 'SUPER_ADMIN', 'MEDECIN', 'NURSE', 'RECEPTION', 'PHARMACIST', 'LABORATORY', 'ACCOUNTING'],
    permissions: ['events:READ'],
  },
  {
    id: 'hr',
    label: 'Personnel & RH',
    path: '/hr',
    icon: Users,
    roles: ['ADMIN', 'SUPER_ADMIN'],
    permissions: ['hr:READ'],
  },
  {
    id: 'inventory',
    label: 'Gestion des Stocks',
    path: '/inventory',
    icon: Archive,
    roles: ['ADMIN', 'SUPER_ADMIN', 'PHARMACIST', 'LABORATORY'],
    permissions: ['inventory:READ'],
  },
  {
    id: 'billing',
    label: 'Facturation (Invoices)',
    path: '/billing',
    icon: FileText,
    roles: ['ADMIN', 'SUPER_ADMIN', 'ACCOUNTING'],
    permissions: ['billing:READ'],
  },
  {
    id: 'quality',
    label: 'Qualité & Incidents',
    path: '/quality',
    icon: AlertTriangle,
    roles: ['ADMIN', 'SUPER_ADMIN', 'MEDECIN', 'NURSE'],
    permissions: ['quality:READ'],
  },
  {
    id: 'surgery',
    label: 'Chirurgie & Blocs',
    path: '/surgery',
    icon: Scissors,
    roles: ['ADMIN', 'SUPER_ADMIN', 'MEDECIN', 'NURSE'],
    permissions: ['surgery:READ'],
  },
  {
    id: 'notifications',
    label: 'Notifications',
    path: '/notifications',
    icon: Bell,
    roles: ['ADMIN', 'SUPER_ADMIN', 'MEDECIN', 'NURSE', 'RECEPTION', 'PHARMACIST', 'LABORATORY', 'ACCOUNTING', 'URGENCY', 'HOSPITALIZATION_SERVICE', 'PATIENT'],
    permissions: ['notifications:READ'],
  },
  {
    id: 'reporting',
    label: 'Rapports & PDF',
    path: '/reporting',
    icon: FileBarChart,
    roles: ['ADMIN', 'SUPER_ADMIN', 'ACCOUNTING'],
    permissions: ['reporting:READ'],
  },
  {
    id: 'analytics',
    label: 'Analytics',
    path: '/analytics',
    icon: BarChart3,
    roles: ['ADMIN', 'SUPER_ADMIN', 'ACCOUNTING'],
    permissions: ['analytics:READ'],
  },
  {
    id: 'ai-assistant',
    label: 'Assistant IA',
    path: '/ai-assistant',
    icon: Bot,
    roles: ['ADMIN', 'SUPER_ADMIN', 'MEDECIN', 'NURSE'],
    permissions: ['ai:READ'],
    visible: (user) => user.permissions.includes('ai:READ'),
  },
  {
    id: 'emergency',
    label: 'Urgences',
    path: '/emergency',
    icon: Siren,
    roles: ['ADMIN', 'SUPER_ADMIN', 'MEDECIN', 'NURSE', 'URGENCY'],
    permissions: ['emergency:READ'],
  },
  {
    id: 'settings',
    label: 'Paramètres',
    path: '/settings',
    icon: Settings,
    roles: ['ADMIN', 'SUPER_ADMIN'],
    permissions: ['settings:ADMIN'],
  },
];

export function filterNavigationByRole(
  items: NavigationItem[],
  userRole: Role,
  userPermissions: NavigationPermission[]
): NavigationItem[] {
  return items.filter((item) => {
    if (item.roles && !item.roles.includes(userRole)) {
      return false;
    }
    if (item.permissions && !item.permissions.some((p) => userPermissions.includes(p))) {
      return false;
    }
    if (item.visible) {
      return item.visible({ role: userRole, permissions: userPermissions });
    }
    return true;
  });
}

export function getNavigationItem(id: string): NavigationItem | undefined {
  return NAVIGATION_REGISTRY.find((item) => item.id === id);
}

export function getBreadcrumbs(pathname: string): NavigationItem[] {
  const item = NAVIGATION_REGISTRY.find((item) => item.path === pathname);
  if (!item) return [];
  return [
    { id: 'home', label: 'Accueil', path: '/', icon: LayoutDashboard },
    item,
  ];
}