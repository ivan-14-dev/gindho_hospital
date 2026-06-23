# GinDHO – Commandes de test Kubernetes

Guide complet pour lancer le cluster Minikube, builder les images, déployer et tester chaque service.

---

## Prérequis

| Outil | Version |
|-------|---------|
| kubectl | >= v1.28 |
| minikube | >= v1.30 |
| docker | >= v24 |
| helm | >= v3.14 |
| maven | >= 3.9 |
| java | 21 |
| jq | -- |

---

## 1. Démarrer Minikube

```bash
cd /media/ivan/Ultimate/Ivan/script/common/GinDHO/hospital

minikube start --cpus=4 --memory=8192 --disk-size=50g --driver=docker

# Vérifier le noeud
kubectl get nodes
```

---

## 2. Builder les images et les charger dans Minikube

```bash
# Utiliser le daemon Docker de Minikube
eval $(minikube docker-env)

# Build complet du projet (skip tests)
mvn -B -f pom.xml package -DskipTests

# Build des images Docker pour tous les services avec Dockerfile
for svc_dir in services/*/; do
  name=$(basename "$svc_dir")
  if [ -f "${svc_dir}Dockerfile" ]; then
    docker build -t "gindho/${name}:latest" -f "${svc_dir}Dockerfile" "$svc_dir"
  fi
done

# Charger les images dans Minikube
minikube image load $(docker images --format '{{.Repository}}:{{.Tag}}' | grep gindho)
```

---

## 3. Déployer la stack complète

```bash
chmod +x deploy.sh
./deploy.sh k8s
```

### Déploiement manuel (étape par étape)

```bash
# Namespaces
kubectl apply -f k8s/namespaces.yaml

# Secrets (adapter les valeurs via .env)
envsubst < k8s/infrastructure/secrets.yaml | kubectl apply -f -

# CRD OpenTelemetry + infra
kubectl apply -f k8s/crds/opentelemetrycollector-crd.yaml
kubectl apply -k k8s/infrastructure-namespace

# Sécurité + monitoring
kubectl apply -f k8s/security -R
kubectl apply -f k8s/monitoring -R

# Services métier via Helm charts
for chart in k8s/helm/*/; do
  name=$(basename "$chart")
  ns="$name"
  helm upgrade --install "$name" "$chart" \
    --namespace "$ns" --create-namespace \
    -f "$chart/values.yaml"
done
```

---

## 3b. Setup via navigateur (Wizard)

Le service `setup-service` est déployé automatiquement et exposé via Kong sur `/setup`.

```bash
# Exposer le setup-service (si pas de LoadBalancer)
kubectl port-forward -n apigateway svc/api-gateway 9000:9000 &
```

Puis ouvrir : **http://localhost:9000/setup**

Le wizard guidera à travers :
- Informations de l'hôpital et base de données
- Création du SuperAdmin
- Configuration SMTP
- Configuration de l'application
- **Déploiement automatique de tous les manifests K8s**
- Initialisation PostgreSQL
- Configuration Keycloak
- Création des topics Kafka
- Test SMTP

La progression est affichée en temps réel étape par étape.

---

## 4. Tester chaque service (port-forward)

### API Gateway
```bash
kubectl port-forward -n apigateway svc/api-gateway 9000:9000 &
curl -s http://localhost:9000/actuator/health | jq .
```

### Identity Service
```bash
kubectl port-forward -n security svc/identity-service 9001:9001 &
curl -s http://localhost:9001/actuator/health | jq .
```

### Patient Service
```bash
kubectl port-forward -n patient svc/patient-service 9004:9004 &
curl -s http://localhost:9004/actuator/health | jq .
```

### Appointment Service
```bash
kubectl port-forward -n appointment svc/appointment-service 9005:9005 &
curl -s http://localhost:9005/actuator/health | jq .
```

### Medical Record Service
```bash
kubectl port-forward -n emr svc/medical-record-service 9006:9006 &
curl -s http://localhost:9006/actuator/health | jq .
```

### Admission Service
```bash
kubectl port-forward -n admission svc/admission-service 9007:9007 &
curl -s http://localhost:9007/actuator/health | jq .
```

### Emergency Service
```bash
kubectl port-forward -n emergency svc/emergency-service 9008:9008 &
curl -s http://localhost:9008/actuator/health | jq .
```

### Ward Service
```bash
kubectl port-forward -n ward svc/ward-service 9009:9009 &
curl -s http://localhost:9009/actuator/health | jq .
```

### Bed Service
```bash
kubectl port-forward -n bed svc/bed-service 9010:9010 &
curl -s http://localhost:9010/actuator/health | jq .
```

### Round Service
```bash
kubectl port-forward -n round svc/round-service 9011:9011 &
curl -s http://localhost:9011/actuator/health | jq .
```

### Surgery Service
```bash
kubectl port-forward -n surgery svc/surgery-service 9012:9012 &
curl -s http://localhost:9012/actuator/health | jq .
```

### Prescription Service
```bash
kubectl port-forward -n prescription svc/prescription-service 9013:9013 &
curl -s http://localhost:9013/actuator/health | jq .
```

