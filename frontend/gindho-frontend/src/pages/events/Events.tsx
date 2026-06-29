import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { Plus, Calendar, Clock, MapPin, Users, AlertCircle, CheckCircle } from 'lucide-react';
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
import type { Event } from '@/types';

const eventSchema = z.object({
  titre: z.string().min(3, 'Titre requis'),
  type: z.string().min(1, 'Type requis'),
  dateHeure: z.string().min(1, 'Date et heure requises'),
  lieu: z.string().optional(),
  description: z.string().optional(),
});

type EventFormData = z.infer<typeof eventSchema>;

function CreateEventDialog() {
  const [open, setOpen] = useState(false);

  const form = useForm<EventFormData>({
    resolver: zodResolver(eventSchema),
  });

  async function onSubmit(data: EventFormData) {
    try {
      await apiClient.post('/events', {
        ...data,
        organisateurId: 'current-user',
        statut: 'planned',
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
          Nouvel événement
        </Button>
      </DialogTrigger>
      <DialogContent className="max-w-md">
        <DialogHeader>
          <DialogTitle>Créer un événement</DialogTitle>
          <DialogDescription>Organiser un nouveau meeting ou formation</DialogDescription>
        </DialogHeader>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="titre"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Titre</FormLabel>
                  <FormControl>
                    <Input placeholder="Ex: Formation CPR" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="type"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Type d'événement</FormLabel>
                  <FormControl>
                    <select {...field} className="w-full border rounded p-2">
                      <option value="">Sélectionner...</option>
                      <option value="meeting">Réunion</option>
                      <option value="training">Formation</option>
                      <option value="conference">Conférence</option>
                      <option value="workshop">Atelier</option>
                      <option value="announcement">Annonce</option>
                      <option value="other">Autre</option>
                    </select>
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

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
              name="lieu"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Lieu</FormLabel>
                  <FormControl>
                    <Input placeholder="Amphi A, Salle 101, etc..." {...field} />
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
              Créer
            </Button>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}

function EventCard({ event }: { event: Event }) {
  const statusConfig = {
    planned: { label: 'Planifiée', color: 'bg-blue-100 text-blue-800' },
    ongoing: { label: 'En cours', color: 'bg-purple-100 text-purple-800' },
    completed: { label: 'Terminée', color: 'bg-green-100 text-green-800' },
    cancelled: { label: 'Annulée', color: 'bg-red-100 text-red-800' },
  };

  const config = statusConfig[event.statut as keyof typeof statusConfig] || statusConfig.planned;
  const eventDate = new Date(event.dateHeure);
  const now = new Date();
  const isPast = eventDate < now;

  return (
    <Card className={isPast ? 'opacity-60' : ''}>
      <CardHeader>
        <div className="flex justify-between items-start">
          <div>
            <CardTitle className="flex items-center gap-2">
              <Calendar className="h-5 w-5" />
              {event.titre}
            </CardTitle>
            {event.type && (
              <CardDescription>{event.type}</CardDescription>
            )}
          </div>
          <Badge className={config.color}>{config.label}</Badge>
        </div>
      </CardHeader>
      <CardContent className="space-y-3">
        <div className="flex items-center gap-2 text-sm">
          <Clock className="h-4 w-4 text-gray-500" />
          {eventDate.toLocaleString('fr-FR')}
        </div>

        {event.lieu && (
          <div className="flex items-center gap-2 text-sm">
            <MapPin className="h-4 w-4 text-gray-500" />
            {event.lieu}
          </div>
        )}

        {event.description && (
          <div className="bg-gray-50 p-3 rounded text-sm">
            <p className="text-gray-700">{event.description}</p>
          </div>
        )}

        {event.participants && event.participants.length > 0 && (
          <div className="flex items-center gap-2 text-sm text-gray-600">
            <Users className="h-4 w-4" />
            {event.participants.length} participant(s)
          </div>
        )}

        <div className="flex gap-2 pt-2">
          <Button size="sm" variant="outline" className="flex-1">
            Détails
          </Button>
          <Button size="sm" variant="default" className="flex-1">
            RSVP
          </Button>
        </div>
      </CardContent>
    </Card>
  );
}

export default function Events() {
  const { data: events, isLoading } = useQuery({
    queryKey: ['events'],
    queryFn: async () => {
      const res = await apiClient.get('/events');
      return res.data as Event[];
    },
  });

  const now = new Date();
  const upcoming = events?.filter(e => new Date(e.dateHeure) > now) || [];
  const past = events?.filter(e => new Date(e.dateHeure) <= now) || [];

  const typeStats = events?.reduce(
    (acc, e) => {
      acc[e.type || 'other'] = (acc[e.type || 'other'] || 0) + 1;
      return acc;
    },
    {} as Record<string, number>
  );

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold tracking-tight">Événements</h1>
        <p className="text-gray-600 mt-2">Calendrier des réunions, formations et annonces</p>
      </div>

      <div className="grid grid-cols-4 gap-4">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-600">Total d'événements</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold">{events?.length || 0}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-600">À venir</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-blue-600">{upcoming.length}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-600">Passées</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-gray-600">{past.length}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-600">Types</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold">{Object.keys(typeStats || {}).length}</div>
          </CardContent>
        </Card>
      </div>

      <div className="grid grid-cols-4 gap-4">
        {typeStats &&
          Object.entries(typeStats).map(([type, count]) => (
            <Card key={type}>
              <CardContent className="pt-6">
                <div className="text-sm">
                  <p className="text-gray-600 capitalize">{type}</p>
                  <p className="text-2xl font-bold mt-1">{count}</p>
                </div>
              </CardContent>
            </Card>
          ))}
      </div>

      <div className="grid grid-cols-3 gap-6">
        <div className="col-span-2 space-y-6">
          <div>
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-2xl font-bold">Événements à venir</h2>
              <CreateEventDialog />
            </div>

            {isLoading ? (
              <div className="text-center py-8">Chargement...</div>
            ) : upcoming.length === 0 ? (
              <Card>
                <CardContent className="py-8 text-center text-gray-600">
                  <Calendar className="h-12 w-12 mx-auto mb-4 opacity-50" />
                  <p>Aucun événement prévu</p>
                </CardContent>
              </Card>
            ) : (
              <div className="grid gap-4">
                {upcoming.map(event => (
                  <EventCard key={event.id} event={event} />
                ))}
              </div>
            )}
          </div>

          {past.length > 0 && (
            <div>
              <h2 className="text-2xl font-bold mb-4">Événements passés</h2>
              <div className="grid gap-4">
                {past.slice(0, 5).map(event => (
                  <EventCard key={event.id} event={event} />
                ))}
              </div>
            </div>
          )}
        </div>

        <div>
          <h2 className="text-2xl font-bold mb-4">Miniature calendrier</h2>
          <Card>
            <CardContent className="pt-6">
              <div className="text-sm text-gray-600">
                <div className="font-medium mb-4">
                  {new Date().toLocaleDateString('fr-FR', { month: 'long', year: 'numeric' })}
                </div>
                <div className="grid grid-cols-7 gap-1 text-center">
                  {['L', 'M', 'M', 'J', 'V', 'S', 'D'].map(day => (
                    <div key={day} className="font-medium text-xs">
                      {day}
                    </div>
                  ))}
                  {Array.from({ length: 35 }, (_, i) => {
                    const date = new Date();
                    date.setDate(i - (date.getDay() - 1));
                    const isToday = date.toDateString() === new Date().toDateString();
                    const hasEvent = events?.some(e => 
                      new Date(e.dateHeure).toDateString() === date.toDateString()
                    );

                    return (
                      <div
                        key={i}
                        className={`p-1 text-xs rounded ${
                          isToday ? 'bg-blue-600 text-white font-bold' : ''
                        } ${hasEvent ? 'font-bold text-blue-600' : ''}`}
                      >
                        {date.getDate()}
                      </div>
                    );
                  })}
                </div>
              </div>
            </CardContent>
          </Card>

          <div className="mt-4">
            <h3 className="font-medium mb-2">Légende</h3>
            <div className="space-y-2 text-sm">
              <div className="flex items-center gap-2">
                <div className="w-3 h-3 bg-blue-600"></div>
                <span>Aujourd'hui</span>
              </div>
              <div className="flex items-center gap-2">
                <div className="w-3 h-3 text-blue-600 font-bold">•</div>
                <span>Avec événement</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
