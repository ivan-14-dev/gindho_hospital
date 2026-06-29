'use client';

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { TrendingUp, TrendingDown, Minus } from 'lucide-react';
import { cn } from '@/lib/utils';

interface KPICardProps {
  title: string;
  value: number | string;
  change: number;
  trend: 'up' | 'down' | 'flat';
  icon: any;
  color: string;
  unit?: string;
  subtitle?: string;
}

export function KPICard({
  title,
  value,
  change,
  trend,
  icon: Icon,
  color,
  unit = '',
  subtitle
}: KPICardProps) {
  const trendIcon = trend === 'up' ? TrendingUp : trend === 'down' ? TrendingDown : Minus;
  const TrendIcon = trendIcon;
  const trendColor = trend === 'up' ? 'text-green-600' : trend === 'down' ? 'text-red-600' : 'text-gray-600';

  return (
    <Card className="hover:shadow-md transition-shadow">
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
        <CardTitle className="text-sm font-medium">{title}</CardTitle>
        <Icon className={cn('h-4 w-4', color)} />
      </CardHeader>
      <CardContent>
        <div className="text-2xl font-bold">
          {value}
          {unit && <span className="text-sm ml-1">{unit}</span>}
        </div>
        <div className="flex items-center gap-1 mt-2">
          <TrendIcon className={cn('h-4 w-4', trendColor)} />
          <span className={cn('text-xs font-medium', trendColor)}>
            {trend === 'up' ? '+' : trend === 'down' ? '-' : ''}{Math.abs(change)}%
          </span>
          <span className="text-xs text-muted-foreground">vs dernier mois</span>
        </div>
        {subtitle && <p className="text-xs text-muted-foreground mt-1">{subtitle}</p>}
      </CardContent>
    </Card>
  );
}
