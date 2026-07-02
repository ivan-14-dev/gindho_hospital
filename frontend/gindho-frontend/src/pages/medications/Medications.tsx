import { useState } from 'react';
import { Card, CardContent, CardHeader } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Search, Plus, Pill, Package } from 'lucide-react';
import { Skeleton } from '@/components/ui/skeleton';

interface Medication {
  id: string;
  nom: string;
  dosage: string;
  forme: string;
  quantite: number;
  stockMin: number;
  fournisseur: string;
  dateExpiration: string;
}

const MOCK_MEDICATIONS: Medication[] = [
  { id: '1', nom: 'Paracétamol', dosage: '500mg', forme: 'Comprimé', quantite: 1200, stockMin: 200, fournisseur: 'PharmaCorp', dateExpiration: '2025-06-01' },
  { id: '2', nom: 'Amoxicilline', dosage: '250mg', forme: 'Gélule', quantite: 85, stockMin: 100, fournisseur: 'MediLab', dateExpiration: '2024-12-15' },
  { id: '3', nom: 'Ibuprofène', dosage: '400mg', forme: 'Comprimé', quantite: 340, stockMin: 150, fournisseur: 'PharmaCorp', dateExpiration: '2025-03-20' },
  { id: '4', nom: 'Doliprane', dosage: '1000mg', forme: 'Comprimé', quantite: 500, stockMin: 100, fournisseur: 'Sanofi', dateExpiration: '2025-09-10' },
  { id: '5', nom: 'Insuline', dosage: '100UI/ml', forme: 'Solution injectable', quantite: 0, stockMin: 50, fournisseur: 'Novo Nordisk', dateExpiration: '2024-10-30' },
];

export function MedicationsPage() {
  const [search, setSearch] = useState('');
  const [isLoading] = useState(false);

  const filtered = MOCK_MEDICATIONS.filter((m) =>
    m.nom.toLowerCase().includes(search.toLowerCase()) ||
    m.dosage.toLowerCase().includes(search.toLowerCase())
  );

  const getStockBadge = (quantite: number, stockMin: number) => {
    if (quantite === 0) return <Badge variant="destructive">Rupture</Badge>;
    if (quantite < stockMin) return <Badge variant="secondary" className="bg-amber-500/10 text-amber-600 border-amber-500/20">Stock bas</Badge>;
    return <Badge variant="default" className="bg-emerald-500/10 text-emerald-600 border-emerald-500/20">En stock</Badge>;
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Médicaments</h1>
          <p className="text-muted-foreground">
            Gestion du stock pharmaceutique et des médicaments
          </p>
        </div>
        <Button variant="default" className="gap-2">
          <Plus className="h-4 w-4" />
          Ajouter médicament
        </Button>
      </div>

      <Card className="glass">
        <CardHeader>
          <div className="flex items-center gap-4">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
              <Input
                placeholder="Rechercher un médicament..."
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
              {filtered.map((med) => (
                <div
                  key={med.id}
                  className="flex items-center justify-between p-4 rounded-lg border border-border/50 bg-card/50 hover:bg-accent/30 transition-all hover:shadow-lg hover:shadow-primary/5"
                >
                  <div className="flex items-start gap-4">
                    <div className="p-3 rounded-lg bg-primary/10">
                      <Pill className="h-6 w-6 text-primary" />
                    </div>
                    <div className="space-y-1">
                      <div className="flex items-center gap-2">
                        <p className="font-semibold text-lg">{med.nom}</p>
                        {getStockBadge(med.quantite, med.stockMin)}
                      </div>
                      <p className="text-sm text-muted-foreground">{med.dosage} - {med.forme}</p>
                      <div className="flex items-center gap-4 text-sm text-muted-foreground">
                        <span className="flex items-center gap-1">
                          <Package className="h-3 w-3" />
                          Stock: {med.quantite} / Min: {med.stockMin}
                        </span>
                        <span>Exp: {med.dateExpiration}</span>
                        <span>Fournisseur: {med.fournisseur}</span>
                      </div>
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    <Button variant="ghost" size="sm">
                      Ajuster stock
                    </Button>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-12 text-muted-foreground">
              <Pill className="h-12 w-12 mx-auto mb-4 opacity-20" />
              <p>Aucun médicament trouvé</p>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
