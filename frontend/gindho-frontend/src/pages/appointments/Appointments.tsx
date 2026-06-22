import { useState } from 'react';
import { useAppointments } from '@/hooks/use-appointments';
import type { Appointment } from '@/hooks/use-appointments';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Calendar, Clock, Plus } from 'lucide-react';
import { format } from 'date-fns';
import { fr } from 'date-fns/locale';
import { usePDFExport } from '@/hooks/use-pdf-export';
import { AppointmentForm } from './AppointmentForm';

export function AppointmentsPage() {
  const [filter, setFilter] = useState<'all' | 'today' | 'upcoming' | 'past'>('all');
  const [formOpen, setFormOpen] = useState(false);
  const { exportAppointment } = usePDFExport();
  const { data: appointments, isLoading } = useAppointments();

  const filteredAppointments = appointments?.filter((apt: Appointment) => {
    const date = new Date(apt.dateDebut);
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    switch (filter) {
      case 'today':
        return date.toDateString() === today.toDateString();
      case 'upcoming':
        return date > today;
      case 'past':
        return date < today;
      default:
        return true;
    }
  });

  const getStatusBadge = (statut: string) => {
    const variants: Record<string, 'default' | 'secondary' | 'destructive' | 'outline'> = {
      PLANIFIE: 'secondary',
      CONFIRME: 'default',
      ANNULE: 'destructive',
      TERMINE: 'outline',
    };
    return variants[statut] || 'secondary';
  };

  const getStatusLabel = (statut: string) => {
    const labels: Record<string, string> = {
      PLANIFIE: 'Planifié',
      CONFIRME: 'Confirmé',
      ANNULE: 'Annulé',
      TERMINE: 'Terminé',
    };
    return labels[statut] || statut;
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Rendez-vous</h1>
          <p className="text-muted-foreground">Gestion des rendez-vous</p>
        </div>
<Button onClick={() => setFormOpen(true)}>
           <Plus className="h-4 w-4 mr-2" />
           Nouveau rendez-vous
         </Button>
      </div>

      <div className="flex gap-2">
        <Button
          variant={filter === 'all' ? 'default' : 'outline'}
          onClick={() => setFilter('all')}
        >
          Tous
        </Button>
        <Button
          variant={filter === 'today' ? 'default' : 'outline'}
          onClick={() => setFilter('today')}
        >
          Aujourd'hui
        </Button>
        <Button
          variant={filter === 'upcoming' ? 'default' : 'outline'}
          onClick={() => setFilter('upcoming')}
        >
          À venir
        </Button>
        <Button
          variant={filter === 'past' ? 'default' : 'outline'}
          onClick={() => setFilter('past')}
        >
          Passés
        </Button>
      </div>

      {isLoading ? (
        <div className="flex items-center justify-center h-64">
          <div className="animate-spin w-8 h-8 border-4 border-primary border-t-transparent rounded-full" />
        </div>
      ) : filteredAppointments && filteredAppointments.length > 0 ? (
        <div className="grid gap-4">
          {filteredAppointments.map((appointment: Appointment) => (
            <Card key={appointment.id}>
              <CardHeader>
                <div className="flex items-start justify-between">
                  <div className="space-y-1">
                    <CardTitle className="text-lg">
                      {appointment.patientNom || `Patient #${appointment.patientId}`}
                    </CardTitle>
                    <div className="flex items-center gap-4 text-sm text-muted-foreground">
                      <span className="flex items-center gap-1">
                        <Calendar className="h-4 w-4" />
                        {format(new Date(appointment.dateDebut), 'dd MMMM yyyy', { locale: fr })}
                      </span>
                      <span className="flex items-center gap-1">
                        <Clock className="h-4 w-4" />
                        {format(new Date(appointment.dateDebut), 'HH:mm')} -{' '}
                        {format(new Date(appointment.dateFin), 'HH:mm')}
                      </span>
                    </div>
                  </div>
                  <Badge variant={getStatusBadge(appointment.statut)}>
                    {getStatusLabel(appointment.statut)}
                  </Badge>
                </div>
              </CardHeader>
              <CardContent>
                {appointment.motif && (
                  <p className="text-sm text-muted-foreground mb-2">
                    <strong>Motif :</strong> {appointment.motif}
                  </p>
                )}
                {appointment.notes && (
                  <p className="text-sm text-muted-foreground">
                    <strong>Notes :</strong> {appointment.notes}
                  </p>
                )}
<div className="flex gap-2 mt-4">
                   <Button variant="outline" size="sm">
                     Détails
                   </Button>
                   <Button variant="outline" size="sm" onClick={() => exportAppointment(appointment)}>
                     PDF
                   </Button>
                   {appointment.statut === 'PLANIFIE' && (
                     <Button variant="default" size="sm">
                       Confirmer
                     </Button>
                   )}
                 </div>
               </CardContent>
             </Card>
           ))}
         </div>
       ) : (
         <Card>
           <CardContent className="text-center py-12">
             <Calendar className="h-12 w-12 mx-auto text-muted-foreground mb-4" />
             <p className="text-muted-foreground">Aucun rendez-vous trouvé</p>
           </CardContent>
         </Card>
       )}
       <AppointmentForm open={formOpen} onOpenChange={setFormOpen} />
     </div>
   );
}