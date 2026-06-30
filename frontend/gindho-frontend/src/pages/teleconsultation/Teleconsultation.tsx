import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { Plus, Video, Phone, Clock, Calendar, User, Share2 } from 'lucide-react';
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
import type { Appointment } from '@/types';

const teleconsultSchema = z.object({
  patientId: z.string().min(1, 'Patient requis'),
  medecinId: z.string().min(1, 'Médecin requis'),
  dateHeure: z.string().min(1, 'Date et heure requises'),
  raison: z.string().min(5, 'Raison requise'),
  dureeMinutes: z.coerce.number().min(15).max(120),
});

type TeleconsultFormData = z.infer<typeof teleconsultSchema>;

const statusConfig = {
  SCHEDULED: { label: 'Planifiée', color: 'bg-blue-100 text-blue-800', icon: Calendar },
  CONFIRMED: { label: 'Confirmée', color: 'bg-green-100 text-green-800', icon: Phone },
  IN_PROGRESS: { label: 'En cours', color: 'bg-purple-100 text-purple-800', icon: Video },
  COMPLETED: { label: 'Terminée', color: 'bg-gray-100 text-gray-800', icon: Phone },
  CANCELLED: { label: 'Annulée', color: 'bg-red-100 text-red-800', icon: Phone },
  NO_SHOW: { label: 'Non présenté', color: 'bg-yellow-100 text-yellow-800', icon: Phone },
};

