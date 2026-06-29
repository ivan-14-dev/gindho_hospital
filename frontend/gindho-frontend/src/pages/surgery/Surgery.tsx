import { useState } from 'react';
import { useScheduleSurgery, useUpdateSurgeryStatus } from '@/hooks/use-api';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { AlertCircle, Plus, Calendar, Clock, User, Scalpel } from 'lucide-react';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
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
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import type { SurgerySchedule } from '@/types';

const surgerySchema = z.object({
  patientId: z.string().min(1, 'Patient requis'),
  chirurgienId: z.string().min(1, 'Chirurgien requis'),
  typeIntervention: z.string().min(3, 'Type d\'intervention requis'),
  datePrevu: z.string().min(1, 'Date requise'),
  heureDebut: z.string().min(1, 'Heure requise'),
  description: z.string().optional(),
  typeAnesthesie: z.string().optional(),
});

type SurgeryFormData = z.infer<typeof surgerySchema>;

function SurgeryDialog() {
  const [open, setOpen] = useState(false);
  const scheduleSurgery = useScheduleSurgery();

  const form = useForm<SurgeryFormData>({
    resolver: zodResolver(surgerySchema),
  });

  async function onSubmit(data: SurgeryFormData) {
    try {
      await scheduleSurgery.mutateAsync({
        ...data,
        priorite: 'routine',
        status: 'scheduled',
      });
      form.reset();
      setOpen(false);
    } catch (error) {
      console.error('Erreur lors de la programmation:', error);
    }
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button>
          <Plus className="h-4 w-4 mr-2" />
          Programmer une intervention
        </Button>
      </DialogTrigger>
      <DialogContent className="max-w-2xl">
        <DialogHeader>
          <DialogTitle>Programmer une intervention chirurgicale</DialogTitle>
          <DialogDescription>Enregistrer une nouvelle intervention</DialogDescription>
        </DialogHeader>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
            <div className="grid grid-cols-2 gap-4">
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

              <FormField
                control={form.control}
                name="chirurgienId"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Chirurgien</FormLabel>
                    <FormControl>
                      <Input placeholder="ID chirurgien" {...field} />
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
                name="heureDebut"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Heure de début</FormLabel>
                    <FormControl>
                      <Input type="time" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="typeIntervention"
                render={({ field }) => (
                  <FormItem className="col-span-2">
                    <FormLabel>Type d&apos;intervention</FormLabel>
                    <FormControl>
                      <Input placeholder="Ex: Appendicectomie" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="typeAnesthesie"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Type d&apos;anesthésie</FormLabel>
                    <FormControl>
                      <Input placeholder="Ex: Générale" {...field} />
                    </FormControl>
                  </FormItem>
                )}
              />
            </div>

            <FormField
              control={form.control}
              name="description"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Description</FormLabel>
                  <FormControl>
                    <Textarea placeholder="Détails de l'intervention..." {...field} />
                  </FormControl>
                </FormItem>
              )}
            />

            <div className="flex gap-2 justify-end">
              <Button type="button" variant="outline" onClick={() => setOpen(false)}>
                Annuler
              </Button>
              <Button type="submit" disabled={scheduleSurgery.isPending}>
                {scheduleSurgery.isPending ? 'Programmation...' : 'Programmer'}
              </Button>
            </div>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}

