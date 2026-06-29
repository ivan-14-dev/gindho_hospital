'use client';

import { useQuery } from '@tanstack/react-query';
import { KPICard } from '../components/KPICard';
import { LineChartComponent, BarChartComponent, AreaChartComponent } from '../components/Charts';
import { FilterBar } from '../components/FilterBar';
import { apiClient } from '@/lib/api-client';
import { Bed, TrendingUp, Clock, Users } from 'lucide-react';
import { useState } from 'react';

export function HospitalizationsView() {
  const [dateRange, setDateRange] = useState({ start: '2024-01-01', end: '2024-06-30' });
  const [period, setPeriod] = useState<'day' | 'week' | 'month' | 'year' | 'custom'>('month');
  const [filters, setFilters] = useState<Record<string, string>>({});

  const { data: metrics = {} } = useQuery({
    queryKey: ['hospitalizations-metrics', dateRange, period, filters],
    queryFn: async () => {
      const response = await apiClient.get('/analytics-service/hospitalizations-metrics', { params: { startDate: dateRange.start, endDate: dateRange.end, period, ...filters } });
      return response.data || {};
    },
  });

  const { data: chartData = {} } = useQuery({
    queryKey: ['hospitalizations-charts', dateRange],
    queryFn: async () => {
      const response = await apiClient.get('/analytics-service/hospitalizations-charts', { params: { startDate: dateRange.start, endDate: dateRange.end } });
      return response.data || { occupancyTrend: [], lengthOfStay: [], bedAvailability: [] };
    },
  });

  const kpiData = [
    { title: 'Patients Hospitalisés', value: metrics.currentlyHospitalized || 0, change: 5.2, trend: 'up' as const, icon: Bed, color: 'text-blue-600' },
    { title: 'Admissions Jour', value: metrics.admissionsToday || 0, change: 3.1, trend: 'up' as const, icon: Users, color: 'text-green-600' },
    { title: 'Durée Moyenne', value: `${metrics.avgStay || 5}j`, change: -1.2, trend: 'down' as const, icon: Clock, color: 'text-orange-600' },
    { title: 'Taux Occupation', value: `${metrics.occupancyRate || 85}%`, change: 2.5, trend: 'up' as const, icon: TrendingUp, color: 'text-purple-600' },
  ];

  return (
    <div className="p-6 space-y-6">
      <div>
        <h1 className="text-3xl font-bold flex items-center gap-2">
          <Bed className="h-8 w-8 text-blue-600" />
          Analyse Hospitalisations
        </h1>
        <p className="text-muted-foreground mt-1">Gestion des lits et durée de séjour</p>
      </div>

      <FilterBar dateRange={dateRange} onDateRangeChange={setDateRange} period={period} onPeriodChange={setPeriod} filters={filters} onFilterChange={(k, v) => setFilters({ ...filters, [k]: v })} onClearFilters={() => setFilters({})} />

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {kpiData.map((kpi, idx) => (<KPICard key={idx} title={kpi.title} value={kpi.value} change={kpi.change} trend={kpi.trend} icon={kpi.icon} color={kpi.color} />))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {chartData.occupancyTrend?.length > 0 && (<AreaChartComponent title="Tendance Occupation" data={chartData.occupancyTrend} icon={<Bed className="h-4 w-4 text-blue-600" />} height={300} />)}
        {chartData.lengthOfStay?.length > 0 && (<BarChartComponent title="Durée de Séjour" data={chartData.lengthOfStay} icon={<Clock className="h-4 w-4 text-green-600" />} height={300} />)}
        {chartData.bedAvailability?.length > 0 && (<LineChartComponent title="Disponibilité Lits" data={chartData.bedAvailability} icon={<TrendingUp className="h-4 w-4 text-orange-600" />} height={300} />)}
      </div>
    </div>
  );
}
