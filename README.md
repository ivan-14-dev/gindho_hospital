# GinDHO Hospital

Microservices hospitaliers - Architecture complète avec 31 services indépendants.

## Architecture

- **31 Microservices** Spring Boot (patient, appointment, billing, etc.)
- **Infrastructure** : PostgreSQL, MongoDB, Redis, Kafka, Keycloak, Kong, Prometheus, Grafana, Jaeger, Loki
- **Frontend** : React + Vite
- **Observabilité** : OpenTelemetry, Jaeger tracing

## Ports (Docker Compose - serveur existant)

| Service | Port externe | Port interne |
|---------|-------------|--------------|
| PostgreSQL | 5435 | 5432 |
| Keycloak | 8081 | 8080 |
| Kong Proxy | 8001 | 8000 |
| Kong Admin | 8002 | 8001 |
| Kafka | 9094 | 9092 |
| MongoDB | 27017 | 27017 |
| Redis | 6379 | 6379 |
| Prometheus | 9091 | 9090 |
| Grafana | 3001 | 3000 |
| Jaeger UI | 16687 | 16686 |

## Services Microservices

| Service | Port |
|---------|------|
| patient-service | 8081 |
| appointment-service | 8082 |
| medical-record-service | 8083 |
| admission-service | 8084 |
| emergency-service | 8085 |
| ward-service | 8086 |
| bed-service | 8087 |
| round-service | 8088 |
| surgery-service | 8089 |
| prescription-service | 8090 |
| pharmacy-service | 8091 |
| laboratory-service | 8092 |
| imaging-service | 8093 |
| billing-service | 8094 |
| insurance-service | 8095 |
| payment-service | 8096 |
| inventory-service | 8097 |
| procurement-service | 8098 |
| asset-service | 8099 |
| ambulance-service | 8100 |
| hr-service | 8101 |
| scheduling-service | 8102 |
| event-service | 8103 |
| notification-service | 8104 |
| reporting-service | 8105 |
| audit-service | 8106 |

## Démarrage rapide (Docker)

```bash
cd hospital

# 1. Vérifier les prérequis
docker --version && docker compose version

# 2. Copier .env si besoin
cp .env.example .env  # (ou utiliser .env existant avec ports ajustés)

# 3. Build + démarrage tout-en-un
./deploy.sh docker-all

# 4. Frontend (dans un autre terminal)
cd frontend/gindho-frontend
npm install
npm run dev
```

## Commandes déploiement

```bash
./deploy.sh clean       # Nettoyer les builds
./deploy.sh build       # Compiler tous les JARs (mvn clean package)
./deploy.sh docker      # Build les images Docker
./deploy.sh local       # Démarrer l'infra + services en local (mvn spring-boot:run)
./deploy.sh docker-all  # Build + tout lancer en Docker
./deploy.sh k8s         # Déployer sur Kubernetes
./deploy.sh info        # Afficher l'aide
```

## Kubernetes

### Prérequis

- kubectl configuré avec cluster
- Helm 3.x
- Istio (optionnel, pour service mesh)

### Déploiement

```bash
# 1. Créer les namespaces
kubectl apply -f k8s/namespaces.yaml

# 2. Appliquer les secrets (adapter les valeurs)
kubectl apply -f k8s/infrastructure/secrets.yaml

# 3. Infrastructure
kubectl apply -f k8s/infrastructure/

# 4. Microservices (exemples)
kubectl apply -f k8s/patient-namespace/
kubectl apply -f k8s/appointment-namespace/
kubectl apply -f k8s/medicalrecord-namespace/
# ... etc

# 5. Monitoring
kubectl apply -f k8s/infrastructure/prometheus/
kubectl apply -f k8s/infrastructure/grafana/
kubectl apply -f k8s/infrastructure/jaeger/
kubectl apply -f k8s/infrastructure/loki/
```

### Utilisation

```bash
# Port-forward pour accès local
kubectl port-forward -n infrastructure svc/keycloak 8081:8080
kubectl port-forward -n infrastructure svc/kong 8001:8000
kubectl port-forward -n monitoring svc/grafana 3001:3000

# Health check
curl http://localhost:8001/api/v1/patients
```

## Structure

```
hospital/
├── deploy.sh                    # Script principal de déploiement
├── docker/
│   ├── docker-compose.yml      # Orchestration complète
│   └── prometheus/prometheus.yml
├── k8s/
│   ├── namespaces.yaml         # 13 namespaces définis
│   ├── infrastructure/         # DB, Kafka, Kong, Keycloak, monitoring
│   ├── *-namespace/           # Deployments + Ingress par domaine
│   └── helm/                   # Charts Helm (appointment, etc.)
├── services/                    # 31 microservices Spring Boot
│   ├── patient-service/
│   ├── appointment-service/
│   └── ...
├── frontend/
│   └── gindho-frontend/        # React + Vite
└── db/
    └── sql/
        └── init-all-schemas.sql
```

## Documentation

- [README-DEPLOYMENT.md](README-DEPLOYMENT.md) - Guide Kubernetes complet
- [INSTALL-RAPIDE.md](k8s/INSTALL-RAPIDE.md) - Guide installation rapide

## Support

- Keycloak : http://localhost:8081 (admin/admin_dev_2024)
- Kong Admin API : http://localhost:8002
- Grafana : http://localhost:3001 (admin/admin_dev_2024)