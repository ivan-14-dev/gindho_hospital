'use client';

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { AlertCircle, AlertTriangle, AlertOctagon } from 'lucide-react';
import { Anomaly, getAnomalyStats } from '../hooks/useAnomalyDetection';

interface AlertPanelProps {
  anomalies: Anomaly[];
  title?: string;
}

export function AlertPanel({ anomalies, title = 'Alertes & Anomalies' }: AlertPanelProps) {
  const stats = getAnomalyStats(anomalies);

  if (anomalies.length === 0) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="text-base flex items-center gap-2">
            <AlertCircle className="h-5 w-5 text-green-600" />
            {title}
          </CardTitle>
        </CardHeader>
        <CardContent>
          <p className="text-muted-foreground text-sm">Tous les métriques sont normales. Aucune alerte détectée.</p>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-base flex items-center justify-between">
          <span className="flex items-center gap-2">
            <AlertTriangle className="h-5 w-5 text-orange-600" />
            {title}
          </span>
          <div className="flex gap-2">
            {stats.critical > 0 && (
              <Badge variant="destructive">{stats.critical} Critique</Badge>
            )}
            {stats.high > 0 && (
              <Badge variant="secondary">{stats.high} Haut</Badge>
            )}
          </div>
        </CardTitle>
      </CardHeader>
      <CardContent className="space-y-3">
        {anomalies.map((anomaly) => (
          <div
            key={anomaly.id}
            className={`p-3 rounded-lg border-l-4 ${
              anomaly.severity === 'critical'
                ? 'bg-red-50 border-red-500'
                : anomaly.severity === 'high'
                ? 'bg-orange-50 border-orange-500'
                : anomaly.severity === 'medium'
                ? 'bg-yellow-50 border-yellow-500'
                : 'bg-blue-50 border-blue-500'
            }`}
          >
            <div className="flex items-start gap-3">
              {anomaly.severity === 'critical' ? (
                <AlertOctagon className="h-5 w-5 text-red-600 mt-0.5 flex-shrink-0" />
              ) : (
                <AlertTriangle className="h-5 w-5 text-orange-600 mt-0.5 flex-shrink-0" />
              )}
              <div className="flex-1">
                <div className="flex items-center gap-2">
                  <p className="font-semibold text-sm">{anomaly.metric}</p>
                  <Badge
                    variant={
                      anomaly.severity === 'critical'
                        ? 'destructive'
                        : anomaly.severity === 'high'
                        ? 'secondary'
                        : 'outline'
                    }
                  >
                    {anomaly.severity}
                  </Badge>
                </div>
                <p className="text-xs text-muted-foreground mt-1">{anomaly.message}</p>
                <p className="text-xs text-muted-foreground mt-1">
                  {anomaly.timestamp.toLocaleString('fr-FR')}
                </p>
              </div>
            </div>
          </div>
        ))}
      </CardContent>
    </Card>
  );
}
