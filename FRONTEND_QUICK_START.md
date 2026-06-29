# GinDHO Frontend - Quick Start Guide

## Prerequisites

- Node.js 18+ or 20+
- npm, pnpm, or yarn
- Kong API Gateway running on `localhost:8000`
- Backend microservices running

## Installation & Setup (5 minutes)

### 1. Install Dependencies

```bash
cd frontend/gindho-frontend
pnpm install
# or: npm install
```

### 2. Environment Variables

Create `.env.local` (or `.env.development.local`):

```env
VITE_GATEWAY_URL=http://localhost:8000
VITE_API_TIMEOUT=30000
VITE_RETRY_ATTEMPTS=3
```

### 3. Start Development Server

```bash
pnpm dev
```

Open browser: `http://localhost:5173`

## First Login

### Test Credentials

```
Email: admin@gindho.local
Password: Admin@123
Role: ADMIN
```

Other test accounts:
```
doctor@gindho.local / Doctor@123 (DOCTOR)
nurse@gindho.local / Nurse@123 (NURSE)
patient@gindho.local / Patient@123 (PATIENT)
```

## Key Pages & Features

### Dashboard (Home)
- **URL**: `/` (redirects based on role)
- **Features**: Multi-role dashboard with KPIs, charts, alerts
- **Roles**: Admin, Doctor, Nurse, Patient (different layouts)

### Patients
- **URL**: `/patients`
- **Features**: Search, create, view details
- **Operations**: CRUD + medical history

### Appointments
- **URL**: `/appointments`
- **Features**: Schedule, confirm, cancel
- **Multi-doctor**: Filter by doctor/date

### Medical Records
- **URL**: `/medical-records`
- **Features**: Consultation history, diagnoses
- **Associated**: Lab tests, prescriptions

### Admissions & Discharges
- **URL**: `/admissions`
- **Features**: Hospital admissions, discharge workflow
- **Operations**: Track admission status, plan discharges

### Wards & Beds
- **URL**: `/wards`
- **Features**: Ward occupancy, bed management
- **Visualization**: Occupancy rates, bed status

### Billing
- **URL**: `/billing`
- **Features**: Invoices, payments
- **Operations**: Create invoice, record payment, track outstanding

### Inventory
- **URL**: `/inventory`
- **Features**: Stock levels, alerts
- **Alerts**: Low stock, expired items

### HR Management
- **URL**: `/hr`
- **Features**: Employee directory, check-in
- **Operations**: Manage personnel, track presence

### Quality & Incidents
- **URL**: `/quality`
- **Features**: Report incidents, track resolution
- **Types**: Safety, infection, medication errors, equipment

### Surgery
- **URL**: `/surgery`
- **Features**: Operating room scheduling
- **Status**: Scheduled, in-progress, completed

## Common Tasks

### Search Patients
1. Go to **Patients** page
2. Enter search term (name, email, phone)
3. Results auto-filter
4. Click patient card to view details

### Create Patient
1. Go to **Patients** page
2. Click **"Nouveau patient"** button
3. Fill form (name, DOB, contact, etc.)
4. Click **"Créer"**

### Schedule Appointment
1. Go to **Appointments** page
2. Click **"Nouveau rendez-vous"** button
3. Select patient, doctor, date/time
4. Add reason/notes
5. Click **"Programmer"**

### Admit Patient
1. Go to **Admissions** page
2. Click **"Nouvelle admission"** button
3. Select patient, doctor, diagnosis
4. Choose admission type (emergency/planned/transfer)
5. Click **"Enregistrer"**

### Record Payment
1. Go to **Billing** page
2. Find invoice
3. Click **"Payer"** button
4. Enter amount, select payment method
5. Click **"Confirmer paiement"**

### Report Incident
1. Go to **Quality** page
2. Click **"Signaler un incident"** button
3. Fill form (type, severity, description, corrective actions)
4. Click **"Signaler"**

## API Integration

### What Endpoints Are Available?

Check `src/services/api.service.ts` for:
- 100+ endpoint functions
- 39+ microservices
- Complete CRUD operations

### Making New API Calls

1. **Add API function** in `services/api.service.ts`:
```typescript
export const myApi = {
  async getMyData(): Promise<MyType[]> {
    return apiClient.get(buildApiUrl('MY_SERVICE', '/endpoint'));
  },
};
```

2. **Create hook** in `hooks/use-api.ts`:
```typescript
export const useMyData = () => {
  return useQuery({
    queryKey: ['my-data'],
    queryFn: () => myApi.getMyData(),
  });
};
```

3. **Use in component**:
```typescript
const { data, isLoading } = useMyData();
```

## Debugging

