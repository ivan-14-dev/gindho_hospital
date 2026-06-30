'use client';
import { useQuery } from '@tanstack/react-query';
import { KPICard } from '../components/KPICard';
import { FilterBar } from '../components/FilterBar';
import { apiClient } from '@/lib/api-client';
import { TrendingUp, AlertTriangle, Bug, Activity } from 'lucide-react';
import { useState } from 'react';

interface MetricsData {
  activeCases?: number;
  incidence?: number;
  epidemics?: number;
  surveillance?: number;
  [key: string]: unknown;
}

export function EpidemiologyView() {
  const [dateRange, setDateRange] = useState({ start: '2024-01-01', end: '2024-06-30' });
  const [period, setPeriod] = useState<'day' | 'week' | 'month' | 'year' | 'custom'>('month');
  const [filters, setFilters] = useState<Record<string, string>>({});

  const { data: metrics = {} as MetricsData } = useQuery({
    queryKey: ['epidemiology-metrics', dateRange, period, filters],
    queryFn: async () => {
      const response = await apiClient.get<{ data?: MetricsData }>('/analytics-service/epidemiology-metrics', { params: { startDate: dateRange.start, endDate: dateRange.end, period, ...filters } });
      return response.data || {};
    },
  });

  const kpiData = [
    { title: 'Cas Actifs', value: metrics.activeCases || 0, change: -2.1, trend: 'down' as const, icon: Bug, color: 'text-red-600' },
    { title: 'Incidence', value: `${metrics.incidence || 12}/100K`, change: -1.5, trend: 'down' as const, icon: TrendingUp, color: 'text-orange-600' },
    { title: 'Épidémies', value: metrics.epidemics || 0, change: 0, trend: 'flat' as const, icon: AlertTriangle, color: 'text-blue-600' },
    { title: 'Surveillance', value: `${metrics.surveillance || 99}%`, change: 0.5, trend: 'up' as const, icon: Activity, color: 'text-green-600' },
  ];

  return (
    <div className="p-6 space-y-6">
      <div><h1 className="text-3xl font-bold flex items-center gap-2"><Bug className="h-8 w-8 text-red-600" />Analyse Épidémiologie</h1><p className="text-muted-foreground mt-1">Surveillance des maladies</p></div>
      <FilterBar dateRange={dateRange} onDateRangeChange={setDateRange} period={period} onPeriodChange={setPeriod} filters={filters} onFilterChange={(k, v) => setFilters({ ...filters, [k]: v })} onClearFilters={() => setFilters({})} />
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">{kpiData.map((kpi, idx) => (<KPICard key={idx} {...kpi} />))}</div>
    </div>
  );
}