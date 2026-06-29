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
    log "Building all microservices..."
    clean
    log "Building microservices (reactor build, tests skipped)..."
    mvn clean install -Dmaven.test.skip=true -f pom.xml
    log "Build complete."
}

build_docker() {
    log "Building Docker images..."

    # Build setup-service Docker image
    log "Building setup-service Docker image..."
    (cd services/setup-service && docker build -t "gindho/setup-service:latest" .)

    # Build microservice images
    for svc in services/*/; do
        name=$(basename "$svc")
        [ "$name" = "setup-service" ] && continue
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

    # 1. Build all JARs first (reactor Maven from root, so parent POM is resolved)
    build

    # 2. Build Docker images (setup-service + all services)
    build_docker

    # 3. Load images into k3s containerd
    log "Loading images into k3s..."
    for img in $(docker images --format '{{.Repository}}:{{.Tag}}' | grep gindho); do
        sudo docker save "$img" | sudo k3s ctr images import - 2>/dev/null || true
    done

    # Create namespaces
    for ns in patient appointment medicalrecord laboratory pharmacy billing inventory hr reporting monitoring security infrastructure admission ambulance apigateway asset audit authorization bed emergency event identity imaging insurance notification payment prescription procurement round scheduling surgery ward setup; do
        kubectl create namespace "$ns" --dry-run=client -o yaml | kubectl apply -f -
    done

    # Install OpenTelemetry CRDs before applying operator resources
    if [ -f k8s/crds/opentelemetrycollector-crd.yaml ]; then
        kubectl delete crd opentelemetrycollectors.opentelemetry.io --ignore-not-found=true
        kubectl apply -f k8s/crds/opentelemetrycollector-crd.yaml
    fi

    # Install Strimzi Kafka Operator (for KafkaTopic CRD)
    log "Installing Strimzi Kafka Operator..."
    helm repo add strimzi https://strimzi.io/charts/ 2>/dev/null || true
    helm repo update
    helm upgrade --install strimzi-kafka-operator strimzi/strimzi-kafka-operator \
        --namespace kube-system --create-namespace \
        --set watchNamespaces="{infrastructure}"

    # Install Prometheus Operator (kube-prometheus-stack for PrometheusRule, ServiceMonitor, etc.)
    log "Installing Prometheus Operator..."
    helm repo add prometheus-community https://prometheus-community.github.io/helm-charts 2>/dev/null || true
    helm repo update
    helm upgrade --install kube-prometheus-stack prometheus-community/kube-prometheus-stack \
        --namespace infrastructure --create-namespace \
        --set prometheus.prometheusSpec.serviceMonitorSelectorNilUsesHelmValues=false

    # Deploy infrastructure (PostgreSQL, Kafka, Keycloak, Kong, Redis, MongoDB, etc.)
    kubectl apply -k k8s/infrastructure

    # Deploy security + setup RBAC
    kubectl apply -f k8s/security -R 2>/dev/null || true
    [ -f k8s/security/setup-rbac.yaml ] && kubectl apply -f k8s/security/setup-rbac.yaml

    # Deploy setup service FIRST (browser wizard)
    if [ -d k8s/setup-namespace ]; then
        log "Deploying setup-service..."
        kubectl apply -f k8s/setup-namespace -R 2>/dev/null || true
    fi

    # Deploy monitoring
    kubectl apply -f k8s/monitoring -R 2>/dev/null || true

    # ⚠️ Les microservices ne sont PAS déployés ici.
    # Ils seront déployés depuis le wizard navigateur setup-service après sélection utilisateur.
    # Le setup-service va appliquer les manifests k8s correspondants aux services choisis.

    log "Kubernetes deployment complete (infra + setup-service only)."
    log "Open http://localhost:9000/setup to finish the hospital setup and deploy services."
    log "Keycloak available at: http://localhost:9001 (admin / admin123)"
}

teardown() {
    log "Tearing down Kubernetes deployment..."
    kubectl delete ns patient appointment medicalrecord laboratory pharmacy billing inventory hr reporting monitoring security infrastructure admission ambulance apigateway asset audit authorization bed emergency event identity imaging insurance notification payment prescription procurement round scheduling surgery ward setup --timeout=60s || true
    kubectl delete crd opentelemetrycollectors.opentelemetry.io --ignore-not-found=true || true
    helm list -A 2>/dev/null | awk 'NR>1 {print "helm uninstall " $1 " -n " $2}' | bash 2>/dev/null || true
    log "Teardown complete for kubernetes"
}


stop_docker() {
    log "Stopping GinDHO deployment..."

    docker compose \
        -f docker/docker-compose.yml \
        --env-file .env \
        --profile all \
        stop

    log "GinDHO deployment stopped."
}

info() {
    echo ""
    echo "==========================================="
    echo "  GinDHO Microservices Architecture"
    echo "==========================================="
    echo ""
    echo "Services:"
    echo "  - Patient Service         :9004"
    echo "  - Appointment Service     :9005"
    echo "  - Medical Record Service  :9006"
    echo "  - Admission Service       :9007"
    echo "  - Emergency Service       :9008"
    echo "  - Ward Service            :9009"
    echo "  - Bed Service             :9010"
    echo "  - Round Service           :9011"
    echo "  - Surgery Service         :9012"
    echo "  - Prescription Service    :9013"
    echo "  - Pharmacy Service        :9014"
    echo "  - Laboratory Service      :9015"
    echo "  - Imaging Service         :9016"
    echo "  - Billing Service         :9017"
    echo "  - Insurance Service       :9018"
    echo "  - Payment Service         :9019"
    echo "  - Inventory Service       :9020"
    echo "  - Procurement Service     :9021"
    echo "  - Asset Service           :9022"
    echo "  - Ambulance Service       :9023"
    echo "  - HR Service              :9024"
    echo "  - Scheduling Service      :9025"
    echo "  - Event Service           :9026"
    echo "  - Notification Service    :9027"
    echo "  - Reporting Service       :9028"
    echo "  - Audit Service           :9029"
    echo "  - Authorization Service   :9030"
    echo "  - Identity Service        :9001 (Keycloak proxy)"
    echo "  - API Gateway             :9000 (Kong)"
    echo "  - Setup Wizard            :9000/setup (browser)"
    echo ""
    echo "Infrastructure:"
    echo "  - Kong           :9000 (API Gateway)"
    echo "  - Keycloak       :9001 (Auth)"
    echo "  - Kafka          :99092 (Event Bus)"
    echo "  - PostgreSQL     :95432 (Databases)"
    echo "  - MongoDB        :97017 (Documents)"
    echo "  - Redis          :96379 (Cache)"
    echo "  - Prometheus     :9990 (Metrics)"
    echo "  - Grafana        :9300 (Dashboards)"
    echo "  - Jaeger         :19686 (Tracing)"
    echo "  - Loki           :9310 (Logs)"
    echo ""
    echo "Commands:"
    echo "  ./deploy.sh local       - Start infra with Docker + run services on host"
    echo "  ./deploy.sh docker-all  - Build and start everything (infra + services) in Docker"
    echo "  ./deploy.sh k8s         - Deploy to Kubernetes"
    echo "  ./deploy.sh build       - Build all JARs"
    echo "  ./deploy.sh docker      - Build all Docker images"
    echo "  ./deploy.sh all         - Build + Deploy locally"
    echo "  ./deploy.sh stop-docker - Stop Deploiement of Docker"
    echo "  ./deploy.sh teardown    - Stop Deploiement of Kubernetes"
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
        log "Starting all containers (infra + services) via Docker Compose..."
        docker compose -f docker/docker-compose.yml --env-file .env --profile all up -d
        ;;
    k8s) deploy_k8s ;;
    teardown) teardown ;;
    stop-docker) stop_docker ;;
    all)
        build
        build_docker
        deploy_local
        ;;
    info|help) info ;;
    *) err "Usage: $0 {clean|build|docker|local|docker-all|k8s|teardown|stop-docker|all|info}" ;;
esac
