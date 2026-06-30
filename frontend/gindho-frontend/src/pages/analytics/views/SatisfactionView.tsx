'use client';
import { useQuery } from '@tanstack/react-query';
import { KPICard } from '../components/KPICard';
import { FilterBar } from '../components/FilterBar';
import { apiClient } from '@/lib/api-client';
import { Smile, Star, MessageSquare, TrendingUp } from 'lucide-react';
import { useState } from 'react';

interface MetricsData {
  patientSatisfaction?: number;
  nps?: number;
  feedbackCount?: number;
  recommendationRate?: number;
  [key: string]: unknown;
}

export function SatisfactionView() {
  const [dateRange, setDateRange] = useState({ start: '2024-01-01', end: '2024-06-30' });
  const [period, setPeriod] = useState<'day' | 'week' | 'month' | 'year' | 'custom'>('month');
  const [filters, setFilters] = useState<Record<string, string>>({});

  const { data: metrics = {} as MetricsData } = useQuery({
    queryKey: ['satisfaction-metrics', dateRange, period, filters],
    queryFn: async () => {
      const response = await apiClient.get<{ data?: MetricsData }>('/analytics-service/satisfaction-metrics', { params: { startDate: dateRange.start, endDate: dateRange.end, period, ...filters } });
      return response.data || {};
    },
  });

  const kpiData = [
    { title: 'Satisfaction Patients', value: `${metrics.patientSatisfaction || 92}%`, change: 3.2, trend: 'up' as const, icon: Smile, color: 'text-green-600' },
    { title: 'Score NPS', value: metrics.nps || 45, change: 5.1, trend: 'up' as const, icon: Star, color: 'text-yellow-600' },
    { title: 'Feedback Reçus', value: metrics.feedbackCount || 0, change: 12.5, trend: 'up' as const, icon: MessageSquare, color: 'text-blue-600' },
    { title: 'Taux Recommandation', value: `${metrics.recommendationRate || 88}%`, change: 2.3, trend: 'up' as const, icon: TrendingUp, color: 'text-purple-600' },
  ];

  return (
    <div className="p-6 space-y-6">
      <div><h1 className="text-3xl font-bold flex items-center gap-2"><Smile className="h-8 w-8 text-green-600" />Analyse Satisfaction</h1><p className="text-muted-foreground mt-1">Satisfaction des patients et feedback</p></div>
      <FilterBar dateRange={dateRange} onDateRangeChange={setDateRange} period={period} onPeriodChange={setPeriod} filters={filters} onFilterChange={(k, v) => setFilters({ ...filters, [k]: v })} onClearFilters={() => setFilters({})} />
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">{kpiData.map((kpi, idx) => (<KPICard key={idx} {...kpi} />))}</div>
    </div>
  );
}