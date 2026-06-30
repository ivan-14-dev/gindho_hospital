'use client';
import { useQuery } from '@tanstack/react-query';
import { KPICard } from '../components/KPICard';
import { FilterBar } from '../components/FilterBar';
import { apiClient } from '@/lib/api-client';
import { CheckCircle, AlertCircle, TrendingUp, Target } from 'lucide-react';
import { useState } from 'react';

interface MetricsData {
  overallQuality?: number;
  incidents?: number;
  compliance?: number;
  auditScore?: number;
  [key: string]: unknown;
}

export function QualityView() {
  const [dateRange, setDateRange] = useState({ start: '2024-01-01', end: '2024-06-30' });
  const [period, setPeriod] = useState<'day' | 'week' | 'month' | 'year' | 'custom'>('month');
  const [filters, setFilters] = useState<Record<string, string>>({});

  const { data: metrics = {} as MetricsData } = useQuery({
    queryKey: ['quality-metrics', dateRange, period, filters],
    queryFn: async () => {
      const start = String(dateRange.start);
      const end = String(dateRange.end);
      const params = new URLSearchParams({
        startDate: start,
        endDate: end,
        period: period,
        ...filters,
      });
      const response = await apiClient.get<{ data?: MetricsData }>(`/analytics-service/quality-metrics?${params.toString()}`);
      return response.data || {};
    },
  });

  const kpiData = [
    { title: 'Qualité Globale', value: `${metrics.overallQuality || 94}%`, change: 2.1, trend: 'up' as const, icon: CheckCircle, color: 'text-green-600' },
    { title: 'Incidents Mois', value: metrics.incidents || 0, change: -1.2, trend: 'down' as const, icon: AlertCircle, color: 'text-red-600' },
    { title: 'Conformité', value: `${metrics.compliance || 98}%`, change: 0.5, trend: 'up' as const, icon: Target, color: 'text-blue-600' },
    { title: 'Score Audit', value: `${metrics.auditScore || 96}%`, change: 3.2, trend: 'up' as const, icon: TrendingUp, color: 'text-purple-600' },
  ];

  return (
    <div className="p-6 space-y-6">
      <div><h1 className="text-3xl font-bold flex items-center gap-2"><CheckCircle className="h-8 w-8 text-green-600" />Analyse Qualité</h1><p className="text-muted-foreground mt-1">Qualité des soins et conformité</p></div>
      <FilterBar dateRange={dateRange} onDateRangeChange={setDateRange} period={period} onPeriodChange={setPeriod} filters={filters} onFilterChange={(k, v) => setFilters({ ...filters, [k]: v })} onClearFilters={() => setFilters({})} />
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">{kpiData.map((kpi, idx) => (<KPICard key={idx} {...kpi} />))}</div>
    </div>
  );
}