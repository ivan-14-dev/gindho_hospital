#!/usr/bin/env bash
# =============================================================================
# GinDHO Kubernetes Enterprise Deployment Script
# =============================================================================

set -euo pipefail

ENVIRONMENT="${1:-dev}"   # dev, staging, prod
CLUSTER_NAME="gindho-${ENVIRONMENT}"

echo "=== Deploying GinDHO to ${ENVIRONMENT} environment ==="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

log_info() { echo -e "${GREEN}[INFO]${NC} $1"; }
log_warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }

# -------------------------------------------------------------------------
# Prerequisites
# -------------------------------------------------------------------------
log_info "Validating prerequisites..."
for cmd in kubectl helm istioctl argocd; do
    command -v $cmd >/dev/null 2>&1 || { log_error "$cmd required but not found"; exit 1; }
done

# -------------------------------------------------------------------------
# Namespace creation
# -------------------------------------------------------------------------
log_info "Creating namespaces..."
kubectl apply -f k8s/namespaces.yaml

# -------------------------------------------------------------------------
# Secrets and ConfigMaps
# -------------------------------------------------------------------------
log_info "Applying secrets and configmaps..."
kubectl apply -f k8s/infrastructure/secrets.yaml
kubectl apply -f k8s/infrastructure/kafka-topics.yaml  # Kafka topics via Strimzi
kubectl apply -f k8s/infrastructure/kong/deployment.yaml  # includes Kong ConfigMap
kubectl apply -f k8s/security/serviceaccounts.yaml
kubectl apply -f k8s/security/rbac.yaml
kubectl apply -f k8s/security/networkpolicies.yaml

# -------------------------------------------------------------------------
# Build & Load Docker Images into local cluster (Minikube)
# -------------------------------------------------------------------------
log_info "Building Docker images..."
# Build all services (skip tests)
for svc_dir in services/*/; do
    if [ -f "$svc_dir/pom.xml" ]; then
        (cd "$svc_dir" && mvn -B package -DskipTests) || true
    fi
done

log_info "Loading images into Minikube..."
eval $(minikube docker-env)
# Build Docker images for services that have Dockerfiles
for dockerfile in services/*/Dockerfile; do
    if [ -f "$dockerfile" ]; then
        svc_name=$(basename "$(dirname "$dockerfile")")
        docker build -t "gindho/$svc_name:latest" -f "$dockerfile" "$(dirname "$dockerfile")"
        minikube image load "gindho/$svc_name:latest"
    fi
done

# -------------------------------------------------------------------------
# Deploy Infrastructure (via Kustomization overlays)
# -------------------------------------------------------------------------
log_info "Deploying infrastructure components..."
kubectl apply -k k8s/overlays/${ENVIRONMENT}/infrastructure/

# Wait for core databases to be ready
log_info "Waiting for PostgreSQL to be ready..."
kubectl wait --namespace=infrastructure --for=condition=ready pod -l app=postgres --timeout=300s
log_info "Waiting for Redis to be ready..."
kubectl wait --namespace=infrastructure --for=condition=ready pod -l app=redis --timeout=300s

# -------------------------------------------------------------------------
# Install Istio Service Mesh
# -------------------------------------------------------------------------
log_info "Installing Istio..."
if [ -f k8s/istio/istio-operator.yaml ]; then
    kubectl apply -f k8s/istio/istio-operator.yaml
    # Wait for Istio control plane pods
    kubectl wait --namespace=istio-system --for=condition=ready pod -l app=istiod --timeout=180s
fi

# Enable Istio injection for all service namespaces
for ns in patient appointment emr billing pharmacy laboratory hr inventory security reporting notification event; do
    kubectl label namespace "$ns" istio-injection=enabled --overwrite 2>/dev/null || true
done

# Apply Istio policies
log_info "Applying Istio policies..."
kubectl apply -f k8s/istio/peer-authentication.yaml
kubectl apply -f k8s/istio/authorization-policies.yaml
kubectl apply -f k8s/istio/destination-rules.yaml

