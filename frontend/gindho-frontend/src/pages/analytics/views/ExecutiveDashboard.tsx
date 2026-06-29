'use client';

import { useQuery } from '@tanstack/react-query';
import { KPICard } from '../components/KPICard';
import { ChartCard } from '../components/ChartCard';
import { FilterBar } from '../components/FilterBar';
import { apiClient } from '@/lib/api-client';
import {
  Users, Stethoscope, Building2, LogOut, Bed, BarChart3,
  TrendingUp, DollarSign, Heart, Clock, Smile
} from 'lucide-react';
import { useState } from 'react';

interface ExecutiveMetrics {
  totalPatients: number;
  consultationsToday: number;
  admissions: number;
  discharges: number;
  hospitalized: number;
  bedsAvailable: number;
  occupancyRate: number;
  waitTime: number;
  revenue: number;
  expenses: number;
  surgeries: number;
  satisfaction: number;
}

export function ExecutiveDashboard() {
  const [dateRange, setDateRange] = useState({ start: '2024-01-01', end: '2024-06-30' });
  const [period, setPeriod] = useState<'day' | 'week' | 'month' | 'year' | 'custom'>('month');
  const [filters, setFilters] = useState<Record<string, string>>({});

  const { data: metrics = {} as ExecutiveMetrics, isLoading } = useQuery({
    queryKey: ['executive-metrics', dateRange, period, filters],
    queryFn: async () => {
      const response = await apiClient.get('/analytics-service/executive-metrics', {
        params: {
          startDate: dateRange.start,
          endDate: dateRange.end,
          period,
          ...filters,
        },
      });
      return response.data || {};
    },
  });

  const kpiData = [
    {
      title: 'Patients Total',
      value: metrics.totalPatients || 0,
      change: 12.5,
      trend: 'up' as const,
      icon: Users,
      color: 'text-blue-600',
    },
    {
      title: 'Consultations Aujourd\'hui',
      value: metrics.consultationsToday || 0,
      change: 8.2,
      trend: 'up' as const,
      icon: Stethoscope,
      color: 'text-orange-600',
    },
    {
      title: 'Admissions',
      value: metrics.admissions || 0,
      change: 5.3,
      trend: 'up' as const,
      icon: Building2,
      color: 'text-red-600',
    },
    {
      title: 'Sorties',
      value: metrics.discharges || 0,
      change: 3.1,
      trend: 'up' as const,
      icon: LogOut,
      color: 'text-green-600',
    },
    {
      title: 'Patients Hospitalisés',
      value: metrics.hospitalized || 0,
      change: 2.4,
      trend: 'flat' as const,
      icon: Building2,
      color: 'text-purple-600',
    },
    {
      title: 'Lits Disponibles',
      value: metrics.bedsAvailable || 0,
      change: 1.2,
      trend: 'down' as const,
      icon: Bed,
      color: 'text-cyan-600',
    },
    {
      title: 'Taux d\'Occupation',
      value: `${metrics.occupancyRate || 87}%`,
      change: 5.0,
      trend: 'up' as const,
      icon: BarChart3,
      color: 'text-indigo-600',
      unit: '%',
    },
    {
      title: 'Temps Moyen d\'Attente',
      value: `${metrics.waitTime || 24}m`,
      change: 2.3,
      trend: 'down' as const,
      icon: Clock,
      color: 'text-yellow-600',
      unit: 'min',
    },
    {
      title: 'Chiffre d\'Affaires',
      value: `€${(metrics.revenue || 150000).toLocaleString()}`,
      change: 15.3,
      trend: 'up' as const,
      icon: DollarSign,
      color: 'text-green-500',
    },
    {
      title: 'Dépenses',
      value: `€${(metrics.expenses || 100000).toLocaleString()}`,
      change: 8.1,
      trend: 'up' as const,
      icon: TrendingUp,
      color: 'text-red-500',
    },
    {
      title: 'Interventions Chirurgicales',
      value: metrics.surgeries || 0,
      change: 12.5,
      trend: 'up' as const,
      icon: Heart,
      color: 'text-pink-600',
    },
    {
      title: 'Satisfaction Patients',
      value: `${metrics.satisfaction || 92}%`,
      change: 2.1,
      trend: 'up' as const,
      icon: Smile,
      color: 'text-rose-600',
    },
  ];

  return (
    <div className="p-6 space-y-6">
      <div>
        <h1 className="text-3xl font-bold flex items-center gap-2">
          <BarChart3 className="h-8 w-8 text-primary" />
          Vue Exécutive
        </h1>
        <p className="text-muted-foreground mt-1">Vue d'ensemble du système hospitalier</p>
      </div>

      <FilterBar
        dateRange={dateRange}
        onDateRangeChange={setDateRange}
        period={period}
        onPeriodChange={setPeriod}
        filters={filters}
        onFilterChange={(key, value) => setFilters({ ...filters, [key]: value })}
        onClearFilters={() => setFilters({})}
      />

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {kpiData.map((kpi, idx) => (
          <KPICard
            key={idx}
            title={kpi.title}
            value={kpi.value}
            change={kpi.change}
            trend={kpi.trend}
            icon={kpi.icon}
            color={kpi.color}
            unit={kpi.unit}
          />
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <ChartCard
          title="Évolution des Patients"
          icon={Users}
          subtitle="Données mensuelles"
          isLoading={isLoading}
        >
          <div className="h-64 flex items-center justify-center text-muted-foreground">
            <p>Graphique - Intégration Recharts</p>
          </div>
        </ChartCard>

        <ChartCard
          title="Revenus vs Dépenses"
          icon={DollarSign}
          subtitle="Comparaison mensuelle"
          isLoading={isLoading}
        >
          <div className="h-64 flex items-center justify-center text-muted-foreground">
            <p>Graphique - Intégration Recharts</p>
          </div>
        </ChartCard>

        <ChartCard
          title="Répartition par Service"
          icon={Building2}
          subtitle="Distribution des patients"
          isLoading={isLoading}
        >
          <div className="h-64 flex items-center justify-center text-muted-foreground">
            <p>Graphique - Intégration Recharts</p>
          </div>
        </ChartCard>

        <ChartCard
          title="Occupation des Lits"
          icon={Bed}
          subtitle="État actuel par service"
          isLoading={isLoading}
        >
          <div className="h-64 flex items-center justify-center text-muted-foreground">
            <p>Graphique - Intégration Recharts</p>
          </div>
        </ChartCard>
      </div>
    </div>
  );
}
