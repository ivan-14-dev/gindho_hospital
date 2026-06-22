import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { useCreateConsultation } from '@/hooks/use-medical-records';
import type { Consultation } from '@/hooks/use-medical-records';

const consultationSchema = z.object({
  patientId: z.string().min(1, 'Le patient est requis'),
  diagnostic: z.string().min(1, 'Le diagnostic est requis'),
  notes: z.string().optional(),
  traitement: z.string().optional(),
});

type ConsultationFormData = z.infer<typeof consultationSchema>;

interface ConsultationFormProps {
  patientId?: string;
  onSuccess?: () => void;
}

export function ConsultationForm({ patientId, onSuccess }: ConsultationFormProps) {
  const [error, setError] = useState<string | null>(null);
  const createConsultation = useCreateConsultation();

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<ConsultationFormData>({
    resolver: zodResolver(consultationSchema),
    defaultValues: {
      patientId: patientId || '',
      diagnostic: '',
      notes: '',
      traitement: '',
    },
  });

  const onSubmit = async (data: ConsultationFormData) => {
    try {
      setError(null);
      const payload = {
        ...data,
        notes: data.notes || undefined,
        traitement: data.traitement || undefined,
      };

      await createConsultation.mutateAsync(payload as Partial<Consultation>);
      onSuccess?.();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Une erreur est survenue');
    }
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle>Nouvelle consultation</CardTitle>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          {error && (
            <div className="p-3 rounded-md bg-destructive/10 text-destructive text-sm">
              {error}
            </div>
          )}

          <div className="space-y-2">
            <Label htmlFor="patientId">Patient *</Label>
            <Input id="patientId" {...register('patientId')} placeholder="ID du patient" />
            {errors.patientId && (
              <p className="text-sm text-destructive">{errors.patientId.message}</p>
            )}
          </div>

          <div className="space-y-2">
            <Label htmlFor="diagnostic">Diagnostic *</Label>
            <Input id="diagnostic" {...register('diagnostic')} placeholder="Ex: Grippe saisonnière" />
            {errors.diagnostic && (
              <p className="text-sm text-destructive">{errors.diagnostic.message}</p>
            )}
          </div>

          <div className="space-y-2">
            <Label htmlFor="traitement">Traitement prescrit</Label>
            <Input id="traitement" {...register('traitement')} placeholder="Ex: Paracétamol 500mg" />
          </div>

          <div className="space-y-2">
            <Label htmlFor="notes">Notes médicales</Label>
            <textarea
              id="notes"
              {...register('notes')}
              placeholder="Observations, recommandations..."
              className="flex min-h-[100px] w-full rounded-md border border-input bg-transparent px-3 py-2 text-base shadow-sm transition-colors"
            />
          </div>

          <div className="flex gap-2 justify-end">
            <Button type="button" variant="outline" onClick={onSuccess}>
              Annuler
            </Button>
            <Button type="submit" disabled={isSubmitting}>
              {isSubmitting ? 'Enregistrement...' : 'Créer la consultation'}
            </Button>
          </div>
        </form>
      </CardContent>
    </Card>
  );
}