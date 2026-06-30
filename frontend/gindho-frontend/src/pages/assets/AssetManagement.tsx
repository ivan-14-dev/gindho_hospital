import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { Plus, Package, Wrench, AlertTriangle, Calendar } from 'lucide-react';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { createFormResolver } from '@/lib/validations';
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import { Textarea } from '@/components/ui/textarea';
import { apiClient } from '@/lib/api-client';

const assetSchema = z.object({
  nom: z.string().min(3, 'Nom requis'),
  categorie: z.string().min(1, 'Catégorie requise'),
  numeroSerie: z.string().min(1, 'Numéro de série requis'),
  prix: z.coerce.number().min(0),
  dateAcquisition: z.string().min(1, 'Date requise'),
});

type AssetFormData = z.infer<typeof assetSchema>;

const maintenanceSchema = z.object({
  assetId: z.string(),
  type: z.enum(['preventive', 'corrective', 'inspection']),
  datePrevu: z.string().min(1, 'Date requise'),
  description: z.string().min(5, 'Description requise'),
});

type MaintenanceFormData = z.infer<typeof maintenanceSchema>;

interface Asset {
  id: string;
  numeroSerie: string;
  nom: string;
  categorie: string;
  prix: number;
  dateAcquisition: string;
  derniereRevision?: string;
  prochainRevision?: string;
  status: 'active' | 'maintenance' | 'retired' | 'disposed';
  departement?: string;
  depreciation?: number;
}

interface MaintenanceLog {
  id: string;
  assetId: string;
  type: 'preventive' | 'corrective' | 'inspection';
  dateExecution: string;
  description: string;
  status: 'scheduled' | 'in_progress' | 'completed';
  technician?: string;
}

interface AssetsResponse {
  data: Asset[];
}

interface MaintenanceResponse {
  data: MaintenanceLog[];
}

