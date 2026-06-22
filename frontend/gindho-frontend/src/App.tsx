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
const AIAssistantPage = lazy(() => import('@/pages/ai/AIAssistant').then(m => ({ default: m.AIAssistantPage })));
const AnalyticsPage = lazy(() => import('@/pages/analytics/Analytics').then(m => ({ default: m.AnalyticsPage })));
const EmergencyPage = lazy(() => import('@/pages/emergency/Emergency').then(m => ({ default: m.EmergencyPage as React.ComponentType<object> })));
const ImagingPage = lazy(() => import('@/pages/imaging/Imaging').then(m => ({ default: m.ImagingPage })));
const ChatPage = lazy(() => import('@/pages/chat/Chat').then(m => ({ default: m.ChatPage })));

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
           <Route path="hospitalization" element={<div className="p-6">Hospitalisation - En construction</div>} />
          <Route path="settings" element={<div className="p-6">Paramètres - En construction</div>} />
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
