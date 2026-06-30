'use client';

import { useQuery } from '@tanstack/react-query';
import { KPICard } from '../components/KPICard';
import { LineChartComponent, BarChartComponent } from '../components/Charts';
import { FilterBar } from '../components/FilterBar';
import { apiClient } from '@/lib/api-client';
import { Stethoscope, Clock, TrendingUp } from 'lucide-react';
import { useState } from 'react';

interface ConsultationsMetrics {
  totalConsultations?: number;
  todayConsultations?: number;
  avgWaitTime?: number;
  utilisationRate?: number;
}

interface ConsultationsChartData {
  consultsPerDay?: unknown[];
  specialityDistribution?: unknown[];
  waitTimes?: unknown[];
}

export function ConsultationsView() {
  const [dateRange, setDateRange] = useState({ start: '2024-01-01', end: '2024-06-30' });
  const [period, setPeriod] = useState<'day' | 'week' | 'month' | 'year' | 'custom'>('month');
  const [filters, setFilters] = useState<Record<string, string>>({});

  const { data: metrics = {} as ConsultationsMetrics } = useQuery<ConsultationsMetrics>({
    queryKey: ['consultations-metrics', dateRange, period, filters],
    queryFn: async () => {
      const response = await apiClient.get('/analytics-service/consultations-metrics', { params: { startDate: dateRange.start, endDate: dateRange.end, period, ...filters } });
      return response.data || {};
    },
  });

  const { data: chartData = {} as ConsultationsChartData } = useQuery<ConsultationsChartData>({
    queryKey: ['consultations-charts', dateRange],
    queryFn: async () => {
      const response = await apiClient.get('/analytics-service/consultations-charts', { params: { startDate: dateRange.start, endDate: dateRange.end } });
      return response.data || { consultsPerDay: [], specialityDistribution: [], waitTimes: [] };
    },
  });

  const kpiData = [
    { title: 'Total Consultations', value: metrics.totalConsultations || 0, change: 15.2, trend: 'up' as const, icon: Stethoscope, color: 'text-blue-600' },
    { title: 'Consultations Aujourd\'hui', value: metrics.todayConsultations || 0, change: 8.5, trend: 'up' as const, icon: Stethoscope, color: 'text-green-600' },
    { title: 'Temps Attente Moyen', value: `${metrics.avgWaitTime || 15}min`, change: -2.3, trend: 'down' as const, icon: Clock, color: 'text-orange-600' },
    { title: 'Taux Utilisation', value: `${metrics.utilisationRate || 85}%`, change: 3.1, trend: 'up' as const, icon: TrendingUp, color: 'text-purple-600' },
  ];

  return (
    <div className="p-6 space-y-6">
      <div>
        <h1 className="text-3xl font-bold flex items-center gap-2">
          <Stethoscope className="h-8 w-8 text-blue-600" />
          Analyse Consultations
        </h1>
        <p className="text-muted-foreground mt-1">Performance et flux des consultations</p>
      </div>

      <FilterBar dateRange={dateRange} onDateRangeChange={setDateRange} period={period} onPeriodChange={setPeriod} filters={filters} onFilterChange={(k, v) => setFilters({ ...filters, [k]: v })} onClearFilters={() => setFilters({})} />

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {kpiData.map((kpi, idx) => (<KPICard key={idx} title={kpi.title} value={kpi.value} change={kpi.change} trend={kpi.trend} icon={kpi.icon} color={kpi.color} />))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {(chartData.consultsPerDay?.length ?? 0) > 0 && (<LineChartComponent title="Consultations par Jour" data={chartData.consultsPerDay!} icon={<Clock className="h-4 w-4 text-blue-600" />} height={300} />)}
        {(chartData.specialityDistribution?.length ?? 0) > 0 && (<BarChartComponent title="Distribution par Spécialité" data={chartData.specialityDistribution!} icon={<Stethoscope className="h-4 w-4 text-green-600" />} height={300} />)}
        {(chartData.waitTimes?.length ?? 0) > 0 && (<LineChartComponent title="Temps d'Attente" data={chartData.waitTimes!} icon={<TrendingUp className="h-4 w-4 text-orange-600" />} height={300} />)}
      </div>
    </div>
  );
}
