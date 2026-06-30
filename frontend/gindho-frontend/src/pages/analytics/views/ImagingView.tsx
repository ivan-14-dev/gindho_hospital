'use client';
import { useQuery } from '@tanstack/react-query';
import { KPICard } from '../components/KPICard';
import { LineChartComponent, BarChartComponent, PieChartComponent } from '../components/Charts';
import { FilterBar } from '../components/FilterBar';
import { apiClient } from '@/lib/api-client';
import { Camera, Clock, TrendingUp, Users } from 'lucide-react';
import { useState } from 'react';

export function ImagingView() {
  const [dateRange, setDateRange] = useState({ start: '2024-01-01', end: '2024-06-30' });
  const [period, setPeriod] = useState<'day' | 'week' | 'month' | 'year' | 'custom'>('month');
  const [filters, setFilters] = useState<Record<string, string>>({});

  const { data: metrics = {} } = useQuery({
    queryKey: ['imaging-metrics', dateRange, period, filters],
    queryFn: async () => {
      const start = String(dateRange.start);
      const end = String(dateRange.end);
      const params = new URLSearchParams({
        startDate: start,
        endDate: end,
        period: period,
        ...filters,
      });
      const response = await apiClient.get(`/analytics-service/imaging-metrics?${params.toString()}`);
      return response.data || {};
    },
  });

  const { data: chartData = {} } = useQuery({
    queryKey: ['imaging-charts', dateRange],
    queryFn: async () => {
      const start = String(dateRange.start);
      const end = String(dateRange.end);
      const params = new URLSearchParams({
        startDate: start,
        endDate: end,
      });
      const response = await apiClient.get(`/analytics-service/imaging-charts?${params.toString()}`);
      return response.data || { examsPerDay: [], modalityDistribution: [], turnaroundTime: [] };
    },
  });

  const kpiData = [
    { title: 'Total Examens', value: metrics.totalExams || 0, change: 10.5, trend: 'up' as const, icon: Camera, color: 'text-blue-600' },
    { title: 'Examens Jour', value: metrics.examsToday || 0, change: 5.2, trend: 'up' as const, icon: Camera, color: 'text-green-600' },
    { title: 'Délai Moyen', value: `${metrics.avgTurnaround || 4}h`, change: -2.1, trend: 'down' as const, icon: Clock, color: 'text-orange-600' },
    { title: 'Taux Qualité', value: `${metrics.qualityRate || 96}%`, change: 1.3, trend: 'up' as const, icon: TrendingUp, color: 'text-purple-600' },
  ];

  return (
    <div className="p-6 space-y-6">
      <div><h1 className="text-3xl font-bold flex items-center gap-2"><Camera className="h-8 w-8 text-blue-600" />Analyse Radiologie</h1><p className="text-muted-foreground mt-1">Imagerie médicale et diagnostique</p></div>
      <FilterBar dateRange={dateRange} onDateRangeChange={setDateRange} period={period} onPeriodChange={setPeriod} filters={filters} onFilterChange={(k, v) => setFilters({ ...filters, [k]: v })} onClearFilters={() => setFilters({})} />
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">{kpiData.map((kpi, idx) => (<KPICard key={idx} {...kpi} />))}</div>
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {chartData.examsPerDay?.length > 0 && (<LineChartComponent title="Examens par Jour" data={chartData.examsPerDay} icon={<Camera className="h-4 w-4 text-blue-600" />} height={300} />)}
        {chartData.modalityDistribution?.length > 0 && (<PieChartComponent title="Types d'Examens" data={chartData.modalityDistribution} icon={<Users className="h-4 w-4 text-green-600" />} height={300} />)}
      </div>
    </div>
  );
}
