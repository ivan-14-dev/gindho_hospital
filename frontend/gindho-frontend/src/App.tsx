import { lazy, Suspense } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from '@/contexts/use-auth-context';
import { Layout } from '@/components/layout/layout';
import { LoginPage } from '@/pages/auth/Login';
import { RegisterPage } from '@/pages/auth/Register';
import { ErrorBoundary } from '@/components/error-boundary';
import { Loader2 } from 'lucide-react';
import type { NavigationPermission } from '@/shared/navigation/navigation-registry';


const UnauthorizedPage = () => (
  <div className="flex h-screen items-center justify-center">
    <div className="text-center space-y-4">
      <h1 className="text-6xl font-bold text-destructive">403</h1>
      <p className="text-lg text-muted-foreground">Accès refusé - Vous n'avez pas les permissions nécessaires</p>
      <button
        onClick={() => (window.location.href = '/')}
        className="text-primary underline"
      >
        Retour à l'accueil
      </button>
    </div>
  </div>
);

const DashboardPage = lazy(() => import('@/pages/dashboard/Dashboard').then(m => ({ default: m.DashboardPage })));
const PatientsPage = lazy(() => import('@/pages/patients/Patients').then(m => ({ default: m.PatientsPage })));
const NotFoundPage = lazy(() => import('@/pages/NotFound').then(m => ({ default: m.NotFoundPage as React.ComponentType<object> })));
const AppointmentsPage = lazy(() => import('@/pages/appointments/Appointments').then(m => ({ default: m.AppointmentsPage })));
const MedicalRecordsPage = lazy(() => import('@/pages/medical-records/MedicalRecords').then(m => ({ default: m.MedicalRecordsPage })));
const LaboratoryPage = lazy(() => import('@/pages/laboratory/Laboratory').then(m => ({ default: m.LaboratoryPage })));
const PharmacyPage = lazy(() => import('@/pages/pharmacy/Pharmacy').then(m => ({ default: m.PharmacyPage })));
const PaymentsPage = lazy(() => import('@/pages/payments/Payments').then(m => ({ default: m.PaymentsPage })));

const AIAssistantPage = lazy(() => import('@/pages/ai/AIAssistant').then(m => ({ default: (m as any).default || (m as any).AIAssistantPage })));
const AnalyticsPage = lazy(() => import('@/pages/analytics/Analytics').then(m => ({ default: (m as any).default || (m as any).AnalyticsPage })));
const ChatPage = lazy(() => import('@/pages/chat/Chat').then(m => ({ default: (m as any).default || (m as any).ChatPage })));

const EmergencyPage = lazy(() => import('@/pages/emergency/Emergency').then(m => ({ default: m.EmergencyPage as React.ComponentType<object> })));
const ImagingPage = lazy(() => import('@/pages/imaging/Imaging').then(m => ({ default: m.ImagingPage as React.ComponentType<object> })));

// Lazy-loaded imports for missing clinical, operations, business and admin pages
const AdmissionsPage = lazy(() => import('@/pages/admissions/Admissions').then(m => ({ default: m.AdmissionsPage })));
const AmbulanceDispatchPage = lazy(() => import('@/pages/ambulance/AmbulanceDispatch'));
const AssetManagementPage = lazy(() => import('@/pages/assets/AssetManagement'));
const AuditLogsPage = lazy(() => import('@/pages/audit/AuditLogs'));
const BillingPage = lazy(() => import('@/pages/billing/Billing').then(m => ({ default: m.BillingPage })));
const EventsPage = lazy(() => import('@/pages/events/Events'));
const HRManagementPage = lazy(() => import('@/pages/hr/HRManagement').then(m => ({ default: m.HRManagementPage })));
const InventoryPage = lazy(() => import('@/pages/inventory/Inventory').then(m => ({ default: m.InventoryPage })));
const NotificationsPage = lazy(() => import('@/pages/notifications/Notifications'));
const NursingCarePage = lazy(() => import('@/pages/nursing/NursingCare'));
const ProcurementPage = lazy(() => import('@/pages/procurement/Procurement'));
const QualityPage = lazy(() => import('@/pages/quality/Quality').then(m => ({ default: m.QualityPage })));
const ReportingPage = lazy(() => import('@/pages/reporting/Reporting'));
const MedicalRoundsPage = lazy(() => import('@/pages/rounds/MedicalRounds'));
const SurgeryPage = lazy(() => import('@/pages/surgery/Surgery').then(m => ({ default: m.SurgeryPage })));
const TeleconsultationPage = lazy(() => import('@/pages/teleconsultation/Teleconsultation'));
const UserManagementPage = lazy(() => import('@/pages/admin/UserManagementPage').then(m => ({ default: m.UserManagementPage })));

