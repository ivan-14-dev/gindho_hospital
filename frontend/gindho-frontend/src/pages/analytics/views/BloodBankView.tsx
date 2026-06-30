'use client';
import { useQuery } from '@tanstack/react-query';
import { KPICard } from '../components/KPICard';
import { FilterBar } from '../components/FilterBar';
import { apiClient } from '@/lib/api-client';
import { Droplet, AlertCircle, TrendingUp } from 'lucide-react';
import { useState } from 'react';

interface BloodBankMetrics {
  totalStock?: number;
  transfusionsMonth?: number;
  criticalStock?: number;
  usageRate?: number;
}

export function BloodBankView() {
  const [dateRange, setDateRange] = useState({ start: '2024-01-01', end: '2024-06-30' });
  const [period, setPeriod] = useState<'day' | 'week' | 'month' | 'year' | 'custom'>('month');
  const [filters, setFilters] = useState<Record<string, string>>({});

  const { data: metrics = {} as BloodBankMetrics } = useQuery<BloodBankMetrics>({
    queryKey: ['bloodbank-metrics', dateRange, period, filters],
    queryFn: async () => {
      const response = await apiClient.get('/analytics-service/bloodbank-metrics', { params: { startDate: dateRange.start, endDate: dateRange.end, period, ...filters } });
      return response.data || {};
    },
  });

  const kpiData = [
    { title: 'Stock Total', value: `${metrics.totalStock || 0}L`, change: 2.5, trend: 'up' as const, icon: Droplet, color: 'text-red-600' },
    { title: 'Transfusions Mois', value: metrics.transfusionsMonth || 0, change: 8.2, trend: 'up' as const, icon: Droplet, color: 'text-blue-600' },
    { title: 'Stock Critique', value: metrics.criticalStock || 0, change: -1.2, trend: 'down' as const, icon: AlertCircle, color: 'text-orange-600' },
    { title: 'Taux Utilisation', value: `${metrics.usageRate || 75}%`, change: 3.1, trend: 'up' as const, icon: TrendingUp, color: 'text-green-600' },
  ];

  return (
    <div className="p-6 space-y-6">
      <div><h1 className="text-3xl font-bold flex items-center gap-2"><Droplet className="h-8 w-8 text-red-600" />Analyse Banque de Sang</h1><p className="text-muted-foreground mt-1">Gestion des stocks sanguins</p></div>
      <FilterBar dateRange={dateRange} onDateRangeChange={setDateRange} period={period} onPeriodChange={setPeriod} filters={filters} onFilterChange={(k, v) => setFilters({ ...filters, [k]: v })} onClearFilters={() => setFilters({})} />
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">{kpiData.map((kpi, idx) => (<KPICard key={idx} {...kpi} />))}</div>
    </div>
  );
}
