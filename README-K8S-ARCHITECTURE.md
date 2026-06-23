# GinDHO - Architecture Kubernetes Enterprise

## Vue d'ensemble

GinDHO est désormais une plateforme hospitalière 100% Kubernetes-native. Tous les microservices Spring Boot 3 / Java 21 fonctionnent exclusivement dans le cluster Kubernetes, sans exécution directe sur l'hôte.

```
┌─────────────────────────────────────────────────────────────────────┐
│                         CLIENTS                                     │
│  Web App  │  Mobile App  │  Admin  │  External Partners             │
└─────────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    KONG API GATEWAY                                 │
│  JWT/OAuth2 │ Rate Limiting │ ACL │ API Versioning │ CORS           │
│  /api/v1/* vers les microservices                                   │
└─────────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    ISTIO SERVICE MESH                               │
│  mTLS STRICT │ Circuit Breakers │ Retries │ Canary │ Blue/Green     │
└─────────────────────────────────────────────────────────────────────┘
                              │
          ┌───────────────────┼───────────────────┐
          ▼                   ▼                   ▼
┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐
│ PATIENT NAMESPACE│ │ APPOINTMENT NAMESPACE│ │ BILLING NAMESPACE│
│ patient-service  │ │ appointment-svc  │ │ billing-service  │
│ emergency-service│ │                  │ │ insurance-service│
│ ward-service     │ │                  │ │ payment-service  │
│ bed-service      │ │                  │ │                  │
│ round-service    │ │                  │ │                  │
│ surgery-service  │ │                  │ │                  │
│ ambulance-service│ │                  │ │                  │
└──────────────────┘ └──────────────────┘ └──────────────────┘
          │                   │                   │
          └───────────────────┼───────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    KAFKA EVENT BUS                                  │
│  patient.created │ appointment.booked │ billing.invoice.created     │
│  lab.result.ready│ prescription.issued│ emergency.triaged           │
└─────────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    DATABASES & STORAGE                              │
│  PostgreSQL │ MongoDB │ Redis │ Keycloak │ Observability            │
└─────────────────────────────────────────────────────────────────────┘
```

## Principe de fonctionnement

### 1. Entrée des requêtes

Toutes les requêtes externes transitent par **Kong API Gateway**:

```
Client → Kong Gateway (9000/9041) → Service Kubernetes → Database
```

Exemple:
```
GET /api/v1/patients/123
  → Kong vérifie le JWT
  → Kong applique le rate limiting
  → Kong route vers patient-service.patient.svc.cluster.local:9004
  → Istio injecte le sidecar Envoy
  → patient-service interroge PostgreSQL
  → Réponse retournée au client
```

### 2. Communication interne entre services

Les services communiquent de deux manières:

**A. Communication synchrone (REST)**
```
patient-service.patient.svc.cluster.local
appointment-service.appointment.svc.cluster.local
billing-service.billing.svc.cluster.local
```

**B. Communication asynchrone (Kafka Events)**
```
patient-service → Kafka Topic patient.created → Consommateurs
  ├── appointment-service
  ├── medical-record-service
  ├── billing-service
  ├── notification-service
  └── audit-service
```

## Architecture des namespaces

```
infrastructure/
├── postgres.infrastructure.svc.cluster.local
├── redis.infrastructure.svc.cluster.local
├── kafka.infrastructure.svc.cluster.local
├── kong-gateway.infrastructure.svc.cluster.local
└── event-service.infrastructure.svc.cluster.local

patient/
├── patient-service.patient.svc.cluster.local
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

pharmacy/
├── pharmacy-service.pharmacy.svc.cluster.local
└── prescription-service.pharmacy.svc.cluster.local

emr/
├── medical-record-service.emr.svc.cluster.local
└── imaging-service.emr.svc.cluster.local

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

reporting/
└── reporting-service.reporting.svc.cluster.local
```

## Flux de données complet

### Cas 1: Création d'un patient

```
1. Client → POST /api/v1/patients
2. Kong Gateway → Validation JWT + Rate Limiting
3. patient-service → Réception de la requête
4. patient-service → PostgreSQL (INSERT patient)
5. patient-service → Kafka Topic patient.created
6. Kafka → Distribution aux consommateurs:
   ├── appointment-service (prépare les rendez-vous)
   ├── medical-record-service (crée le dossier médical)
   ├── billing-service (crée le compte facturation)
   ├── notification-service (envoie confirmation)
   └── audit-service (journalise l'action)
7. Réponse HTTP 201 au client
```

