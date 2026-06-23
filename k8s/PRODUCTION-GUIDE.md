# =============================================================================
# GinDHO Kubernetes Production Guide
# =============================================================================

## Prerequisites

- Kubernetes cluster (v1.28+) with 6+ nodes
- 16GB+ RAM, 8+ CPU per node recommended
- StorageClass for dynamic provisioning
- External load balancer (MetalLB for on-prem, cloud LB for cloud)
- DNS configured for gindho.com

## Installation Steps

### 1. Cluster Preparation

```bash
# Label nodes
kubectl label node node1 gindho.io/tier=infrastructure
kubectl label node node2 gindho.io/tier=infrastructure
kubectl label node node3 gindho.io/tier=services
kubectl label node node4 gindho.io/tier=services
kubectl label node node5 gindho.io/tier=services
kubectl label node node6 gindho.io/tier=services

# Create storage classes
kubectl apply -f k8s/storage-classes.yaml
```

### 2. Install Infrastructure

```bash
# Deploy secrets (edit with real credentials)
kubectl apply -f k8s/infrastructure/secrets.yaml

# Deploy databases
kubectl apply -f k8s/infrastructure/postgresql/statefulset.yaml
kubectl apply -f k8s/infrastructure/mongodb/statefulset.yaml
kubectl apply -f k8s/infrastructure/redis/statefulset.yaml

# Wait for databases
kubectl wait --namespace=infrastructure --for=condition=ready pod -l app=postgres --timeout=300s
kubectl wait --namespace=infrastructure --for=condition=ready pod -l app=mongodb --timeout=300s
```

### 3. Install Istio Service Mesh

```bash
# Install Istio with custom config
istioctl install -f k8s/istio/istio-operator.yaml

# Deploy Istio addons
kubectl apply -f k8s/istio/addons/
```

### 4. Install Kong API Gateway

```bash
# Deploy Kong with declarative config
kubectl apply -f k8s/infrastructure/kong/

# Import routes
kubectl apply -f k8s/infrastructure/kong-declarative.yaml
```

### 5. Deploy Microservices

```bash
# Using Helm
helm install patient-service k8s/helm/patient/ -n patient --create-namespace
helm install appointment-service k8s/helm/appointment/ -n appointment --create-namespace

# Or using ArgoCD
kubectl apply -f k8s/argocd/
```

## Monitoring Access

```bash
# Port forward to access services
kubectl -n monitoring port-forward svc/prometheus-server 9990:9990
kubectl -n monitoring port-forward svc/grafana 9300:9300
kubectl -n infrastructure port-forward svc/kong-gateway 9002:9000
kubectl -n argocd port-forward svc/argocd-server 9001:80
```

## Backup & DR

See `k8s/infrastructure/backup.yaml` for backup schedules.

```bash
# Manual backup
kubectl create job --from=cronjob/postgres-backup manual-backup-$(date +%s)
```

## Scaling

### Horizontal Scaling
```bash
# Scale via HPA (auto)
kubectl get hpa -n patient

# Manual scale
kubectl scale deployment patient-service -n patient --replicas=5
```

### Vertical Scaling
```bash
# VPA recommendations
kubectl get vpa -n patient
```

## Troubleshooting

```bash
# Check pod status
kubectl get pods -A

# Check Istio sidecar injection
kubectl get pods -n patient -o jsonpath='{.items[*].metadata.annotations.sidecar.istio.io/inject}'

# Check mTLS status
istioctl authz tls-check patient-service.patient.svc.cluster.local

# Check route in Kong
kubectl exec deploy/kong-gateway -n infrastructure -- kong admin api list services
```

## Multi-Environment

Use overlays for environment-specific configs:
- `k8s/overlays/dev/` - Development (1 replica, debug logging)
- `k8s/overlays/staging/` - Staging (2 replicas, info logging)
- `k8s/overlays/prod/` - Production (3+ replicas, warn logging)

```bash
kubectl apply -k k8s/overlays/prod/
```

## Security Checklist

- [ ] Replace all REPLACE_WITH_* placeholders in secrets
- [ ] Enable mTLS (Istio PeerAuthentication)
- [ ] Configure TLS certificates (cert-manager recommended)
- [ ] Set up network policies
- [ ] Configure RBAC properly
- [ ] Enable audit logging
- [ ] Set up secrets rotation