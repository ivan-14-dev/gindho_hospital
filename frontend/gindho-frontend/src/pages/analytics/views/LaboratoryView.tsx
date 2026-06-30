'use client';

import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { ChartCard } from '../components/ChartCard';
import { FilterBar } from '../components/FilterBar';
import { apiClient } from '@/lib/api-client';
import { Beaker, BarChart3, Clock, CheckCircle2 } from 'lucide-react';

export function LaboratoryView() {
  const [dateRange, setDateRange] = useState({ start: '2024-01-01', end: '2024-06-30' });
  const [period, setPeriod] = useState<'day' | 'week' | 'month' | 'year' | 'custom'>('month');
  const [filters, setFilters] = useState<Record<string, string>>({});

  const { data: labData, isLoading } = useQuery({
    queryKey: ['lab-data', dateRange, period, filters],
    queryFn: async () => {
      const start = String(dateRange.start);
      const end = String(dateRange.end);
      const params = new URLSearchParams({
        startDate: start,
        endDate: end,
        period: period,
        ...filters,
      });
      const response = await apiClient.get(`/analytics-service/laboratory?${params.toString()}`);
      return response.data || {};
    },
  });

  return (
    <div className="p-6 space-y-6">
      <div>
        <h1 className="text-3xl font-bold flex items-center gap-2">
          <Beaker className="h-8 w-8 text-indigo-600" />
          Laboratoire
        </h1>
        <p className="text-muted-foreground mt-1">Analyses et examens</p>
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
        <ChartCard title="Nombre d'Analyses" icon={BarChart3} isLoading={isLoading}>
          <div className="h-64 flex items-center justify-center text-muted-foreground">Graphique</div>
        </ChartCard>
        <ChartCard title="Types d'Examens" icon={Beaker} isLoading={isLoading}>
          <div className="h-64 flex items-center justify-center text-muted-foreground">Graphique</div>
        </ChartCard>
        <ChartCard title="Temps Moyen de Traitement" icon={Clock} isLoading={isLoading}>
          <div className="h-64 flex items-center justify-center text-muted-foreground">Graphique</div>
        </ChartCard>
        <ChartCard title="Résultats Validés" icon={CheckCircle2} isLoading={isLoading}>
          <div className="h-64 flex items-center justify-center text-muted-foreground">Graphique</div>
        </ChartCard>
      </div>
    </div>
  );
}
