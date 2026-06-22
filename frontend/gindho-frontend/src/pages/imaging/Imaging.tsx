import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Plus } from 'lucide-react';

export function ImagingPage() {
  const examens = [
    { id: '1', patient: 'Dupont Jean', type: 'Radiographie', date: '2024-01-15', statut: 'TERMINE', resultat: 'Normal' },
    { id: '2', patient: 'Martin Marie', type: 'Scanner', date: '2024-01-16', statut: 'EN_COURS', resultat: null },
  ];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Imagerie</h1>
          <p className="text-muted-foreground">Examens d'imagerie médicale</p>
        </div>
        <Button>
          <Plus className="h-4 w-4 mr-2" />
          Nouvel examen
        </Button>
      </div>

      <div className="grid gap-4">
        {examens.map((examen) => (
          <Card key={examen.id}>
            <CardHeader>
              <div className="flex items-start justify-between">
                <div className="space-y-1">
                  <CardTitle className="text-lg">{examen.patient}</CardTitle>
                  <p className="text-sm text-muted-foreground">{examen.type}</p>
                </div>
                <Badge variant={examen.statut === 'TERMINE' ? 'default' : 'secondary'}>{examen.statut}</Badge>
              </div>
            </CardHeader>
            <CardContent>
              {examen.resultat && <p className="text-sm mb-2"><strong>Résultat :</strong> {examen.resultat}</p>}
              <Button variant="outline" size="sm">Détails</Button>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
}