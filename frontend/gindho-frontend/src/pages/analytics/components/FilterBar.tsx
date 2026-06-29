'use client';

import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Card } from '@/components/ui/card';
import { Filter, X, Download } from 'lucide-react';
import { cn } from '@/lib/utils';

interface FilterBarProps {
  dateRange: { start: string; end: string };
  onDateRangeChange: (range: { start: string; end: string }) => void;
  period: 'day' | 'week' | 'month' | 'year' | 'custom';
  onPeriodChange: (period: 'day' | 'week' | 'month' | 'year' | 'custom') => void;
  filters: Record<string, string>;
  onFilterChange: (key: string, value: string) => void;
  onClearFilters: () => void;
  onExport?: () => void;
}

const periods = [
  { value: 'day', label: 'Jour' },
  { value: 'week', label: 'Semaine' },
  { value: 'month', label: 'Mois' },
  { value: 'year', label: 'Année' },
  { value: 'custom', label: 'Personnalisé' },
];

export function FilterBar({
  dateRange,
  onDateRangeChange,
  period,
  onPeriodChange,
  filters,
  onFilterChange,
  onClearFilters,
  onExport
}: FilterBarProps) {
  return (
    <Card className="p-4 space-y-4">
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center gap-2">
          <Filter className="h-5 w-5 text-muted-foreground" />
          <h3 className="font-semibold">Filtres</h3>
        </div>
        {Object.keys(filters).length > 0 && (
          <Button
            size="sm"
            variant="ghost"
            onClick={onClearFilters}
            className="h-8"
          >
            <X className="h-4 w-4 mr-1" />
            Réinitialiser
          </Button>
        )}
      </div>

      {/* Period Selection */}
      <div className="space-y-2">
        <label className="text-sm font-medium">Période</label>
        <div className="flex gap-2 flex-wrap">
          {periods.map((p) => (
            <Button
              key={p.value}
              size="sm"
              variant={period === p.value ? 'default' : 'outline'}
              onClick={() => onPeriodChange(p.value as any)}
              className="text-xs"
            >
              {p.label}
            </Button>
          ))}
        </div>
      </div>

      {/* Date Range (if custom) */}
      {period === 'custom' && (
        <div className="grid grid-cols-2 gap-2">
          <div className="space-y-1">
            <label className="text-xs font-medium">Date Début</label>
            <Input
              type="date"
              value={dateRange.start}
              onChange={(e) => onDateRangeChange({ ...dateRange, start: e.target.value })}
              className="h-8 text-xs"
            />
          </div>
          <div className="space-y-1">
            <label className="text-xs font-medium">Date Fin</label>
            <Input
              type="date"
              value={dateRange.end}
              onChange={(e) => onDateRangeChange({ ...dateRange, end: e.target.value })}
              className="h-8 text-xs"
            />
          </div>
        </div>
      )}

      {/* Advanced Filters */}
      <div className="grid grid-cols-2 gap-2">
        <div className="space-y-1">
          <label className="text-xs font-medium">Service</label>
          <select
            value={filters.service || ''}
            onChange={(e) => onFilterChange('service', e.target.value)}
            className="w-full h-8 text-xs border rounded-md px-2"
          >
            <option value="">Tous</option>
            <option value="emergency">Urgences</option>
            <option value="surgery">Chirurgie</option>
            <option value="maternity">Maternité</option>
            <option value="pediatrics">Pédiatrie</option>
            <option value="cardiology">Cardiologie</option>
          </select>
        </div>
        <div className="space-y-1">
          <label className="text-xs font-medium">Spécialité</label>
          <select
            value={filters.specialty || ''}
            onChange={(e) => onFilterChange('specialty', e.target.value)}
            className="w-full h-8 text-xs border rounded-md px-2"
          >
            <option value="">Toutes</option>
            <option value="general">Médecine Générale</option>
            <option value="cardio">Cardiologie</option>
            <option value="neuro">Neurologie</option>
            <option value="ortho">Orthopédie</option>
          </select>
        </div>
        <div className="space-y-1">
          <label className="text-xs font-medium">Médecin</label>
          <Input
            placeholder="Nom du médecin..."
            value={filters.doctor || ''}
            onChange={(e) => onFilterChange('doctor', e.target.value)}
            className="h-8 text-xs"
          />
        </div>
        <div className="space-y-1">
          <label className="text-xs font-medium">Sexe</label>
          <select
            value={filters.gender || ''}
            onChange={(e) => onFilterChange('gender', e.target.value)}
            className="w-full h-8 text-xs border rounded-md px-2"
          >
            <option value="">Tous</option>
            <option value="M">Homme</option>
            <option value="F">Femme</option>
            <option value="OTHER">Autre</option>
          </select>
        </div>
      </div>

      {/* Export Button */}
      {onExport && (
        <Button
          onClick={onExport}
          variant="outline"
          size="sm"
          className="w-full"
        >
          <Download className="h-4 w-4 mr-2" />
          Exporter
        </Button>
      )}
    </Card>
  );
}
