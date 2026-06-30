import { useState } from 'react';
import { useWards, useBedsByWard } from '@/hooks/use-api';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { AlertCircle, Users, Wind, Zap, Bed } from 'lucide-react';
import type { Ward, Bed as BedType } from '@/types';

function BedStatusIcon({ status }: { status: string }) {
  switch (status) {
    case 'occupied':
      return <Users className="h-4 w-4 text-red-500" />;
    case 'available':
      return <Bed className="h-4 w-4 text-green-500" />;
    case 'cleaning':
      return <Wind className="h-4 w-4 text-blue-500" />;
    case 'maintenance':
      return <Zap className="h-4 w-4 text-yellow-500" />;
    default:
      return <AlertCircle className="h-4 w-4 text-gray-500" />;
  }
}

function BedCard({ bed }: { bed: BedType }) {
  const statusConfig = {
    available: { label: 'Disponible', color: 'bg-green-100 text-green-800' },
    occupied: { label: 'Occupé', color: 'bg-red-100 text-red-800' },
    cleaning: { label: 'Nettoyage', color: 'bg-blue-100 text-blue-800' },
    maintenance: { label: 'Maintenance', color: 'bg-yellow-100 text-yellow-800' },
    reserved: { label: 'Réservé', color: 'bg-purple-100 text-purple-800' },
    out_of_service: { label: 'Hors service', color: 'bg-gray-100 text-gray-800' },
  };

  const config = statusConfig[bed.status as keyof typeof statusConfig] || statusConfig.available;

  return (
    <Card className="p-4">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-3">
          <BedStatusIcon status={bed.status} />
          <div>
            <p className="font-medium">Lit {bed.numero}</p>
            {bed.patientActuelId && (
              <p className="text-sm text-muted-foreground">Patient: {bed.patientActuelId}</p>
            )}
          </div>
        </div>
        <Badge className={config.color}>{config.label}</Badge>
      </div>
      {bed.notes && (
        <p className="text-xs text-muted-foreground mt-2">Remarques: {bed.notes}</p>
      )}
    </Card>
  );
}

function WardPanel({ ward }: { ward: Ward }) {
  const [selectedWard, setSelectedWard] = useState<string | null>(null);
  const { data: beds, isLoading } = useBedsByWard(selectedWard);

  const handleSelectWard = (wardId: string) => {
    setSelectedWard(selectedWard === wardId ? null : wardId);
  };

  const totalBeds = ward.nombreLits || 0;
  const occupiedBeds = beds?.filter((b) => b.status === 'occupied').length || 0;
  const occupancyRate = totalBeds > 0 ? Math.round((occupiedBeds / totalBeds) * 100) : 0;

  return (
    <div className="space-y-4">
      <Card
        className="cursor-pointer hover:bg-accent/50 transition-colors"
        onClick={() => handleSelectWard(ward.id)}
      >
        <CardHeader>
          <div className="flex items-center justify-between">
            <div>
              <CardTitle>{ward.nom}</CardTitle>
              <CardDescription>{ward.description}</CardDescription>
            </div>
            <div className="text-right">
              <Badge variant={occupancyRate > 80 ? 'destructive' : 'secondary'}>
                {occupancyRate}% d&apos;occupancy
              </Badge>
            </div>
          </div>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-3 gap-4 text-sm">
            <div>
              <p className="text-muted-foreground">Nombre de lits</p>
              <p className="text-2xl font-bold">{totalBeds}</p>
            </div>
            <div>
              <p className="text-muted-foreground">Lits occupés</p>
              <p className="text-2xl font-bold text-red-600">{occupiedBeds}</p>
            </div>
            <div>
              <p className="text-muted-foreground">Lits disponibles</p>
              <p className="text-2xl font-bold text-green-600">{totalBeds - occupiedBeds}</p>
            </div>
          </div>
        </CardContent>
      </Card>

      {selectedWard === ward.id && (
        <div className="pl-4 border-l-2 border-primary space-y-3">
          <h4 className="font-semibold">État des lits</h4>
          {isLoading ? (
            <div className="flex items-center justify-center h-32">
              <div className="animate-spin w-6 h-6 border-3 border-primary border-t-transparent rounded-full" />
            </div>
          ) : beds && beds.length > 0 ? (
            <div className="grid grid-cols-2 gap-3">
              {beds.map((bed) => (
                <BedCard key={bed.id} bed={bed} />
              ))}
            </div>
          ) : (
            <p className="text-muted-foreground text-sm">Aucun lit trouvé</p>
          )}
        </div>
      )}
    </div>
  );
}

export function WardsPage() {
  const { data: wards, isLoading } = useWards();

  const activeWards = wards?.filter((w) => w.status === 'active') || [];
  const totalBeds = activeWards.reduce((sum, w) => sum + (w.nombreLits || 0), 0);
  const totalOccupied = activeWards.reduce((sum, w) => {
    const occupied = w.nombreLits ? Math.round((w.capaciteActuelle || 0) / w.nombreLits) : 0;
    return sum + occupied;
  }, 0);

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold">Gestion des Services</h1>
        <p className="text-muted-foreground">Suivi des lits et des occupancies par service</p>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-4 gap-4">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Total des lits</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold">{totalBeds}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Lits occupés</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-red-600">{totalOccupied}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Lits disponibles</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-green-600">{totalBeds - totalOccupied}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Taux d&apos;occupancy</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold">
              {totalBeds > 0 ? Math.round((totalOccupied / totalBeds) * 100) : 0}%
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Services List */}
      {isLoading ? (
        <Card>
          <CardContent className="flex items-center justify-center h-64">
            <div className="animate-spin w-8 h-8 border-4 border-primary border-t-transparent rounded-full" />
          </CardContent>
        </Card>
      ) : activeWards.length > 0 ? (
        <div className="space-y-4">
          {activeWards.map((ward) => (
            <WardPanel key={ward.id} ward={ward} />
          ))}
        </div>
      ) : (
        <Card>
          <CardContent className="flex flex-col items-center justify-center h-64 gap-4">
            <AlertCircle className="h-12 w-12 text-muted-foreground opacity-50" />
            <p className="text-muted-foreground">Aucun service trouvé</p>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
