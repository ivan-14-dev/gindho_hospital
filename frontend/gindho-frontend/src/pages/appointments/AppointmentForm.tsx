import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { useAppointments } from '@/hooks/use-appointments';
import type { Appointment } from '@/hooks/use-appointments';

const appointmentSchema = z.object({
  patientId: z.string().min(1, 'Le patient est requis'),
  medecinId: z.string().min(1, 'Le médecin est requis'),
  dateDebut: z.string().min(1, 'La date de début est requise'),
  dateFin: z.string().min(1, 'La date de fin est requise'),
  motif: z.string().optional(),
  statut: z.enum(['PLANIFIE', 'CONFIRME', 'ANNULE', 'TERMINE']),
});

type AppointmentFormValues = z.infer<typeof appointmentSchema>;

interface AppointmentFormProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

export function AppointmentForm({ open, onOpenChange }: AppointmentFormProps) {
  const { createAppointment } = useAppointments({});
  
  const form = useForm<AppointmentFormValues>({
    resolver: zodResolver(appointmentSchema),
    defaultValues: {
      patientId: '',
      medecinId: '',
      dateDebut: '',
      dateFin: '',
      motif: '',
      statut: 'PLANIFIE',
    },
  });

  const onSubmit = async (data: AppointmentFormValues) => {
    await createAppointment(data as Partial<Appointment>);
    onOpenChange(false);
    form.reset();
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Nouveau Rendez-vous</DialogTitle>
        </DialogHeader>
        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
          <div className="grid gap-2">
            <Label htmlFor="patientId">Patient</Label>
            <Input id="patientId" {...form.register('patientId')} placeholder="ID du patient" />
          </div>
          
          <div className="grid gap-2">
            <Label htmlFor="medecinId">Médecin</Label>
            <Input id="medecinId" {...form.register('medecinId')} placeholder="ID du médecin" />
          </div>
          
          <div className="grid gap-2">
            <Label htmlFor="dateDebut">Date de début</Label>
            <Input id="dateDebut" type="datetime-local" {...form.register('dateDebut')} />
          </div>
          
          <div className="grid gap-2">
            <Label htmlFor="dateFin">Date de fin</Label>
            <Input id="dateFin" type="datetime-local" {...form.register('dateFin')} />
          </div>
          
          <div className="grid gap-2">
            <Label htmlFor="statut">Statut</Label>
            <Select defaultValue="PLANIFIE" onValueChange={(v: string) => form.setValue('statut', v as 'PLANIFIE' | 'CONFIRME' | 'ANNULE' | 'TERMINE')}>
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="PLANIFIE">Planifié</SelectItem>
                <SelectItem value="CONFIRME">Confirmé</SelectItem>
                <SelectItem value="ANNULE">Annulé</SelectItem>
                <SelectItem value="TERMINE">Terminé</SelectItem>
              </SelectContent>
            </Select>
          </div>
          
          <div className="grid gap-2">
            <Label htmlFor="motif">Motif</Label>
            <Input id="motif" {...form.register('motif')} />
          </div>
          
          <Button type="submit" className="w-full" disabled={form.formState.isSubmitting}>
            Créer
          </Button>
        </form>
      </DialogContent>
    </Dialog>
  );
}