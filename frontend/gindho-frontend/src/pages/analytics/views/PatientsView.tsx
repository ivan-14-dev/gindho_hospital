'use client';

import { useQuery } from '@tanstack/react-query';
import { KPICard } from '../components/KPICard';
import { LineChartComponent, BarChartComponent, PieChartComponent } from '../components/Charts';
import { FilterBar } from '../components/FilterBar';
import { apiClient } from '@/lib/api-client';
import { Users, TrendingUp, Calendar, Heart } from 'lucide-react';
import { useState } from 'react';

interface MetricsData {
  activePatients?: number;
  newPatients?: number;
  followUp?: number;
  retentionRate?: number;
  [key: string]: unknown;
}

interface ChartData {
  ageDistribution?: { name: string; value: number }[];
  genderDistribution?: { name: string; value: number }[];
  admissionTrend?: { name: string; value: number }[];
  [key: string]: any;
}

export function PatientsView() {
  const [dateRange, setDateRange] = useState({ start: '2024-01-01', end: '2024-06-30' });
  const [period, setPeriod] = useState<'day' | 'week' | 'month' | 'year' | 'custom'>('month');
  const [filters, setFilters] = useState<Record<string, string>>({});

  const { data: metrics = {} as MetricsData } = useQuery({
    queryKey: ['patients-metrics', dateRange, period, filters],
    queryFn: async () => {
      const start = String(dateRange.start);
      const end = String(dateRange.end);
      const params = new URLSearchParams({
        startDate: start,
        endDate: end,
        period: period,
        ...filters,
      });
      const response = await apiClient.get<MetricsData>(`/analytics-service/patients-metrics?${params.toString()}`);
      return response || {};
    },
  });

  const { data: chartData = {} as ChartData } = useQuery({
    queryKey: ['patients-charts', dateRange],
    queryFn: async () => {
      const start = String(dateRange.start);
      const end = String(dateRange.end);
      const params = new URLSearchParams({
        startDate: start,
        endDate: end,
      });
      const response = await apiClient.get<ChartData>(`/analytics-service/patients-charts?${params.toString()}`);
      return response || { ageDistribution: [], genderDistribution: [], admissionTrend: [] };
    },
  });

  const kpiData = [
    { title: 'Patients Actifs', value: metrics.activePatients || 0, change: 12.5, trend: 'up' as const, icon: Users, color: 'text-blue-600' },
    { title: 'Nouveaux Patients', value: metrics.newPatients || 0, change: 8.2, trend: 'up' as const, icon: Users, color: 'text-green-600' },
    { title: 'Patients en Suivi', value: metrics.followUp || 0, change: 5.3, trend: 'up' as const, icon: Heart, color: 'text-red-600' },
    { title: 'Taux Rétention', value: `${metrics.retentionRate || 85}%`, change: 2.1, trend: 'up' as const, icon: TrendingUp, color: 'text-purple-600' },
  ];

  return (
    <div className="p-6 space-y-6">
      <div>
        <h1 className="text-3xl font-bold flex items-center gap-2">
          <Users className="h-8 w-8 text-blue-600" />
          Analyse Patients
        </h1>
        <p className="text-muted-foreground mt-1">Démographie et comportement des patients</p>
      </div>

      <FilterBar dateRange={dateRange} onDateRangeChange={setDateRange} period={period} onPeriodChange={setPeriod} filters={filters} onFilterChange={(k, v) => setFilters({ ...filters, [k]: v })} onClearFilters={() => setFilters({})} />

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {kpiData.map((kpi, idx) => (<KPICard key={idx} title={kpi.title} value={kpi.value} change={kpi.change} trend={kpi.trend} icon={kpi.icon} color={kpi.color} />))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {chartData.ageDistribution && chartData.ageDistribution.length > 0 && (<BarChartComponent title="Distribution par Âge" data={chartData.ageDistribution} icon={<Calendar className="h-4 w-4 text-orange-600" />} height={300} />)}
        {chartData.genderDistribution && chartData.genderDistribution.length > 0 && (<PieChartComponent title="Répartition par Sexe" data={chartData.genderDistribution} icon={<Users className="h-4 w-4 text-pink-600" />} height={300} />)}
        {chartData.admissionTrend && chartData.admissionTrend.length > 0 && (<LineChartComponent title="Tendance Admissions" data={chartData.admissionTrend} icon={<TrendingUp className="h-4 w-4 text-green-600" />} height={300} />)}
      </div>
    </div>
  );
}