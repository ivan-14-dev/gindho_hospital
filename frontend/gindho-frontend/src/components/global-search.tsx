import { useState, useEffect } from 'react';
import { Input } from '@/components/ui/input';
import { Search } from 'lucide-react';

interface SearchResult {
  id: string;
  type: 'patient' | 'appointment' | 'consultation';
  title: string;
  subtitle?: string;
  path: string;
}

export function useGlobalSearch() {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState<SearchResult[]>([]);
  const [isOpen, setIsOpen] = useState(false);

  useEffect(() => {
    if (!query) {
      setResults([]);
      return;
    }
    
    const mockResults: SearchResult[] = [
      { id: '1', type: 'patient', title: 'Dupont Jean', subtitle: 'Patient', path: '/patients/1' },
    ];
    setResults(mockResults);
  }, [query]);

  return { query, setQuery, results, isOpen, setIsOpen };
}

export function GlobalSearchTrigger({ onOpen }: { onOpen: () => void }) {
  return (
    <div className="relative w-full max-w-sm">
      <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
      <Input
        placeholder="Recherche globale..."
        className="pl-10"
        onClick={onOpen}
        readOnly
      />
    </div>
  );
}