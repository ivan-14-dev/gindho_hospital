import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { QueryClientProvider } from '@tanstack/react-query';
import { AuthProvider } from '@/contexts/auth-context';
import { initSentry } from '@/lib/monitoring/sentry';
import { initWebVitals } from '@/lib/monitoring/web-vitals';
import { BrowserRouter } from 'react-router-dom';
import { queryClient } from '@/lib/query-client';
import App from '@/App';
import '@/index.css';

initSentry();
initWebVitals();

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <BrowserRouter>
      <QueryClientProvider client={queryClient}>
        <AuthProvider>
          <App />
        </AuthProvider>
      </QueryClientProvider>
    </BrowserRouter>
  </StrictMode>
);
