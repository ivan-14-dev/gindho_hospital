import { useCurrentUser } from '@/hooks/use-auth';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Users, Calendar, FileText, Activity } from 'lucide-react';

export function DashboardPage() {
  const { data: user } = useCurrentUser();

  const stats = [
    { title: 'Patients', value: '1,234', icon: Users, color: 'text-blue-600' },
    { title: 'Rendez-vous', value: '56', icon: Calendar, color: 'text-green-600' },
    { title: 'Consultations', value: '89', icon: FileText, color: 'text-purple-600' },
    { title: 'Urgences', value: '12', icon: Activity, color: 'text-red-600' },
  ];

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold">Dashboard</h1>
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
          <CardTitle>Activité récente</CardTitle>
        </CardHeader>
        <CardContent>
          <p className="text-muted-foreground">Aucune activité récente</p>
        </CardContent>
      </Card>
    </div>
  );
}