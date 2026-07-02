import { useState } from 'react';
import { Card, CardContent, CardHeader } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Search, Plus, MapPin, Users, Activity } from 'lucide-react';
import { Skeleton } from '@/components/ui/skeleton';

interface Facility {
  id: string;
  nom: string;
  type: string;
  localisation: string;
  capacite: number;
  occupation: number;
  statut: 'ACTIF' | 'MAINTENANCE' | 'FERME';
}

const MOCK_FACILITIES: Facility[] = [
  { id: '1', nom: 'Bloc Opératoire A', type: 'Chirurgie', localisation: 'Bâtiment A - Niveau 2', capacite: 12, occupation: 8, statut: 'ACTIF' },
  { id: '2', nom: 'Unité de Soins Intensifs', type: 'Réanimation', localisation: 'Bâtiment B - Niveau 1', capacite: 20, occupation: 18, statut: 'ACTIF' },
  { id: '3', nom: 'Laboratoire Central', type: 'Diagnostic', localisation: 'Bâtiment C - Rez-de-chaussée', capacite: 8, occupation: 5, statut: 'ACTIF' },
  { id: '4', nom: 'Salle d\'Urgence', type: 'Urgences', localisation: 'Bâtiment A - Niveau 1', capacite: 30, occupation: 22, statut: 'ACTIF' },
  { id: '5', nom: 'Maternité', type: 'Maternité', localisation: 'Bâtiment D - Niveau 2', capacite: 15, occupation: 0, statut: 'MAINTENANCE' },
];

export function FacilitiesPage() {
  const [search, setSearch] = useState('');
  const [isLoading] = useState(false);

  const filtered = MOCK_FACILITIES.filter((f) =>
    f.nom.toLowerCase().includes(search.toLowerCase()) ||
    f.type.toLowerCase().includes(search.toLowerCase())
  );

  const getStatutBadge = (statut: Facility['statut']) => {
    switch (statut) {
      case 'ACTIF':
        return <Badge variant="default" className="bg-emerald-500/10 text-emerald-600 border-emerald-500/20">Actif</Badge>;
      case 'MAINTENANCE':
        return <Badge variant="secondary" className="bg-amber-500/10 text-amber-600 border-amber-500/20">Maintenance</Badge>;
      case 'FERME':
        return <Badge variant="destructive" className="bg-red-500/10 text-red-600 border-red-500/20">Fermé</Badge>;
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Infrastructures</h1>
          <p className="text-muted-foreground">
            Gestion des installations et infrastructures hospitalières
          </p>
        </div>
        <Button variant="default" className="gap-2">
          <Plus className="h-4 w-4" />
          Nouvelle infrastructure
        </Button>
      </div>

      <Card className="glass">
        <CardHeader>
          <div className="flex items-center gap-4">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
              <Input
                placeholder="Rechercher une infrastructure..."
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
            <div className="space-y-4">
              {filtered.map((facility) => (
                <div
                  key={facility.id}
                  className="flex items-center justify-between p-4 rounded-lg border border-border/50 bg-card/50 hover:bg-accent/30 transition-all hover:shadow-lg hover:shadow-primary/5"
                >
                  <div className="flex items-start gap-4">
                    <div className="p-3 rounded-lg bg-primary/10">
                      <MapPin className="h-6 w-6 text-primary" />
                    </div>
                    <div className="space-y-1">
                      <div className="flex items-center gap-2">
                        <p className="font-semibold text-lg">{facility.nom}</p>
                        {getStatutBadge(facility.statut)}
                      </div>
                      <p className="text-sm text-muted-foreground">{facility.type}</p>
                      <div className="flex items-center gap-4 text-sm text-muted-foreground">
                        <span className="flex items-center gap-1">
                          <MapPin className="h-3 w-3" />
                          {facility.localisation}
                        </span>
                        <span className="flex items-center gap-1">
                          <Users className="h-3 w-3" />
                          {facility.capacite} places
                        </span>
                        <span className="flex items-center gap-1">
                          <Activity className="h-3 w-3" />
                          {facility.occupation} occupés
                        </span>
                      </div>
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    <Button variant="ghost" size="sm">
                      Voir détails
                    </Button>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-12 text-muted-foreground">
              <MapPin className="h-12 w-12 mx-auto mb-4 opacity-20" />
              <p>Aucune infrastructure trouvée</p>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
