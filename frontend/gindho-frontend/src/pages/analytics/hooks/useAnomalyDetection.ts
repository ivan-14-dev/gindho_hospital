import { useEffect, useState } from 'react';

export interface Anomaly {
  id: string;
  metric: string;
  value: number;
  threshold: number;
  severity: 'low' | 'medium' | 'high' | 'critical';
  message: string;
  timestamp: Date;
}

export function useAnomalyDetection(data: any[], thresholds: Record<string, number>) {
  const [anomalies, setAnomalies] = useState<Anomaly[]>([]);

  useEffect(() => {
    const detected: Anomaly[] = [];

    data.forEach((item, index) => {
      Object.entries(thresholds).forEach(([metric, threshold]) => {
        const value = item[metric];

        if (value === undefined || value === null) return;

        let severity: 'low' | 'medium' | 'high' | 'critical' = 'low';
        let message = '';

        if (typeof value === 'number' && typeof threshold === 'number') {
          const deviation = ((value - threshold) / threshold) * 100;

          if (deviation < -50) {
            severity = 'critical';
            message = `${metric} est critiquement bas: ${value} (attendu: ${threshold})`;
          } else if (deviation < -25) {
            severity = 'high';
            message = `${metric} est significativement bas: ${value}`;
          } else if (deviation > 50) {
            severity = 'critical';
            message = `${metric} est critiquement élevé: ${value} (limité à: ${threshold})`;
          } else if (deviation > 25) {
            severity = 'high';
            message = `${metric} est significativement élevé: ${value}`;
          }

          if (severity !== 'low') {
            detected.push({
              id: `${metric}-${index}`,
              metric,
              value,
              threshold,
              severity,
              message,
              timestamp: new Date(),
            });
          }
        }
      });
    });

    setAnomalies(detected);
  }, [data, thresholds]);

  return anomalies;
}

export function filterAnomaliesBySeverity(
  anomalies: Anomaly[],
  severity: 'low' | 'medium' | 'high' | 'critical'
): Anomaly[] {
  return anomalies.filter(a => a.severity === severity);
}

export function getAnomalyStats(anomalies: Anomaly[]) {
  return {
    total: anomalies.length,
    critical: anomalies.filter(a => a.severity === 'critical').length,
    high: anomalies.filter(a => a.severity === 'high').length,
    medium: anomalies.filter(a => a.severity === 'medium').length,
    low: anomalies.filter(a => a.severity === 'low').length,
  };
}
