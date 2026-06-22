import { useQuery } from '@tanstack/react-query';
import { emergencyApi } from '@/services/api.service';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Plus } from 'lucide-react';

export interface Urgence {
  id: string;
  patientId: string;
  patientNom?: string;
  niveau: 'URGENT' | 'STAFFE' | 'NORMAL';
  motif?: string;
  date: string;
}

export function useUrgences() {
  return useQuery({
    queryKey: ['urgences'],
    queryFn: () => emergencyApi.getUrgencies(),
  });
}

export function EmergencyPage() {
  const { data: urgences, isLoading } = useUrgences();

  const getNiveauBadge = (niveau: string) => {
    const variants: Record<string, 'destructive' | 'secondary' | 'outline'> = {
      URGENT: 'destructive',
      STAFFE: 'secondary',
      NORMAL: 'outline',
    };
    return variants[niveau] || 'secondary';
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin w-8 h-8 border-4 border-primary border-t-transparent rounded-full" />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Urgences</h1>
          <p className="text-muted-foreground">Gestion des passages aux urgences</p>
        </div>
        <Button>
          <Plus className="h-4 w-4 mr-2" />
          Nouvelle urgence
        </Button>
      </div>

      <div className="grid gap-4">
        {(urgences as Urgence[] | undefined)?.map((urgence) => (
          <Card key={urgence.id}>
            <CardHeader>
              <div className="flex items-start justify-between">
                <div className="space-y-1">
                  <CardTitle className="text-lg">{urgence.patientNom || `Patient #${urgence.patientId}`}</CardTitle>
                  <p className="text-sm text-muted-foreground">{urgence.motif || '-'}</p>
                </div>
                <Badge variant={getNiveauBadge(urgence.niveau)}>{urgence.niveau}</Badge>
              </div>
            </CardHeader>
            <CardContent>
              <p className="text-sm text-muted-foreground">
                {new Date(urgence.date).toLocaleString()}
              </p>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
}