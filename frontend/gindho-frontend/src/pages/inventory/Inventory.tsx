import { useState } from 'react';
import { useInventoryItems, useInventoryAlerts } from '@/hooks/use-api';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { AlertTriangle, TrendingUp, TrendingDown, AlertCircle, Package } from 'lucide-react';
import type { InventoryItem } from '@/types';

function InventoryItemCard({ item }: { item: InventoryItem }) {
  const statusConfig = {
    in_stock: { label: 'En stock', color: 'bg-green-100 text-green-800', icon: TrendingUp },
    low_stock: { label: 'Stock faible', color: 'bg-yellow-100 text-yellow-800', icon: AlertTriangle },
    out_of_stock: { label: 'Rupture', color: 'bg-red-100 text-red-800', icon: TrendingDown },
    expired: { label: 'Expiré', color: 'bg-gray-100 text-gray-800', icon: AlertCircle },
    discontinued: { label: 'Discontinué', color: 'bg-blue-100 text-blue-800', icon: Package },
  };

  const config = statusConfig[item.status as keyof typeof statusConfig];
  const StatusIcon = config.icon;
  const percentageOfMax = item.quantiteMaximale
    ? Math.round((item.quantite / item.quantiteMaximale) * 100)
    : 0;

  return (
    <Card>
      <CardHeader className="pb-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <Package className="h-5 w-5 text-muted-foreground" />
            <div>
              <CardTitle className="text-lg">{item.nom}</CardTitle>
              <CardDescription>{item.code}</CardDescription>
            </div>
          </div>
          <Badge className={config.color} variant="secondary">
            <StatusIcon className="h-3 w-3 mr-1" />
            {config.label}
          </Badge>
        </div>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="grid grid-cols-3 gap-4 text-sm">
          <div>
            <p className="text-muted-foreground">Quantité</p>
            <p className="text-2xl font-bold">{item.quantite}</p>
            <p className="text-xs text-muted-foreground">{item.unite || 'unité'}</p>
          </div>
          <div>
            <p className="text-muted-foreground">Minimum requis</p>
            <p className="font-medium">{item.quantiteMinimale || 0}</p>
          </div>
          <div>
            <p className="text-muted-foreground">Prix unitaire</p>
            <p className="font-medium">{item.prix.toFixed(2)} €</p>
          </div>
        </div>

        <div>
          <div className="flex justify-between mb-2">
            <p className="text-sm font-medium">Taux de stock</p>
            <p className="text-sm text-muted-foreground">{percentageOfMax}%</p>
          </div>
          <div className="w-full bg-gray-200 rounded-full h-2">
            <div
              className={`h-2 rounded-full transition-all ${
                percentageOfMax > 75
                  ? 'bg-green-500'
                  : percentageOfMax > 30
                    ? 'bg-yellow-500'
                    : 'bg-red-500'
              }`}
              style={{ width: `${percentageOfMax}%` }}
            />
          </div>
        </div>

        {item.dateExpiration && (
          <div className="text-xs text-muted-foreground">
            Expiration: {new Date(item.dateExpiration).toLocaleDateString('fr-FR')}
          </div>
        )}

        <div className="flex gap-2 pt-2 border-t">
          <Button size="sm" variant="outline" className="flex-1">
            Détails
          </Button>
          <Button size="sm" variant="outline" className="flex-1">
            Commander
          </Button>
        </div>
      </CardContent>
    </Card>
  );
}

export function InventoryPage() {
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('');
  const { data: items, isLoading } = useInventoryItems();
  const { data: alerts } = useInventoryAlerts();

  const filteredItems = items?.filter((item) => {
    const matchesSearch =
      item.nom.toLowerCase().includes(search.toLowerCase()) ||
      item.code?.toLowerCase().includes(search.toLowerCase());
    const matchesStatus = !statusFilter || item.status === statusFilter;
    return matchesSearch && matchesStatus;
  }) || [];

  const stats = {
    totalItems: items?.length || 0,
    lowStock: items?.filter((i) => i.status === 'low_stock').length || 0,
    outOfStock: items?.filter((i) => i.status === 'out_of_stock').length || 0,
    expired: items?.filter((i) => i.status === 'expired').length || 0,
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Gestion de l&apos;Inventaire</h1>
          <p className="text-muted-foreground">Suivi des stocks et des équipements</p>
        </div>
        <Button>+ Ajouter un article</Button>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-4 gap-4">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Total articles</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold">{stats.totalItems}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Stock faible</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-yellow-600">{stats.lowStock}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Ruptures</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-red-600">{stats.outOfStock}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Expirés</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-gray-600">{stats.expired}</div>
          </CardContent>
        </Card>
      </div>

      {/* Alerts */}
      {alerts && (alerts.rupture?.length || 0 > 0 || (alerts.peremption?.length || 0 > 0)) && (
        <Card className="border-yellow-200 bg-yellow-50">
          <CardHeader>
            <CardTitle className="text-yellow-800 flex items-center gap-2">
              <AlertTriangle className="h-5 w-5" />
              Alertes d&apos;inventaire
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            {alerts.rupture && alerts.rupture.length > 0 && (
              <div>
                <h4 className="font-medium text-yellow-900 mb-2">Ruptures de stock</h4>
                <ul className="space-y-1 text-sm text-yellow-800">
                  {alerts.rupture.map((item: any) => (
                    <li key={item.id}>• {item.nom}</li>
                  ))}
                </ul>
              </div>
            )}

            {alerts.peremption && alerts.peremption.length > 0 && (
              <div>
                <h4 className="font-medium text-yellow-900 mb-2">Articles périssables</h4>
                <ul className="space-y-1 text-sm text-yellow-800">
                  {alerts.peremption.map((item: any) => (
                    <li key={item.id}>• {item.nom} - Expire: {item.dateExpiration}</li>
                  ))}
                </ul>
              </div>
            )}
          </CardContent>
        </Card>
      )}

      {/* Search and Filter */}
      <Card>
        <CardHeader>
          <div className="flex gap-4">
            <div className="flex-1 relative">
              <Input
                placeholder="Rechercher un article..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
              />
            </div>
            <div className="flex gap-2">
              <Button
                variant={statusFilter === '' ? 'default' : 'outline'}
                onClick={() => setStatusFilter('')}
                size="sm"
              >
                Tous
              </Button>
              {['in_stock', 'low_stock', 'out_of_stock'].map((status) => (
                <Button
                  key={status}
                  variant={statusFilter === status ? 'default' : 'outline'}
                  onClick={() => setStatusFilter(status)}
                  size="sm"
                >
                  {status === 'in_stock' ? 'En stock' : status === 'low_stock' ? 'Stock faible' : 'Ruptures'}
                </Button>
              ))}
            </div>
          </div>
        </CardHeader>
      </Card>

      {/* Items Grid */}
      {isLoading ? (
        <Card>
          <CardContent className="flex items-center justify-center h-64">
            <div className="animate-spin w-8 h-8 border-4 border-primary border-t-transparent rounded-full" />
          </CardContent>
        </Card>
      ) : filteredItems.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {filteredItems.map((item) => (
            <InventoryItemCard key={item.id} item={item} />
          ))}
        </div>
      ) : (
        <Card>
          <CardContent className="flex flex-col items-center justify-center h-64 gap-4">
            <AlertCircle className="h-12 w-12 text-muted-foreground opacity-50" />
            <p className="text-muted-foreground">Aucun article trouvé</p>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