### Check API Calls
1. Open Browser DevTools → Network tab
2. Look for API calls to `localhost:8000`
3. Check:
   - Status code (200 = success, 401 = auth, 500 = server error)
   - Request headers (Authorization: Bearer token)
   - Response payload

### Check React State
1. Install React DevTools extension
2. Click Components tab
3. Search for component name
4. View props and hooks in right panel

### Check Redux/React Query
1. Install React Query DevTools browser extension
2. Click icon in bottom-left
3. View cached queries and their status

### Common Errors

| Error | Cause | Solution |
|-------|-------|----------|
| 401 Unauthorized | Token expired | Login again |
| 403 Forbidden | No permission | Check role permissions |
| 404 Not Found | Wrong endpoint | Check API service |
| 500 Server Error | Backend error | Check microservice logs |
| Connection refused | Kong not running | Start Kong: `docker-compose up kong` |
| CORS error | Domain mismatch | Check VITE_GATEWAY_URL |

## Build & Deployment

### Production Build
```bash
pnpm build
```

Creates `dist/` folder with optimized bundle.

### Preview Build
```bash
pnpm build
pnpm preview
```

Test production build locally.

### Docker Build
```bash
docker build -t gindho-frontend:latest .
docker run -p 3000:3000 gindho-frontend:latest
```

### Deploy to Vercel
```bash
vercel deploy
```

## Performance Tips

1. **Use Pagination**: Don't load 10,000 records at once
2. **Cache Data**: React Query caches automatically (2-30 min)
3. **Lazy Load**: Big lists load on scroll (virtualization)
4. **Compress Images**: Reduce image file sizes
5. **Production Mode**: Build always for production deploys

## Keyboard Shortcuts

| Shortcut | Action |
|----------|--------|
| `Cmd/Ctrl + K` | Search (if implemented) |
| `Esc` | Close modals/dialogs |
| `Tab` | Navigate form fields |
| `Enter` | Submit forms |

## File Organization Quick Reference

```
src/
├── pages/
│   ├── patients/        # Patient management
│   ├── admissions/      # Admissions workflow
│   ├── wards/           # Ward & bed management
│   ├── billing/         # Invoicing
│   ├── inventory/       # Stock management
│   ├── hr/              # HR management
│   ├── quality/         # Quality & incidents
│   └── surgery/         # Surgery scheduling
├── hooks/
│   └── use-api.ts       # All 100+ API hooks
├── services/
│   └── api.service.ts   # All 39 microservices
└── types/
    └── index.ts         # 350+ TypeScript types
```

## Need More Help?

### Documentation Files
- `FRONTEND_ARCHITECTURE.md` - Full technical architecture
- `README.md` - Project overview
- `API_DOCUMENTATION.md` - Backend API details

### Common Resources
- React Docs: https://react.dev
- React Query: https://tanstack.com/query
- TypeScript: https://typescriptlang.org
- Tailwind CSS: https://tailwindcss.com

### Troubleshooting Commands
```bash
# Clear cache and reinstall
rm -rf node_modules package-lock.json
pnpm install

# Check TypeScript errors
pnpm type-check

# Lint code
pnpm lint

# Format code
pnpm format
```

## What's Included in This Frontend

✅ **Complete UI**: 25+ pages covering all hospital operations
✅ **API Integration**: 100+ hooks for 39 microservices
✅ **Authentication**: Login, permissions, role-based access
✅ **Type Safety**: 350+ TypeScript interfaces
✅ **Responsive Design**: Mobile, tablet, desktop
✅ **Error Handling**: Global error boundaries, API error handling
✅ **Forms**: React Hook Form + Zod validation
✅ **Real-time**: WebSocket notifications support
✅ **Charts**: Recharts for analytics dashboards
✅ **Accessibility**: Semantic HTML, ARIA labels

## What's NOT Included (To Add)

- [ ] Unit tests (Jest + React Testing Library)
- [ ] E2E tests (Playwright)
- [ ] Dark mode theme
- [ ] Internationalization (i18n)
- [ ] PWA features (offline support)
- [ ] Advanced charting (3D, maps)
- [ ] Video conferencing integration
- [ ] AI chat assistant

## Next Steps

1. ✅ Start dev server: `pnpm dev`
2. ✅ Login with test credentials
3. ✅ Explore dashboard (matches your role)
4. ✅ Try creating a patient
5. ✅ Schedule an appointment
6. ✅ Check API calls in DevTools
7. ✅ Read full architecture doc
8. ✅ Customize for your needs

## Support & Feedback

- **Issues**: Check GitHub issues for known problems
- **Questions**: Review documentation first
- **Feature Requests**: Open GitHub discussion
- **Bugs**: Report with error logs and reproduction steps

---

Happy coding! 🚀
