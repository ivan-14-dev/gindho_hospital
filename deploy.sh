#!/bin/bash
set -euo pipefail

# GinDHO Microservices - Deployment Script
# Usage: ./deploy.sh {local|k8s|build|all}

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

log() { echo -e "${GREEN}[DEPLOY]${NC} $1"; }
warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
err() { echo -e "${RED}[ERROR]${NC} $1"; exit 1; }

clean() {
    log "Cleaning old builds..."
    find . -name "target" -type d -exec rm -rf {} + 2>/dev/null || true
    log "Clean complete."
}

build() {
    log "Building backend and all microservices..."
    clean
    log "Building microservices (reactor build, tests skipped)..."
    mvn clean install -Dmaven.test.skip=true -f pom.xml
    log "Building backend..."
    mvn clean package -Dmaven.test.skip=true -f services/backend-service/pom.xml
    log "Build complete."
}

build_docker() {
    log "Building Docker images..."
    # Build monolithic backend image
    log "Building backend..."
    docker build -t "gindho/backend:latest" ./backend

    # Build microservice images
    for svc in services/*/; do
        name=$(basename "$svc")
        if [ -f "${svc}Dockerfile" ]; then
            log "Building $name..."
            (cd "$svc" && docker build -t "gindho/$name:latest" .)
        else
            warn "Skipping $name (no Dockerfile found)"
        fi
    done
    log "Docker images built."
}

deploy_local() {
    log "Starting infrastructure (Postgres, Kafka, Keycloak, Kong, Monitoring)..."
    docker compose -f docker/docker-compose.yml --env-file .env up -d
    log "Infrastructure started. Waiting for services..."
    sleep 10

    log "Starting microservices..."
    for svc_dir in services/*/; do
        name=$(basename "$svc_dir")
        [ "$name" = "common" ] && continue
        [ "$name" = "api-gateway" ] && continue
        log "Starting $name on port $(grep -oP 'port:\s*\K\d+' "$svc_dir/src/main/resources/application.yml" | head -1)..."
        cd "$svc_dir"
        mvn spring-boot:run -DskipTests &
        cd ../..
        sleep 2
    done
    wait
    log "All services running."
}

deploy_k8s() {
    log "Deploying to Kubernetes..."

    # Create namespaces
    for ns in patient appointment emr laboratory pharmacy billing inventory hr reporting monitoring security infrastructure; do
        kubectl create namespace "$ns" --dry-run=client -o yaml | kubectl apply -f -
    done

    # Install OpenTelemetry CRDs before applying operator resources
    if [ -f k8s/crds/opentelemetrycollector-crd.yaml ]; then
        kubectl delete crd opentelemetrycollectors.opentelemetry.io --ignore-not-found=true
        kubectl apply -f k8s/crds/opentelemetrycollector-crd.yaml
    fi

    # Deploy infrastructure
    kubectl apply -k k8s/infrastructure-namespace

    # Deploy security
    kubectl apply -f k8s/security -R 2>/dev/null || true

    # Deploy monitoring
    kubectl apply -f k8s/monitoring -R 2>/dev/null || true

    # Deploy business services
    kubectl apply -f k8s/patient-namespace -R
    kubectl apply -f k8s/appointment-namespace -R
    kubectl apply -f k8s/medicalrecord-namespace -R
    kubectl apply -f k8s/laboratory-namespace -R
    kubectl apply -f k8s/pharmacy-namespace -R
    kubectl apply -f k8s/billing-namespace -R
    kubectl apply -f k8s/inventory-namespace -R
    kubectl apply -f k8s/hr-namespace -R
    kubectl apply -f k8s/reporting-namespace -R

    log "Kubernetes deployment complete."
    log "Kong ingress available at: http://localhost:8000"
    log "Keycloak available at: http://localhost:8080"
}

info() {
    echo ""
    echo "==========================================="
    echo "  GinDHO Microservices Architecture"
    echo "==========================================="
    echo ""
    echo "Services:"
    echo "  - Patient Service         :8081"
    echo "  - Appointment Service     :8082"
    echo "  - Medical Record Service  :8083"
    echo "  - Admission Service       :8084"
    echo "  - Emergency Service       :8085"
    echo "  - Ward Service            :8086"
    echo "  - Bed Service             :8087"
    echo "  - Round Service           :8088"
    echo "  - Surgery Service         :8089"
    echo "  - Prescription Service    :8090"
    echo "  - Pharmacy Service        :8091"
    echo "  - Laboratory Service      :8092"
    echo "  - Imaging Service         :8093"
    echo "  - Billing Service         :8094"
    echo "  - Insurance Service       :8095"
    echo "  - Payment Service         :8096"
    echo "  - Inventory Service       :8097"
    echo "  - Procurement Service     :8098"
    echo "  - Asset Service           :8099"
    echo "  - Ambulance Service       :8100"
    echo "  - HR Service              :8101"
    echo "  - Scheduling Service      :8102"
    echo "  - Event Service           :8103"
    echo "  - Notification Service    :8104"
    echo "  - Reporting Service       :8105"
    echo "  - Audit Service           :8106"
    echo "  - Authorization Service   :8107"
    echo "  - Identity Service        :8080 (Keycloak proxy)"
    echo "  - API Gateway             :8000 (Kong)"
    echo ""
    echo "Infrastructure:"
    echo "  - Kong           :8000 (API Gateway)"
    echo "  - Keycloak       :8080 (Auth)"
    echo "  - Kafka          :9092 (Event Bus)"
    echo "  - PostgreSQL     :5432 (Databases)"
    echo "  - MongoDB        :27017 (Documents)"
    echo "  - Redis          :6379 (Cache)"
    echo "  - Prometheus     :9090 (Metrics)"
    echo "  - Grafana        :3000 (Dashboards)"
    echo "  - Jaeger         :16686 (Tracing)"
    echo "  - Loki           :3100 (Logs)"
    echo ""
    echo "Commands:"
    echo "  ./deploy.sh local       - Start infra with Docker + run services on host"
    echo "  ./deploy.sh docker-all  - Build and start everything (infra + backend + services) in Docker"
    echo "  ./deploy.sh k8s         - Deploy to Kubernetes"
    echo "  ./deploy.sh build       - Build all JARs"
    echo "  ./deploy.sh docker      - Build all Docker images"
    echo "  ./deploy.sh all         - Build + Deploy locally"
    echo ""
}

# Main
case "${1:-help}" in
    clean) clean ;;
    build) build ;;
    docker) build_docker ;;
    local) deploy_local ;;
    docker-all)
        build
        build_docker
        log "Starting all containers (infra + backend + services) via Docker Compose..."
        docker compose -f docker/docker-compose.yml --env-file .env --profile all up -d
        ;;
    k8s) deploy_k8s ;;
    all)
        build
        build_docker
        deploy_local
        ;;
    info|help) info ;;
    *) err "Usage: $0 {clean|build|docker|local|docker-all|k8s|all|info}" ;;
esac
