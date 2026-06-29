# Architecture Frontend GinDHO - Complete Medical Management System

## Overview

The GinDHO frontend is a comprehensive React 19 application consuming 39+ microservices via Kong API Gateway. It provides complete support for all hospital operations with role-based dashboards and advanced real-time features.

## Tech Stack

- **Framework**: React 19 with TypeScript
- **Build Tool**: Vite
- **State Management**: React Query (TanStack Query)
- **Form Management**: React Hook Form + Zod
- **UI Components**: shadcn/ui + Tailwind CSS
- **HTTP Client**: Custom Fetch-based API Client with retry logic
- **Routing**: React Router v6
- **Charts**: Recharts
- **Icons**: Lucide React

## Project Structure

```
src/
├── components/          # Reusable UI components
│   ├── ui/             # shadcn/ui base components
│   ├── layout/         # Layout wrappers
│   └── forms/          # Form components
├── pages/              # Page components (route views)
│   ├── dashboard/      # Dashboard (multi-role)
│   ├── patients/       # Patient management
│   ├── admissions/     # Hospital admissions
│   ├── wards/          # Ward management
│   ├── surgery/        # Surgical operations
│   ├── billing/        # Billing & invoices
│   ├── inventory/      # Inventory management
│   ├── hr/             # HR management
│   ├── quality/        # Quality & incidents
│   └── auth/           # Authentication
├── hooks/              # React hooks
│   ├── use-api.ts      # All API hooks (React Query)
│   └── use-*.ts        # Specialized hooks
├── services/           # API service layer
│   ├── api.service.ts  # All 39 microservice endpoints
│   ├── auth.service.ts # Authentication
│   └── *.service.ts    # Domain-specific services
├── lib/                # Utilities & helpers
│   ├── api-client.ts   # HTTP client with retry
│   ├── config.ts       # Configuration (Kong Gateway)
│   └── utils.ts        # Helper functions
├── types/              # TypeScript interfaces (350+ types)
│   └── index.ts        # Comprehensive type definitions
├── contexts/           # React Context (Auth, Theme)
└── App.tsx             # Root component with routing

```

## Key Features

### 1. Authentication & Authorization
- **Login/Register**: Via auth microservice
- **JWT Tokens**: AccessToken + RefreshToken
- **Permission-based**: RBAC with per-action permissions
- **Auto-logout**: On 401 (Unauthorized)

### 2. Core Modules (39 Microservices)

#### Clinical
- **Patients**: Search, create, update, delete
- **Appointments**: Scheduling, confirmations, cancellations
- **Medical Records**: Consultation history, diagnoses
- **Lab Tests**: Ordering, results tracking
- **Imaging**: X-ray, CT, MRI, Ultrasound requests
- **Pharmacy**: Prescriptions, dispensations, inventory
- **Surgery**: Operating room scheduling, reports

#### Hospital Operations
- **Admissions**: Patient admissions, discharge planning
- **Wards**: Bed management, occupancy tracking
- **Ambulance**: Request dispatch, GPS tracking
- **Emergency**: Urgent case management

#### Business
- **Billing**: Invoice generation, payment tracking
- **Inventory**: Stock management, procurement
- **Insurance**: Policy verification, coverage calculation
- **Payments**: Multiple payment methods support

#### Administration
- **HR**: Employee management, scheduling, leave requests
- **Quality**: Incident reporting, compliance tracking
- **Audit Logs**: Full activity logging
- **Notifications**: Real-time alerts and updates
- **Events & Rounds**: Medical rounds, team coordination

### 3. Multi-Role Dashboards

#### Admin Dashboard
- System-wide metrics (patients, occupancy, revenue)
- Alerts (critical incidents, stock alerts)
- 7-day trends chart (admissions/patients)
- Occupancy pie chart
- Critical incident tracking

#### Doctor Dashboard
- Patient list by doctor
- Appointment schedules
- Pending test results
- Surgery schedules
- Clinical alerts

#### Nurse Dashboard
- Assigned patients (12 max)
- Medications to administer
- Planned care activities
- Vital sign alerts
- Priority tasks checklist

#### Patient Dashboard
- Upcoming appointments
- Test results
- Active prescriptions
- Billing status
- Health history

### 4. Advanced Features

#### Real-time Updates
- WebSocket notifications
- Auto-refresh on mutations
- Optimistic updates via React Query
- Stale-while-revalidate caching

#### Search & Filtering
- Advanced search across all modules
- Multi-field filtering
- Date range filters
- Status-based filtering

#### Reporting & Export
- PDF export for records
- Excel export for bulk operations
- CSV data download
- Custom report generation

