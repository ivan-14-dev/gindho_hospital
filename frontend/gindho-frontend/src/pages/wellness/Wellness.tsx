import { useState } from 'react';
import { Card, CardContent, CardHeader } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Search, Plus, Heart, Activity, Moon } from 'lucide-react';
import { Skeleton } from '@/components/ui/skeleton';

interface WellnessProgram {
  id: string;
  nom: string;
  categorie: 'Sport' | 'Nutrition' | 'Mental' | 'Sommeil' | 'Bien-etre';
  participants: number;
  dateDebut: string;
  responsable: string;
  statut: 'En cours' | 'Planifié' | 'Terminé';
}

const MOCK_PROGRAMS: WellnessProgram[] = [
  { id: '1', nom: 'Yoga du matin', categorie: 'Sport', participants: 24, dateDebut: '2024-01-10', responsable: 'Marie Lefebvre', statut: 'En cours' },
  { id: '2', nom: 'Atelier nutrition équilibrée', categorie: 'Nutrition', participants: 18, dateDebut: '2024-01-15', responsable: 'Jean Duval', statut: 'Planifié' },
  { id: '3', nom: 'Gestion du stress', categorie: 'Mental', participants: 30, dateDebut: '2024-01-05', responsable: 'Sophie Martin', statut: 'En cours' },
  { id: '4', nom: 'Amélioration du sommeil', categorie: 'Sommeil', participants: 15, dateDebut: '2024-01-01', responsable: 'Luc Petit', statut: 'Terminé' },
  { id: '5', nom: 'Marche nordique', categorie: 'Sport', participants: 20, dateDebut: '2024-01-12', responsable: 'Claire Dubois', statut: 'En cours' },
];

export function WellnessPage() {
  const [search, setSearch] = useState('');
  const [isLoading] = useState(false);

  const filtered = MOCK_PROGRAMS.filter((p) =>
    p.nom.toLowerCase().includes(search.toLowerCase()) ||
    p.categorie.toLowerCase().includes(search.toLowerCase()) ||
    p.responsable.toLowerCase().includes(search.toLowerCase())
  );

  const getCategorieIcon = (categorie: WellnessProgram['categorie']) => {
    switch (categorie) {
      case 'Sport':
        return <Activity className="h-4 w-4" />;
      case 'Nutrition':
        return <Heart className="h-4 w-4" />;
      case 'Mental':
        return <Heart className="h-4 w-4" />;
      case 'Sommeil':
        return <Moon className="h-4 w-4" />;
      default:
        return <Heart className="h-4 w-4" />;
    }
  };

  const getStatutBadge = (statut: WellnessProgram['statut']) => {
    switch (statut) {
      case 'En cours':
        return <Badge variant="default" className="bg-emerald-500/10 text-emerald-600 border-emerald-500/20">En cours</Badge>;
      case 'Planifié':
        return <Badge variant="secondary" className="bg-blue-500/10 text-blue-600 border-blue-500/20">Planifié</Badge>;
      case 'Terminé':
        return <Badge variant="outline" className="text-muted-foreground">Terminé</Badge>;
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Bien-être</h1>
          <p className="text-muted-foreground">
            Programmes de bien-être et de prévention pour le personnel
          </p>
        </div>
        <Button variant="default" className="gap-2">
          <Plus className="h-4 w-4" />
          Nouveau programme
        </Button>
      </div>

      <Card className="glass">
        <CardHeader>
          <div className="flex items-center gap-4">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
              <Input
                placeholder="Rechercher un programme..."
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
                  <Skeleton className="h-6 w-48" />
                  <Skeleton className="h-4 w-full" />
                  <Skeleton className="h-4 w-32" />
                </div>
              ))}
            </div>
          ) : filtered.length > 0 ? (
            <div className="space-y-3">
              {filtered.map((program) => (
                <div
                  key={program.id}
                  className="flex items-center justify-between p-4 rounded-lg border border-border/50 bg-card/50 hover:bg-accent/30 transition-all hover:shadow-lg hover:shadow-primary/5"
                >
                  <div className="flex items-start gap-4">
                    <div className="p-3 rounded-lg bg-primary/10">
                      <Heart className="h-6 w-6 text-primary" />
                    </div>
                    <div className="space-y-1">
                      <div className="flex items-center gap-2">
                        <p className="font-semibold text-lg">{program.nom}</p>
                        {getStatutBadge(program.statut)}
                      </div>
                      <div className="flex items-center gap-2 text-sm text-muted-foreground">
                        {getCategorieIcon(program.categorie)}
                        <span>{program.categorie}</span>
                      </div>
                      <div className="flex items-center gap-4 text-sm text-muted-foreground">
                        <span>{program.participants} participants</span>
                        <span>Début: {program.dateDebut}</span>
                        <span>Responsable: {program.responsable}</span>
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
              <Heart className="h-12 w-12 mx-auto mb-4 opacity-20" />
              <p>Aucun programme trouvé</p>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
