# GinDHO - Architecture Microservices Hospitalière

## Vue d'Ensemble

```
                    Internet
                        │
                 Load Balancer
                        │
                 Kong API Gateway (8000)
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
              │  (Keycloak :8080)  │
              └─────────┬──────────┘
                        │
     ┌──────────────────┼──────────────────────┐
     │                  │                       │
  Patient         Appointment            Medical Record
  :8081           :8082                  :8083
     │                  │                       │
  Admission        Emergency              Ward
  :8084            :8085                  :8086
     │                  │                       │
  Bed              Round                  Surgery
  :8087            :8088                  :8089
     │                  │                       │
  Prescription     Pharmacy               Laboratory
  :8090            :8091                  :8092
     │                  │                       │
  Imaging          Billing                Insurance
  :8093            :8094                  :8095
     │                  │                       │
  Payment          Inventory              Procurement
  :8096            :8097                  :8098
     │                  │                       │
  Asset            Ambulance              HR
  :8099            :8100                  :8101
     │                  │                       │
  Scheduling       Event                  Notification
  :8102            :8103                  :8104
     │                  │                       │
  Reporting        Audit                  Authorization
  :8105            :8106                  :8107
                    │
              Apache Kafka (:9092)
                    │
     ┌──────────────┼────────────────┐
     │              │                │
  PostgreSQL    MongoDB          Redis
  (:5432)      (:27017)         (:6379)
                    │
              Observabilité
     ┌──────────────┬────────────────┐
     │              │                │
  Prometheus    Grafana          Jaeger
  (:9090)      (:3000)          (:16686)
     │              │
     Loki           │
     (:3100)        │
              └─────┴─────┐
                  ELK Stack
```

## Architecture par Couche

### 1. API Gateway (Kong)
Point d'entrée unique. Routes :
- `/patient/*` → patient-service:8081
- `/appointment/*` → appointment-service:8082
- `/emr/*` → medical-record-service:8083
- `/billing/*` → billing-service:8094
- `/lab/*` → laboratory-service:8092
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
| patient-service | 8081 | patient_db | Patients, contacts, assurances, documents |
| appointment-service | 8082 | appointment_db | Rendez-vous, calendrier, liste d'attente |
| medical-record-service | 8083 | emr_db | Dossier médical (consultations, diagnostics) |
| admission-service | 8084 | patient_db | Admissions, transferts, sorties |
| emergency-service | 8085 | patient_db | Triage, urgences |
| ward-service | 8086 | patient_db | Services hospitaliers |
| bed-service | 8087 | patient_db | Lits, chambres, occupation |
| round-service | 8088 | emr_db | Rondes médicales |
| surgery-service | 8089 | emr_db | Bloc opératoire |
| prescription-service | 8090 | pharmacy_db | Ordonnances |
| pharmacy-service | 8091 | pharmacy_db | Dispensation, lots |
| laboratory-service | 8092 | lab_db | Examens, résultats |
| imaging-service | 8093 | lab_db | Radiologie, DICOM, PACS |
| billing-service | 8094 | billing_db | Factures |
| insurance-service | 8095 | billing_db | Assurances, remboursements |
| payment-service | 8096 | billing_db | Paiements |
| inventory-service | 8097 | inventory_db | Stocks, inventaires |
| procurement-service | 8098 | inventory_db | Achats, fournisseurs |
| asset-service | 8099 | inventory_db | Équipements |
| ambulance-service | 8100 | dispatch_db | Ambulances |
| hr-service | 8101 | hr_db | Employés, contrats, congés |
| scheduling-service | 8102 | hr_db | Gardes, astreintes, planning |
| event-service | 8103 | event_db | Événements hospitaliers |
| notification-service | 8104 | notification_db | SMS, Email, Push |
| reporting-service | 8105 | reporting_db | Rapports, statistiques |
| audit-service | 8106 | audit_db | Traçabilité, historique |
| authorization-service | 8107 | authz_db | Permissions dynamiques RBAC |
| identity-service | 8080 | keycloak | Identity Provider (proxy Keycloak) |
| api-gateway | 8000 | - | Kong API Gateway |

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
| Prometheus | 9090 | Métriques |
| Grafana | 3000 | Dashboards |
| Jaeger | 16686 | Tracing distribué |
| Loki | 3100 | Agrégation de logs |
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
