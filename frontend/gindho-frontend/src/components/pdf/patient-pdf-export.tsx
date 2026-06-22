import { useRef } from 'react';
import { useReactToPrint } from 'react-to-print';
import jsPDF from 'jspdf';
import type { Patient } from '@/types';

interface PatientPDFExportProps {
  patient: Patient;
  onExport?: () => void;
}

export function PatientPDFExport({ patient, onExport }: PatientPDFExportProps) {
  const printRef = useRef<HTMLDivElement>(null);

  const handlePrint = useReactToPrint({
    contentRef: printRef,
    onAfterPrint: onExport,
  });

  const handleDownloadPDF = () => {
    const doc = new jsPDF();
    doc.setFontSize(16);
    doc.text(`Dossier Patient: ${patient.prenom} ${patient.nom}`, 20, 20);
    doc.setFontSize(12);
    doc.text(`Date de naissance: ${patient.dateNaissance}`, 20, 40);
    doc.text(`Email: ${patient.email || 'Non renseigné'}`, 20, 50);
    doc.text(`Téléphone: ${patient.telephone || 'Non renseigné'}`, 20, 60);
    doc.save(`patient-${patient.id}.pdf`);
    onExport?.();
  };

  return (
    <div>
      <div ref={printRef} className="hidden">
        <div className="p-4">
          <h2>Dossier Patient</h2>
          <p>Nom: {patient.nom}</p>
          <p>Prénom: {patient.prenom}</p>
          <p>Email: {patient.email || 'Non renseigné'}</p>
          <p>Téléphone: {patient.telephone || 'Non renseigné'}</p>
        </div>
      </div>
      <div className="flex gap-2">
        <button
          onClick={handlePrint}
          className="px-3 py-1 text-sm border rounded hover:bg-accent"
        >
          Imprimer
        </button>
        <button
          onClick={handleDownloadPDF}
          className="px-3 py-1 text-sm border rounded hover:bg-accent"
        >
          Télécharger PDF
        </button>
      </div>
    </div>
  );
}