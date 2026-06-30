import { useState } from 'react';
import { useActiveAdmissions, useCreateAdmission, useDischargePatient } from '@/hooks/use-api';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { Plus, Clock, UserCheck, LogOut, AlertCircle, Search } from 'lucide-react';
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
import type { Admission } from '@/types';

const admissionSchema = z.object({
  patientId: z.string().min(1, 'Patient requis'),
  medecinAdmettantId: z.string().min(1, 'Médecin requis'),
  raison: z.string().min(5, 'Motif requis (minimum 5 caractères)'),
  diagnosticPrincipal: z.string().min(3, 'Diagnostic requis'),
  typeAdmission: z.enum(['EMERGENCY', 'PLANNED', 'TRANSFER']),
  priorite: z.enum(['urgent', 'non-urgent', 'planifiee']),
});

type AdmissionFormData = z.infer<typeof admissionSchema>;

function AdmissionDialog() {
  const [open, setOpen] = useState(false);
  const createAdmission = useCreateAdmission();

  const form = useForm<AdmissionFormData>({
    resolver: zodResolver(admissionSchema),
    defaultValues: {
      typeAdmission: 'PLANNED',
      priorite: 'non-urgent',
    },
  });

  async function onSubmit(data: AdmissionFormData) {
    try {
      await createAdmission.mutateAsync({
        ...data,
        dateAdmission: new Date().toISOString(),
        heureAdmission: new Date().toLocaleTimeString(),
      });
      form.reset();
      setOpen(false);
    } catch (error) {
      console.error('Erreur lors de l\'admission:', error);
    }
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button>
          <Plus className="h-4 w-4 mr-2" />
          Nouvelle admission
        </Button>
      </DialogTrigger>
      <DialogContent className="max-w-2xl">
        <DialogHeader>
          <DialogTitle>Nouvelle admission</DialogTitle>
          <DialogDescription>Enregistrer l&apos;admission d&apos;un patient</DialogDescription>
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
                name="medecinAdmettantId"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Médecin admettant</FormLabel>
                    <FormControl>
                      <Input placeholder="ID médecin" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="typeAdmission"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Type d&apos;admission</FormLabel>
                    <Select onValueChange={field.onChange} defaultValue={field.value}>
                      <FormControl>
                        <SelectTrigger>
                          <SelectValue />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        <SelectItem value="EMERGENCY">Urgence</SelectItem>
                        <SelectItem value="PLANNED">Programmée</SelectItem>
                        <SelectItem value="TRANSFER">Transfert</SelectItem>
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
                        <SelectItem value="planifiee">Planifiée</SelectItem>
                      </SelectContent>
                    </Select>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>

            <FormField
              control={form.control}
              name="diagnosticPrincipal"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Diagnostic principal</FormLabel>
                  <FormControl>
                    <Input placeholder="Diagnostic principal" {...field} />
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
                  <FormLabel>Motif de l&apos;admission</FormLabel>
                  <FormControl>
                    <Textarea placeholder="Décrivez le motif de l'admission..." {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <div className="flex gap-2 justify-end">
              <Button type="button" variant="outline" onClick={() => setOpen(false)}>
                Annuler
              </Button>
              <Button type="submit" disabled={createAdmission.isPending}>
                {createAdmission.isPending ? 'Enregistrement...' : 'Enregistrer'}
              </Button>
            </div>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}

function AdmissionCard({ admission }: { admission: Admission }) {
  const [dischargingId, setDischargingId] = useState<string | null>(null);
  const dischargePatient = useDischargePatient();

  const statusConfig = {
    admitted: { icon: UserCheck, label: 'Admis', color: 'bg-blue-100 text-blue-800' },
    hospitalized: { icon: Clock, label: 'Hospitalisé', color: 'bg-yellow-100 text-yellow-800' },
    discharged: { icon: LogOut, label: 'Sorti', color: 'bg-green-100 text-green-800' },
  };

  const config = statusConfig[admission.status as keyof typeof statusConfig] || statusConfig.admitted;

  async function handleDischarge() {
    try {
      await dischargePatient.mutateAsync(admission.id);
      setDischargingId(null);
    } catch (error) {
      console.error('Erreur lors de la sortie:', error);
    }
  }

  return (
    <Card>
      <CardHeader className="pb-3">
        <div className="flex items-center justify-between">
          <div>
            <CardTitle>Patient #{admission.patientId}</CardTitle>
            <CardDescription>{admission.raison}</CardDescription>
          </div>
          <Badge className={config.color}>
            {config.label}
          </Badge>
        </div>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="grid grid-cols-2 gap-4 text-sm">
          <div>
            <p className="text-muted-foreground">Diagnostic principal</p>
            <p className="font-medium">{admission.diagnosticPrincipal}</p>
          </div>
          <div>
            <p className="text-muted-foreground">Type d&apos;admission</p>
            <p className="font-medium">{admission.typeAdmission}</p>
          </div>
          <div>
            <p className="text-muted-foreground">Date d&apos;admission</p>
            <p className="font-medium">{new Date(admission.dateAdmission).toLocaleDateString('fr-FR')}</p>
          </div>
          <div>
            <p className="text-muted-foreground">Priorité</p>
            <Badge variant={admission.priorite === 'urgent' ? 'destructive' : 'secondary'}>
              {admission.priorite}
            </Badge>
          </div>
        </div>

        {admission.status !== 'discharged' && (
          <div className="pt-4 border-t flex gap-2">
            <Button
              size="sm"
              variant="destructive"
              onClick={() => setDischargingId(admission.id)}
              disabled={dischargePatient.isPending}
            >
              <LogOut className="h-4 w-4 mr-2" />
              Sortir le patient
            </Button>
          </div>
        )}

        {dischargingId === admission.id && (
          <Dialog open={!!dischargingId} onOpenChange={(open) => !open && setDischargingId(null)}>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Confirmer la sortie</DialogTitle>
              </DialogHeader>
              <p className="text-sm text-muted-foreground">
                Êtes-vous sûr de vouloir enregistrer la sortie de ce patient ?
              </p>
              <div className="flex gap-2 justify-end">
                <Button variant="outline" onClick={() => setDischargingId(null)}>
                  Annuler
                </Button>
                <Button
                  variant="destructive"
                  onClick={handleDischarge}
                  disabled={dischargePatient.isPending}
                >
                  {dischargePatient.isPending ? 'Traitement...' : 'Confirmer sortie'}
                </Button>
              </div>
            </DialogContent>
          </Dialog>
        )}
      </CardContent>
    </Card>
  );
}

export function AdmissionsPage() {
  const [search, setSearch] = useState('');
  const { data: admissions, isLoading } = useActiveAdmissions();

  const filteredAdmissions = admissions?.filter((a) =>
    a.diagnosticPrincipal?.toLowerCase().includes(search.toLowerCase()) ||
    a.raison?.toLowerCase().includes(search.toLowerCase())
  ) || [];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Admissions & Sorties</h1>
          <p className="text-muted-foreground">Gestion des admissions et des sorties hospitalières</p>
        </div>
        <AdmissionDialog />
      </div>

      <Card>
        <CardHeader>
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
            <Input
              placeholder="Rechercher une admission..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="pl-10"
            />
          </div>
        </CardHeader>
      </Card>

      <div className="grid gap-4">
        {isLoading ? (
          <Card>
            <CardContent className="flex items-center justify-center h-64">
              <div className="animate-spin w-8 h-8 border-4 border-primary border-t-transparent rounded-full" />
            </CardContent>
          </Card>
        ) : filteredAdmissions.length > 0 ? (
          filteredAdmissions.map((admission) => (
            <AdmissionCard key={admission.id} admission={admission} />
          ))
        ) : (
          <Card>
            <CardContent className="flex flex-col items-center justify-center h-64 gap-4">
              <AlertCircle className="h-12 w-12 text-muted-foreground opacity-50" />
              <p className="text-muted-foreground">Aucune admission trouvée</p>
            </CardContent>
          </Card>
        )}
      </div>
    </div>
  );
}
