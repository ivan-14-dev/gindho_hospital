import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { usePatients } from '@/hooks/use-patients';
import type { Patient } from '@/types';

const patientSchema = z.object({
  nom: z.string().min(1, 'Le nom est requis'),
  prenom: z.string().min(1, 'Le prénom est requis'),
  dateNaissance: z.string().min(1, 'La date de naissance est requise'),
  sexe: z.enum(['M', 'F', 'Autre']),
  telephone: z.string().optional(),
  email: z.string().email('Email invalide').optional(),
  adresse: z.string().optional(),
});

type PatientFormValues = z.infer<typeof patientSchema>;

interface PatientFormProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  patientId?: string;
}

export function PatientForm({ open, onOpenChange }: PatientFormProps) {
  const { createPatient } = usePatients({});
  
  const form = useForm<PatientFormValues>({
    resolver: zodResolver(patientSchema),
    defaultValues: {
      nom: '',
      prenom: '',
      dateNaissance: '',
      sexe: 'Autre',
    },
  });

  const onSubmit = async (data: PatientFormValues) => {
    await createPatient(data as Partial<Patient>);
    onOpenChange(false);
    form.reset();
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Nouveau Patient</DialogTitle>
        </DialogHeader>
        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
          <div className="grid gap-2">
            <Label htmlFor="nom">Nom</Label>
            <Input id="nom" {...form.register('nom')} />
          </div>
          
          <div className="grid gap-2">
            <Label htmlFor="prenom">Prénom</Label>
            <Input id="prenom" {...form.register('prenom')} />
          </div>
          
          <div className="grid gap-2">
            <Label htmlFor="dateNaissance">Date de naissance</Label>
            <Input id="dateNaissance" type="date" {...form.register('dateNaissance')} />
          </div>
          
          <div className="grid gap-2">
            <Label htmlFor="sexe">Sexe</Label>
            <Select defaultValue="Autre" onValueChange={(v: string) => form.setValue('sexe', v as 'M' | 'F' | 'Autre')}>
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="M">Masculin</SelectItem>
                <SelectItem value="F">Féminin</SelectItem>
                <SelectItem value="Autre">Autre</SelectItem>
              </SelectContent>
            </Select>
          </div>
          
          <div className="grid gap-2">
            <Label htmlFor="telephone">Téléphone</Label>
            <Input id="telephone" {...form.register('telephone')} />
          </div>
          
          <div className="grid gap-2">
            <Label htmlFor="email">Email</Label>
            <Input id="email" type="email" {...form.register('email')} />
          </div>
          
          <div className="grid gap-2">
            <Label htmlFor="adresse">Adresse</Label>
            <Input id="adresse" {...form.register('adresse')} />
          </div>
          
          <Button type="submit" className="w-full" disabled={form.formState.isSubmitting}>
            Créer
          </Button>
        </form>
      </DialogContent>
    </Dialog>
  );
}