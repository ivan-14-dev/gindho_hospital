import { useState } from 'react';
import { useMedicalRecords } from '@/hooks/use-medical-records';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { FileText, Search, Plus, Eye, Download } from 'lucide-react';
import { format } from 'date-fns';
import { fr } from 'date-fns/locale';

export function MedicalRecordsPage() {
  const [search, setSearch] = useState('');
  const { data: consultations, isLoading } = useMedicalRecords();

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Dossier Médical</h1>
          <p className="text-muted-foreground">Consultations et historique médical</p>
        </div>
        <div className="flex gap-2">
          <Button variant="outline">
            <Download className="h-4 w-4 mr-2" />
            Exporter PDF
          </Button>
          <Button>
            <Plus className="h-4 w-4 mr-2" />
            Nouvelle consultation
          </Button>
        </div>
      </div>

      <Card>
        <CardHeader>
          <div className="flex items-center gap-4">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
              <Input
                placeholder="Rechercher un patient..."
                value={search}
                onChange={(e: React.ChangeEvent<HTMLInputElement>) => setSearch(e.target.value)}
                className="pl-10"
              />
            </div>
          </div>
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <div className="flex items-center justify-center h-64">
              <div className="animate-spin w-8 h-8 border-4 border-primary border-t-transparent rounded-full" />
            </div>
          ) : consultations && consultations.length > 0 ? (
            <div className="space-y-4">
              {consultations.map((consultation) => (
                <Card key={consultation.id}>
                  <CardHeader>
                    <div className="flex items-start justify-between">
                      <div className="space-y-1">
                        <CardTitle className="text-lg">
                          {consultation.patientNom}
                        </CardTitle>
                        <p className="text-sm text-muted-foreground">
                          {format(new Date(consultation.date), 'dd MMMM yyyy', { locale: fr })}
                        </p>
                      </div>
                      <Badge>{consultation.medecin}</Badge>
                    </div>
                  </CardHeader>
                  <CardContent>
                    <div className="space-y-2">
                      <div>
                        <strong>Diagnostic :</strong> {consultation.diagnostic}
                      </div>
                      <div>
                        <strong>Notes :</strong> {consultation.notes}
                      </div>
                    </div>
                    <div className="flex gap-2 mt-4">
                      <Button variant="outline" size="sm">
                        <Eye className="h-4 w-4 mr-2" />
                        Détails
                      </Button>
                      <Button variant="outline" size="sm">
                        <FileText className="h-4 w-4 mr-2" />
                        Documents
                      </Button>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          ) : (
            <div className="text-center py-12 text-muted-foreground">
              Aucune consultation trouvée
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}