import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Checkbox } from '@/components/ui/checkbox';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { Plus, Activity, Pill, Clock, AlertCircle, CheckCircle, TrendingUp, Heart } from 'lucide-react';
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
import type { Patient, Admission } from '@/types';

const vitalSignSchema = z.object({
  patientId: z.string().min(1, 'Patient requis'),
  temperature: z.coerce.number().min(35).max(42),
  systolic: z.coerce.number().min(50).max(200),
  diastolic: z.coerce.number().min(30).max(150),
  heartRate: z.coerce.number().min(40).max(180),
  oxygenSaturation: z.coerce.number().min(50).max(100),
  respiratoryRate: z.coerce.number().min(8).max(50),
  notes: z.string().optional(),
});

type VitalSignData = z.infer<typeof vitalSignSchema>;

const careTaskSchema = z.object({
  patientId: z.string().min(1, 'Patient requis'),
  description: z.string().min(5, 'Description requise'),
  dueTime: z.string().min(1, 'Heure requise'),
  priority: z.enum(['low', 'medium', 'high']),
});

type CareTaskData = z.infer<typeof careTaskSchema>;

interface VitalSigns {
  id: string;
  patientId: string;
  temperature: number;
  systolic: number;
  diastolic: number;
  heartRate: number;
  oxygenSaturation: number;
  respiratoryRate: number;
  timestamp: string;
  notes?: string;
}

interface CareTask {
  id: string;
  patientId: string;
  description: string;
  dueTime: string;
  priority: 'low' | 'medium' | 'high';
  completed: boolean;
  completedAt?: string;
  completedBy?: string;
}

