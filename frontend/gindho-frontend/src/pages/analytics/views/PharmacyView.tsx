'use client';

import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { ChartCard } from '../components/ChartCard';
import { FilterBar } from '../components/FilterBar';
import { apiClient } from '@/lib/api-client';
import { Pill, AlertCircle, TrendingUp, Box } from 'lucide-react';

export function PharmacyView() {
  const [dateRange, setDateRange] = useState({ start: '2024-01-01', end: '2024-06-30' });
  const [period, setPeriod] = useState<'day' | 'week' | 'month' | 'year' | 'custom'>('month');
  const [filters, setFilters] = useState<Record<string, string>>({});

  const { data: _, isLoading } = useQuery({
    queryKey: ['pharmacy-data', dateRange, period, filters],
    queryFn: async () => {
      const start = String(dateRange.start);
      const end = String(dateRange.end);
      const params = new URLSearchParams({
        startDate: start,
        endDate: end,
        period: period,
        ...filters,
      });
      const response = await apiClient.get<{ data?: Record<string, unknown> }>(`/analytics-service/pharmacy?${params.toString()}`);
      return response.data || {};
    },
  });

  return (
    <div className="p-6 space-y-6">
      <div>
        <h1 className="text-3xl font-bold flex items-center gap-2">
          <Pill className="h-8 w-8 text-cyan-600" />
          Pharmacie
        </h1>
        <p className="text-muted-foreground mt-1">Gestion des médicaments et inventaire</p>
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
        <ChartCard title="Top 10 Médicaments Prescrits" icon={Pill} isLoading={isLoading}>
          <div className="h-64 flex items-center justify-center text-muted-foreground">Graphique</div>
        </ChartCard>
        <ChartCard title="Stock Critique" icon={AlertCircle} isLoading={isLoading}>
          <div className="h-64 flex items-center justify-center text-muted-foreground">Alerte</div>
        </ChartCard>
        <ChartCard title="Valeur du Stock" icon={Box} isLoading={isLoading}>
          <div className="h-64 flex items-center justify-center text-muted-foreground">Graphique</div>
        </ChartCard>
        <ChartCard title="Rotation du Stock" icon={TrendingUp} isLoading={isLoading}>
          <div className="h-64 flex items-center justify-center text-muted-foreground">Graphique</div>
        </ChartCard>
      </div>
    </div>
  );
}
