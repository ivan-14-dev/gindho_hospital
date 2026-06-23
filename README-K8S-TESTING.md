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

## 4. Tester chaque service (port-forward)

### API Gateway
```bash
kubectl port-forward -n apigateway svc/api-gateway 8000:8080 &
curl -s http://localhost:8000/actuator/health | jq .
```

### Identity Service
```bash
kubectl port-forward -n security svc/identity-service 8080:8080 &
curl -s http://localhost:8080/actuator/health | jq .
```

### Patient Service
```bash
kubectl port-forward -n patient svc/patient-service 8081:8081 &
curl -s http://localhost:8081/actuator/health | jq .
```

### Appointment Service
```bash
kubectl port-forward -n appointment svc/appointment-service 8082:8082 &
curl -s http://localhost:8082/actuator/health | jq .
```

### Medical Record Service
```bash
kubectl port-forward -n emr svc/medical-record-service 8083:8083 &
curl -s http://localhost:8083/actuator/health | jq .
```

### Admission Service
```bash
kubectl port-forward -n admission svc/admission-service 8084:8084 &
curl -s http://localhost:8084/actuator/health | jq .
```

### Emergency Service
```bash
kubectl port-forward -n emergency svc/emergency-service 8085:8085 &
curl -s http://localhost:8085/actuator/health | jq .
```

### Ward Service
```bash
kubectl port-forward -n ward svc/ward-service 8086:8086 &
curl -s http://localhost:8086/actuator/health | jq .
```

### Bed Service
```bash
kubectl port-forward -n bed svc/bed-service 8087:8087 &
curl -s http://localhost:8087/actuator/health | jq .
```

### Round Service
```bash
kubectl port-forward -n round svc/round-service 8088:8088 &
curl -s http://localhost:8088/actuator/health | jq .
```

### Surgery Service
```bash
kubectl port-forward -n surgery svc/surgery-service 8089:8089 &
curl -s http://localhost:8089/actuator/health | jq .
```

### Prescription Service
```bash
kubectl port-forward -n prescription svc/prescription-service 8090:8090 &
curl -s http://localhost:8090/actuator/health | jq .
```

### Pharmacy Service
```bash
kubectl port-forward -n pharmacy svc/pharmacy-service 8091:8091 &
curl -s http://localhost:8091/actuator/health | jq .
```

### Laboratory Service
```bash
kubectl port-forward -n laboratory svc/laboratory-service 8092:8092 &
curl -s http://localhost:8092/actuator/health | jq .
```

### Imaging Service
```bash
kubectl port-forward -n imaging svc/imaging-service 8093:8093 &
curl -s http://localhost:8093/actuator/health | jq .
```

### Billing Service
```bash
kubectl port-forward -n billing svc/billing-service 8094:8094 &
curl -s http://localhost:8094/actuator/health | jq .
```

### Insurance Service
```bash
kubectl port-forward -n insurance svc/insurance-service 8095:8095 &
curl -s http://localhost:8095/actuator/health | jq .
```

### Payment Service
```bash
kubectl port-forward -n payment svc/payment-service 8096:8096 &
curl -s http://localhost:8096/actuator/health | jq .
```

### Inventory Service
```bash
kubectl port-forward -n inventory svc/inventory-service 8097:8097 &
curl -s http://localhost:8097/actuator/health | jq .
```

### Procurement Service
```bash
kubectl port-forward -n procurement svc/procurement-service 8098:8098 &
curl -s http://localhost:8098/actuator/health | jq .
```

### Asset Service
```bash
kubectl port-forward -n asset svc/asset-service 8099:8099 &
curl -s http://localhost:8099/actuator/health | jq .
```

### Ambulance Service
```bash
kubectl port-forward -n ambulance svc/ambulance-service 8100:8100 &
curl -s http://localhost:8100/actuator/health | jq .
```

### HR Service
```bash
kubectl port-forward -n hr svc/hr-service 8101:8101 &
curl -s http://localhost:8101/actuator/health | jq .
```

### Scheduling Service
```bash
kubectl port-forward -n scheduling svc/scheduling-service 8102:8102 &
curl -s http://localhost:8102/actuator/health | jq .
```

### Event Service
```bash
kubectl port-forward -n event svc/event-service 8103:8103 &
curl -s http://localhost:8103/actuator/health | jq .
```

### Notification Service
```bash
kubectl port-forward -n notification svc/notification-service 8104:8104 &
curl -s http://localhost:8104/actuator/health | jq .
```

### Reporting Service
```bash
kubectl port-forward -n reporting svc/reporting-service 8105:8105 &
curl -s http://localhost:8105/actuator/health | jq .
```

### Audit Service
```bash
kubectl port-forward -n audit svc/audit-service 8106:8106 &
curl -s http://localhost:8106/actuator/health | jq .
```

### Authorization Service
```bash
kubectl port-forward -n authorization svc/authorization-service 8107:8107 &
curl -s http://localhost:8107/actuator/health | jq .
```

### Outgoing Service
```bash
kubectl port-forward -n outgoing svc/outgoing-service 8110:8080 &
curl -s http://localhost:8110/actuator/health | jq .
```

### Interconnect Service
```bash
kubectl port-forward -n interconnect svc/interconnect-service 8111:8080 &
curl -s http://localhost:8111/actuator/health | jq .
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
kubectl patch svc patient-service -n patient -p '{"spec":{"type":"NodePort","ports":[{"port":8081,"nodePort":30081}]}}'

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
| PostgreSQL prêt | `kubectl exec -n infrastructure deploy/postgres -- pg_isready -h localhost -p 5432` |
| Kafka prêt | `kubectl exec -n infrastructure deploy/kafka -- kafka-topics.sh --bootstrap-server localhost:9092 --list` |
| DNS inter-services | `kubectl exec -n patient deploy/patient-service -- nslookup appointment-service.appointment.svc.cluster.local` |
| API Gateway santé | `curl http://localhost:8000/actuator/health` |

---

*End of document*
