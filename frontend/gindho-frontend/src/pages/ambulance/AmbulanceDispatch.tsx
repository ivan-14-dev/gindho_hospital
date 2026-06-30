import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { Plus, MapPin, Clock, AlertCircle } from 'lucide-react';
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
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Textarea } from '@/components/ui/textarea';
import { apiClient } from '@/lib/api-client';
import type { AmbulanceRequest, Ambulance } from '@/types';

const ambulanceRequestSchema = z.object({
  patientId: z.string().min(1, 'Patient requis'),
  pointDepart: z.object({
    adresse: z.string().min(5, 'Adresse requise'),
  }),
  pointDestination: z.object({
    adresse: z.string().min(5, 'Destination requise'),
  }),
  typeTransport: z.enum(['EMERGENCY', 'ROUTINE', 'INTER_HOSPITAL']),
  priorite: z.enum(['urgent', 'non-urgent']),
  notes: z.string().optional(),
});

type AmbulanceFormData = z.infer<typeof ambulanceRequestSchema>;

const statusConfig = {
  requested: { label: 'Demandée', color: 'bg-yellow-100 text-yellow-800' },
  assigned: { label: 'Assignée', color: 'bg-blue-100 text-blue-800' },
  in_transit: { label: 'En transit', color: 'bg-purple-100 text-purple-800' },
  arrived: { label: 'Arrivée', color: 'bg-green-100 text-green-800' },
  completed: { label: 'Terminée', color: 'bg-gray-100 text-gray-800' },
  cancelled: { label: 'Annulée', color: 'bg-red-100 text-red-800' },
};

