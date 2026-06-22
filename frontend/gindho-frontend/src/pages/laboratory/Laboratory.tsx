import { useLaboratory } from '@/hooks/use-laboratory';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Plus, Eye } from 'lucide-react';

export function LaboratoryPage() {
  const { data: analyses, isLoading } = useLaboratory();

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Laboratoire</h1>
          <p className="text-muted-foreground">Analyses et résultats</p>
        </div>
        <Button>
          <Plus className="h-4 w-4 mr-2" />
          Nouvelle analyse
        </Button>
      </div>

      {isLoading ? (
        <div className="flex items-center justify-center h-64">
          <div className="animate-spin w-8 h-8 border-4 border-primary border-t-transparent rounded-full" />
        </div>
      ) : analyses && analyses.length > 0 ? (
        <div className="grid gap-4">
          {analyses.map((analyse) => (
            <Card key={analyse.id}>
              <CardHeader>
                <div className="flex items-start justify-between">
                  <div className="space-y-1">
                    <CardTitle className="text-lg">{analyse.patientNom}</CardTitle>
                    <p className="text-sm text-muted-foreground">{analyse.type}</p>
                  </div>
                  <Badge variant={analyse.statut === 'TERMINE' ? 'default' : 'secondary'}>
                    {analyse.statut}
                  </Badge>
                </div>
              </CardHeader>
              <CardContent>
                {analyse.resultat && (
                  <p className="text-sm mb-2"><strong>Résultat :</strong> {analyse.resultat}</p>
                )}
                <div className="flex gap-2">
                  <Button variant="outline" size="sm">
                    <Eye className="h-4 w-4 mr-2" />
                    Détails
                  </Button>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      ) : (
        <Card>
          <CardContent className="text-center py-12">
            <p className="text-muted-foreground">Aucune analyse trouvée</p>
          </CardContent>
        </Card>
      )}
    </div>
  );
}