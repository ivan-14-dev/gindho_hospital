# GinDHO - Architecture Microservices Hospitalière

## Vue d'Ensemble

```
                    Internet
                        │
                 Load Balancer
                        │
                 Kong API Gateway (9000)
                        │
                 ┌──────────────────┐
                 │  Service Mesh    │
                 └──────────────────┘
                        │
              ┌─────────┴──────────┐
              │  ~~~~~~~~~~~~~~~   │
              │  Kubernetes        │
              │  ~~~~~~~~~~~~~~~   │
              │                    │
              │  Identity Service  │
              │  (Keycloak :9001)  │
              └─────────┬──────────┘
                        │
     ┌──────────────────┼──────────────────────┐
     │                  │                       │
  Patient         Appointment            Medical Record
  :9004           :9005                  :9006
     │                  │                       │
  Admission        Emergency              Ward
  :9007            :9008                  :9009
     │                  │                       │
  Bed              Round                  Surgery
  :9010            :9011                  :9012
     │                  │                       │
  Prescription     Pharmacy               Laboratory
  :9013            :9014                  :9015
     │                  │                       │
  Imaging          Billing                Insurance
  :9016            :9017                  :9018
     │                  │                       │
  Payment          Inventory              Procurement
  :9019            :9020                  :9021
     │                  │                       │
  Asset            Ambulance              HR
  :9022            :9023                  :9024
     │                  │                       │
  Scheduling       Event                  Notification
  :9025            :9026                  :9027
     │                  │                       │
  Reporting        Audit                  Authorization
  :9028            :9029                  :9030
                    │
              Apache Kafka (:99092)
                    │
     ┌──────────────┼────────────────┐
     │              │                │
  PostgreSQL    MongoDB          Redis
  (:95432)      (:97017)         (:96379)
                    │
              Observabilité
     ┌──────────────┬────────────────┐
     │              │                │
  Prometheus    Grafana          Jaeger
  (:9990)      (:9300)          (:19686)
     │              │
     Loki           │
     (:9310)        │
              └─────┴─────┐
                  ELK Stack
```

## Architecture par Couche

### 1. API Gateway (Kong)
Point d'entrée unique. Routes :
- `/patient/*` → patient-service:9004
- `/appointment/*` → appointment-service:9005
- `/emr/*` → medical-record-service:9006
- `/billing/*` → billing-service:9017
- `/lab/*` → laboratory-service:9015
- etc.

Plugins Kong activés : JWT, Rate-Limiting, CORS, Prometheus

### 2. Authentification (Keycloak)
- OAuth2 / OpenID Connect
- Single Sign-On (SSO)
- Multi-Factor Authentication (MFA)
- LDAP / Active Directory integration
- Realm: `gindho`
- Clients: `gindho-backend` (confidentiel), `gindho-frontend` (public)

### 3. Microservices Métier (29 services)

| Service | Port | Base | Description |
|---------|------|------|-------------|
| patient-service | 9004 | patient_db | Patients, contacts, assurances, documents |
| appointment-service | 9005 | appointment_db | Rendez-vous, calendrier, liste d'attente |
| medical-record-service | 9006 | emr_db | Dossier médical (consultations, diagnostics) |
| admission-service | 9007 | patient_db | Admissions, transferts, sorties |
| emergency-service | 9008 | patient_db | Triage, urgences |
| ward-service | 9009 | patient_db | Services hospitaliers |
| bed-service | 9010 | patient_db | Lits, chambres, occupation |
| round-service | 9011 | emr_db | Rondes médicales |
| surgery-service | 9012 | emr_db | Bloc opératoire |
| prescription-service | 9013 | pharmacy_db | Ordonnances |
| pharmacy-service | 9014 | pharmacy_db | Dispensation, lots |
| laboratory-service | 9015 | lab_db | Examens, résultats |
| imaging-service | 9016 | lab_db | Radiologie, DICOM, PACS |
| billing-service | 9017 | billing_db | Factures |
| insurance-service | 9018 | billing_db | Assurances, remboursements |
| payment-service | 9019 | billing_db | Paiements |
| inventory-service | 9020 | inventory_db | Stocks, inventaires |
| procurement-service | 9021 | inventory_db | Achats, fournisseurs |
| asset-service | 9022 | inventory_db | Équipements |
| ambulance-service | 9023 | dispatch_db | Ambulances |
| hr-service | 9024 | hr_db | Employés, contrats, congés |
| scheduling-service | 9025 | hr_db | Gardes, astreintes, planning |
| event-service | 9026 | event_db | Événements hospitaliers |
| notification-service | 9027 | notification_db | SMS, Email, Push |
| reporting-service | 9028 | reporting_db | Rapports, statistiques |
| audit-service | 9029 | audit_db | Traçabilité, historique |
| authorization-service | 9030 | authz_db | Permissions dynamiques RBAC |
| identity-service | 9001 | keycloak | Identity Provider (proxy Keycloak) |
| api-gateway | 9000 | - | Kong API Gateway |

