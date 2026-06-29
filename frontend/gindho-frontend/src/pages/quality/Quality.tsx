import { useState } from 'react';
import { useQualityIncidents, useReportIncident } from '@/hooks/use-api';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { AlertTriangle, Plus, CheckCircle, AlertCircle as AlertIcon, Zap } from 'lucide-react';
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
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import type { QualityIncident } from '@/types';

const incidentSchema = z.object({
  titre: z.string().min(5, 'Titre requis'),
  description: z.string().min(10, 'Description requise'),
  type: z.enum(['PATIENT_SAFETY', 'INFECTION', 'MEDICATION_ERROR', 'EQUIPMENT_FAILURE', 'STAFFING', 'COMMUNICATION', 'OTHER']),
  severite: z.enum(['low', 'medium', 'high', 'critical']),
  departement: z.string().optional(),
  personnesImpliquees: z.string().optional(),
  actionsCorrectives: z.string().optional(),
});

type IncidentFormData = z.infer<typeof incidentSchema>;

function IncidentDialog() {
  const [open, setOpen] = useState(false);
  const reportIncident = useReportIncident();

  const form = useForm<IncidentFormData>({
    resolver: zodResolver(incidentSchema),
  });

  async function onSubmit(data: IncidentFormData) {
    try {
      await reportIncident.mutateAsync({
        ...data,
        dateIncident: new Date().toISOString(),
        status: 'open',
      });
      form.reset();
      setOpen(false);
    } catch (error) {
      console.error('Erreur lors du signalement:', error);
    }
  }

  const incidentTypes = [
    { value: 'PATIENT_SAFETY', label: 'Sécurité du patient' },
    { value: 'INFECTION', label: 'Infection' },
    { value: 'MEDICATION_ERROR', label: 'Erreur médicamenteuse' },
    { value: 'EQUIPMENT_FAILURE', label: 'Défaillance d\'équipement' },
    { value: 'STAFFING', label: 'Ressources humaines' },
    { value: 'COMMUNICATION', label: 'Communication' },
    { value: 'OTHER', label: 'Autre' },
  ];

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button>
          <Plus className="h-4 w-4 mr-2" />
          Signaler un incident
        </Button>
      </DialogTrigger>
      <DialogContent className="max-w-2xl">
        <DialogHeader>
          <DialogTitle>Signaler un incident qualité</DialogTitle>
          <DialogDescription>Documenter un incident pour amélioration continue</DialogDescription>
        </DialogHeader>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
            <FormField
              control={form.control}
              name="titre"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Titre</FormLabel>
                  <FormControl>
                    <Input placeholder="Résumé de l'incident" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <div className="grid grid-cols-2 gap-4">
              <FormField
                control={form.control}
                name="type"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Type d&apos;incident</FormLabel>
                    <Select onValueChange={field.onChange}>
                      <FormControl>
                        <SelectTrigger>
                          <SelectValue placeholder="Sélectionner" />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        {incidentTypes.map((t) => (
                          <SelectItem key={t.value} value={t.value}>
                            {t.label}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="severite"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Sévérité</FormLabel>
                    <Select onValueChange={field.onChange}>
                      <FormControl>
                        <SelectTrigger>
                          <SelectValue placeholder="Sélectionner" />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        <SelectItem value="low">Faible</SelectItem>
                        <SelectItem value="medium">Moyen</SelectItem>
                        <SelectItem value="high">Élevé</SelectItem>
                        <SelectItem value="critical">Critique</SelectItem>
                      </SelectContent>
                    </Select>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>

            <FormField
              control={form.control}
              name="description"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Description détaillée</FormLabel>
                  <FormControl>
                    <Textarea placeholder="Décrivez l'incident en détail..." rows={4} {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <div className="grid grid-cols-2 gap-4">
              <FormField
                control={form.control}
                name="departement"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Département</FormLabel>
                    <FormControl>
                      <Input placeholder="Département" {...field} />
                    </FormControl>
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="personnesImpliquees"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Personnes impliquées</FormLabel>
                    <FormControl>
                      <Input placeholder="Noms/IDs" {...field} />
                    </FormControl>
                  </FormItem>
                )}
              />
            </div>

            <FormField
              control={form.control}
              name="actionsCorrectives"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Actions correctives</FormLabel>
                  <FormControl>
                    <Textarea placeholder="Actions recommandées..." rows={3} {...field} />
                  </FormControl>
                </FormItem>
              )}
            />

            <div className="flex gap-2 justify-end">
              <Button type="button" variant="outline" onClick={() => setOpen(false)}>
                Annuler
              </Button>
              <Button type="submit" disabled={reportIncident.isPending}>
                {reportIncident.isPending ? 'Signalement...' : 'Signaler'}
              </Button>
            </div>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}

