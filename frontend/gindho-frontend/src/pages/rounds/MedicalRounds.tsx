import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { Plus, Clock, Stethoscope, CheckCircle, AlertCircle, Play } from 'lucide-react';
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
import type { MedicalRound } from '@/types';

const roundSchema = z.object({
  wardId: z.string().min(1, 'Service requis'),
  medecinId: z.string().min(1, 'Médecin requis'),
  dateHeure: z.string().min(1, 'Date et heure requises'),
});

type RoundFormData = z.infer<typeof roundSchema>;

const assessmentSchema = z.object({
  roundId: z.string(),
  patientId: z.string().min(1, 'Patient requis'),
  diagnosticUpdate: z.string().min(5, 'Diagnostic requis'),
  treatmentPlan: z.string().min(5, 'Plan requis'),
  followUpActions: z.string().optional(),
});

type AssessmentData = z.infer<typeof assessmentSchema>;

interface PatientAssessment {
  id: string;
  roundId: string;
  patientId: string;
  diagnosticUpdate: string;
  treatmentPlan: string;
  followUpActions?: string;
  timestamp: string;
}

function ScheduleRoundDialog() {
  const [open, setOpen] = useState(false);

  const form = useForm<RoundFormData>({
    resolver: zodResolver(roundSchema),
  });

  async function onSubmit(data: RoundFormData) {
    try {
      await apiClient.post('/rounds/schedule', {
        ...data,
        status: 'scheduled',
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
          Programmer visite
        </Button>
      </DialogTrigger>
      <DialogContent className="max-w-md">
        <DialogHeader>
          <DialogTitle>Programmer une visite médicale</DialogTitle>
          <DialogDescription>Créer une nouvelle tournée</DialogDescription>
        </DialogHeader>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="wardId"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Service</FormLabel>
                  <FormControl>
                    <Input placeholder="ID service" {...field} />
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
                  <FormLabel>Médecin responsable</FormLabel>
                  <FormControl>
                    <Input placeholder="ID médecin" {...field} />
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

            <Button type="submit" className="w-full">
              Programmer
            </Button>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}

function PatientAssessmentDialog({ roundId }: { roundId: string }) {
  const [open, setOpen] = useState(false);

  const form = useForm<AssessmentData>({
    resolver: zodResolver(assessmentSchema),
    defaultValues: { roundId },
  });

  async function onSubmit(data: AssessmentData) {
    try {
      await apiClient.post(`/rounds/${roundId}/assessments`, data);
      form.reset();
      setOpen(false);
    } catch (error) {
      console.error('Erreur:', error);
    }
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button size="sm" variant="outline">
          <Plus className="h-4 w-4 mr-1" />
          Évaluation
        </Button>
      </DialogTrigger>
      <DialogContent className="max-w-2xl">
        <DialogHeader>
          <DialogTitle>Évaluation du patient</DialogTitle>
        </DialogHeader>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
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
              name="diagnosticUpdate"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Mise à jour diagnostic</FormLabel>
                  <FormControl>
                    <Textarea placeholder="Diagnostic actuel..." {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="treatmentPlan"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Plan de traitement</FormLabel>
                  <FormControl>
                    <Textarea placeholder="Détails du traitement..." {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="followUpActions"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Actions de suivi</FormLabel>
                  <FormControl>
                    <Textarea placeholder="Tests, consultations supplémentaires..." {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <Button type="submit" className="w-full">
              Enregistrer évaluation
            </Button>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}

function RoundCard({ round }: { round: MedicalRound }) {
  const statusConfig = {
    scheduled: { label: 'Programmée', icon: Clock, color: 'bg-blue-100 text-blue-800' },
    in_progress: { label: 'En cours', icon: Play, color: 'bg-purple-100 text-purple-800' },
    completed: { label: 'Terminée', icon: CheckCircle, color: 'bg-green-100 text-green-800' },
    cancelled: { label: 'Annulée', icon: AlertCircle, color: 'bg-red-100 text-red-800' },
  };

  const config = statusConfig[round.status as keyof typeof statusConfig] || statusConfig.scheduled;

  return (
    <Card>
      <CardHeader>
        <div className="flex justify-between items-start">
          <div>
            <CardTitle className="flex items-center gap-2">
              <Stethoscope className="h-5 w-5" />
              Tournée du Dr {round.medecinId}
            </CardTitle>
            <CardDescription>
              Service: {round.wardId}
            </CardDescription>
          </div>
          <Badge className={config.color}>{config.label}</Badge>
        </div>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="flex items-center gap-2 text-sm">
          <Clock className="h-4 w-4 text-gray-500" />
          {new Date(round.dateHeure).toLocaleString('fr-FR')}
        </div>

        {round.dureeMinutes && (
          <div className="text-sm">
            <p className="text-gray-600">Durée estimée: {round.dureeMinutes} minutes</p>
          </div>
        )}

        {round.patients && round.patients.length > 0 && (
          <div className="bg-gray-50 p-3 rounded">
            <p className="text-sm font-medium mb-2">Patients:</p>
            <div className="space-y-1">
              {round.patients.map((patientId, idx) => (
                <p key={idx} className="text-sm text-gray-600">• {patientId}</p>
              ))}
            </div>
          </div>
        )}

        {round.observations && (
          <div className="bg-blue-50 p-3 rounded">
            <p className="text-sm font-medium mb-1">Observations:</p>
            <p className="text-sm text-blue-800">{round.observations}</p>
          </div>
        )}

        <div className="flex gap-2 pt-2">
          {round.status === 'scheduled' && (
            <>
              <Button size="sm" variant="default" className="flex-1">
                <Play className="h-4 w-4 mr-2" />
                Démarrer
              </Button>
              <PatientAssessmentDialog roundId={round.id} />
            </>
          )}
          {round.status === 'in_progress' && (
            <>
              <PatientAssessmentDialog roundId={round.id} />
              <Button size="sm" variant="outline" className="flex-1">
                Terminer
              </Button>
            </>
          )}
        </div>
      </CardContent>
    </Card>
  );
}

function AssessmentCard({ assessment }: { assessment: PatientAssessment }) {
  return (
    <Card>
      <CardHeader className="pb-3">
        <CardTitle className="text-sm">Patient: {assessment.patientId}</CardTitle>
        <CardDescription>
          {new Date(assessment.timestamp).toLocaleString('fr-FR')}
        </CardDescription>
      </CardHeader>
      <CardContent className="space-y-3">
        <div>
          <p className="text-sm font-medium text-gray-700">Diagnostic:</p>
          <p className="text-sm text-gray-600">{assessment.diagnosticUpdate}</p>
        </div>
        <div>
          <p className="text-sm font-medium text-gray-700">Plan de traitement:</p>
          <p className="text-sm text-gray-600">{assessment.treatmentPlan}</p>
        </div>
        {assessment.followUpActions && (
          <div>
            <p className="text-sm font-medium text-gray-700">Suivi:</p>
            <p className="text-sm text-gray-600">{assessment.followUpActions}</p>
          </div>
        )}
      </CardContent>
    </Card>
  );
}

export default function MedicalRounds() {
  const { data: rounds } = useQuery({
    queryKey: ['medical-rounds'],
    queryFn: async () => {
      const res = await apiClient.get('/rounds');
      return res.data as MedicalRound[];
    },
  });

  const { data: assessments } = useQuery({
    queryKey: ['round-assessments'],
    queryFn: async () => {
      const res = await apiClient.get('/rounds/assessments');
      return res.data as PatientAssessment[];
    },
  });

  const scheduled = rounds?.filter(r => r.status === 'scheduled') || [];
  const inProgress = rounds?.filter(r => r.status === 'in_progress') || [];
  const completed = rounds?.filter(r => r.status === 'completed') || [];

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold tracking-tight">Tournées médicales</h1>
        <p className="text-gray-600 mt-2">Gestion et suivi des visites médicales par service</p>
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
            <div className="text-3xl font-bold text-green-600">{completed.length}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-600">Évaluations</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold">{assessments?.length || 0}</div>
          </CardContent>
        </Card>
      </div>

      <div className="grid grid-cols-3 gap-6">
        <div className="col-span-2 space-y-6">
          <div>
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-2xl font-bold">Tournées actives</h2>
              <ScheduleRoundDialog />
            </div>

            {scheduled.length === 0 && inProgress.length === 0 ? (
              <Card>
                <CardContent className="py-8 text-center text-gray-600">
                  <Clock className="h-12 w-12 mx-auto mb-4 opacity-50" />
                  <p>Aucune tournée active</p>
                </CardContent>
              </Card>
            ) : (
              <div className="grid gap-4">
                {inProgress.map(round => (
                  <RoundCard key={round.id} round={round} />
                ))}
                {scheduled.map(round => (
                  <RoundCard key={round.id} round={round} />
                ))}
              </div>
            )}
          </div>

          <div>
            <h2 className="text-2xl font-bold mb-4">Historique des tournées</h2>
            <div className="grid gap-4">
              {completed.slice(0, 5).map(round => (
                <RoundCard key={round.id} round={round} />
              ))}
            </div>
          </div>
        </div>

        <div>
          <h2 className="text-2xl font-bold mb-4">Évaluations récentes</h2>
          <div className="grid gap-4">
            {assessments && assessments.length > 0 ? (
              assessments.slice(0, 5).map(assessment => (
                <AssessmentCard key={assessment.id} assessment={assessment} />
              ))
            ) : (
              <Card>
                <CardContent className="py-8 text-center text-gray-600">
                  <AlertCircle className="h-8 w-8 mx-auto mb-4 opacity-50" />
                  <p className="text-sm">Aucune évaluation</p>
                </CardContent>
              </Card>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