### 4. Communication Inter-Services

#### Synchrone (REST/gRPC)
- Patient Service ←→ Appointment Service
- Medical Record ←→ tous les services cliniques
- Billing ←→ Insurance, Payment

#### Asynchrone (Apache Kafka)
Topics principaux :
- `patient` : PatientCreated, PatientUpdated
- `appointment` : AppointmentBooked, AppointmentCancelled
- `billing` : InvoiceCreated, InvoicePaid
- `notification` : NotificationSent
- `audit` : AuditLog
- `medical` : LabResultReady, PrescriptionCreated
- `inventory` : InventoryLow
- `emergency` : EmergencyTriaged

### 5. Bases de Données
Principe : 1 microservice = 1 base de données

- **PostgreSQL** : Données relationnelles (patients, billing, inventory, hr)
- **MongoDB** : Documents médicaux, dossiers patients, logs
- **Redis** : Cache, sessions, files d'attente

### 6. Kubernetes

Namespaces par domaine :
```
hospital-prod/
├── patient-namespace
├── appointment-namespace
├── emr-namespace
├── laboratory-namespace
├── pharmacy-namespace
├── billing-namespace
├── inventory-namespace
├── hr-namespace
├── reporting-namespace
├── monitoring-namespace
├── security-namespace
└── infrastructure-namespace
```

Chaque service possède :
- ✅ Deployment (2 replicas, rolling update)
- ✅ Service (ClusterIP)
- ✅ ConfigMap (configuration)
- ✅ Secret (credentials, JWT)
- ✅ Ingress (Kong)
- ✅ HPA (CPU 70%, Memory 80%)

### 7. Observabilité

| Outil | Port | Usage |
|-------|------|-------|
| Prometheus | 9990 | Métriques |
| Grafana | 9300 | Dashboards |
| Jaeger | 19686 | Tracing distribué |
| Loki | 9310 | Agrégation de logs |
| ELK Stack | - | Logs avancés |

### 8. Sécurité

- **TLS** : Chiffrement partout
- **mTLS** : Entre services (via Istio/Linkerd)
- **JWT** : Validation token dans chaque service
- **RBAC** : Permissions dynamiques (Authorization Service)
- **Audit** : Traçabilité complète (qui, quand, IP, action)
- **Secrets** : Kubernetes Secrets + HashiCorp Vault (recommandé)

## Démarrage

### Local (Docker Compose)
```bash
# Infrastructure
docker compose -f docker/docker-compose.yml up -d

# Build tous les services
./deploy.sh build

# Démarrer un service spécifique
cd services/patient-service && mvn spring-boot:run
```

### Kubernetes
```bash
# Déploiement complet
./deploy.sh k8s

# Namespaces
kubectl get ns

# Services
kubectl get svc --all-namespaces

# Logs d'un service
kubectl logs -n patient-namespace deployment/patient-service
```

## Schéma de Données (Exemple)

### patient-service
```sql
-- patients_db
patients (id, numero_patient, date_naissance, sexe, telephone, ...)
contacts (id, patient_id, nom, telephone, relation)
assurances (id, patient_id, compagnie, numero_police, validite)
documents (id, patient_id, type, url, uploaded_at)
identifiers (id, patient_id, type, valeur)
```

### appointment-service
```sql
-- appointment_db
appointments (id, patient_id, medecin_id, date_heure, statut, motif, ...)
availability (id, medecin_id, date, creneau, disponible)
waiting_list (id, patient_id, priorite, date_ajout)
```

### medical-record-service
```sql
-- emr_db
consultations (id, patient_id, medecin_id, date, motif, conclusion)
diagnostics (id, consultation_id, code_cim10, libelle, type)
observations (id, consultation_id, type, contenu)
prescriptions (id, consultation_id, medicament, posologie, duree)
```

## Événements Kafka (Exemple)

```json
{
  "eventId": "uuid",
  "eventType": "PatientCreated",
  "source": "patient-service",
  "timestamp": "2024-01-01T00:00:00",
  "payload": {
    "patientId": 123,
    "numeroPatient": "PAT-2024-001",
    "nom": "Dupont",
    "prenom": "Jean"
  }
}
