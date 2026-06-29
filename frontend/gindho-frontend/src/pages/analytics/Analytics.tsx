'use client';

import { useState } from 'react';
import { AnalyticsLayout } from './AnalyticsLayout';
import { ExecutiveDashboard } from './views/ExecutiveDashboard';
import { ActivityView } from './views/ActivityView';
import { FinanceView } from './views/FinanceView';
import { PharmacyView } from './views/PharmacyView';
import { LaboratoryView } from './views/LaboratoryView';

export default function Analytics() {
  const [currentSection, setCurrentSection] = useState('executive');

  const renderSection = () => {
    switch (currentSection) {
      case 'executive':
        return <ExecutiveDashboard />;
      case 'activity':
        return <ActivityView />;
      case 'finance':
        return <FinanceView />;
      case 'pharmacy':
        return <PharmacyView />;
      case 'laboratory':
        return <LaboratoryView />;
      case 'patients':
        return <div className="p-6"><h2 className="text-2xl font-bold">Patients - Sous développement</h2></div>;
      case 'consultations':
        return <div className="p-6"><h2 className="text-2xl font-bold">Consultations - Sous développement</h2></div>;
      case 'hospitalizations':
        return <div className="p-6"><h2 className="text-2xl font-bold">Hospitalisations - Sous développement</h2></div>;
      case 'surgery':
        return <div className="p-6"><h2 className="text-2xl font-bold">Bloc Opératoire - Sous développement</h2></div>;
      case 'imaging':
        return <div className="p-6"><h2 className="text-2xl font-bold">Radiologie - Sous développement</h2></div>;
      case 'blood':
        return <div className="p-6"><h2 className="text-2xl font-bold">Banque de Sang - Sous développement</h2></div>;
      case 'hr':
        return <div className="p-6"><h2 className="text-2xl font-bold">Ressources Humaines - Sous développement</h2></div>;
      case 'quality':
        return <div className="p-6"><h2 className="text-2xl font-bold">Qualité des Soins - Sous développement</h2></div>;
      case 'epidemiology':
        return <div className="p-6"><h2 className="text-2xl font-bold">Épidémiologie - Sous développement</h2></div>;
      case 'satisfaction':
        return <div className="p-6"><h2 className="text-2xl font-bold">Satisfaction - Sous développement</h2></div>;
      case 'reports':
        return <div className="p-6"><h2 className="text-2xl font-bold">Rapports - Sous développement</h2></div>;
      default:
        return <ExecutiveDashboard />;
    }
  };

  return (
    <AnalyticsLayout currentSection={currentSection} onSectionChange={setCurrentSection}>
      {renderSection()}
    </AnalyticsLayout>
  );
}