### Cas 2: Prise de rendez-vous

```
1. Client → POST /api/v1/appointments
2. Kong → patient-service (vérification patient)
3. appointment-service → PostgreSQL (INSERT appointment)
4. appointment-service → Kafka Topic appointment.booked
5. Kafka → Distribution:
   ├── notification-service (SMS/Email confirmation)
   ├── patient-service (mise à jour agenda patient)
   ├── billing-service (pré-facturation)
   └── audit-service (audit trail)
6. Réponse HTTP 201 au client
```

### Cas 3: Résultat de laboratoire

```
1. laboratoire-service → PostgreSQL (INSERT lab_result)
2. laboratoire-service → Kafka Topic lab.result.ready
3. Kafka → Distribution:
   ├── medical-record-service (mise à jour dossier médical)
   ├── notification-service (notification patient)
   └── audit-service (journalisation)
4. medical-record-service → MongoDB (mise à jour dossier patient)
5. patient-service → Kafka Topic patient.updated
6. billing-service → PostgreSQL (facturation acte)
```

### Cas 4: Prescription médicale

```
1. prescription-service → PostgreSQL (INSERT prescription)
2. prescription-service → Kafka Topic prescription.issued
3. Kafka → Distribution:
   ├── pharmacy-service (préparation médicaments)
   ├── notification-service (notification patient)
   └── audit-service (traçabilité)
4. pharmacy-service → PostgreSQL (déstockage médicaments)
5. inventory-service → Kafka Topic inventory.updated
6. procurement-service (réapprovisionnement si seuil bas)
```

## Patterns architecturaux

### Outbox Pattern

Chaque service écrit dans sa table `outbox` dans la même transaction que la donnée métier:

```
Transaction DB:
1. INSERT patient
2. INSERT outbox (patient.created)
3. COMMIT
```

Puis un relayeur publie l'événement Kafka.

### Saga Pattern

Pour les processus transactionnels distribués:

```
Admission → Bed Allocation → Billing
    │            │              │
    │            │              │
    └─ AdmissionStarted ───────┘
              │
              └─ BedAllocated ───── InvoiceCreated
```

En cas d'échec:
```
AdmissionFailed → BedReleased → InvoiceCancelled
```

### Circuit Breaker (Istio)

```
DestinationRule:
  consecutive5xxErrors: 7
  interval: 30s
  baseEjectionTime: 30s
```

### mTLS (Istio)

Tous les services communiquent avec mTLS STRICT:
```
PeerAuthentication:
  mtls:
    mode: STRICT
```

## Sécurité

### Authentification

1. Client → Keycloak (OAuth2/OIDC)
2. Keycloak → JWT token
3. Client → Kong Gateway avec JWT
4. Kong → Validation JWT
5. Kong → Microservice

### RBAC Kubernetes

Chaque service a son propre:
- ServiceAccount
- Role
- RoleBinding

Exemple:
```
patient-service-sa → patient-service-role → patient-service-rolebinding
```

### Network Policies

Politique zero-trust:
```
Ingress:
  - Depuis kong namespace uniquement
  - Ports spécifiques uniquement

Egress:
  - PostgreSQL/Redis/Kafka uniquement
```

## Monitoring & Observabilité

### Prometheus Metrics

Chaque service expose:
```
/actuator/prometheus
/actuator/health
/actuator/metrics
```

### Dashboards

- Hospital Overview
- Patient Service Metrics
- Emergency Service Metrics
- Billing Service Metrics
- Kafka Consumer Lag
- Database Performance

### Tracing

```
Client → Kong → Istio → Service A → Kafka → Service B
  │        │       │        │          │       │
  └──── Jaeger Trace ID propagé dans tous les services ─────┘
```

## Déploiement

### Build d'une image

```bash
cd services/patient-service
mvn package -DskipTests
docker build -t gindho/patient-service:latest .
```

### Chargement dans Minikube

```bash
eval $(minikube docker-env)
docker build -t gindho/patient-service:latest services/patient-service/
minikube image load gindho/patient-service:latest
```

