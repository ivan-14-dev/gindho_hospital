'use client';

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Download, RefreshCw } from 'lucide-react';
import type { ReactNode } from 'react';

interface ChartCardProps {
  title: string;
  icon: any;
  subtitle?: string;
  children: ReactNode;
  onRefresh?: () => void;
  onExport?: () => void;
  isLoading?: boolean;
}

export function ChartCard({
  title,
  icon: Icon,
  subtitle,
  children,
  onRefresh,
  onExport,
  isLoading = false
}: ChartCardProps) {
  return (
    <Card className="hover:shadow-md transition-shadow">
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-4">
        <div className="flex items-center gap-2">
          <Icon className="h-5 w-5 text-primary" />
          <div>
            <CardTitle className="text-base">{title}</CardTitle>
            {subtitle && <p className="text-xs text-muted-foreground mt-1">{subtitle}</p>}
          </div>
        </div>
        <div className="flex gap-1">
          {onRefresh && (
            <Button
              size="sm"
              variant="ghost"
              onClick={onRefresh}
              disabled={isLoading}
              className="h-8 w-8 p-0"
            >
              <RefreshCw className={`h-4 w-4 ${isLoading ? 'animate-spin' : ''}`} />
            </Button>
          )}
          {onExport && (
            <Button
              size="sm"
              variant="ghost"
              onClick={onExport}
              className="h-8 w-8 p-0"
            >
              <Download className="h-4 w-4" />
            </Button>
          )}
        </div>
      </CardHeader>
      <CardContent>
        {isLoading ? (
          <div className="flex items-center justify-center h-64">
            <div className="animate-spin">
              <RefreshCw className="h-6 w-6 text-muted-foreground" />
            </div>
          </div>
        ) : (
          children
        )}
      </CardContent>
    </Card>
  );
}
