'use client';

import { useState } from 'react';
import { AnalyticsLayout } from './AnalyticsLayout';
import { ExecutiveDashboard } from './views/ExecutiveDashboard';
import { ActivityView } from './views/ActivityView';
import { FinanceView } from './views/FinanceView';
import { PharmacyView } from './views/PharmacyView';
import { LaboratoryView } from './views/LaboratoryView';
import { PatientsView } from './views/PatientsView';
import { ConsultationsView } from './views/ConsultationsView';
import { HospitalizationsView } from './views/HospitalizationsView';
import { SurgeryView } from './views/SurgeryView';
import { ImagingView } from './views/ImagingView';
import { BloodBankView } from './views/BloodBankView';
import { HRView } from './views/HRView';
import { QualityView } from './views/QualityView';
import { EpidemiologyView } from './views/EpidemiologyView';
import { SatisfactionView } from './views/SatisfactionView';
import { ReportsView } from './views/ReportsView';

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
        return <PatientsView />;
      case 'consultations':
        return <ConsultationsView />;
      case 'hospitalizations':
        return <HospitalizationsView />;
      case 'surgery':
        return <SurgeryView />;
      case 'imaging':
        return <ImagingView />;
      case 'blood':
        return <BloodBankView />;
      case 'hr':
        return <HRView />;
      case 'quality':
        return <QualityView />;
      case 'epidemiology':
        return <EpidemiologyView />;
      case 'satisfaction':
        return <SatisfactionView />;
      case 'reports':
        return <ReportsView />;
      case 'finance':
        return <FinanceView />;
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
