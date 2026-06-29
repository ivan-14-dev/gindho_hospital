# Missing Frontend Implementation - Action Plan

## Overview

This document outlines the 8 critical pages/services that need frontend implementation to reach 95%+ coverage of the GinDHO backend microservices.

---

## PRIORITY 1 - CRITICAL SERVICES (Days 1-4)

### 1. Ambulance Service - Dispatch Management
**File**: `src/pages/ambulance/AmbulanceDispatch.tsx` (350 LOC)

**Purpose**: Manage ambulance requests, assignments, and real-time tracking

**Required Features**:
- [ ] Create ambulance request form
  - Pickup location (address + coordinates)
  - Destination location (hospital/address)
  - Patient info & urgency level
  - Medical equipment required
  
- [ ] Ambulance assignment board
  - Available ambulances list (real-time)
  - Current location display (map integration)
  - Response time ETA
  - Driver phone & info
  
- [ ] Request status tracking
  - Status tabs: Requested → Assigned → In Transit → Arrived → Completed
  - Real-time location updates (WebSocket/polling)
  - ETA countdown
  - Driver notification system
  
- [ ] Ambulance management
  - Fleet inventory
  - Maintenance history
  - Equipment check-in/out
  - Driver schedules

**API Endpoints Needed**:
```typescript
ambulanceApi.getRequests()
ambulanceApi.createRequest()
ambulanceApi.assignAmbulance()
ambulanceApi.updateStatus()
ambulanceApi.getFleet()
ambulanceApi.trackLocation()
```

**Types Used**:
- AmbulanceRequest
- Ambulance
- ApiResponse<T>

**UI Components**:
- Map component (Google Maps / Leaflet)
- Real-time status badge
- Driver info card
- Timeline component
- Form dialog for new requests

---

### 2. Teleconsultation Service - Telehealth Platform
**File**: `src/pages/teleconsultation/Teleconsultation.tsx` (400 LOC)

**Purpose**: Enable video/audio consultations between doctors and patients

**Required Features**:
- [ ] Consultation scheduling
  - Calendar picker
  - Doctor availability slots
  - Time zone support
  - Reminder notifications
  
- [ ] Video call interface
  - WebRTC video/audio stream (using Jitsi/Daily/Twilio)
  - Screen sharing capability
  - Recording option
  - Chat during call
  - Virtual waiting room
  
- [ ] Prescription during consultation
  - Write prescription in-call
  - Medication selection from pharmacy inventory
  - Dosage & frequency
  - Auto-send to patient
  
- [ ] Consultation records
  - Recording storage
  - Auto-generated notes
  - Transcription (optional)
  - Follow-up scheduling
  
- [ ] Patient portal
  - Upcoming consultations
  - Join consultation link
  - Previous consultation history
  - Download prescriptions

**API Endpoints Needed**:
```typescript
teleconsultationApi.scheduleConsultation()
teleconsultationApi.getConsultations()
teleconsultationApi.startCall()
teleconsultationApi.endCall()
teleconsultationApi.saveRecording()
teleconsultationApi.generateTranscription()
```

**Types Used**:
- Consultation
- Prescription
- MedicalRecord
- ApiResponse<T>

**UI Components**:
- Video frame container
- Call controls (mute, hang up, screen share)
- Prescription form (pre-filled doctor info)
- Chat widget
- Timer component

**External Libraries**:
- `@jitsi/react-sdk` or similar
- `react-simple-peer` for WebRTC

---

### 3. Nursing Service - Care Plans & Task Management
**File**: `src/pages/nursing/NursingCare.tsx` (300 LOC)

**Purpose**: Manage patient care tasks, vital signs, and nursing workflows

**Required Features**:
- [ ] Assigned patients list
  - Patient vitals (current)
  - Care priority/urgency
  - Tasks pending
  - Last check-in time
  
- [ ] Patient care plan
  - Daily care tasks (medications, dressings, exercises)
  - Vital signs monitoring schedule
  - Dietary requirements
  - Mobility assistance needed
  - Special precautions
  
- [ ] Vital signs tracking
  - Blood pressure, heart rate, temperature, oxygen
  - Graph history (24h, 7d, 30d)
  - Abnormality alerts
  - Auto-notify doctor if critical
  
- [ ] Task management
  - Task list by priority
  - Check-off completion
  - Time tracking
  - Notes/observations
  - Task reassignment
  
- [ ] Shift handover
  - Incoming nurse reports
  - Patient status summary
  - Critical alerts summary
  - Task handoff checklist

