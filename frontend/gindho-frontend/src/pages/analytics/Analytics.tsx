'use client';

import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import {
  TrendingUp, TrendingDown, Download, Filter, Calendar, Users, Activity, Zap
} from 'lucide-react';
import { Badge } from '@/components/ui/badge';
import { apiClient } from '@/lib/api-client';

interface AnalyticMetric {
  label: string;
  value: number | string;
  change: number;
  trend: 'up' | 'down' | 'flat';
  icon: any;
  color: string;
}

interface ChartData {
  name: string;
  patients: number;
  rendezVous: number;
  revenue: number;
}

export default function Analytics() {
  const [dateRange, setDateRange] = useState({ start: '2024-01-01', end: '2024-06-30' });
  const [department, setDepartment] = useState('all');

  const { data: analytics = {}, isLoading } = useQuery({
    queryKey: ['analytics', dateRange, department],
    queryFn: async () => {
      const response = await apiClient.get('/analytics-service/metrics', {
        params: {
          startDate: dateRange.start,
          endDate: dateRange.end,
          department: department !== 'all' ? department : undefined,
        },
      });
      return response.data || {};
    },
  });

  const { data: chartData = [], isLoading: chartLoading } = useQuery({
    queryKey: ['analytics-chart', dateRange],
    queryFn: async () => {
      const response = await apiClient.get('/analytics-service/chart-data', {
        params: {
          startDate: dateRange.start,
          endDate: dateRange.end,
        },
      });
      return response.data || [];
    },
  });

  const metrics: AnalyticMetric[] = [
    {
      label: 'Total Patients',
      value: analytics.totalPatients || 0,
      change: analytics.patientChange || 12.5,
      trend: 'up',
      icon: Users,
      color: 'text-green-600',
    },
    {
      label: 'Rendez-vous',
      value: analytics.totalAppointments || 0,
      change: analytics.appointmentChange || 8.2,
      trend: 'up',
      icon: Activity,
      color: 'text-blue-600',
    },
    {
      label: 'Taux Occupation',
      value: `${analytics.occupancyRate || 87}%`,
      change: analytics.occupancyChange || 5,
      trend: 'up',
      icon: Zap,
      color: 'text-purple-600',
    },
    {
      label: 'Revenus',
      value: `€${(analytics.revenue || 45000).toLocaleString()}`,
      change: analytics.revenueChange || 15.3,
      trend: 'up',
      icon: TrendingUp,
      color: 'text-orange-600',
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Analytics & Insights</h1>
          <p className="text-muted-foreground">Statistiques complètes & prédictions</p>
        </div>
        <Button variant="outline">
          <Download className="h-4 w-4 mr-2" />
          Exporter
        </Button>
      </div>

      <Card className="p-4">
        <div className="flex gap-4 flex-wrap">
          <div className="flex-1 min-w-64">
            <label className="text-sm font-medium">Date Début</label>
            <Input
              type="date"
              value={dateRange.start}
              onChange={(e) => setDateRange({ ...dateRange, start: e.target.value })}
              className="mt-1"
            />
          </div>
          <div className="flex-1 min-w-64">
            <label className="text-sm font-medium">Date Fin</label>
            <Input
              type="date"
              value={dateRange.end}
              onChange={(e) => setDateRange({ ...dateRange, end: e.target.value })}
              className="mt-1"
            />
          </div>
          <div className="flex-1 min-w-64">
            <label className="text-sm font-medium">Département</label>
            <select
              value={department}
              onChange={(e) => setDepartment(e.target.value)}
              className="w-full mt-1 px-3 py-2 border rounded-md text-sm"
            >
              <option value="all">Tous les départements</option>
              <option value="emergency">Urgences</option>
              <option value="surgery">Chirurgie</option>
              <option value="maternity">Maternité</option>
              <option value="pediatrics">Pédiatrie</option>
            </select>
          </div>
        </div>
      </Card>

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        {metrics.map((metric, idx) => {
          const Icon = metric.icon;
          return (
            <Card key={idx}>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">{metric.label}</CardTitle>
                <Icon className={`h-4 w-4 ${metric.color}`} />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">{metric.value}</div>
                <div className="flex items-center gap-1 mt-1">
                  {metric.trend === 'up' ? (
                    <TrendingUp className="h-4 w-4 text-green-600" />
                  ) : (
                    <TrendingDown className="h-4 w-4 text-red-600" />
                  )}
                  <p className={`text-xs font-medium ${metric.trend === 'up' ? 'text-green-600' : 'text-red-600'}`}>
                    {metric.trend === 'up' ? '+' : '-'}{Math.abs(metric.change)}%
                  </p>
                  <p className="text-xs text-muted-foreground">vs dernier mois</p>
                </div>
              </CardContent>
            </Card>
          );
        })}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card>
          <CardHeader>
            <CardTitle>Patients par Mois</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {isLoading ? (
                <p className="text-muted-foreground">Chargement...</p>
              ) : chartData.length > 0 ? (
                chartData.map((month: ChartData, idx: number) => {
                  const maxPatients = Math.max(...chartData.map((d: ChartData) => d.patients));
                  const percentage = (month.patients / maxPatients) * 100;
                  return (
                    <div key={idx}>
                      <div className="flex justify-between mb-1">
                        <p className="text-sm font-medium">{month.name}</p>
                        <p className="text-sm text-muted-foreground">{month.patients}</p>
                      </div>
                      <div className="w-full h-2 bg-muted rounded-full overflow-hidden">
                        <div
                          className="h-full bg-primary transition-all"
                          style={{ width: `${percentage}%` }}
                        />
                      </div>
                    </div>
                  );
                })
              ) : (
                <p className="text-muted-foreground">Pas de données</p>
              )}
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Rendez-vous par Mois</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {isLoading ? (
                <p className="text-muted-foreground">Chargement...</p>
              ) : chartData.length > 0 ? (
                chartData.map((month: ChartData, idx: number) => {
                  const maxAppointments = Math.max(...chartData.map((d: ChartData) => d.rendezVous));
                  const percentage = (month.rendezVous / maxAppointments) * 100;
                  return (
                    <div key={idx}>
                      <div className="flex justify-between mb-1">
                        <p className="text-sm font-medium">{month.name}</p>
                        <p className="text-sm text-muted-foreground">{month.rendezVous}</p>
                      </div>
                      <div className="w-full h-2 bg-muted rounded-full overflow-hidden">
                        <div
                          className="h-full bg-blue-500 transition-all"
                          style={{ width: `${percentage}%` }}
                        />
                      </div>
                    </div>
                  );
                })
              ) : (
                <p className="text-muted-foreground">Pas de données</p>
              )}
            </div>
          </CardContent>
        </Card>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Insights & Recommandations</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-3">
            <div className="flex items-start gap-3 p-3 bg-green-50 rounded-lg border border-green-200">
              <TrendingUp className="h-5 w-5 text-green-600 mt-0.5 flex-shrink-0" />
              <div>
                <p className="font-medium text-sm">Augmentation des patients</p>
                <p className="text-xs text-muted-foreground">+12.5% ce mois vs le mois précédent</p>
              </div>
            </div>
            <div className="flex items-start gap-3 p-3 bg-blue-50 rounded-lg border border-blue-200">
              <Activity className="h-5 w-5 text-blue-600 mt-0.5 flex-shrink-0" />
              <div>
                <p className="font-medium text-sm">Taux d'occupation élevé</p>
                <p className="text-xs text-muted-foreground">87% - Envisager augmenter la capacité</p>
              </div>
            </div>
            <div className="flex items-start gap-3 p-3 bg-orange-50 rounded-lg border border-orange-200">
              <Zap className="h-5 w-5 text-orange-600 mt-0.5 flex-shrink-0" />
              <div>
                <p className="font-medium text-sm">Revenus en hausse</p>
                <p className="text-xs text-muted-foreground">+15.3% - Excellente performance financière</p>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
