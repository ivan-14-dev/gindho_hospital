import { useState } from 'react';
import { useDoctors, useEmployees, useCheckIn } from '@/hooks/use-api';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Clock, Users, AlertCircle, LogIn, PhoneOff } from 'lucide-react';
import type { Employee, User } from '@/types';

function EmployeeCard({ employee }: { employee: Employee }) {
  const [isCheckingIn, setIsCheckingIn] = useState(false);
  const checkIn = useCheckIn();

  const handleCheckIn = async () => {
    setIsCheckingIn(true);
    try {
      await checkIn.mutateAsync(employee.id);
    } catch (error) {
      console.error('Erreur lors du pointage:', error);
    } finally {
      setIsCheckingIn(false);
    }
  };

  const statusConfig = {
    active: { label: 'Actif', color: 'bg-green-100 text-green-800' },
    on_leave: { label: 'En congé', color: 'bg-yellow-100 text-yellow-800' },
    inactive: { label: 'Inactif', color: 'bg-red-100 text-red-800' },
    retired: { label: 'Retraité', color: 'bg-gray-100 text-gray-800' },
  };

  const config = statusConfig[employee.status as keyof typeof statusConfig] || statusConfig.active;

  return (
    <Card>
      <CardHeader className="pb-3">
        <div className="flex items-center justify-between">
          <div>
            <CardTitle>{employee.prenom} {employee.nom}</CardTitle>
            <CardDescription>{employee.poste}</CardDescription>
          </div>
          <Badge className={config.color}>{config.label}</Badge>
        </div>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="grid grid-cols-2 gap-4 text-sm">
          <div>
            <p className="text-muted-foreground">Département</p>
            <p className="font-medium">{employee.departement}</p>
          </div>
          <div>
            <p className="text-muted-foreground">Type de contrat</p>
            <p className="font-medium">{employee.typeContrat}</p>
          </div>
          <div className="col-span-2">
            <p className="text-muted-foreground">Email</p>
            <p className="font-medium text-sm">{employee.email}</p>
          </div>
          <div className="col-span-2">
            <p className="text-muted-foreground">Téléphone</p>
            <p className="font-medium text-sm">{employee.telephone}</p>
          </div>
        </div>

        {employee.qualifications && employee.qualifications.length > 0 && (
          <div>
            <p className="text-sm font-medium mb-2">Qualifications</p>
            <div className="flex flex-wrap gap-2">
              {employee.qualifications.map((qual) => (
                <Badge key={qual} variant="secondary" className="text-xs">
                  {qual}
                </Badge>
              ))}
            </div>
          </div>
        )}

        <div className="pt-2 border-t">
          <Button
            className="w-full"
            onClick={handleCheckIn}
            disabled={isCheckingIn || employee.status !== 'active'}
          >
            <LogIn className="h-4 w-4 mr-2" />
            {isCheckingIn ? 'Pointage...' : 'Pointer présence'}
          </Button>
        </div>
      </CardContent>
    </Card>
  );
}

function DoctorCard({ doctor }: { doctor: User }) {
  return (
    <Card>
      <CardHeader className="pb-3">
        <div className="flex items-center justify-between">
          <div>
            <CardTitle>{doctor.prenom} {doctor.nom}</CardTitle>
            <CardDescription>{doctor.specialite || doctor.department}</CardDescription>
          </div>
          <Badge>{doctor.role}</Badge>
        </div>
      </CardHeader>
      <CardContent className="space-y-3 text-sm">
        <div>
          <p className="text-muted-foreground">Email</p>
          <p className="font-medium">{doctor.email}</p>
        </div>
        <div>
          <p className="text-muted-foreground">Téléphone</p>
          <p className="font-medium">{doctor.telephone}</p>
        </div>
        {doctor.numeroLicence && (
          <div>
            <p className="text-muted-foreground">Numéro de licence</p>
            <p className="font-medium">{doctor.numeroLicence}</p>
          </div>
        )}
        <Button className="w-full mt-2" variant="outline" size="sm">
          Voir profil
        </Button>
      </CardContent>
    </Card>
  );
}

