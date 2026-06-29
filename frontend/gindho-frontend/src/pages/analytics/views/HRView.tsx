'use client';
import { useQuery } from '@tanstack/react-query';
import { KPICard } from '../components/KPICard';
import { BarChartComponent, LineChartComponent } from '../components/Charts';
import { FilterBar } from '../components/FilterBar';
import { apiClient } from '@/lib/api-client';
import { Users, TrendingUp, Clock, Award } from 'lucide-react';
import { useState } from 'react';

export function HRView() {
  const [dateRange, setDateRange] = useState({ start: '2024-01-01', end: '2024-06-30' });
  const [period, setPeriod] = useState<'day' | 'week' | 'month' | 'year' | 'custom'>('month');
  const [filters, setFilters] = useState<Record<string, string>>({});

  const { data: metrics = {} } = useQuery({
    queryKey: ['hr-metrics', dateRange, period, filters],
    queryFn: async () => {
      const response = await apiClient.get('/analytics-service/hr-metrics', { params: { startDate: dateRange.start, endDate: dateRange.end, period, ...filters } });
      return response.data || {};
    },
  });

  const kpiData = [
    { title: 'Effectif Total', value: metrics.totalStaff || 0, change: 1.2, trend: 'up' as const, icon: Users, color: 'text-blue-600' },
    { title: 'Présence Moyenne', value: `${metrics.attendance || 95}%`, change: 2.1, trend: 'up' as const, icon: Clock, color: 'text-green-600' },
    { title: 'Taux Turnover', value: `${metrics.turnoverRate || 5}%`, change: -0.5, trend: 'down' as const, icon: TrendingUp, color: 'text-orange-600' },
    { title: 'Satisfaction Staff', value: `${metrics.satisfaction || 88}%`, change: 3.2, trend: 'up' as const, icon: Award, color: 'text-purple-600' },
  ];

  return (
    <div className="p-6 space-y-6">
      <div><h1 className="text-3xl font-bold flex items-center gap-2"><Users className="h-8 w-8 text-blue-600" />Analyse RH</h1><p className="text-muted-foreground mt-1">Gestion des ressources humaines</p></div>
      <FilterBar dateRange={dateRange} onDateRangeChange={setDateRange} period={period} onPeriodChange={setPeriod} filters={filters} onFilterChange={(k, v) => setFilters({ ...filters, [k]: v })} onClearFilters={() => setFilters({})} />
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">{kpiData.map((kpi, idx) => (<KPICard key={idx} {...kpi} />))}</div>
    </div>
  );
}
