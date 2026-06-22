import { useState, useMemo } from 'react';
import { Search } from 'lucide-react';
import { Input } from '@/components/ui/input';
import type { Patient } from '@/types';
import type { Appointment } from '@/hooks/use-appointments';

export interface SearchResult {
  id: string;
  type: 'patient' | 'appointment' | 'consultation' | 'analyse';
  title: string;
  subtitle: string;
  path: string;
}

interface GlobalSearchProps {
  patients?: Patient[];
  appointments?: Appointment[];
  onResultClick?: (result: SearchResult) => void;
}

export function GlobalSearch({ patients = [], appointments = [], onResultClick }: GlobalSearchProps) {
  const [query, setQuery] = useState('');
  const [isOpen, setIsOpen] = useState(false);

  const results = useMemo(() => {
    if (!query.trim()) return [];

    const searchResults: SearchResult[] = [];
    const searchTerm = query.toLowerCase();

    for (const patient of patients) {
      if (
        patient.nom.toLowerCase().includes(searchTerm) ||
        patient.prenom.toLowerCase().includes(searchTerm) ||
        patient.email?.toLowerCase().includes(searchTerm)
      ) {
        searchResults.push({
          id: `patient-${patient.id}`,
          type: 'patient',
          title: `${patient.prenom} ${patient.nom}`,
          subtitle: patient.email || 'Pas d\'email',
          path: `/patients/${patient.id}`,
        });
      }
    }

    for (const appt of appointments) {
      if (
        appt.motif?.toLowerCase().includes(searchTerm) ||
        appt.notes?.toLowerCase().includes(searchTerm)
      ) {
        searchResults.push({
          id: `appointment-${appt.id}`,
          type: 'appointment',
          title: appt.motif || 'Rendez-vous',
          subtitle: new Date(appt.dateDebut).toLocaleDateString(),
          path: `/appointments/${appt.id}`,
        });
      }
    }

    return searchResults.slice(0, 10);
  }, [query, patients, appointments]);

  return (
    <div className="relative w-full max-w-md">
      <div className="relative">
        <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
        <Input
          placeholder="Recherche globale..."
          value={query}
          onChange={(e) => {
            setQuery(e.target.value);
            setIsOpen(true);
          }}
          onFocus={() => setIsOpen(true)}
          onBlur={() => setTimeout(() => setIsOpen(false), 200)}
          className="pl-10"
        />
      </div>
      {isOpen && results.length > 0 && (
        <div className="absolute top-full mt-1 w-full bg-background border rounded-md shadow-lg z-50 max-h-64 overflow-y-auto">
          {results.map((result) => (
            <button
              key={result.id}
              className="w-full px-4 py-2 text-left hover:bg-accent transition-colors"
              onClick={() => {
                onResultClick?.(result);
                setQuery('');
              }}
            >
              <div className="font-medium">{result.title}</div>
              <div className="text-sm text-muted-foreground">{result.subtitle}</div>
            </button>
          ))}
        </div>
      )}
    </div>
  );
}