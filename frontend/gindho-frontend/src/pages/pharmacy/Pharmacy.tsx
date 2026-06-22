import { usePharmacy } from '@/hooks/use-pharmacy';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Plus, Eye } from 'lucide-react';

export function PharmacyPage() {
  const { data: prescriptions, isLoading } = usePharmacy();

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Pharmacie</h1>
          <p className="text-muted-foreground">Prescriptions et médicaments</p>
        </div>
        <Button>
          <Plus className="h-4 w-4 mr-2" />
          Nouvelle prescription
        </Button>
      </div>

      {isLoading ? (
        <div className="flex items-center justify-center h-64">
          <div className="animate-spin w-8 h-8 border-4 border-primary border-t-transparent rounded-full" />
        </div>
      ) : prescriptions && prescriptions.length > 0 ? (
        <div className="grid gap-4">
          {prescriptions.map((prescription) => (
            <Card key={prescription.id}>
              <CardHeader>
                <div className="flex items-start justify-between">
                  <div className="space-y-1">
                    <CardTitle className="text-lg">{prescription.patientNom}</CardTitle>
                    <p className="text-sm text-muted-foreground">{prescription.medicaments}</p>
                  </div>
                  <Badge variant={prescription.statut === 'ACTIVE' ? 'default' : 'secondary'}>
                    {prescription.statut}
                  </Badge>
                </div>
              </CardHeader>
              <CardContent>
                <p className="text-sm text-muted-foreground mb-2">
                  Date: {prescription.date}
                </p>
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
            <p className="text-muted-foreground">Aucune prescription trouvée</p>
          </CardContent>
        </Card>
      )}
    </div>
  );
}