import { lazy, Suspense } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from '@/contexts/use-auth-context';
import { Layout } from '@/components/layout/layout';
import { LoginPage } from '@/pages/auth/Login';
import { RegisterPage } from '@/pages/auth/Register';
import { ErrorBoundary } from '@/components/error-boundary';
import { PermissionGuard } from '@/components/auth/permission-guard';
import { Loader2 } from 'lucide-react';


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
const PaymentsPage = lazy(() => import('@/pages/payments/Payments').then(m => ({ default: m.PaymentsPage })));

const AIAssistantPage = lazy(() => import('@/pages/ai/AIAssistant').then(m => ({ default: (m as any).default || (m as any).AIAssistantPage })));
const AnalyticsPage = lazy(() => import('@/pages/analytics/Analytics').then(m => ({ default: (m as any).default || (m as any).AnalyticsPage })));
const ChatPage = lazy(() => import('@/pages/chat/Chat').then(m => ({ default: (m as any).default || (m as any).ChatPage })));

const EmergencyPage = lazy(() => import('@/pages/emergency/Emergency').then(m => ({ default: m.EmergencyPage as React.ComponentType<object> })));
const ImagingPage = lazy(() => import('@/pages/imaging/Imaging').then(m => ({ default: m.ImagingPage as React.ComponentType<object> })));

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

