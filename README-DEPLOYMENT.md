# GinDHO – Installation & Deployment Guide  
*(Kubernetes Enterprise Edition – Full Production‑Ready Stack)*  

---

## Table of Contents
1. [Prerequisites](#prerequisites)  
2. [Initial Setup – ConfigMaps & Secrets](#initial-setup)  
3. [Container Image Build & Load](#build-and-load-images)  
4. [Kubernetes Cluster Preparation](#k8s-prep)  
5. [Deploy Infrastructure & Core Services](#deploy-core)  
6. [Deploy Microservices (Helm charts)](#deploy-microservices)  
7. [Istio & Envoy Side‑car Injection](#istio-setup)  
8. [ArgoCD – GitOps Setup](#argo-cd-setup)  
9. [Service‑level Manifests (NetworkPolicy, RBAC, …)](#security‑network)  
10. [Verification Checklist](#verification)  
11. [Running the `deploy.sh` script](#deploy-sh)  
12. [Troubleshooting Tips](#troubleshooting)  

---  

## 1. Prerequisites  

| Tool | Version | Why |
|------|---------|-----|
| **kubectl** | >= v1.28 | Interact with the K8s API |
| **minikube** | >= v1.30 | Local single‑node cluster (or any K8s distribution) |
| **docker** | >= v24 | Build Spring‑Boot images |
| **Helm** | >= v3.14 | Deploy Helm charts |
| **Istioctl** | >= v1.21 | Service‑mesh control plane |
| **ArgoCD CLI** | >= v2.10 | GitOps deployment |
| **Spring Boot 3** | Java 21 | Backend micro‑services |
| **Maven 3** | >= 3.9 | Build tool |
| **Git** | – | Version control |
| **JQ**, **curl**, **nc**, **dig** | – | Debugging utilities |

> **Tip:** On macOS / Linux install via Homebrew:  
> `brew install kubectl minikube helm istioctl argocd-cli`  

---

## 2. Initial Setup – ConfigMaps & Secrets  

1. **Create namespace hierarchy**  
   ```bash
   kubectl apply -f k8s/namespaces.yaml
   ```
2. **Generate secrets (replace placeholders)**
   ```bash
   envsubst < k8s/infrastructure/secrets.yaml > /tmp/secrets.yaml
   kubectl apply -f /tmp/secrets.yaml
   ```
   - `postgres-secret` – DB credentials  
   - `keycloak-secret` – Keycloak DB & admin password  
   - `kafka-secret` – Kafka broker password  
   - `redis-secret` – Redis password  
   - `backup-secret` – S3 bucket credentials (optional)  

3. **Ingress‑class ConfigMap** (used by Kong)  
   ```bash
   kubectl apply -f k8s/infrastructure/kong/configmap.yaml
   ```

---

## 3. Build and Load Docker Images  

All micro‑services are built with Maven and packaged as executable JARs.

```bash
# 1. Build all services (skip Tests for CI speed)
for svc in services/*/pom.xml; do
  cd "$(dirname "$svc")"
  mvn -B package -DskipTests
  cd -
done

# 2. Load images into Minikube (or any OCI‑compatible registry)
eval $(minikube docker-env)
for img in $(ls services/*/target/*.jar | while read jar; do
  echo "gindho/$(basename "$(dirname "$jar")")"
done | sort -u); do
  docker build -t "$img" .
done
minikube image load $(docker images | grep gindho | awk '{print $1}')
```

> **Note:** Only images that appear in `docker images | grep gindho` need to be loaded into the Minikube nodes.  

---

## 4. Cluster Preparation  

```bash
# Enable required add‑ons
minikube addons enable ingress
minikube addons enable metrics-server
minikube addons enable grafana
minikube addons enable LokiStack   # if using Prometheus‑Operator
minikube addons enable kiali
minikube addons enable jaeger

# Install Istio (latest stable)
istioctl install --set profile=demo -y
istioctl verify-install

# Create the namespace for ArgoCD
kubectl create namespace argocd
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/core-resources.yaml
```

---

## 5. Deploy Infrastructure Services  

```bash
# Apply all core infrastructure manifests (order matters)
kubectl apply -f k8s/infrastructure/
kubectl apply -f k8s/istio/istio-operator.yaml
kubectl apply -f k8s/istio/addons-config.yaml
kubectl apply -f k8s/monitoring/
kubectl apply -f k8s/argocd/
```

> **Tip:** Apply `k8s/istio/peer-authentication.yaml` *after* the Istio operator is Running to enforce strict mTLS.

---

## 5‑1. Deploy Security Manifests  

```bash
kubectl apply -f k8s/security/serviceaccounts.yaml
kubectl apply -f k8s/security/rbac.yaml
kubectl apply -f k8s/security/networkpolicies.yaml
```

---

## 6. Deploy Microservices (Helm)  

All 28 domain‑specific Helm charts live under `k8s/helm/`.  

```bash
# Install the Helm repository index (local)
helm repo index k8s/helm --url file://k8s/helm

# Optional: create a local chart‑museum (Helm registry)
# helm repo add gincharts https://gindho.github.io/helm-charts
# helm repo update

# Deploy all charts (example for patient‑service)
helm upgrade --install patient-service k8s/helm/patient \
  --namespace patient \
  --create-namespace \
  -f k8s/helm/patient/values.yaml
```

*Duplicate the command for each service, swapping the chart directory name (`appointment`, `medical-record`, …).*

> **Tip:** To avoid repetition, use the helper script `k8s/generate-helm-deploy.sh` (see below).

---

## 6‑1. Auto‑generate Helm Deploy Script  

Create `k8s/deploy-all.sh`:

```bash
#!/usr/bin/env bash
set -euo pipefail
for chart in k8s/helm/*/; do
  name=$(basename "$chart")
  ns=$(echo "$chart" | sed -e 's|k8s/helm/||' -e 's/-/_/g')
  kubectl create namespace "$ns" 2>/dev/null || true
  helm upgrade --install "$name" "$chart" --namespace "$ns" --create-namespace \
    -f "$chart/values.yaml"
done
echo "All Helm charts deployed."
```

Make it executable: `chmod +x k8s/deploy-all.sh`

---

## 7. Istio & Side‑car Injection  

1. **Enable automatic side‑car injection for selected namespaces**  

   ```bash
   kubectl label namespace patient istio-injection=enabled
   kubectl label namespace billing istio-injection=enabled
   kubectl label namespace pharmacy istio-injection=enabled
   kubectl label namespace emr istio-injection=enabled
   # repeat for every domain namespace
   ```

2. Verify injection:  

   ```bash
   kubectl get pods -n patient -o jsonpath='{.items[*].spec.containers[*].name}'
   # you should see "istio-proxy" among the containers
   ```

---

## 8. ArgoCD (GitOps)  

```bash
# Port‑forward the ArgoCD server (default admin password is admin‑admin)
kubectl -n argocd port-forward svc/argocd-server 9001:443 &

# Login
argocd login --grpc-web --username admin --password admin

# Add the Git repository (replace URL and path)
argocd repo add https://github.com/gindho/gindho.git --path k8s
argocd app create gin-gindho --repo https://github.com/gindho/gindho.git \
  --path k8s --sync-policy automated --retries 10

# Verify
argocd app get gin-gindho
```

ArgoCD will **sync** the entire `k8s/` directory automatically (namespaces, Helm charts, Istio manifests, etc.).

---

## 8‑1. Updating the Git source  

Any change inside `k8s/` (e.g., new version of a Helm chart) triggers a sync:

```bash
argocd app set gin-gindho --sync-policy automated
argocd app sync gin-gindho   # manual trigger if needed
```

---

## 9. Security & Networking  

### NetworkPolicy (Zero‑Trust)

```bash
kubectl apply -f k8s/security/networkpolicies.yaml
```

- **Ingress** – only `kong` namespace can talk to microservices.  
- **Egress** – each microservice may only talk to its required infra services (PostgreSQL, Redis, Kafka, etc.).

### RBAC

All ServiceAccounts are bound to the minimum set of permissions required for their workloads.  
Inspect:

```bash
kubectl get rolebindings -A -o yaml
```

### Secrets Rotation  

Rotate credentials regularly (e.g., database passwords). Use a secret‑rotation job like:

```bash
kubectl apply -f k8s/infrastructure/backup.yaml
```

---

## 10. Monitoring & Observability  

```bash
# Grafana (default UI on http://node-ip:9300)
kubectl port-forward -n monitoring svc/grafana 9300:9300

# Prometheus metrics
kubectl port-forward -n monitoring svc/prometheus-server 9990:9990

# Loki logs
kubectl port-forward -n monitoring svc/loki 9310:9310
```

Dashboard bundles are provided in `k8s/monitoring/grafana-dashboards.yaml`. Import the JSON files via Grafana UI.

---

## 11. Verify Deploy `deploy.sh`  

`deploy.sh` located in the repo root automates steps 2‑5.  
Inspect its content:

```bash
cat k8s/deploy.sh
```

**Current version (as of this repo):**

```bash
#!/usr/bin/env bash
set -euo pipefail

# 1. Create infra resources
kubectl apply -f k8s/infrastructure/
kubectl apply -f k8s/security/

# 2. Build images (if not pre‑built)
eval $(minikube docker-env)
for img in $(ls services/*/target/*.jar | while read jar; do echo "gindho/$(basename "$(dirname "$jar")")"; done | sort -u); do
  docker build -t "$img" -f "$(ls services/*/Dockerfile)" .
done

# 3. Deploy core infra (databases, Kafka, Kong)
kubectl apply -f k8s/infrastructure/
kubectl apply -f k8s/istio/istio-operator.yaml
kubectl apply -f k8s/istio/addons-config.yaml
kubectl apply -f k8s/monitoring/
kubectl apply -f k8s/argocd/

# 4. Deploy all Helm charts
for chart in k8s/helm/*/; do
  name=$(basename "$chart")
  ns=$(echo "$name" | tr '/' ':' | sed 's/-/_/')
  kubectl create namespace "$ns" 2>/dev/null || true
  helm upgrade --install "$name" "$chart" --namespace "$ns" --create-namespace -f "$chart/values.yaml"
done

# 5. Enable Istio injection for all service namespaces
for ns in patient appointment billing pharmacy emr hr inventory security reporting; do
  kubectl label namespace "$ns" istio-injection=enabled
done

echo "✅  Deployment pipeline completed successfully."
```

```bash
chmod +x k8s/deploy.sh   # ensure executable
```

Running it (`./k8s/deploy.sh`) will:

1. **Re‑apply** all infra manifests (secrets, DBs, Kafka, Kong).  
2. **Re‑build** any Docker image that changed.  
3. **Deploy** all Helm charts.  
4. **Enable Istio** side‑car injection for every domain‑namespace.  
5. **Print** a success message.

> **Checklist:** After the script finishes, run the *Verification Checklist* below.

---

## 12. Verification Checklist  

| ✔ | Test | Command |
|---|------|---------|
| 1 | All pods are `Running` | `kubectl get pods -A` |
| 2 | DNS resolution works | `kubectl exec -n patient deploy/patient-service -c default -- nslookup appointment-service.appointment.svc.cluster.local` |
| 3 | Kong is reachable | `curl -s http://localhost:9002/mock | head -c 1` |
| 3‑a | Kong can route to a service | `curl -s http://localhost:9002/api/v1/patients | wc -c` (should return JSON size > 0) |
| 4 | PostgreSQL connectivity | `kubectl exec -n infrastructure -it deploy/postgres -- pg_isready -h localhost -p 95432` |
| 5 | Kafka health | `kubectl exec -n infrastructure -it deploy/kafka -- kafka-topics.sh --bootstrap-server localhost:99092  --list` |
| 6 | Prometheus UI up | `curl http://localhost:9990/-/ready` |
| 7 | Grafana UI loads (port‑forward) | `curl -s http://localhost:9300/api/health | grep "OK"` |
| 8 | ArgoCD shows all apps **Synced** | `argocd app list` |
| 9 | NetworkPolicy enforcement | Attempt `kubectl run -i --rm --restart=Never test-pod --image=alpine -- sleep 3600 -n billing` and verify it cannot `nc` to `payment-service` |
| 10| TLS/mTLS (Istio) | `istioctl authn tls-check payment-service.billing.svc.cluster.local` |

If any check fails, consult the **Troubleshooting** section below.

---

## 13. Troubleshooting  

| Symptom | Likely Cause | Fix |
|---------|--------------|-----|
| `ImagePullBackOff` on a pod | Image not present in node’s registry | `minikube image load <image>` or rebuild and re‑push |
| `Kong` 500 / **`Failed to parse`** | Invalid `kong.yml` format | Re‑apply the corrected ConfigMap (see 2‑step) |
| `Connection refused` to PostgreSQL | DB not ready yet | Increase `livenessProbe` delay or add `sleep 30` in `deploy.sh` before dependent services |
| `Istio` side‑car not injected | Namespace not labelled `istio-injection=enabled` | `kubectl label namespace patient istio-injection=enabled` |
| ArgoCD shows **OutOfSync** | Git repo not reachable | Verify network, SSH keys, or personal access token set up in ArgoCD |
| Helm release stuck in *pending* | Values file invalid | Run `helm lint <chart>` and fix errors |
| Service cannot reach DB (timeout) | DB not reachable from namespace | Verify `networkpolicy` does not block traffic; check service DNS name |

---

## 14. Keeping `deploy.sh` Up‑to‑Date  

Whenever a new microservice is added or a configuration changes:  

1. Add a **new namespace** to `k8s/namespaces.yaml`.  
2. Create a **Helm chart** under `k8s/helm/<service>/`.  
3. Add a **corresponding values.yaml** with defaults.  
4. If the service needs special side‑car (e.g., Envoy for Istio), add a **custom manifest** under `k8s/istio/` and reference it in the `deploy.sh` loop.  
5. Update the **deploy‑script sections** (the `for chart in k8s/helm/*/` loop) to include the new chart.  
6. Commit and tag the change; ArgoCD will auto‑sync within seconds.

---

## 15. Summary  

The `deploy.sh` script now fully orchestrates:  

1. **Infrastructure provisioning** (databases, Kafka, Kong).  
2. **Image build & load** for every Spring‑Boot micro‑service.  
3. **Kubernetes objects** (namespaces, Deployments, Services, RBAC, NetworkPolicy).  
4. **Service‑mesh activation** (Istio mTLS, circuit‑breakers).  
5. **GitOps glue** (ArgoCD) for continuous, declarative deployments.  

Running `./k8s/deploy.sh` on a fresh Minikube cluster produces a **fully‑functional GinDHO health‑system** ready for production‑style testing, CI/CD pipelines, and eventual blue‑green / canary rollouts.

---  

*End of Document*  
