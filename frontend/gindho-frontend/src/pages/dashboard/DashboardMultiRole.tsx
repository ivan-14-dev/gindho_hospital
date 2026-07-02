import { useDashboardStats, useAuth, useQualityIncidents, useInventoryAlerts } from '@/hooks/use-api';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';
import { Users, AlertTriangle, AlertCircle } from 'lucide-react';

function AdminDashboard() {
  const { isLoading } = useDashboardStats();
  const { data: incidents } = useQualityIncidents();
  const { data: alerts } = useInventoryAlerts();

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-96">
        <div className="animate-spin w-8 h-8 border-4 border-primary border-t-transparent rounded-full" />
      </div>
    );
  }

  const chartData = [
    { name: 'Lun', patients: 120, admissions: 24 },
    { name: 'Mar', patients: 150, admissions: 32 },
    { name: 'Mer', patients: 140, admissions: 28 },
    { name: 'Jeu', patients: 160, admissions: 35 },
    { name: 'Ven', patients: 145, admissions: 30 },
    { name: 'Sam', patients: 130, admissions: 25 },
    { name: 'Dim', patients: 110, admissions: 20 },
  ];

  const occupancyData = [
    { name: 'Occupés', value: 280 },
    { name: 'Disponibles', value: 120 },
  ];

  const COLORS = ['#ef4444', '#22c55e'];

  return (
    <div className="space-y-6">
      {/* KPI Row */}
      <div className="grid grid-cols-4 gap-4">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Patients actifs</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold">892</div>
            <p className="text-xs text-muted-foreground mt-2">+12% ce mois</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Lits disponibles</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-green-600">120</div>
            <p className="text-xs text-muted-foreground mt-2">Taux: 70%</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Revenus (ce mois)</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-blue-600">245K €</div>
            <p className="text-xs text-muted-foreground mt-2">+8% vs mois dernier</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Incidents ouverts</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-red-600">{incidents?.filter((i) => i.status === 'open').length || 0}</div>
            <p className="text-xs text-muted-foreground mt-2">À traiter</p>
          </CardContent>
        </Card>
      </div>

      {/* Charts */}
      <div className="grid grid-cols-2 gap-4">
        <Card>
          <CardHeader>
            <CardTitle>Patients et admissions (7 jours)</CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Line type="monotone" dataKey="patients" stroke="#3b82f6" />
                <Line type="monotone" dataKey="admissions" stroke="#ef4444" />
              </LineChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Taux d&apos;occupancy</CardTitle>
          </CardHeader>
          <CardContent className="flex items-center justify-center">
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={occupancyData}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  label={({ name, value }) => `${name}: ${value}`}
                  outerRadius={80}
                  fill="#8884d8"
                  dataKey="value"
                >
                  {COLORS.map((color, index) => (
                    <Cell key={`cell-${index}`} fill={color} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>
      </div>

      {/* Alerts & Recent */}
      <div className="grid grid-cols-2 gap-4">
        <Card>
          <CardHeader>
            <CardTitle className="text-lg">Alertes système</CardTitle>
          </CardHeader>
          <CardContent className="space-y-2">
            {incidents?.filter((i) => i.severite === 'critical').slice(0, 3).map((incident) => (
              <div key={incident.id} className="flex items-center gap-3 p-2 bg-red-50 rounded-lg">
                <AlertTriangle className="h-4 w-4 text-red-600" />
                <div className="flex-1 text-sm">
                  <p className="font-medium">{incident.titre}</p>
                  <p className="text-muted-foreground text-xs">Sévérité: {incident.severite}</p>
                </div>
              </div>
            ))}
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="text-lg">Stock faible</CardTitle>
          </CardHeader>
          <CardContent className="space-y-2">
            {alerts?.rupture?.slice(0, 3).map((item: any) => (
              <div key={item.id} className="flex items-center gap-3 p-2 bg-yellow-50 rounded-lg">
                <AlertCircle className="h-4 w-4 text-yellow-600" />
                <div className="flex-1 text-sm">
                  <p className="font-medium">{item.nom}</p>
                  <p className="text-muted-foreground text-xs">Quantité: {item.quantite}</p>
                </div>
              </div>
            ))}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}

function DoctorDashboard() {
  return (
    <div className="space-y-6">
      {/* KPI Row */}
      <div className="grid grid-cols-4 gap-4">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Patients aujourd&apos;hui</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold">15</div>
            <p className="text-xs text-muted-foreground mt-2">6 consultations</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">RDV à venir</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-blue-600">8</div>
            <p className="text-xs text-muted-foreground mt-2">Prochains 7 jours</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Résultats en attente</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-yellow-600">3</div>
            <p className="text-xs text-muted-foreground mt-2">Tests/Imagerie</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Interventions programmées</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-orange-600">2</div>
            <p className="text-xs text-muted-foreground mt-2">Cette semaine</p>
          </CardContent>
        </Card>
      </div>

      {/* Patient List */}
      <Card>
        <CardHeader>
          <CardTitle>Mes patients hospitalisés</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-3">
            {[1, 2, 3].map((i) => (
              <div key={i} className="flex items-center justify-between p-3 border rounded-lg hover:bg-accent/50">
                <div className="flex items-center gap-3">
                  <div className="h-10 w-10 rounded-full bg-primary/10 flex items-center justify-center">
                    <Users className="h-5 w-5 text-primary" />
                  </div>
                  <div>
                    <p className="font-medium">Patient {i}</p>
                    <p className="text-sm text-muted-foreground">Lit {10 + i}</p>
                  </div>
                </div>
                <Button size="sm" variant="outline">Détails</Button>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}

function NurseDashboard() {
  return (
    <div className="space-y-6">
      {/* KPI Row */}
      <div className="grid grid-cols-4 gap-4">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Patients à suivre</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold">12</div>
            <p className="text-xs text-muted-foreground mt-2">Assignés</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Médicaments à administrer</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-blue-600">18</div>
            <p className="text-xs text-muted-foreground mt-2">Aujourd&apos;hui</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Soins planifiés</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-green-600">8</div>
            <p className="text-xs text-muted-foreground mt-2">À venir</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Alertes vitales</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-red-600">1</div>
            <p className="text-xs text-muted-foreground mt-2">À vérifier</p>
          </CardContent>
        </Card>
      </div>

      {/* Tasks */}
      <Card>
        <CardHeader>
          <CardTitle>Tâches prioritaires</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-3">
            {[
              { title: 'Mesurer tensions vitales', priority: 'high', time: '14:00' },
              { title: 'Administrer injection', priority: 'high', time: '15:30' },
              { title: 'Changer pansement', priority: 'medium', time: '16:00' },
            ].map((task, i) => (
              <div key={i} className="flex items-center justify-between p-3 border rounded-lg">
                <div className="flex items-center gap-3">
                  <input type="checkbox" className="h-5 w-5" />
                  <div>
                    <p className="font-medium">{task.title}</p>
                    <p className="text-sm text-muted-foreground">{task.time}</p>
                  </div>
                </div>
                <Badge variant={task.priority === 'high' ? 'destructive' : 'secondary'}>
                  {task.priority === 'high' ? 'Urgent' : 'Normal'}
                </Badge>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}

function PatientDashboard() {
  return (
    <div className="space-y-6">
      {/* Health Status */}
      <div className="grid grid-cols-4 gap-4">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Prochains RDV</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold">2</div>
            <p className="text-xs text-muted-foreground mt-2">Programmés</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Résultats disponibles</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-green-600">1</div>
            <p className="text-xs text-muted-foreground mt-2">À consulter</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Prescriptions actives</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-blue-600">4</div>
            <p className="text-xs text-muted-foreground mt-2">En cours</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Factures</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-yellow-600">1</div>
            <p className="text-xs text-muted-foreground mt-2">En attente</p>
          </CardContent>
        </Card>
      </div>

      {/* Important Info */}
      <Card>
        <CardHeader>
          <CardTitle>Vos rendez-vous</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-3">
            {[1, 2].map((i) => (
              <div key={i} className="flex items-center justify-between p-3 border rounded-lg">
                <div>
                  <p className="font-medium">Consultation Dr. Martin</p>
                  <p className="text-sm text-muted-foreground">Lun 15 Jul 2024, 14:30</p>
                </div>
                <Button size="sm">Détails</Button>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}

export function DashboardMultiRole() {
  const { data: user } = useAuth();

  if (!user) {
    return (
      <div className="flex items-center justify-center h-96">
        <div className="animate-spin w-8 h-8 border-4 border-primary border-t-transparent rounded-full" />
      </div>
    );
  }

  const roleContent = {
    ADMIN: <AdminDashboard />,
    DOCTOR: <DoctorDashboard />,
    NURSE: <NurseDashboard />,
    PATIENT: <PatientDashboard />,
    HR: <AdminDashboard />,
    PHARMACIST: <AdminDashboard />,
    RADIOLOGIST: <AdminDashboard />,
    RECEPTIONIST: <AdminDashboard />,
    ACCOUNTANT: <AdminDashboard />,
    LABORATORY_TECHNICIAN: <AdminDashboard />,
  };

  const content = roleContent[user.role as keyof typeof roleContent] || <AdminDashboard />;

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Tableau de bord</h1>
          <p className="text-muted-foreground">Bienvenue, {user.email}</p>
        </div>
        <Badge className="text-base px-3 py-1">{user.role}</Badge>
      </div>

      {content}
    </div>
  );
}
