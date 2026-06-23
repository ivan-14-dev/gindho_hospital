# =============================================================================
# GinDHO Kubernetes Enterprise Architecture
# =============================================================================

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              INTERNET                                      │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                        KONG API GATEWAY (LoadBalancer)                      │
│                    https://api.gindho.com (443) / (80)                       │
│                    JWT, OAuth2, Rate Limiting, CORS, Prometheus             │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                    ┌───────────────┼───────────────┐
                    │               │               │
                    ▼               ▼               ▼
┌─────────────────────────┐ ┌─────────────────┐ ┌─────────────────┐
│   ISTIO GATEWAY         │ │   ISTIO INGRESS │ │   ISTIO Egress  │
│   (External)            │ │                 │ │                 │
└─────────────────────────┘ └─────────────────┘ └─────────────────┘
                    │
    ┌───────────────┼───────────────┐
    │               │               │
    ▼               ▼               ▼
┌─────────┐   ┌─────────┐    ┌─────────────┐
│mTLS/TLS│   │JWT Auth │    │Rate Limiting│
└─────────┘   └─────────┘    └─────────────┘
    │               │               │
    └───────────────┼───────────────┘
                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                        SERVICE MESH (ISTIO)                                  │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │ patient-ns  │  │appointment-n│  │    billing  │  │laboratory-n │         │
│  │             │  │     s       │  │             │  │     s       │         │
│  │• patient-svc│  │• appt-svc    │  │• billing-svc │  │• lab-svc    │         │
│  │• HPA/VPA    │  │• HPA/VPA     │  │• HPA/VPA     │  │• HPA/VPA     │         │
│  │• PDB         │  │• PDB         │  │• PDB         │  │• PDB         │         │
│  │• NetworkPol   │  │• NetworkPol  │  │• NetworkPol  │  │• NetworkPol  │         │
└─────────────────────────────────────────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                       INFRASTRUCTURE NAMESPACE                              │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐      │
│  │PostgreSQL│  │ MongoDB │  │  Redis  │  │  Kafka  │  │Keycloak │      │
│  │(3-node)  │  │(3-node) │  │(3-node) │  │(3-broker│  │(HA)     │      │
│  └─────────┘  └─────────┘  └─────────┘  └─────────┘  └─────────┘      │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐                  │
│  │Prometheus│  │ Grafana │  │  Loki   │  │ Jaeger  │                  │
│  │         │  │         │  │         │  │         │                  │
│  └─────────┘  └─────────┘  └─────────┘  └─────────┘                  │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Service Communication Matrix

| From Service           | To Service           | Protocol | Purpose                          |
|------------------------|----------------------|----------|----------------------------------|
| patient-service        | postgres             | JDBC     | Patient data persistence          |
| patient-service        | kafka                | Kafka    | Patient events publication        |
| appointment-service    | postgres             | JDBC     | Appointment data                  |
| appointment-service    | kafka                | Kafka    | Appointment events                |
| medical-record-service | mongodb              | MongoDB  | Medical records (documents)       |
| billing-service        | postgres + mongodb   | Multi    | Billing + invoice data            |
| laboratory-service     | postgres             | JDBC     | Lab results                       |
| pharmacy-service       | postgres             | JDBC     | Medication inventory              |
| emergency-service      | postgres             | JDBC     | Emergency cases                 |
| hr-service             | postgres             | JDBC     | Staff records                   |

## Namespace Design

```
infrastructure/
├── postgres.*.svc.cluster.local
├── mongodb.*.svc.cluster.local  
├── redis.*.svc.cluster.local
├── kafka.infrastructure.svc.cluster.local
└── keycloak.infrastructure.svc.cluster.local

patient/
├── patient-service.patient.svc.cluster.local
├── admission-service.patient.svc.cluster.local
├── emergency-service.patient.svc.cluster.local
├── ward-service.patient.svc.cluster.local
├── bed-service.patient.svc.cluster.local
├── round-service.patient.svc.cluster.local
├── surgery-service.patient.svc.cluster.local
└── ambulance-service.patient.svc.cluster.local

billing/
├── billing-service.billing.svc.cluster.local
├── insurance-service.billing.svc.cluster.local
└── payment-service.billing.svc.cluster.local

emr/
├── medical-record-service.emr.svc.cluster.local
└── imaging-service.emr.svc.cluster.local

pharmacy/
├── prescription-service.pharmacy.svc.cluster.local
└── pharmacy-service.pharmacy.svc.cluster.local

laboratory/
└── laboratory-service.laboratory.svc.cluster.local

hr/
├── hr-service.hr.svc.cluster.local
└── scheduling-service.hr.svc.cluster.local

inventory/
├── inventory-service.inventory.svc.cluster.local
├── procurement-service.inventory.svc.cluster.local
└── asset-service.inventory.svc.cluster.local

security/
├── identity-service.security.svc.cluster.local
├── authorization-service.security.svc.cluster.local
└── audit-service.security.svc.cluster.local

monitoring/
├── prometheus.monitoring.svc.cluster.local
├── grafana.monitoring.svc.cluster.local
├── loki.monitoring.svc.cluster.local
└── jaeger.monitoring.svc.cluster.local
```

## Deployment Commands

```bash
# Install infrastructure
kubectl apply -f k8s/infrastructure/

# Install Istio
istioctl install -f k8s/istio/istio-operator.yaml

# Deploy monitoring
kubectl apply -f k8s/monitoring/

# Deploy via ArgoCD
kubectl apply -f k8s/argocd/
argocd app sync --all

# Or using Helm
helm install patient-service k8s/helm/patient/ --namespace patient --create-namespace
```

## Resource Sizing

### Critical Services (3+ replicas)
- emergency-service: CPU 500m/1Gi, HPA up to 20
- billing-service: CPU 500m/1Gi, HPA up to 15  
- patient-service: CPU 250m/512Mi, HPA up to 15

### Standard Services (3+ replicas)
- appointment-service, medical-record-service, pharmacy-service, laboratory-service, hr-service

### Background Services (2+ replicas)
- notification-service, reporting-service, event-service, audit-service

### Infrastructure
- PostgreSQL: 3 replicas (10Gi storage each)
- MongoDB: 3 replicas (20Gi storage each)
- Redis: 3 replicas (5Gi storage each)
- Kafka: 3 brokers (20Gi storage each)
- Keycloak: 3 replicas (5Gi storage each)
- Kong: 2 replicas

## Health Checks

Each service exposes:
- `/actuator/health/liveness` - liveness probe
- `/actuator/health/readiness` - readiness probe  
- `/actuator/prometheus` - metrics endpoint
- `/actuator/info` - version info

## Security

- All services use mTLS via Istio
- JWT/OAuth2 via Kong plugins
- Rate limiting: 60 req/min per service
- CORS configured for web origins
- Network policies restrict ingress/egress
- RBAC with least privilege principle