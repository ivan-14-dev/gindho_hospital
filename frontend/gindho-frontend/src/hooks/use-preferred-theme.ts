import { useEffect } from 'react';
import { useTheme } from '@/lib/theme-provider';

export function usePreferredTheme() {
  const { theme, setTheme } = useTheme();
  
  useEffect(() => {
    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
    
    if (theme === 'system') {
      const systemTheme = mediaQuery.matches ? 'dark' : 'light';
      document.documentElement.classList.toggle('dark', systemTheme === 'dark');
    }
    
    const handler = (e: MediaQueryListEvent) => {
      if (theme === 'system') {
        document.documentElement.classList.toggle('dark', e.matches);
      }
    };
    
    mediaQuery.addEventListener('change', handler);
    return () => mediaQuery.removeEventListener('change', handler);
  }, [theme]);
  
  return { theme, setTheme };
}