#### Responsive Design
- Mobile-first approach
- Tablet optimization
- Desktop full-width layouts
- Touch-friendly interfaces

## API Integration

### HTTP Client (`lib/api-client.ts`)

```typescript
// Features:
- Automatic JWT token injection (Authorization header)
- Built-in retry logic (3 attempts, 1s exponential backoff)
- 30-second timeout per request
- Error handling with status codes
- 401 auto-logout trigger
- 403 permission denied handling
```

### Configuration (`lib/config.ts`)

```typescript
// Kong API Gateway routing
const API_CONFIG = {
  GATEWAY_URL: 'http://localhost:8000', // Kong
  SERVICES: {
    AUTH: '/api/auth',
    PATIENTS: '/api/patients',
    APPOINTMENTS: '/api/appointments',
    // ... 36 more services
  },
};
```

### API Service Layer (`services/api.service.ts`)

All 39 microservices are organized into logical groups:

```typescript
// 100+ endpoint functions covering:
authApi.login()
patientsApi.getPatients(), createPatient(), etc.
appointmentsApi.getAppointments(), etc.
medicalRecordsApi.getConsultations(), etc.
laboratoryApi.getAnalyses(), etc.
imagingApi.requestImaging(), etc.
pharmacyApi.getPrescriptions(), etc.
billingApi.getInvoices(), etc.
hospitalisationApi.getAdmissions(), etc.
inventoryApi.getStocks(), etc.
hrApi.getEmployees(), etc.
// ... and more
```

## React Hooks (React Query)

### Hook Organization (`hooks/use-api.ts`)

Comprehensive hooks for all 39 services with 100+ hooks:

```typescript
// Query Hooks (data fetching)
usePatients(params?)
useAppointments(params?)
useLaboratoryTests(params?)
useMedicalRecords(patientId?)
// ... + 50 more query hooks

// Mutation Hooks (create/update/delete)
useCreatePatient()
useUpdatePatient()
useDeletePatient()
useCreateAppointment()
useDischargePatient()
// ... + 30 more mutation hooks

// Specialized Hooks
useAuth()
useLogin()
useLogout()
useDashboardStats()
useNotifications()
useInventoryAlerts()
```

### Features
- **Automatic Caching**: Stale time varies by data type
- **Invalidation**: Auto-invalidate on mutations
- **Loading States**: `isLoading`, `isPending` status
- **Error Handling**: Built-in error states
- **Optimistic Updates**: Real-time UI feedback
- **Pagination**: Built-in support

## Type System (350+ Types)

Complete TypeScript coverage organized by domain:

```typescript
// Core
User, Permission, AuthResponse

// Clinical
Patient, Appointment, MedicalRecord, Prescription
LaboratoryTest, ImagingRequest, Dispensation

// Operations
Admission, Discharge, Ward, Bed, SurgerySchedule
OperatingRoom, AmbulanceRequest, Ambulance

// Business
Invoice, Payment, InventoryItem, ProcurementOrder
Supplier, Employee, Schedule, Leave

// Quality
QualityIncident, AuditLog, Notification

// API
PaginatedResponse, ApiError, ApiResponse

// Dashboard
Dashboard, DashboardWidget, Report
```

## Pages & Components

### Core Pages
- **Login**: Authentication entry point
- **Dashboard**: Multi-role dashboard (admin/doctor/nurse/patient)
- **Patients**: Patient directory with search/filter
- **Appointments**: Scheduling & management
- **Medical Records**: Consultation history

### Hospital Operations
- **Admissions**: Patient admission workflow
- **Wards**: Bed management & occupancy
- **Surgery**: Operating room scheduling
- **Ambulance**: Dispatch & tracking

### Business Operations
- **Billing**: Invoice & payment management
- **Inventory**: Stock level tracking
- **HR**: Employee & schedule management
- **Quality**: Incident reporting

### Utilities
- **Notifications**: Real-time alerts
- **Audit Logs**: Activity tracking
- **Reports**: Data export & analytics

## Data Flow

```
User Action
    ↓
React Component
    ↓
useHook (React Query)
    ↓
API Service (services/api.service.ts)
    ↓
HTTP Client (lib/api-client.ts)
    ↓
Kong API Gateway (:8000)
    ↓
Microservice (Spring Boot)
    ↓
Database (PostgreSQL/MongoDB)
```

## State Management Strategy

### React Query (Server State)
- **Use for**: API data, remote state
- **Cache time**: 2-30 minutes (varies by data freshness)
- **Auto-refetch**: On window focus, network reconnect

### React State (Local State)
- **Use for**: UI state (modals, filters, pagination)
- **Scope**: Component or small component tree

