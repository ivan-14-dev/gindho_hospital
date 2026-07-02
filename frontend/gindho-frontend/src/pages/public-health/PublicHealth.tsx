import { useState } from 'react';
import { Card, CardContent, CardHeader } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Search, Download, AlertTriangle, Users, TrendingUp } from 'lucide-react';
import { Skeleton } from '@/components/ui/skeleton';

interface PublicHealthAlert {
  id: string;
  titre: string;
  type: 'Epidemie' | 'Vaccination' | 'Pollution' | 'Autre';
  dateDebut: string;
  dateFin?: string;
  region: string;
  gravite: 'Faible' | 'Moyenne' | 'Elevee' | 'Critique';
  statut: 'En cours' | 'Résolu';
}

const MOCK_ALERTS: PublicHealthAlert[] = [
  { id: '1', titre: 'Pic de grippe saisonnière', type: 'Epidemie', dateDebut: '2024-01-10', region: 'Île-de-France', gravite: 'Elevee', statut: 'En cours' },
  { id: '2', titre: 'Campagne de vaccination COVID-19', type: 'Vaccination', dateDebut: '2024-01-01', dateFin: '2024-03-01', region: 'National', gravite: 'Moyenne', statut: 'En cours' },
  { id: '3', titre: 'Qualité de l\'air dégradée', type: 'Pollution', dateDebut: '2024-01-12', region: 'Lyon', gravite: 'Faible', statut: 'Résolu' },
  { id: '4', titre: 'Hépatite A - cluster', type: 'Epidemie', dateDebut: '2024-01-08', gravite: 'Critique', statut: 'En cours', region: 'Marseille' },
];

export function PublicHealthPage() {
  const [search, setSearch] = useState('');
  const [isLoading] = useState(false);

  const filtered = MOCK_ALERTS.filter((a) =>
    a.titre.toLowerCase().includes(search.toLowerCase()) ||
    a.region.toLowerCase().includes(search.toLowerCase()) ||
    a.type.toLowerCase().includes(search.toLowerCase())
  );

  const getGraviteBadge = (gravite: PublicHealthAlert['gravite']) => {
    switch (gravite) {
      case 'Critique':
        return <Badge variant="destructive">Critique</Badge>;
      case 'Elevee':
        return <Badge variant="secondary" className="bg-red-500/10 text-red-600 border-red-500/20">Élevée</Badge>;
      case 'Moyenne':
        return <Badge variant="secondary" className="bg-amber-500/10 text-amber-600 border-amber-500/20">Moyenne</Badge>;
      case 'Faible':
        return <Badge variant="secondary" className="bg-blue-500/10 text-blue-600 border-blue-500/20">Faible</Badge>;
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Santé Publique</h1>
          <p className="text-muted-foreground">
            Surveillance épidémiologique et alertes sanitaires
          </p>
        </div>
        <div className="flex gap-2">
          <Button variant="outline" className="gap-2">
            <Download className="h-4 w-4" />
            Rapport
          </Button>
        </div>
      </div>

      <div className="grid gap-4 md:grid-cols-3">
        <Card className="glass">
          <CardContent className="pt-6">
            <div className="flex items-center gap-4">
              <div className="p-3 rounded-lg bg-destructive/10">
                <AlertTriangle className="h-6 w-6 text-destructive" />
              </div>
              <div>
                <p className="text-2xl font-bold">4</p>
                <p className="text-sm text-muted-foreground">Alertes actives</p>
              </div>
            </div>
          </CardContent>
        </Card>
        <Card className="glass">
          <CardContent className="pt-6">
            <div className="flex items-center gap-4">
              <div className="p-3 rounded-lg bg-amber-500/10">
                <TrendingUp className="h-6 w-6 text-amber-600" />
              </div>
              <div>
                <p className="text-2xl font-bold">1</p>
                <p className="text-sm text-muted-foreground">Critique</p>
              </div>
            </div>
          </CardContent>
        </Card>
        <Card className="glass">
          <CardContent className="pt-6">
            <div className="flex items-center gap-4">
              <div className="p-3 rounded-lg bg-primary/10">
                <Users className="h-6 w-6 text-primary" />
              </div>
              <div>
                <p className="text-2xl font-bold">2.4M</p>
                <p className="text-sm text-muted-foreground">Personnes suivies</p>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      <Card className="glass">
        <CardHeader>
          <div className="flex items-center gap-4">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
              <Input
                placeholder="Rechercher une alerte..."
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
              {Array.from({ length: 3 }).map((_, i) => (
                <div key={i} className="space-y-2">
                  <Skeleton className="h-6 w-64" />
                  <Skeleton className="h-4 w-full" />
                  <Skeleton className="h-4 w-40" />
                </div>
              ))}
            </div>
          ) : filtered.length > 0 ? (
            <div className="space-y-3">
              {filtered.map((alert) => (
                <div
                  key={alert.id}
                  className="flex items-center justify-between p-4 rounded-lg border border-border/50 bg-card/50 hover:bg-accent/30 transition-all hover:shadow-lg hover:shadow-primary/5"
                >
                  <div className="flex items-start gap-4">
                    <div className="p-3 rounded-lg bg-primary/10">
                      <AlertTriangle className="h-6 w-6 text-primary" />
                    </div>
                    <div className="space-y-1">
                      <div className="flex items-center gap-2">
                        <p className="font-semibold">{alert.titre}</p>
                        {getGraviteBadge(alert.gravite)}
                      </div>
                      <p className="text-sm text-muted-foreground">{alert.type}</p>
                      <div className="flex items-center gap-4 text-sm text-muted-foreground">
                        <span>Région: {alert.region}</span>
                        <span>Depuis: {alert.dateDebut}</span>
                        {alert.dateFin && <span>Jusqu'au: {alert.dateFin}</span>}
                        <Badge variant="outline" className="text-xs">{alert.statut}</Badge>
                      </div>
                    </div>
                  </div>
                  <Button variant="ghost" size="sm">
                    Détails
                  </Button>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-12 text-muted-foreground">
              <AlertTriangle className="h-12 w-12 mx-auto mb-4 opacity-20" />
              <p>Aucune alerte trouvée</p>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
