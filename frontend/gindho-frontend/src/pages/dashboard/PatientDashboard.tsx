import { useCurrentUser } from '@/hooks/use-auth';
import { useAppointments } from '@/hooks/use-appointments';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Calendar, FileText, FlaskRound, Pill } from 'lucide-react';
import { format } from 'date-fns';
import { fr } from 'date-fns/locale';

export function PatientDashboard() {
  const { data: user } = useCurrentUser();
  const { data: appointments } = useAppointments();

  const upcomingAppointments = appointments?.filter(
    (apt) => new Date(apt.dateDebut) > new Date() && apt.statut !== 'ANNULE'
  ).slice(0, 5);

  const stats = [
    { title: 'Rendez-vous à venir', value: upcomingAppointments?.length || 0, icon: Calendar, color: 'text-blue-600' },
    { title: 'Consultations', value: '12', icon: FileText, color: 'text-green-600' },
    { title: 'Analyses', value: '3', icon: FlaskRound, color: 'text-purple-600' },
    { title: 'Prescriptions', value: '2', icon: Pill, color: 'text-orange-600' },
  ];

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold">Mon Espace Patient</h1>
        <p className="text-muted-foreground">
          Bienvenue, {user?.prenom} {user?.nom}
        </p>
      </div>

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        {stats.map((stat) => {
          const Icon = stat.icon;
          return (
            <Card key={stat.title}>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">{stat.title}</CardTitle>
                <Icon className={`h-4 w-4 ${stat.color}`} />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">{stat.value}</div>
              </CardContent>
            </Card>
          );
        })}
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Prochains rendez-vous</CardTitle>
        </CardHeader>
        <CardContent>
          {upcomingAppointments && upcomingAppointments.length > 0 ? (
            <div className="space-y-4">
              {upcomingAppointments.map((apt) => (
                <div key={apt.id} className="flex items-center justify-between p-4 border rounded-lg">
                  <div className="flex items-center gap-4">
                    <Calendar className="h-8 w-8 text-primary" />
                    <div>
                      <p className="font-medium">
                        {format(new Date(apt.dateDebut), 'dd MMMM yyyy', { locale: fr })}
                      </p>
                      <p className="text-sm text-muted-foreground">
                        {format(new Date(apt.dateDebut), 'HH:mm')} - Dr. {apt.medecinId}
                      </p>
                    </div>
                  </div>
                  <Badge variant={apt.statut === 'CONFIRME' ? 'default' : 'secondary'}>
                    {apt.statut}
                  </Badge>
                </div>
              ))}
            </div>
          ) : (
            <p className="text-center text-muted-foreground py-8">Aucun rendez-vous à venir</p>
          )}
        </CardContent>
      </Card>
    </div>
  );
}