const FacilitiesPage = lazy(() => import('@/pages/facilities/Facilities').then(m => ({ default: m.FacilitiesPage })));
const LabResultsPage = lazy(() => import('@/pages/lab/LabResults').then(m => ({ default: m.LabResultsPage })));
const MedicationsPage = lazy(() => import('@/pages/medications/Medications').then(m => ({ default: m.MedicationsPage })));
const ProfilePage = lazy(() => import('@/pages/profile/Profile').then(m => ({ default: m.ProfilePage })));
const PublicHealthPage = lazy(() => import('@/pages/public-health/PublicHealth').then(m => ({ default: m.PublicHealthPage })));
const WellnessPage = lazy(() => import('@/pages/wellness/Wellness').then(m => ({ default: m.WellnessPage })));

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
            <PermissionGuard permission="patients:READ">
              <Suspense fallback={<PageLoader />}>
                <PatientsPage />
              </Suspense>
            </PermissionGuard>
          } />
          <Route path="appointments" element={
            <PermissionGuard permission="appointments:READ">
              <Suspense fallback={<PageLoader />}>
                <AppointmentsPage />
              </Suspense>
            </PermissionGuard>
          } />
          <Route path="medical-records" element={
            <PermissionGuard permission="medical_records:READ">
              <Suspense fallback={<PageLoader />}>
                <MedicalRecordsPage />
              </Suspense>
            </PermissionGuard>
          } />
          <Route path="lab" element={
            <PermissionGuard permission="laboratory:READ">
              <Suspense fallback={<PageLoader />}>
                <LaboratoryPage />
              </Suspense>
            </PermissionGuard>
          } />
          <Route path="lab-results" element={
            <PermissionGuard permission="laboratory:READ">
              <Suspense fallback={<PageLoader />}>
                <LabResultsPage />
              </Suspense>
            </PermissionGuard>
          } />
          <Route path="medications" element={
            <PermissionGuard permission="pharmacy:READ">
              <Suspense fallback={<PageLoader />}>
                <MedicationsPage />
              </Suspense>
            </PermissionGuard>
          } />
          <Route path="payments" element={
            <PermissionGuard permission="payments:READ">
              <Suspense fallback={<PageLoader />}>
                <PaymentsPage />
              </Suspense>
            </PermissionGuard>
          } />
          <Route path="ai-assistant" element={
            <PermissionGuard permission="ai:READ">
              <Suspense fallback={<PageLoader />}>
                <AIAssistantPage />
              </Suspense>
            </PermissionGuard>
          } />
          <Route path="analytics" element={
            <PermissionGuard permission="analytics:READ">
              <Suspense fallback={<PageLoader />}>
                <AnalyticsPage />
              </Suspense>
            </PermissionGuard>
          } />
          <Route path="emergency" element={
            <PermissionGuard permission="emergency:READ">
              <Suspense fallback={<PageLoader />}>
                <EmergencyPage />
              </Suspense>
            </PermissionGuard>
          } />
          <Route path="imaging" element={
            <PermissionGuard permission="imaging:READ">
              <Suspense fallback={<PageLoader />}>
                <ImagingPage />
              </Suspense>
            </PermissionGuard>
          } />
          <Route path="chat" element={
            <PermissionGuard permission="chat:READ">
              <Suspense fallback={<PageLoader />}>
                <ChatPage />
              </Suspense>
            </PermissionGuard>
          } />
          <Route path="hospitalization" element={
            <PermissionGuard permission="hospitalization:READ">
              <Suspense fallback={<PageLoader />}>
                <AdmissionsPage />
              </Suspense>
            </PermissionGuard>
          } />
          <Route path="admissions" element={
            <PermissionGuard permission="admissions:READ">
              <Suspense fallback={<PageLoader />}>
                <AdmissionsPage />
              </Suspense>
            </PermissionGuard>
          } />
          <Route path="wards" element={
            <PermissionGuard permission="wards:READ">
              <Suspense fallback={<PageLoader />}>
                <WardsPage />
              </Suspense>
            </PermissionGuard>
          } />
          <Route path="nursing" element={
            <PermissionGuard permission="nursing:READ">
              <Suspense fallback={<PageLoader />}>
                <NursingCarePage />
              </Suspense>
            </PermissionGuard>
          } />
          <Route path="rounds" element={
            <PermissionGuard permission="rounds:READ">
              <Suspense fallback={<PageLoader />}>
                <MedicalRoundsPage />
              </Suspense>
            </PermissionGuard>
          } />
          <Route path="ambulance" element={
            <PermissionGuard permission="ambulance:READ">
              <Suspense fallback={<PageLoader />}>
                <AmbulanceDispatchPage />
              </Suspense>
            </PermissionGuard>
          } />
          <Route path="teleconsultation" element={
            <PermissionGuard permission="teleconsultation:READ">
              <Suspense fallback={<PageLoader />}>
                <TeleconsultationPage />
              </Suspense>
            </PermissionGuard>
          } />
          <Route path="procurement" element={
            <PermissionGuard permission="procurement:READ">
              <Suspense fallback={<PageLoader />}>
                <ProcurementPage />
              </Suspense>
            </PermissionGuard>
          } />
          <Route path="assets" element={
            <PermissionGuard permission="assets:READ">
              <Suspense fallback={<PageLoader />}>
                <AssetManagementPage />
              </Suspense>
            </PermissionGuard>
          } />
          <Route path="audit" element={
            <PermissionGuard permission="audit:READ">
              <Suspense fallback={<PageLoader />}>
                <AuditLogsPage />
              </Suspense>
            </PermissionGuard>
          } />
          <Route path="events" element={
            <PermissionGuard permission="events:READ">
              <Suspense fallback={<PageLoader />}>
                <EventsPage />
              </Suspense>
            </PermissionGuard>
          } />
          <Route path="hr" element={
            <PermissionGuard permission="hr:READ">
              <Suspense fallback={<PageLoader />}>
                <HRManagementPage />
              </Suspense>
            </PermissionGuard>
          } />
          <Route path="inventory" element={
            <PermissionGuard permission="inventory:READ">
              <Suspense fallback={<PageLoader />}>
                <InventoryPage />
              </Suspense>
            </PermissionGuard>
          } />
          <Route path="billing" element={
            <PermissionGuard permission="billing:READ">
              <Suspense fallback={<PageLoader />}>
                <BillingPage />
              </Suspense>
            </PermissionGuard>
          } />
          <Route path="quality" element={
            <PermissionGuard permission="quality:READ">
              <Suspense fallback={<PageLoader />}>
                <QualityPage />
              </Suspense>
            </PermissionGuard>
          } />
          <Route path="surgery" element={
            <PermissionGuard permission="surgery:READ">
              <Suspense fallback={<PageLoader />}>
                <SurgeryPage />
              </Suspense>
            </PermissionGuard>
          } />
          <Route path="notifications" element={
            <PermissionGuard permission="notifications:READ">
              <Suspense fallback={<PageLoader />}>
                <NotificationsPage />
              </Suspense>
            </PermissionGuard>
          } />
          <Route path="reporting" element={
            <PermissionGuard permission="reporting:READ">
              <Suspense fallback={<PageLoader />}>
                <ReportingPage />
              </Suspense>
            </PermissionGuard>
          } />
          <Route path="facilities" element={
            <PermissionGuard permission="facilities:READ">
              <Suspense fallback={<PageLoader />}>
                <FacilitiesPage />
              </Suspense>
            </PermissionGuard>
          } />
          <Route path="profile" element={
            <Suspense fallback={<PageLoader />}>
              <ProfilePage />
            </Suspense>
          } />
          <Route path="public-health" element={
            <PermissionGuard permission="public_health:READ">
              <Suspense fallback={<PageLoader />}>
                <PublicHealthPage />
              </Suspense>
            </PermissionGuard>
          } />
          <Route path="wellness" element={
            <PermissionGuard permission="wellness:READ">
              <Suspense fallback={<PageLoader />}>
                <WellnessPage />
              </Suspense>
            </PermissionGuard>
          } />
          <Route path="admin/users" element={
            <PermissionGuard permission="users:READ">
              <Suspense fallback={<PageLoader />}>
                <UserManagementPage />
              </Suspense>
            </PermissionGuard>
          } />
          <Route path="settings" element={
            <PermissionGuard roles={['ADMIN', 'SUPER_ADMIN']}>
              <div className="p-6">Paramètres</div>
            </PermissionGuard>
          } />
          <Route path="404" element={<NotFoundPage />} />
          <Route path="*" element={<NotFoundPage />} />
        </Route>
      </Routes>
    </ErrorBoundary>
  );
}

export default function App() {
  return <AppRoutes />;
}
