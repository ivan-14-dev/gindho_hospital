#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"

PROFILE="${1:-gindho-dev}"
NAMESPACE_INFRA="infrastructure"
NAMESPACE_IDENTITY="identity"
NAMESPACE_KONG="infrastructure"

echo "=== [1/6] Starting minikube ==="
if ! minikube profile list 2>/dev/null | grep -q "$PROFILE"; then
  minikube start --driver=docker --cpus=3 --memory=6144 --profile="$PROFILE" --addons=ingress,metrics-server,storage-provisioner
else
  echo "Profile $PROFILE already exists, reusing."
fi

echo "=== [2/6] Enable ingress ==="
minikube addons enable ingress --profile="$PROFILE"

echo "=== [3/6] Apply secrets/configmaps ==="
kubectl apply -f k8s/infrastructure/secrets.yaml --namespace="$NAMESPACE_INFRA"
kubectl apply -f k8s/infrastructure/postgresql/ --namespace="$NAMESPACE_INFRA"
kubectl apply -f k8s/infrastructure/redis/ --namespace="$NAMESPACE_INFRA"

echo "=== [4/6] Deploy Kong ==="
kubectl apply -f k8s/infrastructure/kong/deployment.yaml --namespace="$NAMESPACE_KONG"

echo "=== [5/6] Deploy identity-service ==="
kubectl apply -f k8s/identity-namespace/identity-service-deployment.yaml --namespace="$NAMESPACE_IDENTITY"
kubectl apply -f k8s/identity-namespace/identity-service-ingress.yaml --namespace="$NAMESPACE_IDENTITY"

echo "=== [6/6] Wait core pods + ports ==="
kubectl wait --namespace="$NAMESPACE_INFRA" --for=condition=ready pod -l app=postgres --timeout=240s || true
kubectl wait --namespace="$NAMESPACE_INFRA" --for=condition=ready pod -l app=redis --timeout=240s || true
kubectl wait --namespace="$NAMESPACE_INFRA" --for=condition=ready pod -l app=kong-gateway --timeout=240s || true
kubectl wait --namespace="$NAMESPACE_IDENTITY" --for=condition=ready pod -l app=identity-service --timeout=240s || true

echo "=== Optional: port-forward for local frontend ==="
cat <<'PF'
minikube service -n infrastructure kong-gateway --url --profile=gindho-dev
minikube service -n identity identity-service --url --profile=gindho-dev
PF
