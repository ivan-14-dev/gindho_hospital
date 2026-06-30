'use client';

import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { ChartCard } from '../components/ChartCard';
import { FilterBar } from '../components/FilterBar';
import { apiClient } from '@/lib/api-client';
import { DollarSign, TrendingUp, PieChart, BarChart3 } from 'lucide-react';

export function FinanceView() {
  const [dateRange, setDateRange] = useState({ start: '2024-01-01', end: '2024-06-30' });
  const [period, setPeriod] = useState<'day' | 'week' | 'month' | 'year' | 'custom'>('month');
  const [filters, setFilters] = useState<Record<string, string>>({});

  const { isLoading } = useQuery({
    queryKey: ['finance-data', dateRange, period, filters],
    queryFn: async () => {
      const response = await apiClient.get('/analytics-service/finance', {
        params: { startDate: dateRange.start, endDate: dateRange.end, period, ...filters },
      });
      return response.data || {};
    },
  });

  return (
    <div className="p-6 space-y-6">
      <div>
        <h1 className="text-3xl font-bold flex items-center gap-2">
          <DollarSign className="h-8 w-8 text-green-500" />
          Finances
        </h1>
        <p className="text-muted-foreground mt-1">Analyse financière complète</p>
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
        <ChartCard title="Revenus Quotidiens" icon={BarChart3} subtitle="Tendance" isLoading={isLoading}>
          <div className="h-64 flex items-center justify-center text-muted-foreground">Graphique</div>
        </ChartCard>
        <ChartCard title="Revenus Mensuels" icon={TrendingUp} subtitle="Vue mensuelle" isLoading={isLoading}>
          <div className="h-64 flex items-center justify-center text-muted-foreground">Graphique</div>
        </ChartCard>
        <ChartCard title="Factures Payées vs Impayées" icon={PieChart} subtitle="Ratio" isLoading={isLoading}>
          <div className="h-64 flex items-center justify-center text-muted-foreground">Graphique</div>
        </ChartCard>
        <ChartCard title="Recettes par Service" icon={BarChart3} subtitle="Répartition" isLoading={isLoading}>
          <div className="h-64 flex items-center justify-center text-muted-foreground">Graphique</div>
        </ChartCard>
      </div>
    </div>
  );
}
