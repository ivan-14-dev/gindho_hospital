# =============================================================================
# GinDHO - Guide d'Installation Rapide
# =============================================================================

## Testé sur Minikube

```bash
# 1. Démarrer Minikube
eval $(minikube docker-env)
minikube start --cpus=4 --memory=8192

# 2. Déployer l'infrastructure
kubectl apply -f k8s/overlays/dev/namespace-infrastructure.yaml

# 3. Déployer un service test
kubectl apply -f k8s/patient-service-dev.yaml

# 4. Vérifier
kubectl get pods -A
kubectl get svc -A
kubectl exec -it deploy/patient-service -n patient -- nslookup postgres.infrastructure.svc.cluster.local

# 5. Port-forward pour tester
kubectl port-forward svc/postgres -n infrastructure 5432:5432
kubectl port-forward svc/patient-service -n patient 8081:8081
```

## Architecture Validée

- ✅ Namespaces créés (infrastructure, patient, billing)
- ✅ DNS interne fonctionnel (postgres.infrastructure.svc.cluster.local)
- ✅ Services ClusterIP opérationnels
- ✅ Déploiement patient-service avec succès

## Prochaines Étapes

1. Build des images: `docker build -t gindho/patient-service:latest services/patient-service/`
2. Installer Istio: `istioctl install -f k8s/istio/istio-operator.yaml`
3. Installer ArgoCD: `kubectl apply -f k8s/argocd/`
4. Déployer via ArgoCD: `argocd app sync patient-service`