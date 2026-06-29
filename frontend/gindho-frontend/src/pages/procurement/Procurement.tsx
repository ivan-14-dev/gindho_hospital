import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { Plus, ShoppingCart, Truck, CheckCircle, Clock, AlertCircle, DollarSign } from 'lucide-react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
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
import type { ProcurementOrder } from '@/types';

const poSchema = z.object({
  fournisseurId: z.string().min(1, 'Fournisseur requis'),
  dateLivraisonPrevue: z.string().min(1, 'Date prévue requise'),
});

type POFormData = z.infer<typeof poSchema>;

const statusConfig = {
  draft: { label: 'Brouillon', color: 'bg-gray-100 text-gray-800' },
  submitted: { label: 'Soumise', color: 'bg-blue-100 text-blue-800' },
  confirmed: { label: 'Confirmée', color: 'bg-green-100 text-green-800' },
  partial_delivery: { label: 'Livraison partielle', color: 'bg-yellow-100 text-yellow-800' },
  completed: { label: 'Complétée', color: 'bg-green-100 text-green-800' },
  cancelled: { label: 'Annulée', color: 'bg-red-100 text-red-800' },
};

function CreatePODialog() {
  const [open, setOpen] = useState(false);

  const form = useForm<POFormData>({
    resolver: zodResolver(poSchema),
  });

  async function onSubmit(data: POFormData) {
    try {
      await apiClient.post('/procurement/orders', {
        ...data,
        status: 'draft',
        items: [],
        montantTotal: 0,
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
          Nouvelle commande
        </Button>
      </DialogTrigger>
      <DialogContent className="max-w-md">
        <DialogHeader>
          <DialogTitle>Créer commande d'achat</DialogTitle>
          <DialogDescription>Nouvelle PO</DialogDescription>
        </DialogHeader>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="fournisseurId"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Fournisseur</FormLabel>
                  <FormControl>
                    <Input placeholder="ID fournisseur" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="dateLivraisonPrevue"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Date de livraison prévue</FormLabel>
                  <FormControl>
                    <Input type="date" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <Button type="submit" className="w-full">
              Créer
            </Button>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}

function POCard({ order }: { order: ProcurementOrder }) {
  const config = statusConfig[order.status as keyof typeof statusConfig] || statusConfig.draft;

  return (
    <Card>
      <CardHeader>
        <div className="flex justify-between items-start">
          <div>
            <CardTitle className="flex items-center gap-2">
              <ShoppingCart className="h-5 w-5" />
              PO #{order.numero || order.id.substring(0, 8)}
            </CardTitle>
            <CardDescription>
              Fournisseur: {order.fournisseurId}
            </CardDescription>
          </div>
          <Badge className={config.color}>{config.label}</Badge>
        </div>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <div>
            <p className="text-sm text-gray-600">Date commande</p>
            <p className="font-medium">
              {new Date(order.dateCommande).toLocaleDateString('fr-FR')}
            </p>
          </div>
          {order.dateLivraisonPrevue && (
            <div>
              <p className="text-sm text-gray-600">Livraison prévue</p>
              <p className="font-medium">
                {new Date(order.dateLivraisonPrevue).toLocaleDateString('fr-FR')}
              </p>
            </div>
          )}
        </div>

        <div className="border-t pt-3">
          <div className="flex justify-between items-center">
            <span className="text-sm font-medium">Montant total:</span>
            <span className="text-lg font-bold text-blue-600">
              {order.montantTotal.toLocaleString('fr-FR', { style: 'currency', currency: 'EUR' })}
            </span>
          </div>
        </div>

        {order.items && order.items.length > 0 && (
          <div className="bg-gray-50 p-3 rounded text-sm">
            <p className="font-medium mb-2">{order.items.length} article(s)</p>
            {order.items.slice(0, 3).map((item, idx) => (
              <p key={idx} className="text-gray-600 text-xs">
                • Qté: {item.quantite}
              </p>
            ))}
          </div>
        )}

        <div className="flex gap-2">
          <Button size="sm" variant="outline" className="flex-1">
            Détails
          </Button>
          {order.status === 'draft' && (
            <Button size="sm" variant="default" className="flex-1">
              Soumettre
            </Button>
          )}
        </div>
      </CardContent>
    </Card>
  );
}

export default function Procurement() {
  const { data: orders } = useQuery({
    queryKey: ['procurement-orders'],
    queryFn: async () => {
      const res = await apiClient.get('/procurement/orders');
      return res.data as ProcurementOrder[];
    },
  });

  const draft = orders?.filter(o => o.status === 'draft') || [];
  const active = orders?.filter(o => ['submitted', 'confirmed', 'partial_delivery'].includes(o.status)) || [];
  const completed = orders?.filter(o => ['completed', 'cancelled'].includes(o.status)) || [];
  const totalSpend = orders?.reduce((sum, o) => sum + (o.montantTotal || 0), 0) || 0;

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold tracking-tight">Approvisionnement</h1>
        <p className="text-gray-600 mt-2">Gestion des commandes d'achat et des fournisseurs</p>
      </div>

      <div className="grid grid-cols-4 gap-4">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-600">Brouillons</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold">{draft.length}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-600">Commandes actives</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-orange-600">{active.length}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-600">Complétées</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-green-600">{completed.length}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-600">Dépenses</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-blue-600">
              {(totalSpend / 1000).toFixed(0)}K€
            </div>
          </CardContent>
        </Card>
      </div>

      <div className="grid grid-cols-3 gap-6">
        <div className="col-span-2 space-y-6">
          <div>
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-2xl font-bold">Commandes actives</h2>
              <CreatePODialog />
            </div>

            {active.length === 0 ? (
              <Card>
                <CardContent className="py-8 text-center text-gray-600">
                  <AlertCircle className="h-12 w-12 mx-auto mb-4 opacity-50" />
                  <p>Aucune commande active</p>
                </CardContent>
              </Card>
            ) : (
              <div className="grid gap-4">
                {active.map(order => (
                  <POCard key={order.id} order={order} />
                ))}
              </div>
            )}
          </div>

          <div>
            <h2 className="text-2xl font-bold mb-4">Historique</h2>
            <div className="grid gap-4">
              {completed.slice(0, 5).map(order => (
                <POCard key={order.id} order={order} />
              ))}
            </div>
          </div>
        </div>

        <div>
          <h2 className="text-2xl font-bold mb-4">Brouillons</h2>
          <div className="grid gap-4">
            {draft.map(order => (
              <POCard key={order.id} order={order} />
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