### Pharmacy Service
```bash
kubectl port-forward -n pharmacy svc/pharmacy-service 9014:9014 &
curl -s http://localhost:9014/actuator/health | jq .
```

### Laboratory Service
```bash
kubectl port-forward -n laboratory svc/laboratory-service 9015:9015 &
curl -s http://localhost:9015/actuator/health | jq .
```

### Imaging Service
```bash
kubectl port-forward -n imaging svc/imaging-service 9016:9016 &
curl -s http://localhost:9016/actuator/health | jq .
```

### Billing Service
```bash
kubectl port-forward -n billing svc/billing-service 9017:9017 &
curl -s http://localhost:9017/actuator/health | jq .
```

### Insurance Service
```bash
kubectl port-forward -n insurance svc/insurance-service 9018:9018 &
curl -s http://localhost:9018/actuator/health | jq .
```

### Payment Service
```bash
kubectl port-forward -n payment svc/payment-service 9019:9019 &
curl -s http://localhost:9019/actuator/health | jq .
```

### Inventory Service
```bash
kubectl port-forward -n inventory svc/inventory-service 9020:9020 &
curl -s http://localhost:9020/actuator/health | jq .
```

### Procurement Service
```bash
kubectl port-forward -n procurement svc/procurement-service 9021:9021 &
curl -s http://localhost:9021/actuator/health | jq .
```

### Asset Service
```bash
kubectl port-forward -n asset svc/asset-service 9022:9022 &
curl -s http://localhost:9022/actuator/health | jq .
```

### Ambulance Service
```bash
kubectl port-forward -n ambulance svc/ambulance-service 9023:9023 &
curl -s http://localhost:9023/actuator/health | jq .
```

### HR Service
```bash
kubectl port-forward -n hr svc/hr-service 9024:9024 &
curl -s http://localhost:9024/actuator/health | jq .
```

### Scheduling Service
```bash
kubectl port-forward -n scheduling svc/scheduling-service 9025:9025 &
curl -s http://localhost:9025/actuator/health | jq .
```

### Event Service
```bash
kubectl port-forward -n event svc/event-service 9026:9026 &
curl -s http://localhost:9026/actuator/health | jq .
```

### Notification Service
```bash
kubectl port-forward -n notification svc/notification-service 9027:9027 &
curl -s http://localhost:9027/actuator/health | jq .
```

### Reporting Service
```bash
kubectl port-forward -n reporting svc/reporting-service 9028:9028 &
curl -s http://localhost:9028/actuator/health | jq .
```

### Audit Service
```bash
kubectl port-forward -n audit svc/audit-service 9029:9029 &
curl -s http://localhost:9029/actuator/health | jq .
```

### Authorization Service
```bash
kubectl port-forward -n authorization svc/authorization-service 9030:9030 &
curl -s http://localhost:9030/actuator/health | jq .
```

### Outgoing Service
```bash
kubectl port-forward -n outgoing svc/outgoing-service 9003:9003 &
curl -s http://localhost:9003/actuator/health | jq .
```

### Interconnect Service
```bash
kubectl port-forward -n interconnect svc/interconnect-service 9002:9002 &
curl -s http://localhost:9002/actuator/health | jq .
```

---

## 5. Vérifications globales

```bash
# Tous les pods
kubectl get pods -A

# Tous les services
kubectl get svc -A

# Tous les deployments
kubectl get deployments -A

# Logs d'un pod spécifique
kubectl logs -n patient deploy/patient-service --tail=50

# Décrire un pod en erreur
kubectl describe pod -n patient deploy/patient-service

# Port-forward multi-services (via socat ou kubectl port-forward en background)
# Vérifier qu'aucun port-forward ne quitte déjà
jobs -l
```

---

## 6. Exposer sans port-forward (NodePort / LoadBalancer)

Si tu es sur un serveur distant, remplace les services de type `ClusterIP` par `NodePort` :

```bash
# Exemple pour patient-service
kubectl patch svc patient-service -n patient -p '{"spec":{"type":"NodePort","ports":[{"port":9004,"nodePort":30081}]}}'

# Accès via l'IP du noeud Minikube
MINIKUBE_IP=$(minikube ip)
curl http://$MINIKUBE_IP:30081/actuator/health
```

Pour MetalLB (si installé), utiliser `type: LoadBalancer` directement.

---

## 7. Checklist après déploiement

| ✓ | Test | Commande |
|---|------|----------|
| Tous les pods Running | `kubectl get pods -A` |
| PostgreSQL prêt | `kubectl exec -n infrastructure deploy/postgres -- pg_isready -h localhost -p 95432` |
| Kafka prêt | `kubectl exec -n infrastructure deploy/kafka -- kafka-topics.sh --bootstrap-server localhost:99092  --list` |
| DNS inter-services | `kubectl exec -n patient deploy/patient-service -- nslookup appointment-service.appointment.svc.cluster.local` |
| API Gateway santé | `curl http://localhost:9000/actuator/health` |

---

*End of document*