function VitalSignDialog() {
  const [open, setOpen] = useState(false);

  const form = useForm<VitalSignData>({
    resolver: zodResolver(vitalSignSchema),
  });

  async function onSubmit(data: VitalSignData) {
    try {
      await apiClient.post('/nursing/vital-signs', {
        ...data,
        timestamp: new Date().toISOString(),
      });
      form.reset();
      setOpen(false);
    } catch (error) {
      console.error('Erreur lors de l\'enregistrement:', error);
    }
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button>
          <Plus className="h-4 w-4 mr-2" />
          Enregistrer signes vitaux
        </Button>
      </DialogTrigger>
      <DialogContent className="max-w-2xl">
        <DialogHeader>
          <DialogTitle>Signes vitaux</DialogTitle>
          <DialogDescription>Enregistrer les signes vitaux du patient</DialogDescription>
        </DialogHeader>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
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

            <div className="grid grid-cols-3 gap-4">
              <FormField
                control={form.control}
                name="temperature"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Température (°C)</FormLabel>
                    <FormControl>
                      <Input type="number" step="0.1" min="35" max="42" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="heartRate"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Fréquence cardiaque (bpm)</FormLabel>
                    <FormControl>
                      <Input type="number" min="40" max="180" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="respiratoryRate"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Fréquence respiratoire</FormLabel>
                    <FormControl>
                      <Input type="number" min="8" max="50" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>

            <div className="grid grid-cols-3 gap-4">
              <FormField
                control={form.control}
                name="systolic"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Systolique (mmHg)</FormLabel>
                    <FormControl>
                      <Input type="number" min="50" max="200" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="diastolic"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Diastolique (mmHg)</FormLabel>
                    <FormControl>
                      <Input type="number" min="30" max="150" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="oxygenSaturation"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>SpO2 (%)</FormLabel>
                    <FormControl>
                      <Input type="number" min="50" max="100" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>

            <FormField
              control={form.control}
              name="notes"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Observations</FormLabel>
                  <FormControl>
                    <Textarea placeholder="Notes supplémentaires..." {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <Button type="submit" className="w-full">
              Enregistrer
            </Button>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}

function CareTaskDialog() {
  const [open, setOpen] = useState(false);

  const form = useForm<CareTaskData>({
    resolver: zodResolver(careTaskSchema),
    defaultValues: {
      priority: 'medium',
    },
  });

  async function onSubmit(data: CareTaskData) {
    try {
      await apiClient.post('/nursing/care-tasks', data);
      form.reset();
      setOpen(false);
    } catch (error) {
      console.error('Erreur lors de la création de la tâche:', error);
    }
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button variant="outline">
          <Plus className="h-4 w-4 mr-2" />
          Nouvelle tâche
        </Button>
      </DialogTrigger>
      <DialogContent className="max-w-md">
        <DialogHeader>
          <DialogTitle>Nouvelle tâche de soins</DialogTitle>
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
              name="description"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Description</FormLabel>
                  <FormControl>
                    <Textarea placeholder="Décrire la tâche..." {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="dueTime"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Heure limite</FormLabel>
                  <FormControl>
                    <Input type="time" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="priority"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Priorité</FormLabel>
                  <FormControl>
                    <select {...field} className="w-full border rounded p-2">
                      <option value="low">Basse</option>
                      <option value="medium">Moyenne</option>
                      <option value="high">Haute</option>
                    </select>
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <Button type="submit" className="w-full">
              Créer tâche
            </Button>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}

function VitalSignCard({ vitals }: { vitals: VitalSigns }) {
  const getStatusColor = (value: number, type: string) => {
    switch (type) {
      case 'temperature':
        return value > 38 ? 'text-red-600' : value < 36 ? 'text-blue-600' : 'text-green-600';
      case 'oxygenSaturation':
        return value < 94 ? 'text-red-600' : 'text-green-600';
      case 'heartRate':
        return value > 100 || value < 60 ? 'text-orange-600' : 'text-green-600';
      default:
        return 'text-gray-600';
    }
  };

  return (
    <Card>
      <CardHeader className="pb-3">
        <CardTitle className="text-sm">
          {new Date(vitals.timestamp).toLocaleTimeString('fr-FR')}
        </CardTitle>
      </CardHeader>
      <CardContent>
        <div className="grid grid-cols-3 gap-4">
          <div>
            <p className="text-xs text-gray-600">Température</p>
            <p className={`text-lg font-bold ${getStatusColor(vitals.temperature, 'temperature')}`}>
              {vitals.temperature}°C
            </p>
          </div>
          <div>
            <p className="text-xs text-gray-600">Fréquence cardiaque</p>
            <p className={`text-lg font-bold ${getStatusColor(vitals.heartRate, 'heartRate')}`}>
              {vitals.heartRate} bpm
            </p>
          </div>
          <div>
            <p className="text-xs text-gray-600">SpO2</p>
            <p className={`text-lg font-bold ${getStatusColor(vitals.oxygenSaturation, 'oxygenSaturation')}`}>
              {vitals.oxygenSaturation}%
            </p>
          </div>
        </div>
        <div className="grid grid-cols-3 gap-4 mt-3 pt-3 border-t">
          <div>
            <p className="text-xs text-gray-600">TA</p>
            <p className="font-medium">{vitals.systolic}/{vitals.diastolic}</p>
          </div>
          <div>
            <p className="text-xs text-gray-600">Respiration</p>
            <p className="font-medium">{vitals.respiratoryRate}/min</p>
          </div>
        </div>
      </CardContent>
    </Card>
  );
}

function CareTaskItem({ task, onToggle }: { task: CareTask; onToggle: (id: string) => void }) {
  const priorityConfig = {
    low: 'bg-blue-100 text-blue-800',
    medium: 'bg-yellow-100 text-yellow-800',
    high: 'bg-red-100 text-red-800',
  };

  return (
    <Card className={task.completed ? 'opacity-60' : ''}>
      <CardContent className="pt-6">
        <div className="flex items-start gap-4">
          <Checkbox
            checked={task.completed}
            onCheckedChange={() => onToggle(task.id)}
            className="mt-1"
          />
          <div className="flex-1">
            <div className="flex items-start justify-between gap-4">
              <div>
                <p className={`font-medium ${task.completed ? 'line-through text-gray-500' : ''}`}>
                  {task.description}
                </p>
                <p className="text-sm text-gray-600 mt-1 flex items-center gap-1">
                  <Clock className="h-4 w-4" />
                  {task.dueTime}
                </p>
              </div>
              <div className="flex items-center gap-2">
                <Badge className={priorityConfig[task.priority]}>
                  {task.priority === 'low' ? 'Basse' : task.priority === 'medium' ? 'Moyenne' : 'Haute'}
                </Badge>
                {task.completed && <CheckCircle className="h-5 w-5 text-green-600" />}
              </div>
            </div>
          </div>
        </div>
      </CardContent>
    </Card>
  );
}

export default function NursingCare() {
  const { data: hospitalizations } = useQuery({
    queryKey: ['nursing-patients'],
    queryFn: async () => {
      const res = await apiClient.get('/nursing/assigned-patients');
      return res.data;
    },
  });

  const { data: vitalSigns } = useQuery({
    queryKey: ['vital-signs'],
    queryFn: async () => {
      const res = await apiClient.get('/nursing/vital-signs');
      return res.data as VitalSigns[];
    },
  });

  const { data: careTasks } = useQuery({
    queryKey: ['care-tasks'],
    queryFn: async () => {
      const res = await apiClient.get('/nursing/care-tasks');
      return res.data as CareTask[];
    },
  });

  const assignedPatients = hospitalizations || [];
  const activeTasks = careTasks?.filter(t => !t.completed) || [];
  const completedTodayCount = careTasks?.filter(t => t.completed).length || 0;

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold tracking-tight">Soins infirmiers</h1>
        <p className="text-gray-600 mt-2">Gestion des plans de soins et suivi des patients</p>
      </div>

      <div className="grid grid-cols-4 gap-4">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-600">Patients assignés</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold">{assignedPatients.length}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-600">Tâches actives</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-orange-600">{activeTasks.length}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-600">Tâches complétées</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-green-600">{completedTodayCount}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-600">Signes vitaux</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold">{vitalSigns?.length || 0}</div>
          </CardContent>
        </Card>
      </div>

      <div className="grid grid-cols-3 gap-6">
        <div className="col-span-2 space-y-6">
          <div>
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-2xl font-bold">Tâches de soins</h2>
              <CareTaskDialog />
            </div>

            {activeTasks.length === 0 ? (
              <Card>
                <CardContent className="py-8 text-center text-gray-600">
                  <CheckCircle className="h-12 w-12 mx-auto mb-4 opacity-50" />
                  <p>Aucune tâche active</p>
                </CardContent>
              </Card>
            ) : (
              <div className="grid gap-3">
                {activeTasks.map(task => (
                  <CareTaskItem
                    key={task.id}
                    task={task}
                    onToggle={(id) => {
                      // Handle toggle
                    }}
                  />
                ))}
              </div>
            )}
          </div>

          <div>
            <h2 className="text-2xl font-bold mb-4">Patients assignés</h2>
            <div className="grid gap-4">
              {assignedPatients.map((patient: any) => (
                <Card key={patient.id}>
                  <CardHeader>
                    <CardTitle>{patient.nom} {patient.prenom}</CardTitle>
                    <CardDescription>Patient ID: {patient.id}</CardDescription>
                  </CardHeader>
                  <CardContent>
                    <div className="text-sm space-y-1">
                      <p>Groupe sanguin: {patient.groupeSanguin}</p>
                      <p>Allergies: {patient.allergie?.join(', ') || 'Aucune'}</p>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          </div>
        </div>

        <div className="space-y-6">
          <div>
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-xl font-bold">Signes vitaux</h2>
              <VitalSignDialog />
            </div>

            {vitalSigns && vitalSigns.length > 0 ? (
              <div className="grid gap-4">
                {vitalSigns.slice(0, 5).map(vital => (
                  <VitalSignCard key={vital.id} vitals={vital} />
                ))}
              </div>
            ) : (
              <Card>
                <CardContent className="py-8 text-center text-gray-600">
                  <Heart className="h-8 w-8 mx-auto mb-4 opacity-50" />
                  <p className="text-sm">Aucun enregistrement</p>
                </CardContent>
              </Card>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