**API Endpoints Needed**:
```typescript
nursingApi.getAssignedPatients()
nursingApi.getCarePlan()
nursingApi.recordVitalSigns()
nursingApi.getVitalSigns()
nursingApi.getTasks()
nursingApi.completeTask()
nursingApi.createHandoverReport()
```

**Types Used**:
- Patient
- MedicalRecord
- Prescription
- Vital Signs (custom)
- Care Task (custom)

**UI Components**:
- Patient cards grid
- Vital signs chart
- Task checklist
- Handover form
- Alert badges

---

## PRIORITY 2 - IMPORTANT SERVICES (Days 5-7)

### 4. Medical Rounds Service - Ward Rounds Management
**File**: `src/pages/rounds/MedicalRounds.tsx` (250 LOC)

**Purpose**: Manage doctor ward rounds and patient assessments

**Required Features**:
- [ ] Round scheduling
  - Schedule rounds by ward
  - Doctor assignment
  - Expected duration
  - Participants list (nurses, specialists)
  
- [ ] Patient progression tracking
  - Patient assessment form
  - Current diagnosis
  - Recent test results summary
  - Treatment effectiveness
  - Necessary adjustments
  
- [ ] Round notes
  - Structured clinical notes
  - Photo/image upload
  - Medical terminology autocomplete
  - Digital signature
  
- [ ] Follow-up actions
  - New prescriptions
  - Test/imaging orders
  - Specialist referrals
  - Discharge planning

**API Endpoints Needed**:
```typescript
roundApi.scheduleRound()
roundApi.getRounds()
roundApi.startRound()
roundApi.updatePatientAssessment()
roundApi.saveRoundNotes()
roundApi.completeRound()
```

**Types Used**:
- MedicalRound (existing)
- Prescription
- LaboratoryTest
- ApiResponse<T>

**UI Components**:
- Round schedule timeline
- Patient cards (in-round)
- Assessment form
- Notes editor
- Follow-up action list

---

### 5. Procurement Service - Purchase Order Management
**File**: `src/pages/procurement/Procurement.tsx` (300 LOC)

**Purpose**: Manage supplier orders and procurement workflows

**Required Features**:
- [ ] Purchase order creation
  - Item selection from catalog
  - Quantity & price entry
  - Supplier selection
  - Delivery date
  - Budget approval
  
- [ ] Supplier management
  - Supplier directory
  - Contact information
  - Payment terms
  - Performance rating
  - Previous orders history
  
- [ ] Order tracking
  - Order status (draft/submitted/confirmed/delivered)
  - Receiving inspection
  - Invoice matching (3-way match)
  - Payment processing
  
- [ ] Analytics
  - Spending by supplier
  - Delivery performance
  - Cost trends
  - Budget utilization

**API Endpoints Needed**:
```typescript
procurementApi.createPO()
procurementApi.getPOs()
procurementApi.updatePOStatus()
procurementApi.getSuppliers()
procurementApi.createSupplier()
procurementApi.recordReceipt()
procurementApi.getProcurementAnalytics()
```

**Types Used**:
- ProcurementOrder
- OrderItem
- Supplier
- InventoryItem
- ApiResponse<T>

**UI Components**:
- PO form with item selector
- Supplier list/cards
- Order status timeline
- Receipt form
- Analytics charts

---

### 6. Asset Service - Equipment & Asset Inventory
**File**: `src/pages/assets/AssetManagement.tsx` (250 LOC)

**Purpose**: Track hospital equipment, maintenance, and asset lifecycle

**Required Features**:
- [ ] Asset registry
  - Asset code/barcode
  - Equipment type/model
  - Purchase date & cost
  - Current location
  - Responsible staff member
  
- [ ] Maintenance tracking
  - Maintenance schedule
  - Service history
  - Maintenance alerts
  - Cost tracking
  - Vendor information
  
- [ ] Depreciation tracking
  - Useful life calculation
  - Salvage value
  - Depreciation schedule
  - Asset valuation reports
  
- [ ] Asset transfers/disposal
  - Transfer between departments
  - Decommissioning
  - Disposal reason
  - Audit trail

**API Endpoints Needed**:
```typescript
assetApi.getAssets()
assetApi.createAsset()
assetApi.updateAsset()
assetApi.getMaintenanceHistory()
assetApi.scheduleeMaintenance()
assetApi.recordMaintenance()
assetApi.transferAsset()
assetApi.getDepreciationReport()
```

**Types Used**:
- Asset (custom type needed)
- ApiResponse<T>

