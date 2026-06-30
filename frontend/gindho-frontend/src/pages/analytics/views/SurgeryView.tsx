'use client';

import { useQuery } from '@tanstack/react-query';
import { KPICard } from '../components/KPICard';
import { LineChartComponent, BarChartComponent } from '../components/Charts';
import { FilterBar } from '../components/FilterBar';
import { apiClient } from '@/lib/api-client';
import { Heart, Clock, TrendingUp, Users } from 'lucide-react';
import { useState } from 'react';

interface MetricsData {
  surgiesThisMonth?: number;
  surgiestoday?: number;
  avgDuration?: number;
  successRate?: number;
  [key: string]: unknown;
}

interface ChartData {
  surgeriesPerDay?: any[];
  typeDistribution?: any[];
  surgeryDuration?: any[];
  [key: string]: any;
}

export function SurgeryView() {
  const [dateRange, setDateRange] = useState({ start: '2024-01-01', end: '2024-06-30' });
  const [period, setPeriod] = useState<'day' | 'week' | 'month' | 'year' | 'custom'>('month');
  const [filters, setFilters] = useState<Record<string, string>>({});

  const { data: metrics = {} as MetricsData } = useQuery({
    queryKey: ['surgery-metrics', dateRange, period, filters],
    queryFn: async () => {
      const start = String(dateRange.start);
      const end = String(dateRange.end);
      const params = new URLSearchParams({
        startDate: start,
        endDate: end,
        period: period,
        ...filters,
      });
      const response = await apiClient.get<{ data?: MetricsData }>(`/analytics-service/surgery-metrics?${params.toString()}`);
      return response.data || {};
    },
  });

  const { data: chartData = {} as ChartData } = useQuery({
    queryKey: ['surgery-charts', dateRange],
    queryFn: async () => {
      const start = String(dateRange.start);
      const end = String(dateRange.end);
      const params = new URLSearchParams({
        startDate: start,
        endDate: end,
      });
      const response = await apiClient.get<{ data?: ChartData }>(`/analytics-service/surgery-charts?${params.toString()}`);
      return response.data || { surgeriesPerDay: [], typeDistribution: [], surgeryDuration: [] };
    },
  });

  const kpiData = [
    { title: 'Chirurgies Mois', value: metrics.surgiesThisMonth || 0, change: 12.5, trend: 'up' as const, icon: Heart, color: 'text-blue-600' },
    { title: 'Chirurgies Aujourd\'hui', value: metrics.surgiestoday || 0, change: 8.2, trend: 'up' as const, icon: Heart, color: 'text-green-600' },
    { title: 'Durée Moyenne', value: `${metrics.avgDuration || 120}min`, change: -3.1, trend: 'down' as const, icon: Clock, color: 'text-orange-600' },
    { title: 'Taux Succès', value: `${metrics.successRate || 98}%`, change: 0.5, trend: 'up' as const, icon: TrendingUp, color: 'text-purple-600' },
  ];

  return (
    <div className="p-6 space-y-6">
      <div>
        <h1 className="text-3xl font-bold flex items-center gap-2">
          <Heart className="h-8 w-8 text-red-600" />
          Analyse Bloc Opératoire
        </h1>
        <p className="text-muted-foreground mt-1">Performance et activité chirurgicale</p>
      </div>

      <FilterBar dateRange={dateRange} onDateRangeChange={setDateRange} period={period} onPeriodChange={setPeriod} filters={filters} onFilterChange={(k, v) => setFilters({ ...filters, [k]: v })} onClearFilters={() => setFilters({})} />

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {kpiData.map((kpi, idx) => (<KPICard key={idx} title={kpi.title} value={kpi.value} change={kpi.change} trend={kpi.trend} icon={kpi.icon} color={kpi.color} />))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {chartData.surgeriesPerDay && chartData.surgeriesPerDay.length > 0 && (<LineChartComponent title="Chirurgies par Jour" data={chartData.surgeriesPerDay} icon={<Heart className="h-4 w-4 text-red-600" />} height={300} />)}
        {chartData.typeDistribution && chartData.typeDistribution.length > 0 && (<BarChartComponent title="Types de Chirurgies" data={chartData.typeDistribution} icon={<Users className="h-4 w-4 text-green-600" />} height={300} />)}
        {chartData.surgeryDuration && chartData.surgeryDuration.length > 0 && (<LineChartComponent title="Durée Chirurgies" data={chartData.surgeryDuration} icon={<Clock className="h-4 w-4 text-orange-600" />} height={300} />)}
      </div>
    </div>
  );
}