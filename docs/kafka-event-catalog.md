# GinDHO — Catalogue d'Événements Kafka

> Domains, topics, schemas et stratégie Outbox/Saga.
> Aligné sur PLAN_AMELIORATION_SYSTEME.md §2.3 et ARCHITECTURE.md §4.

## Architecture Event-Driven

```
┌──────────────┐     ┌───────────┐     ┌────────────────┐
│   Service    │──▶  │  Outbox   │──▶  │  Kafka Topic   │──▶  Consommateurs
│  (transac.)  │     │  Table    │     │  (log compact) │
└──────────────┘     └───────────┘     └────────────────┘
                           │
                    ┌──────┴──────┐
                    │  CDC (Deb.) │  ← Optionnel: Debezium
                    └─────────────┘
```

## Règles

1. **Outbox Pattern obligatoire** : chaque service écrit dans sa table `outbox` au sein de la même transaction DB
2. **CDC** : Debezium lit l'outbox et publie sur Kafka (ou publication directe si pas de CDC)
3. **Avro/Protobuf** : tous les événements sont versionnés avec Schema Registry
4. **DLQ** : chaque consumer a une Dead Letter Queue avec retry exponentiel

## Topics Canoniques

### Patient Domain

| Topic | Key | Schéma | Producteur | Consommateurs |
|---|---|---|---|---|
| `patient.created` | `patientId` | `PatientCreatedEvent` | patient-service | appointment, emr, billing, notification, audit |
| `patient.updated` | `patientId` | `PatientUpdatedEvent` | patient-service | appointment, emr, audit |
| `patient.merged` | `patientId` | `PatientMergedEvent` | patient-service | all |
| `patient.deleted` | `patientId` | `PatientDeletedEvent` | patient-service | all |

### Appointment Domain

| Topic | Key | Schéma | Producteur | Consommateurs |
|---|---|---|---|---|
| `appointment.booked` | `appointmentId` | `AppointmentBookedEvent` | appointment-service | notification, patient, billing, audit |
| `appointment.confirmed` | `appointmentId` | `AppointmentConfirmedEvent` | appointment-service | notification, audit |
| `appointment.cancelled` | `appointmentId` | `AppointmentCancelledEvent` | appointment-service | notification, patient, billing, audit |
| `appointment.completed` | `appointmentId` | `AppointmentCompletedEvent` | appointment-service | medical-record, billing, audit |

### Billing Domain

| Topic | Key | Schéma | Producteur | Consommateurs |
|---|---|---|---|---|
| `invoice.created` | `invoiceId` | `InvoiceCreatedEvent` | billing-service | notification, patient, audit |
| `invoice.paid` | `invoiceId` | `InvoicePaidEvent` | billing-service | notification, payment, audit |
| `invoice.overdue` | `invoiceId` | `InvoiceOverdueEvent` | billing-service | notification, audit |

### Medical Domain

| Topic | Key | Schéma | Producteur | Consommateurs |
|---|---|---|---|---|
| `prescription.issued` | `prescriptionId` | `PrescriptionIssuedEvent` | prescription-service | pharmacy, notification, audit |
| `lab.result.ready` | `analysisId` | `LabResultReadyEvent` | laboratory-service | notification, medical-record, audit |
| `admission.started` | `admissionId` | `AdmissionStartedEvent` | admission-service | ward, bed, billing, audit |
| `admission.discharged` | `admissionId` | `AdmissionDischargedEvent` | admission-service | ward, bed, billing, audit |
| `emergency.triaged` | `emergencyId` | `EmergencyTriagedEvent` | emergency-service | notification, admission, audit |

### System Domain

| Topic | Key | Schéma | Producteur | Consommateurs |
|---|---|---|---|---|
| `audit.log` | `eventId` | `AuditLogEvent` | audit-service | audit (log-compacted) |
| `notification.sent` | `notificationId` | `NotificationSentEvent` | notification-service | audit |
| `config.changed` | `configKey` | `ConfigChangedEvent` | config-service | all (hot-reload) |

## Schemas Avro (exemples)

### PatientCreatedEvent.avsc

```json
{
  "type": "record",
  "name": "PatientCreatedEvent",
  "namespace": "com.gindho.event.patient",
  "fields": [
    { "name": "eventId", "type": "string" },
    { "name": "patientId", "type": "int" },
    { "name": "nom", "type": "string" },
    { "name": "prenom", "type": "string" },
    { "name": "email", "type": ["null", "string"], "default": null },
    { "name": "dateNaissance", "type": "string" },
    { "name": "createdAt", "type": "string", "logicalType": "timestamp-millis" },
    { "name": "source", "type": "string", "default": "patient-service" },
    { "name": "version", "type": "int", "default": 1 }
  ]
}
```

### AppointmentBookedEvent.avsc

```json
{
  "type": "record",
  "name": "AppointmentBookedEvent",
  "namespace": "com.gindho.event.appointment",
  "fields": [
    { "name": "eventId", "type": "string" },
    { "name": "appointmentId", "type": "int" },
    { "name": "patientId", "type": "int" },
    { "name": "medecinId", "type": "int" },
    { "name": "dateHeureDebut", "type": "string" },
    { "name": "dateHeureFin", "type": "string" },
    { "name": "motif", "type": ["null", "string"], "default": null },
    { "name": "createdAt", "type": "string" },
    { "name": "version", "type": "int", "default": 1 }
  ]
}
```

## Sagas

### Admission → Bed Allocation → Billing

```
[admission-service]     [ward-service]         [billing-service]
     │                      │                       │
     │──AdmissionStarted───▶│                       │
     │                      │──BedAllocated────────▶│
     │                      │                       │──InvoiceCreated──▶
     │◀───AdmissionConfirmed                        │
     │                      │                       │
     │──AdmissionFailed────▶│                       │  (compensation)
     │                      │──BedReleased─────────▶│  (compensation)
```

## DLQ (Dead Letter Queue)

Chaque topic a un topic DLQ associé : `<original-topic>.dlq`

**Format du message DLQ :**
```json
{
  "originalTopic": "patient.created",
  "originalKey": "123",
  "originalValue": "{...}",
  "errorType": "DeserializationError",
  "errorMessage": "Invalid Avro schema version",
  "timestamp": "2026-06-09T12:00:00Z",
  "retryCount": 3
}
```

**Politique de retry :**
- Retry 1: après 10s
- Retry 2: après 30s
- Retry 3: après 60s
- Après 3 échecs → DLQ
- Notification Slack sur DLQ non vide