### Déploiement

```bash
kubectl apply -f k8s/namespaces.yaml
kubectl apply -f k8s/infrastructure/
kubectl apply -f k8s/helm/patient/
```

### Rollout

```bash
kubectl rollout restart deployment patient-service -n patient
kubectl rollout status deployment patient-service -n patient
```

## Tests

### DNS interne

```bash
kubectl exec -it deploy/patient-service -n patient -- \
  nslookup appointment-service.appointment.svc.cluster.local
```

### Connectivité PostgreSQL

```bash
kubectl exec -it deploy/patient-service -n patient -- \
  nc -zv postgres.infrastructure.svc.cluster.local 95432
```

### Kong Gateway

```bash
kubectl port-forward -n infrastructure svc/kong-gateway 9002:9000
curl -H "Authorization: Bearer <token>" \
  http://localhost:9002/api/v1/patients
```

## Structure des fichiers

```
k8s/
├── namespaces.yaml                    # Tous les namespaces
├── patient-service-dev.yaml           # Patient service (test)
├── infrastructure/
│   ├── postgresql/                    # PostgreSQL HA
│   ├── mongodb/                       # MongoDB ReplicaSet
│   ├── redis/                         # Redis Cluster
│   ├── kafka/                         # Kafka StatefulSet
│   ├── keycloak/                      # Keycloak HA
│   ├── kong/                          # Kong Gateway
│   ├── prometheus/                    # Prometheus
│   ├── grafana/                       # Grafana
│   └── jaeger/                        # Jaeger
├── istio/
│   ├── istio-operator.yaml            # Istio configuration
│   ├── peer-authentication.yaml       # mTLS STRICT
│   ├── authorization-policies.yaml    # AuthorizationPolicies
│   └── destination-rules.yaml         # Circuit breakers
├── argocd/
│   ├── project.yaml                   # AppProject
│   └── applications/                  # ArgoCD Applications
├── monitoring/
│   ├── prometheus.yaml                # Prometheus CR
│   ├── prometheus-rules.yaml          # Alerts
│   ├── grafana-dashboards.yaml        # Dashboards
│   └── grafana-datasources.yaml       # Datasources
├── overlays/
│   ├── dev/                           # Dev environment
│   ├── staging/                       # Staging environment
│   └── prod/                          # Production environment
└── helm/
    ├── patient/                       # Patient chart
    ├── appointment/                   # Appointment chart
    ├── medical-record/                # Medical record chart
    └── [all services...]
```

## Flux de données résumé

```
┌──────────────┐
│   CLIENT     │
└──────┬───────┘
       │ HTTP/HTTPS
       ▼
┌──────────────┐
│ KONG GATEWAY │  ← JWT Validation, Rate Limiting
└──────┬───────┘
       │ REST API
       ▼
┌──────────────┐
│ MICROSERVICE │  ← Spring Boot
└──────┬───────┘
       │ JDBC/MongoDB/Redis
       ▼
┌──────────────┐
│  DATABASE    │
└──────┬───────┘
       │ Outbox Pattern
       ▼
┌──────────────┐
│    KAFKA     │  ← Event Bus
└──────┬───────┘
       │ Events
       ▼
┌──────────────┐
│ CONSUMERS    │  ← Other microservices
└──────────────┘
```

## Notes importantes

1. **Tous les services fonctionnent dans Kubernetes** - Aucune exécution sur l'hôte
2. **Kong est le point d'entrée unique** - Toutes les requêtes passent par le gateway
3. **Istio gère le service mesh** - mTLS, retries, circuit breakers
4. **Kafka gère les événements** - Communication asynchrone entre services
5. **PostgreSQL/MongoDB/Redis** - Stockage des données
6. **Prometheus/Grafana/Loki/Jaeger** - Observabilité complète
7. **ArgoCD** - GitOps pour le déploiement continu

## Conclusion

Cette architecture permet:
- Haute disponibilité (3+ replicas pour les services critiques)
- Scalabilité horizontale (HPA/VPA)
- Sécurité renforcée (mTLS, RBAC, NetworkPolicies)
- Observabilité complète (metrics, logs, traces)
- Déploiement GitOps (ArgoCD)
- Communication fiable entre services (Kafka + Outbox Pattern)
- Multi-environnements (dev/staging/prod)