function SurgeryCard({ surgery }: { surgery: SurgerySchedule }) {
  const [updatingStatus, setUpdatingStatus] = useState<string | null>(null);
  const updateStatus = useUpdateSurgeryStatus();

  const statusConfig = {
    scheduled: { label: 'Programmée', color: 'bg-blue-100 text-blue-800', icon: Calendar },
    confirmed: { label: 'Confirmée', color: 'bg-green-100 text-green-800', icon: Calendar },
    in_progress: { label: 'En cours', color: 'bg-yellow-100 text-yellow-800', icon: Clock },
    completed: { label: 'Complétée', color: 'bg-green-100 text-green-800', icon: Calendar },
    cancelled: { label: 'Annulée', color: 'bg-red-100 text-red-800', icon: AlertCircle },
  };

  const config = statusConfig[surgery.status as keyof typeof statusConfig];

  const handleUpdateStatus = async (newStatus: string) => {
    try {
      await updateStatus.mutateAsync({ id: surgery.id, statut: newStatus });
      setUpdatingStatus(null);
    } catch (error) {
      console.error('Erreur lors de la mise à jour:', error);
    }
  };

  return (
    <Card>
      <CardHeader className="pb-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <Scalpel className="h-5 w-5 text-muted-foreground" />
            <div>
              <CardTitle>{surgery.typeIntervention}</CardTitle>
              <CardDescription>Patient: {surgery.patientId}</CardDescription>
            </div>
          </div>
          <Badge className={config.color}>{config.label}</Badge>
        </div>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="grid grid-cols-2 gap-4 text-sm">
          <div>
            <p className="text-muted-foreground">Date prévue</p>
            <p className="font-medium">{new Date(surgery.datePrevu).toLocaleDateString('fr-FR')}</p>
          </div>
          <div>
            <p className="text-muted-foreground">Heure</p>
            <p className="font-medium">{surgery.heureDebut}</p>
          </div>
          <div>
            <p className="text-muted-foreground">Chirurgien</p>
            <p className="font-medium">{surgery.chirurgienId}</p>
          </div>
          <div>
            <p className="text-muted-foreground">Anesthésie</p>
            <p className="font-medium">{surgery.typeAnesthesie || 'N/A'}</p>
          </div>
        </div>

        {surgery.description && (
          <div>
            <p className="text-sm font-medium mb-2">Description</p>
            <p className="text-sm text-muted-foreground">{surgery.description}</p>
          </div>
        )}

        <div className="pt-2 border-t">
          <select
            value={surgery.status}
            onChange={(e) => handleUpdateStatus(e.target.value)}
            disabled={updateStatus.isPending}
            className="w-full px-3 py-2 border rounded-md text-sm"
          >
            <option value="scheduled">Programmée</option>
            <option value="confirmed">Confirmée</option>
            <option value="in_progress">En cours</option>
            <option value="completed">Complétée</option>
            <option value="cancelled">Annulée</option>
          </select>
        </div>
      </CardContent>
    </Card>
  );
}

export function SurgeryPage() {
  // Dummy data for demo - would come from useScheduledSurgeries hook in real app
  const surgeries: SurgerySchedule[] = [];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Bloc Opératoire</h1>
          <p className="text-muted-foreground">Gestion des interventions chirurgicales</p>
        </div>
        <SurgeryDialog />
      </div>

      <Tabs defaultValue="upcoming" className="w-full">
        <TabsList className="grid w-full grid-cols-3">
          <TabsTrigger value="upcoming">À venir</TabsTrigger>
          <TabsTrigger value="inprogress">En cours</TabsTrigger>
          <TabsTrigger value="completed">Complétées</TabsTrigger>
        </TabsList>

        <TabsContent value="upcoming" className="space-y-4">
          {surgeries.filter((s) => s.status === 'scheduled' || s.status === 'confirmed').length > 0 ? (
            <div className="grid gap-4">
              {surgeries
                .filter((s) => s.status === 'scheduled' || s.status === 'confirmed')
                .map((s) => (
                  <SurgeryCard key={s.id} surgery={s} />
                ))}
            </div>
          ) : (
            <Card>
              <CardContent className="flex flex-col items-center justify-center h-64 gap-4">
                <AlertCircle className="h-12 w-12 text-muted-foreground opacity-50" />
                <p className="text-muted-foreground">Aucune intervention programmée</p>
              </CardContent>
            </Card>
          )}
        </TabsContent>

        <TabsContent value="inprogress" className="space-y-4">
          {surgeries.filter((s) => s.status === 'in_progress').length > 0 ? (
            <div className="grid gap-4">
              {surgeries
                .filter((s) => s.status === 'in_progress')
                .map((s) => (
                  <SurgeryCard key={s.id} surgery={s} />
                ))}
            </div>
          ) : (
            <Card>
              <CardContent className="flex flex-col items-center justify-center h-64 gap-4">
                <AlertCircle className="h-12 w-12 text-muted-foreground opacity-50" />
                <p className="text-muted-foreground">Aucune intervention en cours</p>
              </CardContent>
            </Card>
          )}
        </TabsContent>

        <TabsContent value="completed" className="space-y-4">
          {surgeries.filter((s) => s.status === 'completed').length > 0 ? (
            <div className="grid gap-4">
              {surgeries
                .filter((s) => s.status === 'completed')
                .map((s) => (
                  <SurgeryCard key={s.id} surgery={s} />
                ))}
            </div>
          ) : (
            <Card>
              <CardContent className="flex flex-col items-center justify-center h-64 gap-4">
                <AlertCircle className="h-12 w-12 text-muted-foreground opacity-50" />
                <p className="text-muted-foreground">Aucune intervention complétée</p>
              </CardContent>
            </Card>
          )}
        </TabsContent>
      </Tabs>
    </div>
  );
}
