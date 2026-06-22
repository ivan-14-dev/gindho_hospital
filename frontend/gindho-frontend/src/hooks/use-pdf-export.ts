import { useState } from 'react';
import jsPDF from 'jspdf';
import type { Appointment } from './use-appointments';

/**
 * Hook pour générer des exports PDF
 * Supporte les rendez-vous et consultations
 */
export function usePDFExport() {
  const [isGenerating, setIsGenerating] = useState(false);

  const exportAppointment = async (appointment: Appointment) => {
    setIsGenerating(true);
    try {
      const doc = new jsPDF();
      doc.setFontSize(18);
      doc.text('Détails du Rendez-vous', 14, 22);
      doc.setFontSize(11);
      
      let yPos = 30;
      doc.text(`Date de début: ${new Date(appointment.dateDebut).toLocaleString()}`, 14, yPos);
      yPos += 10;
      doc.text(`Date de fin: ${new Date(appointment.dateFin).toLocaleString()}`, 14, yPos);
      yPos += 10;
      doc.text(`Statut: ${appointment.statut}`, 14, yPos);
      yPos += 10;
      doc.text(`Motif: ${appointment.motif || '-'}`, 14, yPos);
      
      doc.save(`rendez-vous-${appointment.id}.pdf`);
    } finally {
      setIsGenerating(false);
    }
  };

  return { isGenerating, exportAppointment };
}