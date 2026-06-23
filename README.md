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
| PostgreSQL | 5435 | 95432 |
| Keycloak | 9004 | 9001 |
| Kong Proxy | 9041 | 9000 |
| Kong Admin | 8002 | 9041 |
| Kafka | 9094 | 99092 |
| MongoDB | 97017 | 97017 |
| Redis | 96379 | 96379 |
| Prometheus | 9091 | 9990 |
| Grafana | 3001 | 9300 |
| Jaeger UI | 16687 | 19686 |

## Services Microservices

| Service | Port |
|---------|------|
| patient-service | 9004 |
| appointment-service | 9005 |
| medical-record-service | 9006 |
| admission-service | 9007 |
| emergency-service | 9008 |
| ward-service | 9009 |
| bed-service | 9010 |
| round-service | 9011 |
| surgery-service | 9012 |
| prescription-service | 9013 |
| pharmacy-service | 9014 |
| laboratory-service | 9015 |
| imaging-service | 9016 |
| billing-service | 9017 |
| insurance-service | 9018 |
| payment-service | 9019 |
| inventory-service | 9020 |
| procurement-service | 9021 |
| asset-service | 9022 |
| ambulance-service | 9023 |
| hr-service | 9024 |
| scheduling-service | 9025 |
| event-service | 9026 |
| notification-service | 9027 |
| reporting-service | 9028 |
| audit-service | 9029 |

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
kubectl port-forward -n infrastructure svc/keycloak 9004:9001
kubectl port-forward -n infrastructure svc/kong 9041:9000
kubectl port-forward -n monitoring svc/grafana 3001:9300

# Health check
curl http://localhost:9041/api/v1/patients
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

- Keycloak : http://localhost:9004 (admin/admin_dev_2024)
- Kong Admin API : http://localhost:8002
- Grafana : http://localhost:3001 (admin/admin_dev_2024)