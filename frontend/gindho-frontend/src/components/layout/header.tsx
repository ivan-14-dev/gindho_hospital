import { useCurrentUser } from '@/hooks/use-auth';
import { useLogout } from '@/hooks/use-auth';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { LogOut, Bell, ChevronRight, Search } from 'lucide-react';
import { ThemeToggle } from '@/components/ui/theme-toggle';
import type { NavigationItem } from '@/shared/navigation/navigation-registry';

interface HeaderProps {
  onLogout: () => void;
  breadcrumbs?: NavigationItem[];
}

export function Header({ onLogout, breadcrumbs = [] }: HeaderProps) {
  const { data: user } = useCurrentUser();
  const logoutMutation = useLogout();

  const handleLogout = () => {
    logoutMutation.mutate();
    onLogout();
  };

  const initials = user
    ? `${user.prenom[0]}${user.nom[0]}`.toUpperCase()
    : '??';

  return (
    <header className="sticky top-0 z-30 flex h-16 items-center justify-between border-b bg-background px-6">
      <div className="flex items-center gap-4">
        {breadcrumbs.length > 0 && (
          <nav aria-label="Breadcrumb" className="flex items-center space-x-1">
            {breadcrumbs.map((crumb, index) => (
              <div key={crumb.id} className="flex items-center">
                {index > 0 && <ChevronRight className="h-4 w-4 text-muted-foreground" />}
                <span className={index === breadcrumbs.length - 1 ? 'text-foreground font-medium' : 'text-muted-foreground'}>
                  {crumb.label}
                </span>
              </div>
            ))}
          </nav>
        )}
        {breadcrumbs.length === 0 && <h2 className="text-lg font-semibold">GinDHO</h2>}
      </div>

      <div className="flex items-center gap-4">
        <div className="relative hidden md:block">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
          <Input
            placeholder="Rechercher..."
            className="pl-9 w-64"
          />
        </div>
        <ThemeToggle />
        
        <Button variant="ghost" size="icon" className="relative">
          <Bell className="h-5 w-5" />
          <span className="absolute top-1 right-1 h-2 w-2 rounded-full bg-destructive" />
        </Button>

        <div className="flex items-center gap-3">
          <div className="text-right hidden sm:block">
            <p className="text-sm font-medium">
              {user ? `${user.prenom} ${user.nom}` : 'Chargement...'}
            </p>
            <p className="text-xs text-muted-foreground">
              {user?.role || ''}
            </p>
          </div>

          <Avatar>
            <AvatarFallback className="bg-primary text-primary-foreground">
              {initials}
            </AvatarFallback>
          </Avatar>

          <Button
            variant="ghost"
            size="icon"
            onClick={handleLogout}
            title="Déconnexion"
          >
            <LogOut className="h-5 w-5" />
          </Button>
        </div>
      </div>
    </header>
  );
}