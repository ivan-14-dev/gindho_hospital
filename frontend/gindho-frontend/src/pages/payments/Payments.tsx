import { usePayments } from '@/hooks/use-payments';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { DollarSign, Plus, Eye } from 'lucide-react';

export function PaymentsPage() {
  const { data: factures, isLoading } = usePayments();

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Facturation</h1>
          <p className="text-muted-foreground">Gestion des factures et paiements</p>
        </div>
        <Button>
          <Plus className="h-4 w-4 mr-2" />
          Nouvelle facture
        </Button>
      </div>

      {isLoading ? (
        <div className="flex items-center justify-center h-64">
          <div className="animate-spin w-8 h-8 border-4 border-primary border-t-transparent rounded-full" />
        </div>
      ) : factures && factures.length > 0 ? (
        <div className="grid gap-4">
          {factures.map((facture) => (
            <Card key={facture.id}>
              <CardHeader>
                <div className="flex items-start justify-between">
                  <div className="space-y-1">
                    <CardTitle className="text-lg">{facture.patientNom}</CardTitle>
                    <p className="text-sm text-muted-foreground">Date: {facture.date}</p>
                  </div>
                  <div className="text-right">
                    <p className="text-2xl font-bold">{facture.montant}€</p>
                    <Badge variant={facture.statut === 'PAYEE' ? 'default' : 'secondary'}>
                      {facture.statut}
                    </Badge>
                  </div>
                </div>
              </CardHeader>
              <CardContent>
                <div className="flex gap-2">
                  <Button variant="outline" size="sm">
                    <Eye className="h-4 w-4 mr-2" />
                    Détails
                  </Button>
                  {facture.statut === 'EN_ATTENTE' && (
                    <Button size="sm">
                      <DollarSign className="h-4 w-4 mr-2" />
                      Encaisser
                    </Button>
                  )}
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      ) : (
        <Card>
          <CardContent className="text-center py-12">
            <p className="text-muted-foreground">Aucune facture trouvée</p>
          </CardContent>
        </Card>
      )}
    </div>
  );
}