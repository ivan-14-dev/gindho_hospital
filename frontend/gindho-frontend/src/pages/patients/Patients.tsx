import { usePatients } from '@/hooks/use-patients';
import { BookmarkButton } from '@/hooks/use-bookmarks';
import { Card, CardContent, CardHeader } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Search, Plus, Eye, Download } from 'lucide-react';
import { useState } from 'react';
import { PatientForm } from './PatientForm';

export function PatientsPage() {
  const [search, setSearch] = useState('');
  const [page] = useState(0);
  const [formOpen, setFormOpen] = useState(false);

  const { data, isLoading } = usePatients({ search, page, size: 10 });

  return (
    <div className="space-y-6">
      <PatientForm open={formOpen} onOpenChange={setFormOpen} />
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Patients</h1>
          <p className="text-muted-foreground">Gestion des patients</p>
        </div>
        <div className="flex gap-2">
          <Button variant="outline">
            <Download className="h-4 w-4 mr-2" />
            Exporter PDF
          </Button>
          <Button onClick={() => setFormOpen(true)}>
            <Plus className="h-4 w-4 mr-2" />
            Nouveau patient
          </Button>
        </div>
      </div>

      <Card>
        <CardHeader>
          <div className="flex items-center gap-4">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
              <Input
                placeholder="Rechercher un patient..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                className="pl-10"
              />
            </div>
          </div>
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <div className="flex items-center justify-center h-64">
              <div className="animate-spin w-8 h-8 border-4 border-primary border-t-transparent rounded-full" />
            </div>
          ) : data?.content && data.content.length > 0 ? (
            <div className="space-y-4">
              {data.content.map((patient) => (
                <div
                  key={patient.id}
                  className="flex items-center justify-between p-4 border rounded-lg hover:bg-accent/50 transition-colors"
                >
                  <div className="flex items-center gap-4">
                    <div className="h-10 w-10 rounded-full bg-primary/10 flex items-center justify-center">
                      <span className="text-sm font-medium text-primary">
                        {patient.prenom[0]}{patient.nom[0]}
                      </span>
                    </div>
                    <div>
                      <p className="font-medium">
                        {patient.prenom} {patient.nom}
                      </p>
                      <p className="text-sm text-muted-foreground">
                        {patient.email || 'Pas d\'email'}
                      </p>
                    </div>
                  </div>

<div className="flex items-center gap-2">
                     <Badge variant="secondary">
                       {patient.sexe === 'M' ? 'Masculin' : patient.sexe === 'F' ? 'Féminin' : 'Autre'}
                     </Badge>
                     <BookmarkButton
                       id={patient.id}
                       type="patient"
                       title={`${patient.prenom} ${patient.nom}`}
                       path={`/patients/${patient.id}`}
                     />
                     <Button variant="ghost" size="icon">
                       <Eye className="h-4 w-4" />
                     </Button>
                   </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-12 text-muted-foreground">
              Aucun patient trouvé
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}