export function HRManagementPage() {
  const [search, setSearch] = useState('');
  const [tabValue, setTabValue] = useState('employees');
  const { data: employees, isLoading: employeesLoading } = useEmployees();
  const { data: doctors, isLoading: doctorsLoading } = useDoctors();

  const filteredEmployees = employees?.filter((emp) =>
    emp.nom.toLowerCase().includes(search.toLowerCase()) ||
    emp.prenom.toLowerCase().includes(search.toLowerCase()) ||
    emp.poste?.toLowerCase().includes(search.toLowerCase())
  ) || [];

  const filteredDoctors = doctors?.filter?.((doc) =>
    doc.nom.toLowerCase().includes(search.toLowerCase()) ||
    doc.prenom.toLowerCase().includes(search.toLowerCase())
  ) || (Array.isArray(doctors?.content) ? doctors.content.filter((doc: any) =>
    doc.nom.toLowerCase().includes(search.toLowerCase())
  ) : []);

  const stats = {
    totalEmployees: employees?.length || 0,
    activeEmployees: employees?.filter((e) => e.status === 'active').length || 0,
    onLeave: employees?.filter((e) => e.status === 'on_leave').length || 0,
    totalDoctors: Array.isArray(doctors?.content) ? doctors.content.length : (doctors?.length || 0),
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Gestion des Ressources Humaines</h1>
          <p className="text-muted-foreground">Gestion du personnel et des médecins</p>
        </div>
        <Button>+ Ajouter personnel</Button>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-4 gap-4">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Total personnel</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold">{stats.totalEmployees}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Actifs</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-green-600">{stats.activeEmployees}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">En congé</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-yellow-600">{stats.onLeave}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Médecins</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-blue-600">{stats.totalDoctors}</div>
          </CardContent>
        </Card>
      </div>

      <Tabs value={tabValue} onValueChange={setTabValue} className="w-full">
        <TabsList className="grid w-full grid-cols-2">
          <TabsTrigger value="employees">Personnel ({stats.totalEmployees})</TabsTrigger>
          <TabsTrigger value="doctors">Médecins ({stats.totalDoctors})</TabsTrigger>
        </TabsList>

        <TabsContent value="employees" className="space-y-4">
          <Card>
            <CardHeader>
              <Input
                placeholder="Rechercher un employé..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
              />
            </CardHeader>
          </Card>

          {employeesLoading ? (
            <Card>
              <CardContent className="flex items-center justify-center h-64">
                <div className="animate-spin w-8 h-8 border-4 border-primary border-t-transparent rounded-full" />
              </CardContent>
            </Card>
          ) : filteredEmployees.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {filteredEmployees.map((emp) => (
                <EmployeeCard key={emp.id} employee={emp} />
              ))}
            </div>
          ) : (
            <Card>
              <CardContent className="flex flex-col items-center justify-center h-64 gap-4">
                <AlertCircle className="h-12 w-12 text-muted-foreground opacity-50" />
                <p className="text-muted-foreground">Aucun employé trouvé</p>
              </CardContent>
            </Card>
          )}
        </TabsContent>

        <TabsContent value="doctors" className="space-y-4">
          <Card>
            <CardHeader>
              <Input
                placeholder="Rechercher un médecin..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
              />
            </CardHeader>
          </Card>

          {doctorsLoading ? (
            <Card>
              <CardContent className="flex items-center justify-center h-64">
                <div className="animate-spin w-8 h-8 border-4 border-primary border-t-transparent rounded-full" />
              </CardContent>
            </Card>
          ) : filteredDoctors.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {filteredDoctors.map((doc) => (
                <DoctorCard key={doc.id} doctor={doc} />
              ))}
            </div>
          ) : (
            <Card>
              <CardContent className="flex flex-col items-center justify-center h-64 gap-4">
                <AlertCircle className="h-12 w-12 text-muted-foreground opacity-50" />
                <p className="text-muted-foreground">Aucun médecin trouvé</p>
              </CardContent>
            </Card>
          )}
        </TabsContent>
      </Tabs>
    </div>
  );
}