function RequestDialog() {
  const [open, setOpen] = useState(false);

  const form = useForm<AmbulanceFormData>({
    resolver: zodResolver(ambulanceRequestSchema),
    defaultValues: {
      typeTransport: 'ROUTINE',
      priorite: 'non-urgent',
    },
  });

  async function onSubmit(data: AmbulanceFormData) {
    try {
      await apiClient.post('/ambulance/requests', {
        ...data,
        dateHeureCommande: new Date().toISOString(),
      });
      form.reset();
      setOpen(false);
    } catch (error) {
      console.error('Erreur lors de la demande ambulance:', error);
    }
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button>
          <Plus className="h-4 w-4 mr-2" />
          Nouvelle demande
        </Button>
      </DialogTrigger>
      <DialogContent className="max-w-2xl">
        <DialogHeader>
          <DialogTitle>Nouvelle demande ambulance</DialogTitle>
          <DialogDescription>Créer une demande de transport</DialogDescription>
        </DialogHeader>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
            <FormField
              control={form.control}
              name="patientId"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Patient</FormLabel>
                  <FormControl>
                    <Input placeholder="ID patient" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <div className="grid grid-cols-2 gap-4">
              <FormField
                control={form.control}
                name="typeTransport"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Type de transport</FormLabel>
                    <Select onValueChange={field.onChange} defaultValue={field.value}>
                      <FormControl>
                        <SelectTrigger>
                          <SelectValue />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        <SelectItem value="EMERGENCY">Urgence</SelectItem>
                        <SelectItem value="ROUTINE">Routine</SelectItem>
                        <SelectItem value="INTER_HOSPITAL">Inter-hôpital</SelectItem>
                      </SelectContent>
                    </Select>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="priorite"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Priorité</FormLabel>
                    <Select onValueChange={field.onChange} defaultValue={field.value}>
                      <FormControl>
                        <SelectTrigger>
                          <SelectValue />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        <SelectItem value="urgent">Urgent</SelectItem>
                        <SelectItem value="non-urgent">Non-urgent</SelectItem>
                      </SelectContent>
                    </Select>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>

            <FormField
              control={form.control}
              name="pointDepart.adresse"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Point de départ</FormLabel>
                  <FormControl>
                    <Input placeholder="Adresse de départ" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="pointDestination.adresse"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Point destination</FormLabel>
                  <FormControl>
                    <Input placeholder="Adresse destination" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="notes"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Notes supplémentaires</FormLabel>
                  <FormControl>
                    <Textarea placeholder="Observations..." {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <Button type="submit" className="w-full">
              Créer demande
            </Button>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}

function AmbulanceRequestCard({ request }: { request: AmbulanceRequest }) {
  const config = statusConfig[request.status as keyof typeof statusConfig] || { label: request.status, color: 'bg-gray-100' };

  return (
    <Card>
      <CardHeader className="pb-3">
        <div className="flex justify-between items-start">
          <div>
            <CardTitle className="text-lg">Patient: {request.patientId}</CardTitle>
            <CardDescription>
              {new Date(request.dateHeureCommande).toLocaleString('fr-FR')}
            </CardDescription>
          </div>
          <Badge className={config.color}>{config.label}</Badge>
        </div>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <div>
            <p className="text-sm text-gray-600">Départ</p>
            <p className="flex items-center gap-2 font-medium">
              <MapPin className="h-4 w-4" />
              {request.pointDepart?.adresse}
            </p>
          </div>
          <div>
            <p className="text-sm text-gray-600">Destination</p>
            <p className="flex items-center gap-2 font-medium">
              <MapPin className="h-4 w-4" />
              {request.pointDestination?.adresse}
            </p>
          </div>
        </div>

        <div className="grid grid-cols-3 gap-4 py-3 border-y">
          <div>
            <p className="text-sm text-gray-600">Type</p>
            <p className="font-medium">{request.typeTransport}</p>
          </div>
          <div>
            <p className="text-sm text-gray-600">Priorité</p>
            <Badge variant={request.priorite === 'urgent' ? 'destructive' : 'default'}>
              {request.priorite}
            </Badge>
          </div>
          {request.dateHeureArriveeEstimee && (
            <div>
              <p className="text-sm text-gray-600">ETA</p>
              <p className="flex items-center gap-1 font-medium">
                <Clock className="h-4 w-4" />
                {new Date(request.dateHeureArriveeEstimee).toLocaleTimeString('fr-FR', {
                  hour: '2-digit',
                  minute: '2-digit',
                })}
              </p>
            </div>
          )}
        </div>

        {request.ambulanceId && (
          <div className="bg-blue-50 p-3 rounded-lg">
            <p className="text-sm text-gray-600">Ambulance assignée</p>
            <p className="font-medium">{request.ambulanceId}</p>
          </div>
        )}

        {request.notes && (
          <div className="text-sm text-gray-600">
            <p className="font-medium mb-1">Notes:</p>
            <p>{request.notes}</p>
          </div>
        )}
      </CardContent>
    </Card>
  );
}

export default function AmbulanceDispatch() {
  const { data: requests, isLoading } = useQuery({
    queryKey: ['ambulance-requests'],
    queryFn: async () => {
      const res = await apiClient.get('/ambulance/requests');
      return res.data as AmbulanceRequest[];
    },
  });

  const { data: ambulances } = useQuery({
    queryKey: ['ambulances'],
    queryFn: async () => {
      const res = await apiClient.get('/ambulance');
      return res.data as Ambulance[];
    },
  });

  const activeRequests = requests?.filter(r => ['requested', 'assigned', 'in_transit'].includes(r.status)) || [];
  const completedRequests = requests?.filter(r => ['completed', 'cancelled'].includes(r.status)) || [];
  const availableAmbulances = ambulances?.filter(a => a.status === 'available') || [];

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold tracking-tight">Dispatch Ambulances</h1>
        <p className="text-gray-600 mt-2">Gestion des demandes et dispatch des ambulances</p>
      </div>

      <div className="grid grid-cols-3 gap-4">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-600">Demandes actives</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold">{activeRequests.length}</div>
            <p className="text-xs text-gray-500 mt-2">En cours de traitement</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-600">Ambulances disponibles</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold">{availableAmbulances.length}</div>
            <p className="text-xs text-gray-500 mt-2">Prêtes à être assignées</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-600">Complétées aujourd'hui</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold">
              {completedRequests.filter(r => {
                const today = new Date();
                const reqDate = new Date(r.dateHeureCommande);
                return reqDate.toDateString() === today.toDateString();
              }).length}
            </div>
            <p className="text-xs text-gray-500 mt-2">Trajets terminés</p>
          </CardContent>
        </Card>
      </div>

      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold">Demandes actives</h2>
        <RequestDialog />
      </div>

      {isLoading ? (
        <div className="text-center py-8">Chargement...</div>
      ) : activeRequests.length === 0 ? (
        <Card>
          <CardContent className="py-8 text-center text-gray-600">
            <AlertCircle className="h-12 w-12 mx-auto mb-4 opacity-50" />
            <p>Aucune demande active</p>
          </CardContent>
        </Card>
      ) : (
        <div className="grid gap-4">
          {activeRequests.map(request => (
            <AmbulanceRequestCard key={request.id} request={request} />
          ))}
        </div>
      )}

      <div>
        <h2 className="text-2xl font-bold mb-4">Ambulances disponibles</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {ambulances?.map(ambulance => (
            <Card key={ambulance.id}>
              <CardHeader>
                <CardTitle>{ambulance.numeroImmatriculation}</CardTitle>
                <CardDescription>{ambulance.marque} {ambulance.modele}</CardDescription>
              </CardHeader>
              <CardContent className="space-y-3">
                <div className="flex justify-between items-center">
                  <span className="text-sm text-gray-600">Statut:</span>
                  <Badge variant={ambulance.status === 'available' ? 'default' : 'secondary'}>
                    {ambulance.status}
                  </Badge>
                </div>
                <div className="text-sm">
                  <p className="text-gray-600">Capacité: {ambulance.capacite} patients</p>
                  <p className="text-gray-600">Équipements: {ambulance.equipements?.join(', ')}</p>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      </div>

      <div>
        <h2 className="text-2xl font-bold mb-4">Historique</h2>
        <div className="grid gap-4">
          {completedRequests.slice(0, 5).map(request => (
            <AmbulanceRequestCard key={request.id} request={request} />
          ))}
        </div>
      </div>
    </div>
  );
}
