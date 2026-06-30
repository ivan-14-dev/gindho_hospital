'use client';

import { useState } from 'react';
import { useQuery, useMutation } from '@tanstack/react-query';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Badge } from '@/components/ui/badge';
import {
  FileText, Download, Plus, Eye, Trash2
} from 'lucide-react';
import { apiClient } from '@/lib/api-client';
import type { ApiResponse } from '@/types';

interface Report {
  id: string;
  titre: string;
  type: string;
  dateGeneration: string;
  format: 'pdf' | 'excel' | 'csv' | 'json';
  status: 'generating' | 'ready' | 'expired';
  generePar: string;
  url?: string;
  size?: string;
}

export default function Reporting() {
  const [filterType, setFilterType] = useState<string>('all');
  const [searchQuery, setSearchQuery] = useState('');
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
  const [reportType, setReportType] = useState('PATIENT_SUMMARY');
  const [dateRange, setDateRange] = useState({ start: '', end: '' });

  const { data: reports = [], isLoading, refetch } = useQuery<Report[]>({
    queryKey: ['reports', filterType],
    queryFn: async (): Promise<Report[]> => {
      const response = await apiClient.get<ApiResponse<Report[]>>('/reporting-service/reports', {
        params: { type: filterType !== 'all' ? filterType : undefined },
      });
      return response.data ?? [];
    },
  });

  const generateReportMutation = useMutation({
    mutationFn: async (params: any) => {
      const response = await apiClient.post('/reporting-service/reports/generate', {
        type: reportType,
        format: 'pdf',
        dateDebut: dateRange.start,
        dateFin: dateRange.end,
        ...params,
      });
      return response.data;
    },
    onSuccess: () => {
      refetch();
      setIsCreateDialogOpen(false);
    },
  });

  const deleteReportMutation = useMutation({
    mutationFn: async (reportId: string) => {
      await apiClient.delete(`/reporting-service/reports/${reportId}`);
    },
    onSuccess: () => refetch(),
  });

  const downloadReportMutation = useMutation({
    mutationFn: async (report: Report) => {
      const response = await apiClient.get<Blob>(`/reporting-service/reports/${report.id}/download`, {
        responseType: 'blob',
      });
      const url = window.URL.createObjectURL(response);
      const link = document.createElement('a');
      link.href = url;
      link.download = `${report.titre}.${report.format}`;
      link.click();
    },
  });

  const filteredReports = reports.filter((r: Report) =>
    r.titre.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const reportTypes = [
    { value: 'PATIENT_SUMMARY', label: 'Résumé Patients' },
    { value: 'ADMISSION_DISCHARGE', label: 'Admissions/Sorties' },
    { value: 'FINANCIAL', label: 'Financier' },
    { value: 'HR', label: 'Ressources Humaines' },
    { value: 'INVENTORY', label: 'Inventaire' },
    { value: 'OCCUPANCY', label: 'Taux d\'occupation' },
    { value: 'CLINICAL', label: 'Clinique' },
    { value: 'QUALITY', label: 'Qualité' },
  ];

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Rapports & Rapports</h1>
          <p className="text-muted-foreground">{reports.length} rapports générés</p>
        </div>
        <Dialog open={isCreateDialogOpen} onOpenChange={setIsCreateDialogOpen}>
          <DialogTrigger asChild>
            <Button>
              <Plus className="h-4 w-4 mr-2" />
              Nouveau Rapport
            </Button>
          </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Générer un Rapport</DialogTitle>
            </DialogHeader>
            <div className="space-y-4">
              <div>
                <label className="text-sm font-medium">Type de Rapport</label>
                <select
                  value={reportType}
                  onChange={(e) => setReportType(e.target.value)}
                  className="w-full mt-1 px-3 py-2 border rounded-md text-sm"
                >
                  {reportTypes.map((type) => (
                    <option key={type.value} value={type.value}>
                      {type.label}
                    </option>
                  ))}
                </select>
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="text-sm font-medium">Date Début</label>
                  <Input
                    type="date"
                    value={dateRange.start}
                    onChange={(e) => setDateRange({ ...dateRange, start: e.target.value })}
                    className="mt-1"
                  />
                </div>
                <div>
                  <label className="text-sm font-medium">Date Fin</label>
                  <Input
                    type="date"
                    value={dateRange.end}
                    onChange={(e) => setDateRange({ ...dateRange, end: e.target.value })}
                    className="mt-1"
                  />
                </div>
              </div>
              <Button
                onClick={() => generateReportMutation.mutate({})}
                disabled={generateReportMutation.isPending}
                className="w-full"
              >
                {generateReportMutation.isPending ? 'Génération...' : 'Générer'}
              </Button>
            </div>
          </DialogContent>
        </Dialog>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <Card className="p-4">
          <p className="text-sm text-muted-foreground">Rapports Prêts</p>
          <p className="text-3xl font-bold mt-1">{reports.filter((r: Report) => r.status === 'ready').length}</p>
        </Card>
        <Card className="p-4">
          <p className="text-sm text-muted-foreground">En Génération</p>
          <p className="text-3xl font-bold mt-1">{reports.filter((r: Report) => r.status === 'generating').length}</p>
        </Card>
        <Card className="p-4">
          <p className="text-sm text-muted-foreground">Expirés</p>
          <p className="text-3xl font-bold mt-1">{reports.filter((r: Report) => r.status === 'expired').length}</p>
        </Card>
      </div>

      <Card>
        <div className="p-4 border-b space-y-3">
          <div className="flex gap-2">
            <Input
              placeholder="Rechercher un rapport..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="flex-1"
            />
          </div>
          <div className="flex gap-2 flex-wrap">
            <Button
              size="sm"
              variant={filterType === 'all' ? 'default' : 'outline'}
              onClick={() => setFilterType('all')}
            >
              Tous
            </Button>
            {reportTypes.map((type) => (
              <Button
                key={type.value}
                size="sm"
                variant={filterType === type.value ? 'default' : 'outline'}
                onClick={() => setFilterType(type.value)}
              >
                {type.label}
              </Button>
            ))}
          </div>
        </div>

        <ScrollArea className="h-[500px]">
          <div className="p-4 space-y-2">
            {isLoading ? (
              <p className="text-center text-muted-foreground">Chargement...</p>
            ) : filteredReports.length === 0 ? (
              <div className="flex flex-col items-center justify-center h-64">
                <FileText className="h-12 w-12 text-muted-foreground mb-2 opacity-50" />
                <p className="text-muted-foreground">Aucun rapport trouvé</p>
              </div>
            ) : (
              filteredReports.map((report: Report) => (
                <div key={report.id} className="p-4 rounded-lg border hover:bg-muted/50 transition-colors">
                  <div className="flex items-start justify-between">
                    <div className="flex items-start gap-3 flex-1">
                      <FileText className="h-5 w-5 mt-1 text-muted-foreground" />
                      <div className="flex-1">
                        <p className="font-semibold">{report.titre}</p>
                        <p className="text-sm text-muted-foreground">
                          {report.dateGeneration} • {report.format.toUpperCase()} • {report.size || 'N/A'}
                        </p>
                        <div className="flex gap-2 mt-2">
                          <Badge
                            variant={
                              report.status === 'ready'
                                ? 'default'
                                : report.status === 'generating'
                                  ? 'secondary'
                                  : 'destructive'
                            }
                          >
                            {report.status === 'ready' ? 'Prêt' : report.status === 'generating' ? 'Génération' : 'Expiré'}
                          </Badge>
                          <Badge variant="outline">{report.type}</Badge>
                        </div>
                      </div>
                    </div>
                    <div className="flex gap-2">
                      <Dialog>
                        <DialogTrigger asChild>
                          <Button size="sm" variant="outline">
                            <Eye className="h-4 w-4" />
                          </Button>
                        </DialogTrigger>
                        <DialogContent>
                          <DialogHeader>
                            <DialogTitle>{report.titre}</DialogTitle>
                          </DialogHeader>
                          <div className="space-y-3">
                            <div>
                              <p className="text-sm font-medium">Type</p>
                              <Badge className="mt-1">{report.type}</Badge>
                            </div>
                            <div>
                              <p className="text-sm font-medium">Format</p>
                              <Badge className="mt-1">{report.format.toUpperCase()}</Badge>
                            </div>
                            <div>
                              <p className="text-sm font-medium">Statut</p>
                              <Badge className="mt-1">{report.status}</Badge>
                            </div>
                            <div>
                              <p className="text-sm font-medium">Généré par</p>
                              <p className="text-sm text-muted-foreground">{report.generePar}</p>
                            </div>
                            <Button onClick={() => downloadReportMutation.mutate(report)} className="w-full">
                              <Download className="h-4 w-4 mr-2" />
                              Télécharger
                            </Button>
                          </div>
                        </DialogContent>
                      </Dialog>
                      <Button
                        size="sm"
                        variant="outline"
                        onClick={() => downloadReportMutation.mutate(report)}
                        disabled={report.status !== 'ready'}
                      >
                        <Download className="h-4 w-4" />
                      </Button>
                      <Button
                        size="sm"
                        variant="outline"
                        onClick={() => deleteReportMutation.mutate(report.id)}
                      >
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>
                  </div>
                </div>
              ))
            )}
          </div>
        </ScrollArea>
      </Card>
    </div>
  );
}
