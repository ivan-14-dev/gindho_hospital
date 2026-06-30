'use client';

import { useState } from 'react';
import { Button } from '@/components/ui/button';
import {
  BarChart3, Users, Stethoscope, Building2, Pill, Beaker,
  Microscope, Droplet, Heart, DollarSign, UserCheck, TrendingUp,
  FileText, ChevronLeft, ChevronRight, Activity, CheckCircle2
} from 'lucide-react';
import { cn } from '@/lib/utils';

interface NavItem {
  id: string;
  label: string;
  icon: any;
  color: string;
}

interface AnalyticsLayoutProps {
  children: React.ReactNode;
  currentSection: string;
  onSectionChange: (section: string) => void;
}

const navItems: NavItem[] = [
  { id: 'executive', label: 'Vue Exécutive', icon: BarChart3, color: 'text-blue-600' },
  { id: 'activity', label: 'Activité Hospitalière', icon: Activity, color: 'text-green-600' },
  { id: 'patients', label: 'Patients', icon: Users, color: 'text-purple-600' },
  { id: 'consultations', label: 'Consultations', icon: Stethoscope, color: 'text-orange-600' },
  { id: 'hospitalizations', label: 'Hospitalisations', icon: Building2, color: 'text-red-600' },
  { id: 'surgery', label: 'Bloc Opératoire', icon: Heart, color: 'text-pink-600' },
  { id: 'pharmacy', label: 'Pharmacie', icon: Pill, color: 'text-cyan-600' },
  { id: 'laboratory', label: 'Laboratoire', icon: Beaker, color: 'text-indigo-600' },
  { id: 'imaging', label: 'Radiologie', icon: Microscope, color: 'text-violet-600' },
  { id: 'blood', label: 'Banque de Sang', icon: Droplet, color: 'text-red-500' },
  { id: 'finance', label: 'Finances', icon: DollarSign, color: 'text-green-500' },
  { id: 'hr', label: 'Ressources Humaines', icon: UserCheck, color: 'text-yellow-600' },
  { id: 'quality', label: 'Qualité des Soins', icon: CheckCircle2, color: 'text-teal-600' },
  { id: 'epidemiology', label: 'Épidémiologie', icon: TrendingUp, color: 'text-gray-600' },
  { id: 'satisfaction', label: 'Satisfaction', icon: Heart, color: 'text-rose-600' },
  { id: 'reports', label: 'Rapports', icon: FileText, color: 'text-slate-600' },
];

export function AnalyticsLayout({ children, currentSection, onSectionChange }: AnalyticsLayoutProps) {
  const [sidebarOpen, setSidebarOpen] = useState(true);

  return (
    <div className="flex h-[calc(100vh-120px)] gap-4">
      {/* Sidebar */}
      <div
        className={cn(
          'transition-all duration-300 border-r bg-muted/30 overflow-y-auto',
          sidebarOpen ? 'w-64' : 'w-20'
        )}
      >
        <div className="p-4 space-y-2">
          <button
            onClick={() => setSidebarOpen(!sidebarOpen)}
            className="w-full flex justify-end mb-4"
          >
            {sidebarOpen ? (
              <ChevronLeft className="h-4 w-4" />
            ) : (
              <ChevronRight className="h-4 w-4" />
            )}
          </button>

          {navItems.map((item) => {
            const Icon = item.icon;
            const isActive = currentSection === item.id;

            return (
              <Button
                key={item.id}
                variant={isActive ? 'default' : 'ghost'}
                size={sidebarOpen ? 'sm' : 'icon'}
                className={cn(
                  'w-full justify-start gap-2',
                  isActive && 'bg-primary text-primary-foreground',
                  !sidebarOpen && 'justify-center'
                )}
                onClick={() => onSectionChange(item.id)}
                title={sidebarOpen ? undefined : item.label}
              >
                <Icon className={cn('h-4 w-4 flex-shrink-0', !isActive && item.color)} />
                {sidebarOpen && <span className="truncate text-xs">{item.label}</span>}
              </Button>
            );
          })}
        </div>
      </div>

      {/* Main Content */}
      <div className="flex-1 overflow-y-auto">
        {children}
      </div>
    </div>
  );
}
