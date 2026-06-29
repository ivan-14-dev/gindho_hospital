'use client';
import { useQuery } from '@tanstack/react-query';
import { KPICard } from '../components/KPICard';
import { LineChartComponent, BarChartComponent } from '../components/Charts';
import { FilterBar } from '../components/FilterBar';
import { Button } from '@/components/ui/button';
import { apiClient } from '@/lib/api-client';
import { FileText, Download, Calendar, TrendingUp } from 'lucide-react';
import { useState } from 'react';

export function ReportsView() {
  const [dateRange, setDateRange] = useState({ start: '2024-01-01', end: '2024-06-30' });
  const [period, setPeriod] = useState<'day' | 'week' | 'month' | 'year' | 'custom'>('month');
  const [filters, setFilters] = useState<Record<string, string>>({});

  const { data: metrics = {} } = useQuery({
    queryKey: ['reports-metrics', dateRange, period, filters],
    queryFn: async () => {
      const response = await apiClient.get('/analytics-service/reports-metrics', { params: { startDate: dateRange.start, endDate: dateRange.end, period, ...filters } });
      return response.data || {};
    },
  });

  const kpiData = [
    { title: 'Rapports Générés', value: metrics.reportsGenerated || 0, change: 8.2, trend: 'up' as const, icon: FileText, color: 'text-blue-600' },
    { title: 'Utilisation Mois', value: `${metrics.monthlyUsage || 850}`, change: 5.1, trend: 'up' as const, icon: TrendingUp, color: 'text-green-600' },
    { title: 'Rapports Programmés', value: metrics.scheduledReports || 0, change: 2.3, trend: 'up' as const, icon: Calendar, color: 'text-orange-600' },
    { title: 'Téléchargements', value: metrics.downloads || 0, change: 12.5, trend: 'up' as const, icon: Download, color: 'text-purple-600' },
  ];

  return (
    <div className="p-6 space-y-6">
      <div><h1 className="text-3xl font-bold flex items-center gap-2"><FileText className="h-8 w-8 text-blue-600" />Gestion Rapports</h1><p className="text-muted-foreground mt-1">Génération et distribution de rapports</p></div>
      <FilterBar dateRange={dateRange} onDateRangeChange={setDateRange} period={period} onPeriodChange={setPeriod} filters={filters} onFilterChange={(k, v) => setFilters({ ...filters, [k]: v })} onClearFilters={() => setFilters({})} />
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">{kpiData.map((kpi, idx) => (<KPICard key={idx} {...kpi} />))}</div>
      <div className="space-y-4">
        <h2 className="text-xl font-bold">Rapports Disponibles</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {['Rapport Mensuel', 'Rapport Annuel', 'Rapport Finances', 'Rapport Qualité'].map((title, idx) => (
            <div key={idx} className="border rounded-lg p-4 flex items-center justify-between">
              <span className="font-medium">{title}</span>
              <Button size="sm" variant="outline"><Download className="h-4 w-4" /></Button>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
