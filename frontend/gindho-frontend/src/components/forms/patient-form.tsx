import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { useCreatePatient, useUpdatePatient } from '@/hooks/use-patients';
import type { Patient } from '@/types';

const patientSchema = z.object({
  nom: z.string().min(2, 'Le nom doit contenir au moins 2 caractères'),
  prenom: z.string().min(2, 'Le prénom doit contenir au moins 2 caractères'),
  dateNaissance: z.string().min(1, 'La date de naissance est requise'),
  sexe: z.enum(['M', 'F', 'Autre']),
  email: z.string().email('Email invalide').optional().or(z.literal('')),
  telephone: z.string().optional(),
  adresse: z.string().optional(),
});

type PatientFormData = z.infer<typeof patientSchema>;

interface PatientFormProps {
  patient?: Patient;
  onSuccess?: () => void;
}

export function PatientForm({ patient, onSuccess }: PatientFormProps) {
  const [error, setError] = useState<string | null>(null);
  const createPatient = useCreatePatient();
  const updatePatient = useUpdatePatient();

  const {
    register,
    handleSubmit,
    setValue,
    watch,
    formState: { errors, isSubmitting },
  } = useForm<PatientFormData>({
    resolver: zodResolver(patientSchema),
    defaultValues: {
      nom: patient?.nom || '',
      prenom: patient?.prenom || '',
      dateNaissance: patient?.dateNaissance || '',
      sexe: (patient?.sexe as 'M' | 'F' | 'Autre') || 'M',
      email: patient?.email || '',
      telephone: patient?.telephone || '',
      adresse: patient?.adresse || '',
    },
  });

  const sexeValue = watch('sexe');

  const onSubmit = async (data: PatientFormData) => {
    try {
      setError(null);
      const payload = {
        ...data,
        email: data.email || undefined,
        telephone: data.telephone || undefined,
        adresse: data.adresse || undefined,
      };

      if (patient?.id) {
        await updatePatient.mutateAsync({ id: patient.id, data: payload as Partial<Patient> });
      } else {
        await createPatient.mutateAsync(payload as Partial<Patient>);
      }

      onSuccess?.();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Une erreur est survenue');
    }
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle>{patient ? 'Modifier le patient' : 'Nouveau patient'}</CardTitle>
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
              <Label htmlFor="nom">Nom *</Label>
              <Input id="nom" {...register('nom')} />
              {errors.nom && (
                <p className="text-sm text-destructive">{errors.nom.message}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="prenom">Prénom *</Label>
              <Input id="prenom" {...register('prenom')} />
              {errors.prenom && (
                <p className="text-sm text-destructive">{errors.prenom.message}</p>
              )}
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="dateNaissance">Date de naissance *</Label>
              <Input id="dateNaissance" type="date" {...register('dateNaissance')} />
              {errors.dateNaissance && (
                <p className="text-sm text-destructive">{errors.dateNaissance.message}</p>
              )}
            </div>

          <div className="space-y-2">
            <Label htmlFor="sexe">Sexe *</Label>
            <select
              id="sexe"
              value={sexeValue}
              onChange={(e) => setValue('sexe', e.target.value as 'M' | 'F' | 'Autre')}
              className="flex h-9 w-full rounded-md border border-input bg-transparent px-3 py-1 text-base shadow-sm transition-colors"
            >
              <option value="M">Masculin</option>
              <option value="F">Féminin</option>
              <option value="Autre">Autre</option>
            </select>
            {errors.sexe && (
              <p className="text-sm text-destructive">{errors.sexe.message}</p>
            )}
          </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="email">Email</Label>
            <Input id="email" type="email" {...register('email')} />
            {errors.email && (
              <p className="text-sm text-destructive">{errors.email.message}</p>
            )}
          </div>

          <div className="space-y-2">
            <Label htmlFor="telephone">Téléphone</Label>
            <Input id="telephone" {...register('telephone')} />
          </div>

          <div className="space-y-2">
            <Label htmlFor="adresse">Adresse</Label>
            <Input id="adresse" {...register('adresse')} />
          </div>

          <div className="flex gap-2 justify-end">
            <Button type="button" variant="outline" onClick={onSuccess}>
              Annuler
            </Button>
            <Button type="submit" disabled={isSubmitting}>
              {isSubmitting ? 'Enregistrement...' : patient ? 'Modifier' : 'Créer'}
            </Button>
          </div>
        </form>
      </CardContent>
    </Card>
  );
}