function AddAssetDialog() {
  const [open, setOpen] = useState(false);

  const form = useForm<AssetFormData>({
    resolver: createFormResolver(assetSchema),
  });

  async function onSubmit(data: AssetFormData) {
    try {
      await apiClient.post('/assets', {
        ...data,
        status: 'active',
      });
      form.reset();
      setOpen(false);
    } catch (error) {
      console.error('Erreur:', error);
    }
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button>
          <Plus className="h-4 w-4 mr-2" />
          Nouvel équipement
        </Button>
      </DialogTrigger>
      <DialogContent className="max-w-md">
        <DialogHeader>
          <DialogTitle>Ajouter un équipement</DialogTitle>
          <DialogDescription>Enregistrer un nouveau bien</DialogDescription>
        </DialogHeader>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="nom"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Nom</FormLabel>
                  <FormControl>
                    <Input placeholder="Ex: ECG Monitor" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="categorie"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Catégorie</FormLabel>
                  <FormControl>
                    <select {...field} className="w-full border rounded p-2">
                      <option value="">Sélectionner...</option>
                      <option value="medical">Équipement médical</option>
                      <option value="surgical">Instruments chirurgicaux</option>
                      <option value="laboratory">Équipement de laboratoire</option>
                      <option value="administrative">Administratif</option>
                      <option value="other">Autre</option>
                    </select>
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="numeroSerie"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Numéro de série</FormLabel>
                  <FormControl>
                    <Input placeholder="Numéro" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="prix"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Prix d'acquisition (€)</FormLabel>
                  <FormControl>
                    <Input type="number" min="0" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="dateAcquisition"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Date d'acquisition</FormLabel>
                  <FormControl>
                    <Input type="date" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <Button type="submit" className="w-full">
              Enregistrer
            </Button>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}

function ScheduleMaintenanceDialog({ assetId }: { assetId: string }) {
  const [open, setOpen] = useState(false);

  const form = useForm<MaintenanceFormData>({
    resolver: createFormResolver(maintenanceSchema),
    defaultValues: { assetId },
  });

  async function onSubmit(data: MaintenanceFormData) {
    try {
      await apiClient.post(`/assets/${assetId}/maintenance`, {
        ...data,
        status: 'scheduled',
      });
      form.reset();
      setOpen(false);
    } catch (error) {
      console.error('Erreur:', error);
    }
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button size="sm" variant="outline">
          <Wrench className="h-4 w-4 mr-1" />
          Maintenance
        </Button>
      </DialogTrigger>
      <DialogContent className="max-w-md">
        <DialogHeader>
          <DialogTitle>Programmer maintenance</DialogTitle>
        </DialogHeader>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="type"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Type</FormLabel>
                  <FormControl>
                    <select {...field} className="w-full border rounded p-2">
                      <option value="preventive">Préventive</option>
                      <option value="corrective">Corrective</option>
                      <option value="inspection">Inspection</option>
                    </select>
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="datePrevu"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Date prévue</FormLabel>
                  <FormControl>
                    <Input type="date" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="description"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Description</FormLabel>
                  <FormControl>
                    <Textarea placeholder="Détails..." {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <Button type="submit" className="w-full">
              Programmer
            </Button>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}

function AssetCard({ asset }: { asset: Asset }) {
  const statusConfig: Record<string, { label: string; color: string }> = {
    active: { label: 'Actif', color: 'bg-green-100 text-green-800' },
    maintenance: { label: 'Maintenance', color: 'bg-yellow-100 text-yellow-800' },
    retired: { label: 'Retraité', color: 'bg-gray-100 text-gray-800' },
    disposed: { label: 'Vendu', color: 'bg-red-100 text-red-800' },
  };

  const config = statusConfig[asset.status];
  const depreciationRate = asset.depreciation || ((new Date().getFullYear() - new Date(asset.dateAcquisition).getFullYear()) * 10);
  const residualValue = Math.max(asset.prix * (1 - depreciationRate / 100), 0);

  return (
    <Card>
      <CardHeader>
        <div className="flex justify-between items-start">
          <div>
            <CardTitle className="flex items-center gap-2">
              <Package className="h-5 w-5" />
              {asset.nom}
            </CardTitle>
            <CardDescription>{asset.numeroSerie}</CardDescription>
          </div>
          <Badge className={config.color}>{config.label}</Badge>
        </div>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <div>
            <p className="text-sm text-gray-600">Catégorie</p>
            <p className="font-medium">{asset.categorie}</p>
          </div>
          <div>
            <p className="text-sm text-gray-600">Acquisition</p>
            <p className="font-medium">
              {new Date(asset.dateAcquisition).toLocaleDateString('fr-FR')}
            </p>
          </div>
        </div>

        <div className="border-t pt-3">
          <div className="grid grid-cols-2 gap-4 text-sm">
            <div>
              <p className="text-gray-600">Valeur initiale</p>
              <p className="font-medium">{asset.prix.toLocaleString('fr-FR', { style: 'currency', currency: 'EUR' })}</p>
            </div>
            <div>
              <p className="text-gray-600">Valeur résiduelle</p>
              <p className="font-medium">{residualValue.toLocaleString('fr-FR', { style: 'currency', currency: 'EUR' })}</p>
            </div>
          </div>
        </div>

        {asset.prochainRevision && (
          <div className="bg-yellow-50 p-3 rounded text-sm">
            <p className="text-yellow-800">
              <Calendar className="h-4 w-4 inline mr-1" />
              Révision: {new Date(asset.prochainRevision).toLocaleDateString('fr-FR')}
            </p>
          </div>
        )}

        <ScheduleMaintenanceDialog assetId={asset.id} />
      </CardContent>
    </Card>
  );
}

export default function AssetManagement() {
  const { data: assets } = useQuery({
    queryKey: ['assets'],
    queryFn: async () => {
      const res = await apiClient.get<AssetsResponse>('/assets');
      return res.data as Asset[];
    },
  });

  const { data: maintenanceLogs } = useQuery({
    queryKey: ['maintenance-logs'],
    queryFn: async () => {
      const res = await apiClient.get<MaintenanceResponse>('/assets/maintenance');
      return res.data as MaintenanceLog[];
    },
  });

  const active = assets?.filter(a => a.status === 'active') || [];
  const needsMaintenance = assets?.filter(a => a.status === 'maintenance') || [];
  const totalValue = assets?.reduce((sum, a) => sum + a.prix, 0) || 0;
  const upcomingMaintenance = maintenanceLogs?.filter(m => m.status === 'scheduled') || [];

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold tracking-tight">Gestion des équipements</h1>
        <p className="text-gray-600 mt-2">Inventaire, maintenance et valorisation des biens</p>
      </div>

      <div className="grid grid-cols-4 gap-4">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-600">Équipements actifs</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold">{active.length}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-600">Maintenance</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-yellow-600">{needsMaintenance.length}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-600">Valeur totale</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-blue-600">
              {(totalValue / 1000).toFixed(0)}K€
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-600">Révisions à venir</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-orange-600">{upcomingMaintenance.length}</div>
          </CardContent>
        </Card>
      </div>

      <div className="grid grid-cols-3 gap-6">
        <div className="col-span-2 space-y-6">
          <div>
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-2xl font-bold">Équipements actifs</h2>
              <AddAssetDialog />
            </div>

            <div className="grid gap-4">
              {active.slice(0, 5).map(asset => (
                <AssetCard key={asset.id} asset={asset} />
              ))}
            </div>
          </div>

          {needsMaintenance.length > 0 && (
            <div>
              <h2 className="text-2xl font-bold mb-4 flex items-center gap-2">
                <AlertTriangle className="h-6 w-6 text-yellow-600" />
                En maintenance ({needsMaintenance.length})
              </h2>
              <div className="grid gap-4">
                {needsMaintenance.map(asset => (
                  <AssetCard key={asset.id} asset={asset} />
                ))}
              </div>
            </div>
          )}
        </div>

        <div>
          <h2 className="text-2xl font-bold mb-4">Maintenance prévue</h2>
          <div className="grid gap-4">
            {upcomingMaintenance.map(log => (
              <Card key={log.id}>
                <CardContent className="pt-6">
                  <div className="space-y-2">
                    <Badge variant="outline">
                      {log.type === 'preventive' ? 'Préventive' : log.type === 'corrective' ? 'Corrective' : 'Inspection'}
                    </Badge>
                    <p className="text-sm font-medium">{log.description}</p>
                    <p className="text-xs text-gray-600">
                      <Calendar className="h-3 w-3 inline mr-1" />
                      {new Date(log.dateExecution).toLocaleDateString('fr-FR')}
                    </p>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}