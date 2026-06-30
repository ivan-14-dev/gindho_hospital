'use client';

import { Button } from '@/components/ui/button';
import { Download, Share2, Printer } from 'lucide-react';
import { ExportService, type ExportData } from '../utils/exportService';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';

interface ExportBarProps {
  title: string;
  data: any[];
  columns?: string[];
}

export function ExportBar({ title, data, columns }: ExportBarProps) {
  const defaultColumns = columns || (data.length > 0 ? Object.keys(data[0]) : []);
  const exportData: ExportData = { title, data, columns: defaultColumns };

  const handlePrint = () => {
    window.print();
  };

  const handleShare = async () => {
    if (navigator.share) {
      try {
        await navigator.share({
          title,
          text: `Rapport: ${title}`,
        });
      } catch (err) {
        console.log('Erreur lors du partage');
      }
    }
  };

  return (
    <div className="flex gap-2">
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <Button variant="outline" size="sm" className="gap-2">
            <Download className="h-4 w-4" />
            Exporter
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent>
          <DropdownMenuItem onClick={() => ExportService.exportToCSV(exportData)}>
            Exporter en CSV
          </DropdownMenuItem>
          <DropdownMenuItem onClick={() => ExportService.exportToJSON(exportData)}>
            Exporter en JSON
          </DropdownMenuItem>
          <DropdownMenuItem onClick={() => ExportService.exportToXLSX(exportData)}>
            Exporter en Excel
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>

      <Button variant="outline" size="sm" className="gap-2" onClick={handlePrint}>
        <Printer className="h-4 w-4" />
        Imprimer
      </Button>

      <Button variant="outline" size="sm" className="gap-2" onClick={handleShare}>
        <Share2 className="h-4 w-4" />
        Partager
      </Button>
    </div>
  );
}
