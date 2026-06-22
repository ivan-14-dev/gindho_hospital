import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { useCreateAppointment, useUpdateAppointment } from '@/hooks/use-appointments';
import type { Appointment } from '@/hooks/use-appointments';

const appointmentSchema = z.object({
  patientId: z.string().min(1, 'Le patient est requis'),
  medecinId: z.string().min(1, 'Le médecin est requis'),
  dateDebut: z.string().min(1, 'La date de début est requise'),
  dateFin: z.string().min(1, 'La date de fin est requise'),
  motif: z.string().optional(),
  notes: z.string().optional(),
});

type AppointmentFormData = z.infer<typeof appointmentSchema>;

interface AppointmentFormProps {
  appointment?: Appointment;
  onSuccess?: () => void;
}

export function AppointmentForm({ appointment, onSuccess }: AppointmentFormProps) {
  const [error, setError] = useState<string | null>(null);
  const createAppointment = useCreateAppointment();
  const updateAppointment = useUpdateAppointment();

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<AppointmentFormData>({
    resolver: zodResolver(appointmentSchema),
    defaultValues: {
      patientId: appointment?.patientId || '',
      medecinId: appointment?.medecinId || '',
      dateDebut: appointment?.dateDebut ? appointment.dateDebut.slice(0, 16) : '',
      dateFin: appointment?.dateFin ? appointment.dateFin.slice(0, 16) : '',
      motif: appointment?.motif || '',
      notes: appointment?.notes || '',
    },
  });

  const onSubmit = async (data: AppointmentFormData) => {
    try {
      setError(null);
      const payload = {
        ...data,
        dateDebut: new Date(data.dateDebut).toISOString(),
        dateFin: new Date(data.dateFin).toISOString(),
        motif: data.motif || undefined,
        notes: data.notes || undefined,
      };

      if (appointment?.id) {
        await updateAppointment.mutateAsync({ id: appointment.id, data: payload as Partial<Appointment> });
      } else {
        await createAppointment.mutateAsync(payload as Partial<Appointment>);
      }

      onSuccess?.();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Une erreur est survenue');
    }
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle>{appointment ? 'Modifier le rendez-vous' : 'Nouveau rendez-vous'}</CardTitle>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          {error && (
            <div className="p-3 rounded-md bg-destructive/10 text-destructive text-sm">
              {error}
            </div>
          )}

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="patientId">Patient *</Label>
              <Input id="patientId" {...register('patientId')} placeholder="ID du patient" />
              {errors.patientId && (
                <p className="text-sm text-destructive">{errors.patientId.message}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="medecinId">Médecin *</Label>
              <Input id="medecinId" {...register('medecinId')} placeholder="ID du médecin" />
              {errors.medecinId && (
                <p className="text-sm text-destructive">{errors.medecinId.message}</p>
              )}
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="dateDebut">Date et heure de début *</Label>
              <Input id="dateDebut" type="datetime-local" {...register('dateDebut')} />
              {errors.dateDebut && (
                <p className="text-sm text-destructive">{errors.dateDebut.message}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="dateFin">Date et heure de fin *</Label>
              <Input id="dateFin" type="datetime-local" {...register('dateFin')} />
              {errors.dateFin && (
                <p className="text-sm text-destructive">{errors.dateFin.message}</p>
              )}
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="motif">Motif de consultation</Label>
            <Input id="motif" {...register('motif')} placeholder="Ex: Consultation générale" />
          </div>

          <div className="space-y-2">
            <Label htmlFor="notes">Notes</Label>
            <textarea
              id="notes"
              {...register('notes')}
              className="flex min-h-[80px] w-full rounded-md border border-input bg-transparent px-3 py-2 text-base shadow-sm transition-colors"
              placeholder="Notes additionnelles..."
            />
          </div>

          <div className="flex gap-2 justify-end">
            <Button type="button" variant="outline" onClick={onSuccess}>
              Annuler
            </Button>
            <Button type="submit" disabled={isSubmitting}>
              {isSubmitting ? 'Enregistrement...' : appointment ? 'Modifier' : 'Créer'}
            </Button>
          </div>
        </form>
      </CardContent>
    </Card>
  );
}