function IncidentCard({ incident }: { incident: QualityIncident }) {
  const typeConfig = {
    PATIENT_SAFETY: { label: 'Sécurité patient', color: 'destructive' },
    INFECTION: { label: 'Infection', color: 'destructive' },
    MEDICATION_ERROR: { label: 'Erreur médicamenteuse', color: 'destructive' },
    EQUIPMENT_FAILURE: { label: 'Équipement', color: 'secondary' },
    STAFFING: { label: 'RH', color: 'secondary' },
    COMMUNICATION: { label: 'Communication', color: 'secondary' },
    OTHER: { label: 'Autre', color: 'secondary' },
  };

  const severityConfig = {
    low: { label: 'Faible', color: 'bg-blue-100 text-blue-800', icon: AlertIcon },
    medium: { label: 'Moyen', color: 'bg-yellow-100 text-yellow-800', icon: AlertTriangle },
    high: { label: 'Élevé', color: 'bg-orange-100 text-orange-800', icon: AlertTriangle },
    critical: { label: 'Critique', color: 'bg-red-100 text-red-800', icon: Zap },
  };

  const statusConfig = {
    open: { label: 'Ouvert', color: 'bg-red-100 text-red-800', icon: AlertIcon },
    in_investigation: { label: 'Enquête', color: 'bg-yellow-100 text-yellow-800', icon: AlertTriangle },
    resolved: { label: 'Résolu', color: 'bg-green-100 text-green-800', icon: CheckCircle },
    closed: { label: 'Fermé', color: 'bg-gray-100 text-gray-800', icon: CheckCircle },
  };

  const typeConfig_ = typeConfig[incident.type as keyof typeof typeConfig];
  const severityConfig_ = severityConfig[incident.severite as keyof typeof severityConfig];
  const statusConfig_ = statusConfig[incident.status as keyof typeof statusConfig];

  const SeverityIcon = severityConfig_.icon;
  const StatusIcon = statusConfig_.icon;

  return (
    <Card>
      <CardHeader className="pb-3">
        <div className="flex items-center justify-between gap-4">
          <div className="flex-1">
            <CardTitle>{incident.titre}</CardTitle>
            <CardDescription className="mt-1">{incident.dateIncident}</CardDescription>
          </div>
          <div className="flex gap-2">
            <Badge variant={typeConfig_.color as any}>{typeConfig_.label}</Badge>
            <Badge className={severityConfig_.color}>
              <SeverityIcon className="h-3 w-3 mr-1" />
              {severityConfig_.label}
            </Badge>
            <Badge className={statusConfig_.color}>
              <StatusIcon className="h-3 w-3 mr-1" />
              {statusConfig_.label}
            </Badge>
          </div>
        </div>
      </CardHeader>
      <CardContent className="space-y-4">
        <div>
          <p className="text-sm font-medium mb-1">Description</p>
          <p className="text-sm text-muted-foreground">{incident.description}</p>
        </div>

        {incident.departement && (
          <div>
            <p className="text-sm font-medium mb-1">Département</p>
            <p className="text-sm">{incident.departement}</p>
          </div>
        )}

        {incident.personnesImpliquees && incident.personnesImpliquees.length > 0 && (
          <div>
            <p className="text-sm font-medium mb-1">Personnes impliquées</p>
            <p className="text-sm">{incident.personnesImpliquees.join(', ')}</p>
          </div>
        )}

        {incident.actionsCorrectives && (
          <div>
            <p className="text-sm font-medium mb-1">Actions correctives</p>
            <p className="text-sm text-muted-foreground">{incident.actionsCorrectives}</p>
          </div>
        )}

        {incident.dateResolution && (
          <div className="text-xs text-muted-foreground">
            Résolu le: {new Date(incident.dateResolution).toLocaleDateString('fr-FR')}
          </div>
        )}
      </CardContent>
    </Card>
  );
}

export function QualityPage() {
  const { data: incidents, isLoading } = useQualityIncidents();
  const [statusFilter, setStatusFilter] = useState<string>('');

  const filteredIncidents = incidents?.filter((i) => !statusFilter || i.status === statusFilter) || [];

  const stats = {
    openIncidents: incidents?.filter((i) => i.status === 'open').length || 0,
    inInvestigation: incidents?.filter((i) => i.status === 'in_investigation').length || 0,
    criticalIncidents: incidents?.filter((i) => i.severite === 'critical').length || 0,
    resolved: incidents?.filter((i) => i.status === 'resolved').length || 0,
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Qualité & Incidents</h1>
          <p className="text-muted-foreground">Gestion des incidents et amélioration continue</p>
        </div>
        <IncidentDialog />
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-4 gap-4">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Incidents ouverts</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-red-600">{stats.openIncidents}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">En enquête</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-yellow-600">{stats.inInvestigation}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Critiques</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-orange-600">{stats.criticalIncidents}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Résolus</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-green-600">{stats.resolved}</div>
          </CardContent>
        </Card>
      </div>

      {/* Filter */}
      <div className="flex gap-2">
        <Button
          variant={statusFilter === '' ? 'default' : 'outline'}
          onClick={() => setStatusFilter('')}
        >
          Tous
        </Button>
        {['open', 'in_investigation', 'resolved', 'closed'].map((status) => (
          <Button
            key={status}
            variant={statusFilter === status ? 'default' : 'outline'}
            onClick={() => setStatusFilter(status)}
          >
            {status === 'open' ? 'Ouverts' : status === 'in_investigation' ? 'Enquête' : status === 'resolved' ? 'Résolus' : 'Fermés'}
          </Button>
        ))}
      </div>

      {/* Incidents List */}
      {isLoading ? (
        <Card>
          <CardContent className="flex items-center justify-center h-64">
            <div className="animate-spin w-8 h-8 border-4 border-primary border-t-transparent rounded-full" />
          </CardContent>
        </Card>
      ) : filteredIncidents.length > 0 ? (
        <div className="grid gap-4">
          {filteredIncidents.map((incident) => (
            <IncidentCard key={incident.id} incident={incident} />
          ))}
        </div>
      ) : (
        <Card>
          <CardContent className="flex flex-col items-center justify-center h-64 gap-4">
            <CheckCircle className="h-12 w-12 text-green-500 opacity-50" />
            <p className="text-muted-foreground">Aucun incident à afficher</p>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