### Context API (Global State)
- **Auth Context**: User, permissions, login/logout
- **Theme Context**: Dark/light mode
- **Toast Context**: Notifications

## Performance Optimizations

1. **Code Splitting**: Lazy-loaded pages via React.lazy()
2. **Component Memoization**: React.memo on expensive components
3. **Query Caching**: Stale-while-revalidate strategy
4. **Pagination**: Lazy-load list items
5. **Image Optimization**: Responsive images with srcset
6. **Bundle Size**: Tree-shaking, dynamic imports
7. **Rendering**: Virtualization for long lists

## Error Handling

### Global Error Boundaries
- Catches React rendering errors
- Shows fallback UI
- Logs to monitoring service

### API Error Handling
```typescript
try {
  const data = await apiClient.get(url);
} catch (error) {
  if (error.status === 401) logout();  // Auth expired
  if (error.status === 403) navigate('/unauthorized');
  show toast with error message;
}
```

### Form Validation
```typescript
const schema = z.object({
  email: z.string().email('Email invalide'),
  password: z.string().min(8, 'Min 8 caractères'),
});

const form = useForm({
  resolver: zodResolver(schema),
});
```

## Security

1. **JWT Auth**: Token in localStorage (consider Secure HttpOnly cookie for production)
2. **CORS**: Configured at Kong Gateway level
3. **Input Validation**: Zod schemas on all forms
4. **XSS Protection**: React auto-escapes content
5. **CSRF**: CSRF tokens via Kong configuration
6. **Rate Limiting**: Handled at Kong level
7. **Audit Logging**: All mutations logged server-side

## Browser Support

- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

## Getting Started

### Installation

```bash
cd frontend/gindho-frontend
npm install
# or
pnpm install
```

### Development

```bash
npm run dev
```

Runs on `http://localhost:5173`

### Build

```bash
npm run build
```

### Environment Variables

```env
VITE_GATEWAY_URL=http://localhost:8000
VITE_API_TIMEOUT=30000
VITE_RETRY_ATTEMPTS=3
```

## Testing Strategy

- **Unit Tests**: Jest + React Testing Library
- **E2E Tests**: Playwright (key workflows)
- **API Mocking**: MSW (Mock Service Worker)
- **Accessibility**: Axe DevTools

## Monitoring & Analytics

- **Error Tracking**: Sentry integration (recommended)
- **Performance Monitoring**: Web Vitals (LCP, INP, CLS)
- **User Analytics**: Hotjar/Mixpanel (recommended)
- **API Monitoring**: Kong admin metrics

## Deployment

### Development
```bash
npm run dev
```

### Production Build
```bash
npm run build
npm run preview
```

### Docker
```dockerfile
FROM node:20-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build
EXPOSE 3000
CMD ["npm", "run", "preview"]
```

## Common Tasks

### Add New Page
1. Create `src/pages/module/Module.tsx`
2. Add hooks to `src/hooks/use-api.ts`
3. Add API functions to `src/services/api.service.ts`
4. Add route to `App.tsx`
5. Add navigation link

### Add API Hook
1. Add to `services/api.service.ts`
2. Create hook in `hooks/use-api.ts`
3. Add query key constant
4. Export from hooks

### Form Validation
```typescript
const schema = z.object({
  email: z.string().email(),
  age: z.number().int().positive(),
});

const form = useForm({
  resolver: zodResolver(schema),
  defaultValues: {},
});
```

## Troubleshooting

### API Calls Failing
- Check Kong Gateway is running (`localhost:8000`)
- Verify VITE_GATEWAY_URL environment variable
- Check browser DevTools Network tab
- Verify JWT token in localStorage

### Build Errors
- Clear `node_modules` and `package-lock.json`
- Run `npm install` again
- Check Node version (18+)

### Performance Issues
- Use React DevTools Profiler
- Check for unnecessary re-renders
- Review React Query cache settings
- Analyze bundle size with `npm run build -- --analyze`

## Next Steps

1. **Complete Test Coverage**: Add unit & E2E tests
2. **Performance Monitoring**: Integrate Sentry/DataDog
3. **PWA Features**: Add service worker for offline support
4. **Accessibility**: Full WCAG 2.1 AA compliance
5. **Internationalization**: Add multi-language support (i18n)
6. **Dark Mode**: Complete dark theme implementation

## Contributing

- Follow TypeScript strict mode
- Add types for all functions
- Test all API integrations
- Document complex logic
- Use meaningful commit messages

## Support

For issues or questions:
1. Check error logs in browser DevTools
2. Review Kong Gateway logs
3. Check backend microservice logs
4. Open GitHub issue with reproduction steps