# -------------------------------------------------------------------------
# Deploy Monitoring Stack
# -------------------------------------------------------------------------
log_info "Deploying monitoring stack..."
kubectl apply -f k8s/monitoring/
# Wait for Prometheus and Grafana
kubectl wait --namespace=monitoring --for=condition=ready pod -l app.kubernetes.io/name=prometheus --timeout=180s
kubectl wait --namespace=monitoring --for=condition=ready pod -l app.kubernetes.io/name=grafana --timeout=180s

# -------------------------------------------------------------------------
# Deploy ArgoCD (GitOps)
# -------------------------------------------------------------------------
log_info "Deploying ArgoCD..."
kubectl apply -f k8s/argocd/
# Wait for ArgoCD server
kubectl wait --namespace=argocd --for=condition=ready pod -l app.kubernetes.io/name=argocd-server --timeout=180s

# -------------------------------------------------------------------------
# Deploy Core Services via Helm (or raw manifests for infra)
# -------------------------------------------------------------------------
log_info "Deploying Helm charts (microservices)..."
# Using Helm chart loop
for chart_dir in k8s/helm/*/; do
    if [ -d "$chart_dir" ]; then
        chart_name=$(basename "$chart_dir")
        # Determine namespace from chart name or use default mapping
        case "$chart_name" in
            patient|appointment|emr|billing|pharmacy|laboratory|hr|inventory|security|reporting)
                namespace="$chart_name"
                ;;
            *)
                namespace="$chart_name"
                ;;
        esac
        # Ensure namespace exists
        kubectl get namespace "$namespace" >/dev/null 2>&1 || kubectl create namespace "$namespace"
        helm upgrade --install "$chart_name" "$chart_dir" \
            --namespace "$namespace" \
            --create-namespace \
            -f "$chart_dir/values.yaml"
    fi
done

# -------------------------------------------------------------------------
# Verify Deployment
# -------------------------------------------------------------------------
log_info "Verifying deployment status..."
# List running pods per namespace
echo ""
echo "Pods status per namespace:"
kubectl get pods -A | grep -v Completed | grep -v CrashLoopBackOff | grep -v ImagePullBackOff | awk '{print $1, $2, $3}' | column -t
echo ""

# Quick health checks
log_info "Performing health checks..."
# DNS resolution test
kubectl run -it --rm --restart=Never --image=alpine:latest dns-test --namespace=patient -- nslookup appointment-service.appointment.svc.cluster.local >/dev/null 2>&1 && log_info "DNS resolution: OK" || log_warn "DNS resolution: FAILED"
# Kong health
curl -s http://localhost:8001/health >/dev/null 2>&1 && log_info "Kong Admin API: OK" || log_warn "Kong Admin API: NOT REACHABLE (port-forward if needed)"
# PostgreSQL health
kubectl exec -n infrastructure -it deploy/postgres -- pg_isready -h localhost -p 5432 >/dev/null 2>&1 && log_info "PostgreSQL: Ready" || log_warn "PostgreSQL: NOT READY"
# Kafka health (if deployed)
if kubectl get deployment kafka -n infrastructure >/dev/null 2>&1; then
    kubectl exec -n infrastructure -it deploy/kafka -- kafka-topics.sh --bootstrap-server localhost:9092 --list >/dev/null 2>&1 && log_info "Kafka: Ready" || log_warn "Kafka: NOT READY"
fi

log_info "=== Deployment complete ==="
log_info "Access points:"
log_info "  Kong Gateway:          http://localhost:8000 (proxy) / http://localhost:8001 (admin)"
log_info "  ArgoCD UI:             http://localhost:8080 (after port-forward svc/argocd-server 8080:443 -n argocd)"
log_info "  Grafana:               http://localhost:3000 (after port-forward svc/grafana 3000:3000 -n monitoring)"
log_info "  Prometheus:            http://localhost:9090 (after port-forward svc/prometheus-server 9090:9090 -n monitoring)"
log_info ""
log_info "Tip: Use 'kubectl get pods -A -w' to watch pod status."
log_info "Tip: Use 'argocd app list' to see GitOps sync status."