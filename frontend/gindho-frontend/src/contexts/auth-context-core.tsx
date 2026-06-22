import { createContext } from 'react';
import type { User } from '@/types';
import type { NavigationPermission } from '@/shared/navigation/navigation-registry';

export interface AuthContextType {
  user: User | null;
  permissions: NavigationPermission[];
  isLoading: boolean;
  isAuthenticated: boolean;
  logout: () => void;
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined);