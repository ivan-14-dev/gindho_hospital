# Architecture Recommendations by Service  
*GinDHO Kubernetes Enterprise Platform*

---

## 1. Core Domain – Patient (`patient-service`)

| Aspect | Recommendation | Rationale |
|--------|----------------|-----------|
| **Replicas** | 3 | High availability required for patient lookup |
| **Resources** | 512 Mi / 250 m CPU (request), 1 Gi / 500 m (limit) | Large JPA model hydrations during batch ops |
| **PodDisruptionBudget** | minAvailable = 2 | Allows rolling updates without downtime |
| **NetworkPolicy** | Ingress only from `kong`, `istio-system`; Egress to `postgres`, `redis`, `kafka` | Zero‑trust principle |
| **Istio** | PeerAuthentication STRICT, DestinationRule with outlier detection (5xx threshold = 5) | Prevent cascading failures during admission peaks |
| **Kafka Topics** | `patient.created`, `patient.updated`, `patient.deleted`, `patient.merged` | Outbox pattern enables downstream billing/emr sync |
| **OpenTelemetry** | Trace context propagated to `medical-record-service`, `billing-service` | End‑to‑end traceability for GDPR audit |
| **Rate Limit (Kong)** | 60 req/min per IP | Prevent scraping of PHI |
| **JWT Audience** | `gindho-patient` | Audience claim restricts token abuse |

---

## 2. Core Domain – Appointment (`appointment-service`)

| Aspect | Recommendation | Rationale |
|--------|----------------|-----------|
| **Replicas** | 2 | Business hours traffic, HPA can scale to 10 |
| **Resources** | 256 Mi / 200 m CPU (request), 512 Mi / 400 m (limit) | Less memory than patient, but CPU intensive on conflict resolution |
| **PodDisruptionBudget** | minAvailable = 1 | Acceptable to lose 1 replica during off‑hours |
| **NetworkPolicy** | Ingress only from `kong`; Egress to `postgres`, `redis`, `kafka`, `notification-service` | Notification triggered on every booking change |
| **Istio** | Request retry (max = 3) for `medical-record-service` upstream | Retry booking sync on EMR failure |
| **Kafka Topics** | `appointment.booked`, `appointment.confirmed`, `appointment.cancelled`, `appointment.completed` | Triggers lab results, bills, and notifications |
| **ACL Plugin (Kong)** | Role `doctor`, `nurse`, `receptionist` allowed | RBAC enforcement at gateway |
| **VPA** | Max memory = 2 Gi during batch import of historical schedules | VPA can right‑size during nightly jobs |

---

## 3. EMR – Medical Record (`medical-record-service`)

| Aspect | Recommendation | Rationale |
|--------|----------------|-----------|
| **Replicas** | 2 | Document read frequency high; write frequency moderate |
| **Resources** | 384 Mi / 200 m CPU (request), 1 Gi / 500 m (limit) | MongoDB wiredtiger cache tuning |
| **PodDisruptionBudget** | minAvailable = 1 | EMR downtime is critical but tolerable with circuit breaker |
| **NetworkPolicy** | Ingress only from `kong`, `appointment-service`; Egress to `mongodb`, `kafka` | Only `appointment-service` writes on completed visits |
| **Sidecar** | `filebeat` logs shipping to Loki index `emr-` | File‑based logs contain large blobs, need special handling |
| **Kafka Topics** | `medical-record.updated`, `medical-record.closed` | Triggers billing for completed procedures |
| **DLQ** | Dead letter topic `medical-record-dlq` with 3 retries exponential backoff | Prevent record loss on malformed updates |

---

## 4. EMR – Imaging (`imaging-service`)

| Aspect | Recommendation | Rationale |
|--------|----------------|-----------|
| **Replicas** | 2 | Large file uploads benefit from parallel intake |
| **Resources** | 512 Mi / 300 m CPU (request), 2 Gi / 1 CPU (limit) | DICOM file handling uses significant memory |
| **PodDisruptionBudget** | minAvailable = 1 | Tolerate one replica down during scans |
| **NetworkPolicy** | Egress to `mongodb`, `kafka`, `minio` (object storage) | DICOM stored in MinIO via Kafka trigger |
| **Sidecar** | `dicom-gateway` for protocol conversion | External modalities speak DICOM, not REST |
| **Kafka Topics** | `imaging.study.completed` | Notify downstream EMR update |

---

## 5. Billing (`billing-service`)

| Aspect | Recommendation | Rationale |
|--------|----------------|-----------|
| **Replicas** | 3 | Financial transactions must be highly available |
| **Resources** | 512 Mi / 250 m CPU (request), 1 Gi / 500 m (limit) | Heavy reporting endpoints |
| **NetworkPolicy** | Ingress only from `kong`; Egress to `postgres`, `kafka` | Audit trail must be immutable |
| **Istio** | DestinationRule with connection pool 100 rps; outlier 5xx = 3 | Protect against payment gateway overload |
| **Kafka Topics** | `invoice.created`, `invoice.paid`, `invoice.overdue` | Triggers notification and analytics pipelines |
| **Rate Limit (Kong)** | 30 req/min per IP | PCI‑DSS compliance needs strict throttling |
| **JWT Claim** | Must contain `billing-scope` | Segregate financial access from clinical access |

---

## 6. Insurance (`insurance-service`)

| Aspect | Recommendation | Rationale |
|--------|----------------|-----------|
| **Replicas** | 2 | Coverage verification is read‑heavy |
| **Resources** | 256 Mi / 200 m CPU (request), 512 Mi / 400 m (limit) | Cache coverage rules in Redis |
| **NetworkPolicy** | Ingress from `billing-service`, `kong`; Egress to `postgres` | Internal verification endpoint only |
| **Kafka Topics** | `insurance.claim.submitted`, `insurance.claim.approved` | Batch adjudication at night |
| **Sidecar** | `envoy` with retry policy for external insurer APIs | Circuit breaker for external dependency |

---

## 7. Payment (`payment-service`)

| Aspect | Recommendation | Rationale |
|--------|----------------|-----------|
| **Replicas** | 2 | PCI‑DSS isolation zone |
| **Resources** | 256 Mi / 250 m CPU (request), 1 Gi / 1 CPU (limit) | Encrypt payload before insert |
| **NetworkPolicy** | No egress except to `postgres`; no internet egress | PCI zone isolation |
| **Sidecar** | Vault agent injector for tokenization | No raw card data in memory |
| **Rate Limit (Kong)** | 20 req/min per IP | Prevent carding attacks |

---

## 8. Pharmacy (`pharmacy-service`)

| Aspect | Recommendation | Rationale |
|--------|----------------|-----------|
| **Replicas** | 2 | Stock lookup intensive |
| **Resources** | 256 Mi / 200 m CPU (request), 1 Gi / 500 m (limit) | SKU search with Redis cache |
| **NetworkPolicy** | Egress to `postgres`, `kafka`, `redis` | Inventory updates via Kafka |
| **Kafka Topics** | `pharmacy.dispensed`, `pharmacy.lowstock` | Trigger procurement workflow |
| **DLQ** | `pharmacy-dlq` with alerting | Missed dispenses trigger alerts |

---

## 9. Prescription (`prescription-service`)

| Aspect | Recommendation | Rationale |
|--------|----------------|-----------|
| **Replicas** | 1 | Write model, low concurrency |
| **Resources** | 128 Mi / 100 m CPU (request), 512 Mi / 300 m (limit) | Small DB tables |
| **PodDisruptionBudget** | minAvailable = 1 | Single replica OK due to low load |
| **Kafka Topics** | `prescription.issued`, `prescription.cancelled` | Events to pharmacy and EMR |
| **Rate Limit (Kong)** | 120 req/min for doctors | Doctors may issue many prescriptions in a shift |

---

## 10. Laboratory (`laboratory-service`)

| Aspect | Recommendation | Rationale |
|--------|----------------|-----------|
| **Replicas** | 2 | Tests run all day |
| **Resources** | 256 Mi / 200 m CPU (request), 1 Gi / 600 m (limit) | HL7 v2 messages parsing |
| **NetworkPolicy** | Egress to `postgres`, `kafka` | Results published via Kafka |
| **Kafka Topics** | `lab.result.ready`, `lab.sample.received` | Connect to EMR for result updates |
| **Sidecar** | HL7 listener transform to JSON | Standardize lab data format |
| **Circuit Breaker (Istio)** | 5 consecutive 5xx errors eject | Protect lab from downstream EMR overload |

---

## 11. HR (`hr-service`)

| Aspect | Recommendation | Rationale |
|--------|----------------|-----------|
| **Replicas** | 1 | Internal admin use |
| **Resources** | 128 Mi / 100 m CPU (request), 512 Mi / 300 m (limit) | Staff directory queries |
| **NetworkPolicy** | Only internal HR traffic | RBAC enforced at gateway side |
| **Kafka Topics** | `staff.onboarded`, `staff.deactivated` | Triggers access provisioning in Keycloak |

---

## 12. Scheduling (`scheduling-service`)

| Aspect | Recommendation | Rationale |
|--------|----------------|-----------|
| **Replicas** | 1 | Low traffic |
| **Resources** | 128 Mi / 100 m CPU (request), 512 Mi / 300 m (limit) | Recurring job scheduler |
| **CronJob** | N/A – Runs inside the service | Nightly database vacuum for shifts |
| **Kafka Topics** | `schedule.updated`, `schedule.conflict` | Notify HR and doctors |

---

## 13. Inventory (`inventory-service`)

| Aspect | Recommendation | Rationale |
|--------|----------------|-----------|
| **Replicas** | 2 | Stock checks frequent |
| **Resources** | 256 Mi / 200 m CPU (request), 1 Gi / 500 m (limit) | SKU lookup via Redis |
| **NetworkPolicy** | Egress to `postgres`, `kafka`, `redis` | Stock alerts via Kafka |
| **Kafka Topics** | `inventory.item.lowstock`, `inventory.count.updated` | Procurement service subscribes |

---

## 14. Procurement (`procurement-service`)

| Aspect | Recommendation | Rationale |
|--------|----------------|-----------|
| **Replicas** | 1 | Purchase order generation not time critical |
| **Resources** | 128 Mi / 100 m CPU (request), 512 Mi / 300 m (limit) | Simple state machine |
| **Kafka Topics** | `procurement.order.placed`, `procurement.received` | Tracks PO lifecycle |

---

## 15. Assets (`asset-service`)

| Aspect | Recommendation | Rationale |
|--------|----------------|-----------|
| **Replicas** | 1 | Equipment tracking via RFID |
| **Resources** | 128 Mi / 100 m CPU (request), 512 Mi / 300 m (limit) | MQTT bridge optional sidecar |
| **Egress** | Only to `postgres` | Asset registry |

---

## 16. Admission (`admission-service`)

| Aspect | Recommendation | Rationale |
|--------|----------------|-----------|
| **Replicas** | 2 | Emergency surge capacity |
| **Resources** | 256 Mi / 200 m CPU (request), 1 Gi / 500 m (limit) | Bed availability logic |
| **NetworkPolicy** | Ingress from `kong` and `emergency-service`; Egress to `postgres`, `kafka` | Syncs bed status |
| **Kafka Topics** | `admission.started`, `admission.discharged` | Update ward and billing |

---

## 17. Emergency (`emergency-service`)

| Aspect | Recommendation | Rationale |
|--------|----------------|-----------|
| **Replicas** | 2 | Triage traffic spikes |
| **Resources** | 384 Mi / 250 m CPU (request), 1.5 Gi / 1 CPU (limit) | Image uploads for triage photos |
| **NetworkPolicy** | Ingress only from `kong`; Egress to `postgres`, `kafka`, `notification-service` | Immediate alerts |
| **Kafka Topics** | `emergency.triaged`, `emergency.priority` | Notification service urgent alerts |
| **Rate Limit (Kong)** | 30 req/min per IP, 100 burst | Emergency workstation traffic |

---

## 18. Ward (`ward-service`)

| Aspect | Recommendation | Rationale |
|--------|----------------|-----------|
| **Replicas** | 1 | Bed management low frequency |
| **Resources** | 128 Mi / 100 m CPU (request), 512 Mi / 300 m (limit) | Simple state model |
| **Kafka Topics** | `ward.bed.allocated`, `ward.bed.released` | Coordinates with admission |

---

## 19. Bed (`bed-service`)

| Aspect | Recommendation | Rationale |
|--------|----------------|-----------|
| **Replicas** | 1 | Simple status API |
| **Resources** | 128 Mi / 100 m CPU (request), 512 Mi / 300 m (limit) | Bed state machine |
| **Kafka Topics** | `bed.status.changed` | Stream to monitoring dashboard |

---

## 20. Round (`round-service`)

| Aspect | Recommendation | Rationale |
|--------|----------------|-----------|
| **Replicas** | 1 | Doctor round tracking |
| **Resources** | 128 Mi / 100 m CPU (request), 512 Mi / 300 m (limit) | Small traffic |
| **Kafka Topics** | `round.completed` | Notify EMR of follow‑ups |

---

## 21. Surgery (`surgery-service`)

| Aspect | Recommendation | Rationale |
|--------|----------------|-----------|
| **Replicas** | 2 | OR scheduling intensive |
| **Resources** | 256 Mi / 200 m CPU (request), 1 Gi / 500 m (limit) | Schedule optimization |
| **Kafka Topics** | `surgery.scheduled`, `surgery.completed` | Billing and EMR updates |

---

## 22. Ambulance (`ambulance-service`)

| Aspect | Recommendation | Rationale |
|--------|----------------|-----------|
| **Replicas** | 1 | GPS tracking infrequent |
| **Resources** | 256 Mi / 200 m CPU (request), 512 Mi / 400 m (limit) | Location cache in Redis |
| **Kafka Topics** | `ambulance.dispatch`, `ambulance.arrived` | Integrate with EMR triage |
| **Sidecar** | MQTT bridge for real‑time GPS | Ambulances stream coordinates |

---

## 22. Event Service (Infrastructure)

| Aspect | Recommendation | Rationale |
|--------|----------------|-----------|
| **Replicas** | 2 | Centralized event inbox |
| **Resources** | 256 Mi / 200 m CPU (request), 1 Gi / 500 m (limit) | Event normalization |
| **Kafka Topics** | `dlq.global` | Universal dead letter queue |
| **Istio** | Retry policy for Kafka | Ensure event durability |

---

## 23. Notification (`notification-service`)

| Aspect | Recommendation | Rationale |
|--------|----------------|-----------|
| **Replicas** | 2 | Email/SMS spikes |
| **Resources** | 256 Mi / 200 m CPU (request), 1 Gi / 500 m (limit) | Template rendering |
| **Kafka Topics** | All event topics consumed | Multi-Channel delivery |
| **Rate Limit (Kong)** | Unlimited (internal) | Must not throttle alerts |

---

## 24. Reporting (`reporting-service`)

| Aspect | Recommendation | Rationale |
|--------|----------------|-----------|
| **Replicas** | 1 | Scheduled batch jobs |
| **Resources** | 512 Mi / 250 m CPU (request), 2 Gi / 1 CPU (limit) | Large aggregations |
| **CronJob** | Weekly KPI rollups | Heavy queries offloaded |

---

## 25. Identity (`identity-service`)

| Aspect | Recommendation | Rationale |
|--------|----------------|-----------|
| **Replicas** | 2 | Authentication critical |
| **Resources** | 256 Mi / 200 m CPU (request), 1 Gi / 500 m (limit) | JWT signing keys in HSM |
| **NetworkPolicy** | Only to Keycloak DB (postgres) | No egress needed |
| **Rate Limit (Kong)** | 20 req/min per IP | Brute force protection |
| **OpenID Connect** | Discovery endpoint at `/.well-known/openid-configuration` | Standard compliant |

---

## 26. Authorization (`authorization-service`)

| Aspect | Recommendation | Rationale |
|--------|----------------|-----------|
| **Replicas** | 1 | Policy decisions cached |
| **Resources** | 128 Mi / 100 m CPU (request), 512 Mi / 300 m (limit) | Small payload checks |
| **Kafka Topics** | `authz.decision` for audit | Record policy checks |

---

## 27. Audit (`audit-service`)

| Aspect | Recommendation | Rationale |
|--------|----------------|-----------|
| **Replicas** | 2 | High write volume |
| **Resources** | 384 Mi / 250 m CPU (request), 1 Gi / 500 m (limit) | Write‑optimized Cassandra sink |
| **NetworkPolicy** | Egress only to `kafka` | Immutable event trail |
| **Kafka Topics** | `audit.log` (compacted) | Retained forever |

---

## Summary Matrix

| Service | Replicas | CPU Request | Mem Request | NetPol (Zero Trust) | Kafka Topics | Rate Limit | mTLS |
|---------|----------|-------------|-------------|-------------------|--------------|------------|------|
| identity-service | 2 | 200 m | 256 Mi | ✅ | – | 20 rpm | ✅ |
| authorization-service | 1 | 100 m | 128 Mi | ✅ | authz.decision | – | ✅ |
| audit-service | 2 | 250 m | 384 Mi | ✅ | audit.log | – | ✅ |
| patient-service | 3 | 250 m | 512 Mi | ✅ | patient.{created,updated,deleted,merged} | 60 rpm | ✅ |
| appointment-service | 2 | 200 m | 256 Mi | ✅ | appointment.{booked,confirmed,cancelled,completed} | 60 rpm | ✅ |
| medical-record-service | 2 | 200 m | 384 Mi | ✅ | medical-record.{updated,closed} | – | ✅ |
| imaging-service | 2 | 300 m | 512 Mi | ✅ | imaging.study.completed | – | ✅ |
| billing-service | 3 | 250 m | 512 Mi | ✅ | invoice.{created,paid,overdue} | 30 rpm | ✅ |
| insurance-service | 2 | 200 m | 256 Mi | ✅ | insurance.claim.{submitted,approved} | – | ✅ |
| payment-service | 2 | 250 m | 256 Mi | ✅ | payment.processed | 20 rpm | ✅ |
| pharmacy-service | 2 | 200 m | 256 Mi | ✅ | pharmacy.{dispensed,lowstock} | – | ✅ |
| prescription-service | 1 | 100 m | 128 Mi | ✅ | prescription.{issued,cancelled} | 120 rpm (doctors) | ✅ |
| laboratory-service | 2 | 200 m | 256 Mi | ✅ | lab.result.ready | – | ✅ |
| hr-service | 1 | 100 m | 128 Mi | ✅ | staff.{onboarded,deactivated} | – | ✅ |
| scheduling-service | 1 | 100 m | 128 Mi | ✅ | schedule.{updated,conflict} | – | ✅ |
| inventory-service | 2 | 200 m | 256 Mi | ✅ | inventory.{item.lowstock, count.updated} | – | ✅ |
| procurement-service | 1 | 100 m | 128 Mi | ✅ | procurement.{order.placed,received} | – | ✅ |
| asset-service | 1 | 100 m | 128 Mi | ✅ | asset.{status.changed} | – | ✅ |
| admission-service | 2 | 200 m | 256 Mi | ✅ | admission.{started,discharged} | – | ✅ |
| emergency-service | 2 | 250 m | 384 Mi | ✅ | emergency.triaged | 30 rpm | ✅ |
| ward-service | 1 | 100 m | 128 Mi | ✅ | ward.{bed.allocated, bed.released} | – | ✅ |
| bed-service | 1 | 100 m | 128 Mi | ✅ | bed.status.changed | – | ✅ |
| round-service | 1 | 100 m | 128 Mi | ✅ | round.completed | – | ✅ |
| surgery-service | 2 | 200 m | 256 Mi | ✅ | surgery.{scheduled,completed} | – | ✅ |
| ambulance-service | 1 | 200 m | 256 Mi | ✅ | ambulance.{dispatch,arrived} | – | ✅ |
| event-service | 2 | 200 m | 256 Mi | ✅ | dlq.global | – | ✅ |
| notification-service | 2 | 200 m | 256 Mi | ✅ | All events | – | ✅ |
| reporting-service | 1 | 250 m | 512 Mi | ✅ | – | – | ✅ |

---
*All recommendations follow the ADRs:*
- ADR‑0001: Strangler Fig Migration Strategy
- ADR‑0002: Zero‑Trust Security Architecture
- ADR‑0003: RBAC Unification via Keycloak
- ADR‑0004: HttpOnly Cookie for Token Migration
- ADR‑0005: Data Schema Ownership Patterns