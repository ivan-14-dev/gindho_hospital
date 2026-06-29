'use client';

import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { ChartCard } from '../components/ChartCard';
import { FilterBar } from '../components/FilterBar';
import { apiClient } from '@/lib/api-client';
import {
  Activity, TrendingUp, Calendar, Clock, AlertCircle, CheckCircle2
} from 'lucide-react';

export function ActivityView() {
  const [dateRange, setDateRange] = useState({ start: '2024-01-01', end: '2024-06-30' });
  const [period, setPeriod] = useState<'day' | 'week' | 'month' | 'year' | 'custom'>('month');
  const [filters, setFilters] = useState<Record<string, string>>({});

  const { data: activityData, isLoading } = useQuery({
    queryKey: ['activity-data', dateRange, period, filters],
    queryFn: async () => {
      const response = await apiClient.get('/analytics-service/activity', {
        params: { startDate: dateRange.start, endDate: dateRange.end, period, ...filters },
      });
      return response.data || {};
    },
  });

  return (
    <div className="p-6 space-y-6">
      <div>
        <h1 className="text-3xl font-bold flex items-center gap-2">
          <Activity className="h-8 w-8 text-green-600" />
          Activité Hospitalière
        </h1>
        <p className="text-muted-foreground mt-1">Vue complète de l'activité</p>
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

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <ChartCard
          title="Consultations par Jour"
          icon={Calendar}
          subtitle="Tendance quotidienne"
          isLoading={isLoading}
        >
          <div className="h-64 flex items-center justify-center text-muted-foreground">
            <p>Graphique linéaire</p>
          </div>
        </ChartCard>

        <ChartCard
          title="Consultations par Semaine"
          icon={TrendingUp}
          subtitle="Comparaison hebdomadaire"
          isLoading={isLoading}
        >
          <div className="h-64 flex items-center justify-center text-muted-foreground">
            <p>Graphique en barres</p>
          </div>
        </ChartCard>

        <ChartCard
          title="Consultations par Spécialité"
          icon={CheckCircle2}
          subtitle="Répartition des demandes"
          isLoading={isLoading}
        >
          <div className="h-64 flex items-center justify-center text-muted-foreground">
            <p>Graphique en secteurs</p>
          </div>
        </ChartCard>

        <ChartCard
          title="Consultations par Médecin"
          icon={AlertCircle}
          subtitle="Top 10 médecins"
          isLoading={isLoading}
        >
          <div className="h-64 flex items-center justify-center text-muted-foreground">
            <p>Graphique horizontal</p>
          </div>
        </ChartCard>

        <ChartCard
          title="Répartition par Tranche d'Âge"
          icon={Calendar}
          subtitle="Distribution démographique"
          isLoading={isLoading}
        >
          <div className="h-64 flex items-center justify-center text-muted-foreground">
            <p>Graphique empilé</p>
          </div>
        </ChartCard>

        <ChartCard
          title="Répartition Homme/Femme"
          icon={Activity}
          subtitle="Ratio démographique"
          isLoading={isLoading}
        >
          <div className="h-64 flex items-center justify-center text-muted-foreground">
            <p>Graphique en donut</p>
          </div>
        </ChartCard>
      </div>
    </div>
  );
}
