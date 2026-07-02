import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Mail, Phone, Shield, Calendar, Briefcase } from 'lucide-react';
import { useAuth } from '@/contexts/use-auth-context';

export function ProfilePage() {
  const { user, isLoading } = useAuth();

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-[200px]">
        <div className="animate-spin w-8 h-8 border-4 border-primary border-t-transparent rounded-full" />
      </div>
    );
  }

  if (!user) {
    return (
      <div className="text-center py-12 text-muted-foreground">
        <p>Aucun utilisateur connecté</p>
      </div>
    );
  }

  const initials = `${user.prenom?.[0] || ''}${user.nom?.[0] || ''}`.toUpperCase();

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold tracking-tight">Mon Profil</h1>
        <p className="text-muted-foreground">
          Informations personnelles et professionnelles
        </p>
      </div>

      <div className="grid gap-6 md:grid-cols-3">
        <Card className="glass md:col-span-1">
          <CardContent className="pt-6">
            <div className="flex flex-col items-center space-y-4">
              <Avatar className="h-24 w-24">
                <AvatarFallback className="text-2xl bg-primary/10 text-primary">
                  {initials}
                </AvatarFallback>
              </Avatar>
              <div className="text-center space-y-1">
                <h2 className="text-xl font-semibold">{user.prenom} {user.nom}</h2>
                <Badge variant="outline" className="capitalize">
                  {user.role.replace(/_/g, ' ').toLowerCase()}
                </Badge>
                <p className="text-sm text-muted-foreground">
                  {user.department || 'Département non défini'}
                </p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card className="glass md:col-span-2">
          <CardHeader>
            <CardTitle>Informations</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="grid gap-4 md:grid-cols-2">
              <div className="space-y-1">
                <p className="text-sm font-medium text-muted-foreground">Email</p>
                <div className="flex items-center gap-2">
                  <Mail className="h-4 w-4 text-muted-foreground" />
                  <span className="text-sm">{user.email}</span>
                </div>
              </div>
              <div className="space-y-1">
                <p className="text-sm font-medium text-muted-foreground">Téléphone</p>
                <div className="flex items-center gap-2">
                  <Phone className="h-4 w-4 text-muted-foreground" />
                  <span className="text-sm">{user.telephone || 'Non renseigné'}</span>
                </div>
              </div>
              <div className="space-y-1">
                <p className="text-sm font-medium text-muted-foreground">Rôle</p>
                <div className="flex items-center gap-2">
                  <Shield className="h-4 w-4 text-muted-foreground" />
                  <span className="text-sm capitalize">{user.role.replace(/_/g, ' ').toLowerCase()}</span>
                </div>
              </div>
              <div className="space-y-1">
                <p className="text-sm font-medium text-muted-foreground">Spécialité</p>
                <div className="flex items-center gap-2">
                  <Briefcase className="h-4 w-4 text-muted-foreground" />
                  <span className="text-sm">{user.specialite || 'Non renseignée'}</span>
                </div>
              </div>
              <div className="space-y-1">
                <p className="text-sm font-medium text-muted-foreground">Date de naissance</p>
                <div className="flex items-center gap-2">
                  <Calendar className="h-4 w-4 text-muted-foreground" />
                  <span className="text-sm">
                    {user.dateNaissance ? new Date(user.dateNaissance).toLocaleDateString('fr-FR') : 'Non renseignée'}
                  </span>
                </div>
              </div>
              <div className="space-y-1">
                <p className="text-sm font-medium text-muted-foreground">Statut</p>
                <Badge variant={user.status === 'active' ? 'default' : 'secondary'}>
                  {user.status === 'active' ? 'Actif' : 'Inactif'}
                </Badge>
              </div>
            </div>

            <div className="pt-4 border-t">
              <p className="text-sm font-medium text-muted-foreground mb-2">Permissions</p>
              <div className="flex flex-wrap gap-2">
                {user.permissions.length > 0 ? (
                  user.permissions.map((perm) => (
                    <Badge key={perm.id} variant="outline" className="text-xs">
                      {perm.name}
                    </Badge>
                  ))
                ) : (
                  <p className="text-sm text-muted-foreground">Aucune permission assignée</p>
                )}
              </div>
            </div>

            <div className="flex gap-4 text-xs text-muted-foreground pt-4 border-t">
              <span>Créé le: {new Date(user.createdAt).toLocaleDateString('fr-FR')}</span>
              <span>Modifié le: {new Date(user.updatedAt).toLocaleDateString('fr-FR')}</span>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