**UI Components**:
- Asset table/cards
- Asset detail page
- Maintenance form
- Transfer dialog
- Depreciation report

---

## PRIORITY 3 - COMPLIANCE & ADMIN (Days 8-9)

### 7. Audit Service - Audit Logs & Compliance
**File**: `src/pages/audit/AuditLogs.tsx` (200 LOC)

**Purpose**: Track all system activities for compliance and security audits

**Required Features**:
- [ ] Audit log viewer
  - Timestamp
  - User who performed action
  - Action type (create/read/update/delete)
  - Resource affected
  - Old value → New value
  - IP address & session info
  
- [ ] Advanced filtering
  - By user
  - By resource type
  - By date range
  - By action type
  - By status (success/failure)
  
- [ ] Export & reporting
  - Export to CSV/JSON
  - Generate compliance reports
  - PDF reports
  - Scheduled email reports
  
- [ ] Alert thresholds
  - Failed login attempts
  - Bulk data access
  - Sensitive field changes
  - Unauthorized access attempts

**API Endpoints Needed**:
```typescript
auditApi.getAuditLogs()
auditApi.filterAuditLogs()
auditApi.exportAuditLogs()
auditApi.generateComplianceReport()
auditApi.getAuditAlerts()
```

**Types Used**:
- AuditLog (existing)
- ApiResponse<T>

**UI Components**:
- Audit table (filterable/sortable)
- Filter form
- Export button
- Report builder
- Alert list

---

### 8. Event Service - Hospital Events & Communications
**File**: `src/pages/events/Events.tsx` (250 LOC)

**Purpose**: Manage hospital events, announcements, and team activities

**Required Features**:
- [ ] Event calendar
  - Create hospital events
  - Event types (training, meeting, announcement, etc.)
  - Date/time and location
  - Participant list
  - RSVP tracking
  
- [ ] Event management
  - Event details & agenda
  - Attendee registration
  - Reminders (email/SMS/app)
  - Event cancellation
  - Documentation/notes
  
- [ ] Announcements
  - Hospital-wide announcements
  - Department-specific
  - Urgent alerts
  - Read receipts
  
- [ ] Meeting rooms
  - Room booking calendar
  - Capacity info
  - Equipment available
  - Booking confirmation

**API Endpoints Needed**:
```typescript
eventApi.getEvents()
eventApi.createEvent()
eventApi.updateEvent()
eventApi.deleteEvent()
eventApi.registerForEvent()
eventApi.sendAnnouncement()
eventApi.bookRoom()
```

**Types Used**:
- Event (existing)
- Notification (existing)
- ApiResponse<T>

**UI Components**:
- Calendar view
- Event form
- Event cards
- Announcement banner
- Room booking form
- RSVP list

---

## Implementation Checklist

### Setup (All Pages)
- [ ] Create page component file
- [ ] Add route to App.tsx
- [ ] Create page-specific types if needed
- [ ] Add API service functions
- [ ] Add React Query hooks
- [ ] Create supporting components
- [ ] Add form validation (Zod)
- [ ] Implement error handling
- [ ] Add loading states
- [ ] Test all CRUD operations

### Testing (All Pages)
- [ ] Unit tests (React Testing Library)
- [ ] Component render tests
- [ ] Form submission tests
- [ ] API mock tests
- [ ] Integration tests

### Documentation (All Pages)
- [ ] JSDoc comments
- [ ] Usage examples
- [ ] API endpoint documentation
- [ ] User manual sections

---

## Estimated Implementation Time

| Priority | Services | Pages | Est. LOC | Est. Time |
|----------|----------|-------|---------|-----------|
| 1 | 3 services | 3 | ~1,050 | 3-4 days |
| 2 | 3 services | 3 | ~800 | 2-3 days |
| 3 | 2 services | 2 | ~450 | 1-2 days |
| **TOTAL** | **8 services** | **8** | **~2,300** | **6-9 days** |

---

## Final Coverage After Implementation

```
Current Coverage:    ███████░░░ 72%
After Priority 1:    ████████░░ 80%
After Priority 2:    █████████░ 90%
After Priority 3:    █████████░ 95%
With Enhancements:   ██████████ 100%
```

**Expected Final Frontend Completion**: 9-10 days from start  
**Total Lines of Code**: ~5,800+ LOC  
**Total Pages**: 36 comprehensive business pages

---

## Next Steps

1. Approve the prioritized implementation order
2. Assign developers to each page
3. Create feature branches for each page
4. Use this document as specification for each feature
5. Submit PRs with full test coverage
6. Deploy to staging for testing
7. Merge to production