function ScheduleDialog() {
  const [open, setOpen] = useState(false);

  const form = useForm<TeleconsultFormData>({
    resolver: createFormResolver(teleconsultSchema),
    defaultValues: {
      dureeMinutes: 30,
    },
  });

  async function onSubmit(data: TeleconsultFormData) {
    try {
      await apiClient.post('/teleconsultation/schedule', {
        ...data,
        type: 'TELECONSULTATION',
        status: 'SCHEDULED',
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
          Nouvelle consultation
        </Button>
      </DialogTrigger>
      <DialogContent className="max-w-2xl">
        <DialogHeader>
          <DialogTitle>Programmer une téléconsultation</DialogTitle>
          <DialogDescription>Créer une nouvelle consultation vidéo</DialogDescription>
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
                name="medecinId"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Médecin</FormLabel>
                    <FormControl>
                      <Input placeholder="ID médecin" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>

            <FormField
              control={form.control}
              name="dateHeure"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Date et heure</FormLabel>
                  <FormControl>
                    <Input type="datetime-local" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="dureeMinutes"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Durée (minutes)</FormLabel>
                  <FormControl>
                    <Input type="number" min="15" max="120" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="raison"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Motif de consultation</FormLabel>
                  <FormControl>
                    <Textarea placeholder="Décrire la raison de la consultation..." {...field} />
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

function ConsultationCard({ appointment }: { appointment: Appointment }) {
  const config = statusConfig[appointment.status as keyof typeof statusConfig] || { 
    label: appointment.status, 
    color: 'bg-gray-100',
    icon: Phone 
  };

  return (
    <Card>
      <CardHeader className="pb-3">
        <div className="flex justify-between items-start">
          <div>
            <CardTitle className="flex items-center gap-2">
              <Video className="h-5 w-5" />
              {appointment.raison || 'Consultation'}
            </CardTitle>
            <CardDescription>
              {new Date(appointment.dateDebut || '').toLocaleString('fr-FR')}
            </CardDescription>
          </div>
          <Badge className={config.color}>{config.label}</Badge>
        </div>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <div className="flex items-center gap-2">
            <User className="h-4 w-4 text-gray-500" />
            <div className="text-sm">
              <p className="text-gray-600">Patient</p>
              <p className="font-medium">{appointment.patientId}</p>
            </div>
          </div>
          <div className="flex items-center gap-2">
            <User className="h-4 w-4 text-gray-500" />
            <div className="text-sm">
              <p className="text-gray-600">Médecin</p>
              <p className="font-medium">{appointment.medecinId}</p>
            </div>
          </div>
        </div>

        {appointment.dureeMinutes && (
          <div className="flex items-center gap-2 text-sm text-gray-600">
            <Clock className="h-4 w-4" />
            Durée: {appointment.dureeMinutes} minutes
          </div>
        )}

        {['IN_PROGRESS', 'CONFIRMED'].includes(appointment.status || '') && (
          <div className="flex gap-2 pt-2">
            <Button variant="default" size="sm" className="flex-1">
              <Video className="h-4 w-4 mr-2" />
              Joindre l'appel
            </Button>
            <Button variant="outline" size="sm">
              <Share2 className="h-4 w-4" />
            </Button>
          </div>
        )}

        {appointment.status === 'COMPLETED' && appointment.resultat && (
          <div className="bg-blue-50 p-3 rounded-lg">
            <p className="text-sm font-medium text-blue-900 mb-2">Notes de consultation:</p>
            <p className="text-sm text-blue-800">{appointment.resultat}</p>
          </div>
        )}
      </CardContent>
    </Card>
  );
}

function VideoCallInterface() {
  const [isMuted, setIsMuted] = useState(false);
  const [isVideoOn, setIsVideoOn] = useState(true);
  const [isScreenSharing, setIsScreenSharing] = useState(false);

  return (
    <div className="bg-black rounded-lg p-4 space-y-4">
      <div className="bg-gray-900 rounded-lg aspect-video flex items-center justify-center">
        <Video className="h-12 w-12 text-gray-600" />
      </div>

      <div className="flex justify-center gap-4">
        <Button
          size="lg"
          variant={isMuted ? 'destructive' : 'default'}
          onClick={() => setIsMuted(!isMuted)}
        >
          <Phone className="h-5 w-5" />
        </Button>
        <Button
          size="lg"
          variant={!isVideoOn ? 'destructive' : 'default'}
          onClick={() => setIsVideoOn(!isVideoOn)}
        >
          <Video className="h-5 w-5" />
        </Button>
        <Button
          size="lg"
          variant={isScreenSharing ? 'default' : 'outline'}
          onClick={() => setIsScreenSharing(!isScreenSharing)}
        >
          <Share2 className="h-5 w-5" />
        </Button>
        <Button size="lg" variant="destructive">
          <Phone className="h-5 w-5 mr-2" />
          Terminer
        </Button>
      </div>
    </div>
  );
}

export default function Teleconsultation() {
  const { data: appointments, isLoading } = useQuery({
    queryKey: ['teleconsultations'],
    queryFn: async () => {
      const res = await apiClient.get('/teleconsultation/appointments');
      return res.data as Appointment[];
    },
  });

  const scheduled = appointments?.filter(a => ['SCHEDULED', 'CONFIRMED'].includes(a.status || '')) || [];
  const inProgress = appointments?.filter(a => a.status === 'IN_PROGRESS') || [];
  const completed = appointments?.filter(a => ['COMPLETED', 'CANCELLED'].includes(a.status || '')) || [];

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold tracking-tight">Téléconsultations</h1>
        <p className="text-gray-600 mt-2">Gestion des consultations vidéo et appels médicaux</p>
      </div>

      <div className="grid grid-cols-4 gap-4">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-600">Programmées</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold">{scheduled.length}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-600">En cours</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-purple-600">{inProgress.length}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-600">Complétées</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold">{completed.length}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-600">Total aujourd'hui</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold">{appointments?.length || 0}</div>
          </CardContent>
        </Card>
      </div>

      {inProgress.length > 0 && (
        <div>
          <h2 className="text-2xl font-bold mb-4">En cours maintenant</h2>
          <div className="bg-gray-100 p-6 rounded-lg">
            <VideoCallInterface />
          </div>
        </div>
      )}

      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold">Consultations programmées</h2>
        <ScheduleDialog />
      </div>

      {isLoading ? (
        <div className="text-center py-8">Chargement...</div>
      ) : scheduled.length === 0 ? (
        <Card>
          <CardContent className="py-8 text-center text-gray-600">
            <Calendar className="h-12 w-12 mx-auto mb-4 opacity-50" />
            <p>Aucune consultation programmée</p>
          </CardContent>
        </Card>
      ) : (
        <div className="grid gap-4">
          {scheduled.map(appointment => (
            <ConsultationCard key={appointment.id} appointment={appointment} />
          ))}
        </div>
      )}

      <div>
        <h2 className="text-2xl font-bold mb-4">Historique</h2>
        <div className="grid gap-4">
          {completed.slice(0, 5).map(appointment => (
            <ConsultationCard key={appointment.id} appointment={appointment} />
          ))}
        </div>
      </div>
    </div>
  );
}
