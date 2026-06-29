import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Download, Filter, Search, AlertCircle, CheckCircle } from 'lucide-react';
import { apiClient } from '@/lib/api-client';
import type { AuditLog } from '@/types';

export default function AuditLogs() {
  const [filters, setFilters] = useState({
    action: '',
    resource: '',
    status: '',
    startDate: '',
    endDate: '',
  });

  const { data: logs, isLoading } = useQuery({
    queryKey: ['audit-logs', filters],
    queryFn: async () => {
      const params = new URLSearchParams();
      Object.entries(filters).forEach(([key, value]) => {
        if (value) params.append(key, value);
      });
      const res = await apiClient.get(`/audit-logs?${params}`);
      return res.data as AuditLog[];
    },
  });

  const exportToCSV = () => {
    const csv = [
      ['User', 'Action', 'Resource', 'Status', 'Timestamp', 'IP Address'],
      ...(logs?.map(log => [
        log.userId,
        log.action,
        `${log.ressource}#${log.ressourceId}`,
        log.status,
        new Date(log.timestamp).toISOString(),
        log.adresseIP || 'N/A',
      ]) || []),
    ]
      .map(row => row.map(cell => `"${cell}"`).join(','))
      .join('\n');

    const blob = new Blob([csv], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `audit-logs-${new Date().toISOString()}.csv`;
    a.click();
  };

  const successCount = logs?.filter(l => l.status === 'success').length || 0;
  const failureCount = logs?.filter(l => l.status === 'failure').length || 0;

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold tracking-tight">Journaux d'audit</h1>
        <p className="text-gray-600 mt-2">Suivi des actions et modifications dans le système</p>
      </div>

      <div className="grid grid-cols-4 gap-4">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-600">Total d'entrées</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold">{logs?.length || 0}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-600">Succès</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-green-600">{successCount}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-600">Erreurs</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-red-600">{failureCount}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-600">Taux de succès</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-blue-600">
              {((successCount / (logs?.length || 1)) * 100).toFixed(0)}%
            </div>
          </CardContent>
        </Card>
      </div>

      <div className="space-y-4">
        <div className="flex justify-between items-center">
          <h2 className="text-2xl font-bold">Filtres</h2>
          <Button onClick={exportToCSV} variant="outline">
            <Download className="h-4 w-4 mr-2" />
            Exporter CSV
          </Button>
        </div>

        <Card>
          <CardContent className="pt-6">
            <div className="grid grid-cols-2 md:grid-cols-5 gap-4">
              <div>
                <label className="text-sm font-medium">Action</label>
                <Input
                  placeholder="Filtrer action"
                  value={filters.action}
                  onChange={(e) => setFilters({ ...filters, action: e.target.value })}
                />
              </div>
              <div>
                <label className="text-sm font-medium">Ressource</label>
                <Input
                  placeholder="Filtrer ressource"
                  value={filters.resource}
                  onChange={(e) => setFilters({ ...filters, resource: e.target.value })}
                />
              </div>
              <div>
                <label className="text-sm font-medium">Statut</label>
                <select
                  value={filters.status}
                  onChange={(e) => setFilters({ ...filters, status: e.target.value })}
                  className="w-full border rounded px-2 py-1 text-sm"
                >
                  <option value="">Tous</option>
                  <option value="success">Succès</option>
                  <option value="failure">Erreur</option>
                </select>
              </div>
              <div>
                <label className="text-sm font-medium">Du</label>
                <Input
                  type="date"
                  value={filters.startDate}
                  onChange={(e) => setFilters({ ...filters, startDate: e.target.value })}
                />
              </div>
              <div>
                <label className="text-sm font-medium">Au</label>
                <Input
                  type="date"
                  value={filters.endDate}
                  onChange={(e) => setFilters({ ...filters, endDate: e.target.value })}
                />
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      <div>
        <h2 className="text-2xl font-bold mb-4">Logs d'audit</h2>
        {isLoading ? (
          <div className="text-center py-8">Chargement...</div>
        ) : logs && logs.length > 0 ? (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b bg-gray-50">
                  <th className="p-3 text-left font-medium">Utilisateur</th>
                  <th className="p-3 text-left font-medium">Action</th>
                  <th className="p-3 text-left font-medium">Ressource</th>
                  <th className="p-3 text-left font-medium">Statut</th>
                  <th className="p-3 text-left font-medium">Timestamp</th>
                  <th className="p-3 text-left font-medium">IP Address</th>
                </tr>
              </thead>
              <tbody>
                {logs.map((log) => (
                  <tr key={log.id} className="border-b hover:bg-gray-50">
                    <td className="p-3">{log.userId}</td>
                    <td className="p-3 font-medium">{log.action}</td>
                    <td className="p-3">
                      <code className="bg-gray-100 px-2 py-1 rounded text-xs">
                        {log.ressource}#{log.ressourceId}
                      </code>
                    </td>
                    <td className="p-3">
                      <Badge
                        variant={log.status === 'success' ? 'default' : 'destructive'}
                        className={
                          log.status === 'success'
                            ? 'bg-green-100 text-green-800'
                            : 'bg-red-100 text-red-800'
                        }
                      >
                        {log.status === 'success' ? (
                          <>
                            <CheckCircle className="h-3 w-3 mr-1" />
                            Succès
                          </>
                        ) : (
                          <>
                            <AlertCircle className="h-3 w-3 mr-1" />
                            Erreur
                          </>
                        )}
                      </Badge>
                    </td>
                    <td className="p-3 text-gray-600">
                      {new Date(log.timestamp).toLocaleString('fr-FR')}
                    </td>
                    <td className="p-3 text-gray-600">{log.adresseIP || 'N/A'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <Card>
            <CardContent className="py-8 text-center text-gray-600">
              <Search className="h-12 w-12 mx-auto mb-4 opacity-50" />
              <p>Aucun log correspondant aux filtres</p>
            </CardContent>
          </Card>
        )}
      </div>
    </div>
  );
}
