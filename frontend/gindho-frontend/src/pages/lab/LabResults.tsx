import { useState } from 'react';
import { Card, CardContent, CardHeader } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Search, Download, Filter, FlaskRound, FileText } from 'lucide-react';
import { Skeleton } from '@/components/ui/skeleton';

interface LabResult {
  id: string;
  patient: string;
  type: string;
  dateDemande: string;
  dateResultat?: string;
  statut: 'En attente' | 'En cours' | 'Terminé' | 'Annulé';
  medecin: string;
}

const MOCK_RESULTS: LabResult[] = [
  { id: '1', patient: 'Jean Dupont', type: 'Analyse de sang', dateDemande: '2024-01-15', dateResultat: '2024-01-16', statut: 'Terminé', medecin: 'Dr. Martin' },
  { id: '2', patient: 'Marie Curie', type: 'IRM', dateDemande: '2024-01-15', statut: 'En cours', medecin: 'Dr. Petit' },
  { id: '3', patient: 'Pierre Durand', type: 'ECG', dateDemande: '2024-01-14', dateResultat: '2024-01-14', statut: 'Terminé', medecin: 'Dr. Leroy' },
  { id: '4', patient: 'Sophie Bernard', type: 'Scanner', dateDemande: '2024-01-13', statut: 'En attente', medecin: 'Dr. Moreau' },
  { id: '5', patient: 'Luc Petit', type: 'Biopsie', dateDemande: '2024-01-12', statut: 'Annulé', medecin: 'Dr. Roux' },
];

export function LabResultsPage() {
  const [search, setSearch] = useState('');
  const [isLoading] = useState(false);

  const filtered = MOCK_RESULTS.filter((r) =>
    r.patient.toLowerCase().includes(search.toLowerCase()) ||
    r.type.toLowerCase().includes(search.toLowerCase())
  );

  const getStatutColor = (statut: LabResult['statut']) => {
    switch (statut) {
      case 'Terminé':
        return 'bg-emerald-500/10 text-emerald-600 border-emerald-500/20';
      case 'En cours':
        return 'bg-blue-500/10 text-blue-600 border-blue-500/20';
      case 'En attente':
        return 'bg-amber-500/10 text-amber-600 border-amber-500/20';
      case 'Annulé':
        return 'bg-red-500/10 text-red-600 border-red-500/20';
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Résultats Laboratoire</h1>
          <p className="text-muted-foreground">
            Suivi et gestion des analyses et examens de laboratoire
          </p>
        </div>
        <div className="flex gap-2">
          <Button variant="outline" className="gap-2">
            <Filter className="h-4 w-4" />
            Filtres
          </Button>
          <Button variant="default" className="gap-2">
            <Download className="h-4 w-4" />
            Exporter
          </Button>
        </div>
      </div>

      <Card className="glass">
        <CardHeader>
          <div className="flex items-center gap-4">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
              <Input
                placeholder="Rechercher un résultat..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                className="pl-10"
              />
            </div>
          </div>
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <div className="space-y-4">
              {Array.from({ length: 4 }).map((_, i) => (
                <div key={i} className="space-y-2">
                  <Skeleton className="h-5 w-48" />
                  <Skeleton className="h-4 w-full" />
                  <Skeleton className="h-4 w-32" />
                </div>
              ))}
            </div>
          ) : filtered.length > 0 ? (
            <div className="space-y-3">
              {filtered.map((result) => (
                <div
                  key={result.id}
                  className="flex items-center justify-between p-4 rounded-lg border border-border/50 bg-card/50 hover:bg-accent/30 transition-all hover:shadow-lg hover:shadow-primary/5"
                >
                  <div className="flex items-start gap-4">
                    <div className="p-3 rounded-lg bg-primary/10">
                      <FlaskRound className="h-6 w-6 text-primary" />
                    </div>
                    <div className="space-y-1">
                      <div className="flex items-center gap-2">
                        <p className="font-semibold">{result.patient}</p>
                        <Badge variant="outline" className={getStatutColor(result.statut)}>
                          {result.statut}
                        </Badge>
                      </div>
                      <p className="text-sm text-muted-foreground">{result.type}</p>
                      <div className="flex items-center gap-4 text-sm text-muted-foreground">
                        <span>Demandé le: {result.dateDemande}</span>
                        {result.dateResultat && <span>Résultat le: {result.dateResultat}</span>}
                        <span>Par: {result.medecin}</span>
                      </div>
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    {result.statut === 'Terminé' && (
                      <Button variant="ghost" size="sm" className="gap-1">
                        <FileText className="h-4 w-4" />
                        Voir
                      </Button>
                    )}
                    <Button variant="ghost" size="sm">
                      Détails
                    </Button>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-12 text-muted-foreground">
              <FlaskRound className="h-12 w-12 mx-auto mb-4 opacity-20" />
              <p>Aucun résultat trouvé</p>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