const WardsPage = lazy(() => import('@/pages/wards/Wards').then(m => ({ default: m.WardsPage })));

const PageLoader = () => (
  <div className="flex items-center justify-center h-screen">
    <Loader2 className="h-8 w-8 animate-spin text-primary" />
  </div>
);

function PrivateRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="animate-spin w-8 h-8 border-4 border-primary border-t-transparent rounded-full" />
      </div>
    );
  }


  return isAuthenticated ? <>{children}</> : <Navigate to="/login" />;
}

function ProtectedRoute({ 
  children, 
  requiredPermission 
}: { 
  children: React.ReactNode; 
  requiredPermission?: NavigationPermission;
}) {
  const { isAuthenticated, isLoading, permissions } = useAuth();

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="animate-spin w-8 h-8 border-4 border-primary border-t-transparent rounded-full" />
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" />;
  }

  if (requiredPermission && !permissions.includes(requiredPermission)) {
    return <Navigate to="/unauthorized" />;
  }

  return <>{children}</>;
}

function PublicRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="animate-spin w-8 h-8 border-4 border-primary border-t-transparent rounded-full" />
      </div>
    );
  }

  return isAuthenticated ? <Navigate to="/" /> : <>{children}</>;
}

function AppRoutes() {
  return (
    <ErrorBoundary>
      <Routes>
        <Route
          path="/login"
          element={
            <PublicRoute>
              <LoginPage />
            </PublicRoute>
          }
        />
        <Route
          path="/register"
          element={
            <PublicRoute>
              <RegisterPage />
            </PublicRoute>
          }
        />
        <Route path="/unauthorized" element={<UnauthorizedPage />} />
        <Route
          path="/"
          element={
            <PrivateRoute>
              <Layout />
            </PrivateRoute>
          }
        >
          <Route index element={
            <Suspense fallback={<PageLoader />}>
              <DashboardPage />
            </Suspense>
          } />
          <Route path="patients" element={
            <ProtectedRoute requiredPermission="patients:READ">
              <Suspense fallback={<PageLoader />}>
                <PatientsPage />
              </Suspense>
            </ProtectedRoute>
          } />
          <Route path="appointments" element={
            <ProtectedRoute requiredPermission="appointments:READ">
              <Suspense fallback={<PageLoader />}>
                <AppointmentsPage />
              </Suspense>
            </ProtectedRoute>
          } />
          <Route path="medical-records" element={
            <ProtectedRoute requiredPermission="medical_records:READ">
              <Suspense fallback={<PageLoader />}>
                <MedicalRecordsPage />
              </Suspense>
            </ProtectedRoute>
          } />
          <Route path="lab" element={
            <ProtectedRoute requiredPermission="laboratory:READ">
              <Suspense fallback={<PageLoader />}>
                <LaboratoryPage />
              </Suspense>
            </ProtectedRoute>
          } />
          <Route path="medications" element={
            <ProtectedRoute requiredPermission="pharmacy:READ">
              <Suspense fallback={<PageLoader />}>
                <PharmacyPage />
              </Suspense>
            </ProtectedRoute>
          } />
          <Route path="payments" element={
            <ProtectedRoute requiredPermission="payments:READ">
              <Suspense fallback={<PageLoader />}>
                <PaymentsPage />
              </Suspense>
            </ProtectedRoute>
          } />
          <Route path="ai-assistant" element={
            <ProtectedRoute requiredPermission="ai:READ">
              <Suspense fallback={<PageLoader />}>
                <AIAssistantPage />
              </Suspense>
            </ProtectedRoute>
          } />
<Route path="analytics" element={
             <ProtectedRoute requiredPermission="analytics:READ">
               <Suspense fallback={<PageLoader />}>
                 <AnalyticsPage />
               </Suspense>
             </ProtectedRoute>
           } />
           <Route path="emergency" element={
             <ProtectedRoute requiredPermission="emergency:READ">
               <Suspense fallback={<PageLoader />}>
                 <EmergencyPage />
               </Suspense>
             </ProtectedRoute>
           } />
           <Route path="imaging" element={
             <ProtectedRoute requiredPermission="imaging:READ">
               <Suspense fallback={<PageLoader />}>
                 <ImagingPage />
               </Suspense>
             </ProtectedRoute>
           } />
           <Route path="chat" element={
             <ProtectedRoute requiredPermission="chat:READ">
               <Suspense fallback={<PageLoader />}>
                 <ChatPage />
               </Suspense>
             </ProtectedRoute>
           } />
            <Route path="hospitalization" element={
              <ProtectedRoute requiredPermission="hospitalization:READ">
                <Suspense fallback={<PageLoader />}>
                  <AdmissionsPage />
                </Suspense>
              </ProtectedRoute>
            } />
            <Route path="admissions" element={
              <ProtectedRoute requiredPermission="admissions:READ">
                <Suspense fallback={<PageLoader />}>
                  <AdmissionsPage />
                </Suspense>
              </ProtectedRoute>
            } />
            <Route path="wards" element={
              <ProtectedRoute requiredPermission="wards:READ">
                <Suspense fallback={<PageLoader />}>
                  <WardsPage />
                </Suspense>
              </ProtectedRoute>
            } />
            <Route path="nursing" element={
              <ProtectedRoute requiredPermission="nursing:READ">
                <Suspense fallback={<PageLoader />}>
                  <NursingCarePage />
                </Suspense>
              </ProtectedRoute>
            } />
            <Route path="rounds" element={
              <ProtectedRoute requiredPermission="rounds:READ">
                <Suspense fallback={<PageLoader />}>
                  <MedicalRoundsPage />
                </Suspense>
              </ProtectedRoute>
            } />
            <Route path="ambulance" element={
              <ProtectedRoute requiredPermission="ambulance:READ">
                <Suspense fallback={<PageLoader />}>
                  <AmbulanceDispatchPage />
                </Suspense>
              </ProtectedRoute>
            } />
            <Route path="teleconsultation" element={
              <ProtectedRoute requiredPermission="teleconsultation:READ">
                <Suspense fallback={<PageLoader />}>
                  <TeleconsultationPage />
                </Suspense>
              </ProtectedRoute>
            } />
            <Route path="procurement" element={
              <ProtectedRoute requiredPermission="procurement:READ">
                <Suspense fallback={<PageLoader />}>
                  <ProcurementPage />
                </Suspense>
              </ProtectedRoute>
            } />
            <Route path="assets" element={
              <ProtectedRoute requiredPermission="assets:READ">
                <Suspense fallback={<PageLoader />}>
                  <AssetManagementPage />
                </Suspense>
              </ProtectedRoute>
            } />
            <Route path="audit" element={
              <ProtectedRoute requiredPermission="audit:READ">
                <Suspense fallback={<PageLoader />}>
                  <AuditLogsPage />
                </Suspense>
              </ProtectedRoute>
            } />
            <Route path="events" element={
              <ProtectedRoute requiredPermission="events:READ">
                <Suspense fallback={<PageLoader />}>
                  <EventsPage />
                </Suspense>
              </ProtectedRoute>
            } />
            <Route path="hr" element={
              <ProtectedRoute requiredPermission="hr:READ">
                <Suspense fallback={<PageLoader />}>
                  <HRManagementPage />
                </Suspense>
              </ProtectedRoute>
            } />
            <Route path="inventory" element={
              <ProtectedRoute requiredPermission="inventory:READ">
                <Suspense fallback={<PageLoader />}>
                  <InventoryPage />
                </Suspense>
              </ProtectedRoute>
            } />
            <Route path="billing" element={
              <ProtectedRoute requiredPermission="billing:READ">
                <Suspense fallback={<PageLoader />}>
                  <BillingPage />
                </Suspense>
              </ProtectedRoute>
            } />
            <Route path="quality" element={
              <ProtectedRoute requiredPermission="quality:READ">
                <Suspense fallback={<PageLoader />}>
                  <QualityPage />
                </Suspense>
              </ProtectedRoute>
            } />
            <Route path="surgery" element={
              <ProtectedRoute requiredPermission="surgery:READ">
                <Suspense fallback={<PageLoader />}>
                  <SurgeryPage />
                </Suspense>
              </ProtectedRoute>
            } />
            <Route path="notifications" element={
              <ProtectedRoute requiredPermission="notifications:READ">
                <Suspense fallback={<PageLoader />}>
                  <NotificationsPage />
                </Suspense>
              </ProtectedRoute>
            } />
            <Route path="reporting" element={
              <ProtectedRoute requiredPermission="reporting:READ">
                <Suspense fallback={<PageLoader />}>
                  <ReportingPage />
                </Suspense>
              </ProtectedRoute>
            } />
          <Route path="settings" element={<div className="p-6">Paramètres - En construction</div>} />
          <Route path="404" element={<NotFoundPage />} />
          <Route path="*" element={<NotFoundPage />} />
                <Route path="admin/users" element={
          <ProtectedRoute requiredPermission="users:READ">
            <Suspense fallback={<PageLoader />}> 
              <UserManagementPage />
            </Suspense>
          </ProtectedRoute>
        } />
        </Route>
      </Routes>
    </ErrorBoundary>
  );
}

export default function App() {
  return <AppRoutes />;
}
