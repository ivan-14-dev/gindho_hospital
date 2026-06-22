import { Suspense } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';

interface ChartCardProps {
  title: string;
  children: React.ReactNode;
  height?: number;
}

export function ChartCard({ title, children, height = 300 }: ChartCardProps) {
  return (
    <Card>
      <CardHeader>
        <CardTitle>{title}</CardTitle>
      </CardHeader>
      <CardContent>
        <Suspense fallback={<Skeleton className="w-full animate-pulse" style={{ height }} />}>
          <div style={{ height }}>{children}</div>
        </Suspense>
      </CardContent>
    </Card>
  );
}