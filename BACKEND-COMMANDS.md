# GinDHO Backend Microservices - Command Reference

## Architecture Overview

- **31 Microservices** Spring Boot 3.2.0 / Java 21
- **Build Tool**: Maven
- **Containerization**: Docker
- **Orchestration**: Kubernetes
- **API Gateway**: Kong
- **Authentication**: Keycloak
- **Message Broker**: Kafka
- **Databases**: PostgreSQL, MongoDB, Redis

---

## 1. BUILD COMMANDS

### Build All Services (Skip Tests)
```bash
# From project root
mvn clean install -DskipTests -f pom.xml

# Or using deploy script
./deploy.sh build
```

### Build Individual Service
```bash
# Build specific microservice
cd services/patient-service
mvn clean package -DskipTests

# Build with tests
mvn clean package

# Build without skipping tests (full build)
mvn clean install
```

### Build Backend (Legacy Monolith)
```bash
mvn clean package -DskipTests -f backend/pom.xml
```

### Clean Build Artifacts
```bash
# Clean all target directories
find . -name "target" -type d -exec rm -rf {} +

# Or via deploy script
./deploy.sh clean
```

---

## 2. DOCKER COMMANDS

### Build Docker Images
```bash
# Build all service images
./deploy.sh docker

# Build specific service image
docker build -t gindho/patient-service:latest ./services/patient-service

# Build with no cache
docker build --no-cache -t gindho/patient-service:latest ./services/patient-service

# Build backend image
docker build -t gindho/backend:latest ./backend
```

### Run Containers Locally (Docker Compose)
```bash
# Start all infrastructure + services
docker compose -f docker/docker-compose.yml --env-file .env --profile all up -d

# Start only infrastructure (no microservices)
docker compose -f docker/docker-compose.yml --env-file .env up -d postgres mongodb redis kafka zookeeper keycloak keycloak-db kong prometheus grafana jaeger loki

# Start specific profile
docker compose -f docker/docker-compose.yml --env-file .env --profile services up -d

# Start legacy backend
docker compose -f docker/docker-compose.yml --env-file .env --profile legacy up -d

# View logs
docker compose -f docker/docker-compose.yml logs -f patient-service

# Stop all services
docker compose -f docker/docker-compose.yml down

# Stop and remove volumes (CAUTION: data loss)
docker compose -f docker/docker-compose.yml down -v
```

### Run Individual Service Locally (Without Docker)
```bash
# Run service directly with Maven
cd services/patient-service
mvn spring-boot:run -DskipTests

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Run in background
mvn spring-boot:run -DskipTests &
```

### Docker Container Management
```bash
# List running containers
docker ps

# List all containers (including stopped)
docker ps -a

# Stop container
docker stop gindho-patient-service

# Start container
docker start gindho-patient-service

# Remove container
docker rm gindho-patient-service

# View container logs
docker logs gindho-patient-service

# Follow logs in real-time
docker logs -f gindho-patient-service

# Execute command in running container
docker exec -it gindho-patient-service sh

# Check container health
docker inspect --format='{{.State.Health.Status}}' gindho-patient-service
```

---

## 3. KUBERNETES COMMANDS

### Deploy to Kubernetes
```bash
# Deploy all microservices
./deploy.sh k8s

# Or manually deploy
kubectl apply -f k8s/infrastructure-namespace -R
kubectl apply -f k8s/security-namespace -R
kubectl apply -f k8s/monitoring-namespace -R
kubectl apply -f k8s/patient-namespace -R
kubectl apply -f k8s/appointment-namespace -R
kubectl apply -f k8s/emr-namespace -R
kubectl apply -f k8s/laboratory-namespace -R
kubectl apply -f k8s/pharmacy-namespace -R
kubectl apply -f k8s/billing-namespace -R
kubectl apply -f k8s/inventory-namespace -R
kubectl apply -f k8s/hr-namespace -R
kubectl apply -f k8s/reporting-namespace -R
```

### Namespace Management
```bash
# Create namespace
kubectl create namespace patient

# List all namespaces
kubectl get namespaces

# Delete namespace (CAUTION: deletes all resources)
kubectl delete namespace patient
```

### Deployment Management
```bash
# Get deployments in namespace
kubectl get deployments -n patient

# Get deployment details
kubectl describe deployment patient-service -n patient

# Scale deployment
kubectl scale deployment patient-service --replicas=5 -n patient

# Restart deployment
kubectl rollout restart deployment patient-service -n patient

# Check rollout status
kubectl rollout status deployment patient-service -n patient

# Undo rollout
kubectl rollout undo deployment patient-service -n patient
```

### Pod Management
```bash
# Get pods in namespace
kubectl get pods -n patient

# Get pods with more details
kubectl get pods -n patient -o wide

# Describe pod
kubectl describe pod patient-service-xxxxx -n patient

# View pod logs
kubectl logs patient-service-xxxxx -n patient

# Follow pod logs
kubectl logs -f patient-service-xxxxx -n patient

# Execute command in pod
kubectl exec -it patient-service-xxxxx -n patient -- sh

# Delete pod (will be recreated by deployment)
kubectl delete pod patient-service-xxxxx -n patient
```

### Service Management
```bash
# Get services
kubectl get services -n patient

# Describe service
kubectl describe service patient-service -n patient

# Get service endpoints
kubectl get endpoints patient-service -n patient
```

### ConfigMap & Secrets
```bash
# Get ConfigMaps
kubectl get configmaps -n patient

# Get Secrets
kubectl get secrets -n patient

# View ConfigMap data
kubectl get configmap patient-service-config -n patient -o yaml

# View Secret (base64 encoded)
kubectl get secret patient-service-secret -n patient -o yaml

# Decode secret value
kubectl get secret patient-service-secret -n patient -o jsonpath='{.data.db_username}' | base64 --decode
```

### Ingress Management
```bash
# Get ingresses
kubectl get ingresses -n patient

# Describe ingress
kubectl describe ingress patient-service-ingress -n patient

# View ingress details
kubectl get ingress patient-service-ingress -n patient -o yaml
```

### Horizontal Pod Autoscaler (HPA)
```bash
# Get HPA
kubectl get hpa -n patient

# Describe HPA
kubectl describe hpa patient-service-hpa -n patient
```

### Troubleshooting
```bash
# Check pod resource usage
kubectl top pods -n patient

# Check node resource usage
kubectl top nodes

# View events
kubectl get events -n patient --sort-by='.lastTimestamp'

# Check for failing pods
kubectl get pods -n patient --field-selector=status.phase!=Running

# Get pod YAML definition
kubectl get pod patient-service-xxxxx -n patient -o yaml
```

---

## 4. MAVEN PROFILES & FLAGS

### Common Maven Flags
```bash
# Skip tests
-DskipTests

# Skip tests and checkstyle
-DskipTests -Dcheckstyle.skip=true

# Build without tests
mvn clean install -DskipTests

# Build with specific Java version
mvn clean install -DskipTests -Djava.version=21

# Build with offline mode
mvn clean install -DskipTests -o

# Build with parallel processing
mvn clean install -DskipTests -T 4

# Build with debug output
mvn clean install -DskipTests -X

# Skip integration tests
mvn clean install -DskipITs

# Run specific test
mvn test -Dtest=PatientServiceTest

# Run tests with specific profile
mvn test -Ptest
```

### Spring Boot Maven Plugin
```bash
# Run service
mvn spring-boot:run -DskipTests

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Run with arguments
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=9004"

# Build and run
mvn clean package spring-boot:run -DskipTests
```

---

## 5. SERVICE-SPECIFIC COMMANDS

### Patient Service (Port: 9004)
```bash
# Build
cd services/patient-service && mvn clean package -DskipTests

# Docker build
docker build -t gindho/patient-service:latest ./services/patient-service

# Run locally
cd services/patient-service && mvn spring-boot:run -DskipTests

# Kubernetes
kubectl get pods -n patient
kubectl logs -f deployment/patient-service -n patient
kubectl exec -it deployment/patient-service -n patient -- sh

# Health check
curl http://localhost:9004/actuator/health
curl http://localhost:9004/actuator/health/readiness
```

### Appointment Service (Port: 9005)
```bash
# Build
cd services/appointment-service && mvn clean package -DskipTests

# Docker build
docker build -t gindho/appointment-service:latest ./services/appointment-service

# Run locally
cd services/appointment-service && mvn spring-boot:run -DskipTests

# Kubernetes
kubectl get pods -n appointment
kubectl logs -f deployment/appointment-service -n appointment

# Health check
curl http://localhost:9005/actuator/health
```

### Medical Record Service (Port: 9006)
```bash
# Build
cd services/medical-record-service && mvn clean package -DskipTests

# Docker build
docker build -t gindho/medical-record-service:latest ./services/medical-record-service

# Run locally
cd services/medical-record-service && mvn spring-boot:run -DskipTests

# Kubernetes
kubectl get pods -n emr
kubectl logs -f deployment/medical-record-service -n emr

# Health check
curl http://localhost:9006/actuator/health
```

### Admission Service (Port: 9007)
```bash
# Build
cd services/admission-service && mvn clean package -DskipTests

# Docker build
docker build -t gindho/admission-service:latest ./services/admission-service

# Run locally
cd services/admission-service && mvn spring-boot:run -DskipTests

# Kubernetes
kubectl get pods -n admission
kubectl logs -f deployment/admission-service -n admission

# Health check
curl http://localhost:9007/actuator/health
```

### Emergency Service (Port: 9008)
```bash
# Build
cd services/emergency-service && mvn clean package -DskipTests

# Docker build
docker build -t gindho/emergency-service:latest ./services/emergency-service

# Run locally
cd services/emergency-service && mvn spring-boot:run -DskipTests

# Kubernetes
kubectl get pods -n emergency
kubectl logs -f deployment/emergency-service -n emergency

# Health check
curl http://localhost:9008/actuator/health
```

### Ward Service (Port: 9009)
```bash
# Build
cd services/ward-service && mvn clean package -DskipTests

# Docker build
docker build -t gindho/ward-service:latest ./services/ward-service

# Run locally
cd services/ward-service && mvn spring-boot:run -DskipTests

# Kubernetes
kubectl get pods -n ward
kubectl logs -f deployment/ward-service -n ward

# Health check
curl http://localhost:9009/actuator/health
```

### Bed Service (Port: 9010)
```bash
# Build
cd services/bed-service && mvn clean package -DskipTests

# Docker build
docker build -t gindho/bed-service:latest ./services/bed-service

# Run locally
cd services/bed-service && mvn spring-boot:run -DskipTests

# Kubernetes
kubectl get pods -n bed
kubectl logs -f deployment/bed-service -n bed

# Health check
curl http://localhost:9010/actuator/health
```

### Round Service (Port: 9011)
```bash
# Build
cd services/round-service && mvn clean package -DskipTests

# Docker build
docker build -t gindho/round-service:latest ./services/round-service

# Run locally
cd services/round-service && mvn spring-boot:run -DskipTests

# Kubernetes
kubectl get pods -n round
kubectl logs -f deployment/round-service -n round

# Health check
curl http://localhost:9011/actuator/health
```

### Surgery Service (Port: 9012)
```bash
# Build
cd services/surgery-service && mvn clean package -DskipTests

# Docker build
docker build -t gindho/surgery-service:latest ./services/surgery-service

# Run locally
cd services/surgery-service && mvn spring-boot:run -DskipTests

# Kubernetes
kubectl get pods -n surgery
kubectl logs -f deployment/surgery-service -n surgery

# Health check
curl http://localhost:9012/actuator/health
```

### Prescription Service (Port: 9013)
```bash
# Build
cd services/prescription-service && mvn clean package -DskipTests

# Docker build
docker build -t gindho/prescription-service:latest ./services/prescription-service

# Run locally
cd services/prescription-service && mvn spring-boot:run -DskipTests

# Kubernetes
kubectl get pods -n prescription
kubectl logs -f deployment/prescription-service -n prescription

# Health check
curl http://localhost:9013/actuator/health
```

### Pharmacy Service (Port: 9014)
```bash
# Build
cd services/pharmacy-service && mvn clean package -DskipTests

# Docker build
docker build -t gindho/pharmacy-service:latest ./services/pharmacy-service

# Run locally
cd services/pharmacy-service && mvn spring-boot:run -DskipTests

# Kubernetes
kubectl get pods -n pharmacy
kubectl logs -f deployment/pharmacy-service -n pharmacy

# Health check
curl http://localhost:9014/actuator/health
```

### Laboratory Service (Port: 9015)
```bash
# Build
cd services/laboratory-service && mvn clean package -DskipTests

# Docker build
docker build -t gindho/laboratory-service:latest ./services/laboratory-service

# Run locally
cd services/laboratory-service && mvn spring-boot:run -DskipTests

# Kubernetes
kubectl get pods -n laboratory
kubectl logs -f deployment/laboratory-service -n laboratory

# Health check
curl http://localhost:9015/actuator/health
```

### Imaging Service (Port: 9016)
```bash
# Build
cd services/imaging-service && mvn clean package -DskipTests

# Docker build
docker build -t gindho/imaging-service:latest ./services/imaging-service

# Run locally
cd services/imaging-service && mvn spring-boot:run -DskipTests

# Kubernetes
kubectl get pods -n imaging
kubectl logs -f deployment/imaging-service -n imaging

# Health check
curl http://localhost:9016/actuator/health
```

### Billing Service (Port: 9017)
```bash
# Build
cd services/billing-service && mvn clean package -DskipTests

# Docker build
docker build -t gindho/billing-service:latest ./services/billing-service

# Run locally
cd services/billing-service && mvn spring-boot:run -DskipTests

# Kubernetes
kubectl get pods -n billing
kubectl logs -f deployment/billing-service -n billing

# Health check
curl http://localhost:9017/actuator/health
```

### Insurance Service (Port: 9018)
```bash
# Build
cd services/insurance-service && mvn clean package -DskipTests

# Docker build
docker build -t gindho/insurance-service:latest ./services/insurance-service

# Run locally
cd services/insurance-service && mvn spring-boot:run -DskipTests

# Kubernetes
kubectl get pods -n insurance
kubectl logs -f deployment/insurance-service -n insurance

# Health check
curl http://localhost:9018/actuator/health
```

### Payment Service (Port: 9019)
```bash
# Build
cd services/payment-service && mvn clean package -DskipTests

# Docker build
docker build -t gindho/payment-service:latest ./services/payment-service

# Run locally
cd services/payment-service && mvn spring-boot:run -DskipTests

# Kubernetes
kubectl get pods -n payment
kubectl logs -f deployment/payment-service -n payment

# Health check
curl http://localhost:9019/actuator/health
```

### Inventory Service (Port: 9020)
```bash
# Build
cd services/inventory-service && mvn clean package -DskipTests

# Docker build
docker build -t gindho/inventory-service:latest ./services/inventory-service

# Run locally
cd services/inventory-service && mvn spring-boot:run -DskipTests

# Kubernetes
kubectl get pods -n inventory
kubectl logs -f deployment/inventory-service -n inventory

# Health check
curl http://localhost:9020/actuator/health
```

### Procurement Service (Port: 9021)
```bash
# Build
cd services/procurement-service && mvn clean package -DskipTests

# Docker build
docker build -t gindho/procurement-service:latest ./services/procurement-service

# Run locally
cd services/procurement-service && mvn spring-boot:run -DskipTests

# Kubernetes
kubectl get pods -n procurement
kubectl logs -f deployment/procurement-service -n procurement

# Health check
curl http://localhost:9021/actuator/health
```

### Asset Service (Port: 9022)
```bash
# Build
cd services/asset-service && mvn clean package -DskipTests

# Docker build
docker build -t gindho/asset-service:latest ./services/asset-service

# Run locally
cd services/asset-service && mvn spring-boot:run -DskipTests

# Kubernetes
kubectl get pods -n asset
kubectl logs -f deployment/asset-service -n asset

# Health check
curl http://localhost:9022/actuator/health
```

### Ambulance Service (Port: 9023)
```bash
# Build
cd services/ambulance-service && mvn clean package -DskipTests

# Docker build
docker build -t gindho/ambulance-service:latest ./services/ambulance-service

# Run locally
cd services/ambulance-service && mvn spring-boot:run -DskipTests

# Kubernetes
kubectl get pods -n ambulance
kubectl logs -f deployment/ambulance-service -n ambulance

# Health check
curl http://localhost:9023/actuator/health
```

### HR Service (Port: 9024)
```bash
# Build
cd services/hr-service && mvn clean package -DskipTests

# Docker build
docker build -t gindho/hr-service:latest ./services/hr-service

# Run locally
cd services/hr-service && mvn spring-boot:run -DskipTests

# Kubernetes
kubectl get pods -n hr
kubectl logs -f deployment/hr-service -n hr

# Health check
curl http://localhost:9024/actuator/health
```

### Scheduling Service (Port: 9025)
```bash
# Build
cd services/scheduling-service && mvn clean package -DskipTests

# Docker build
docker build -t gindho/scheduling-service:latest ./services/scheduling-service

# Run locally
cd services/scheduling-service && mvn spring-boot:run -DskipTests

# Kubernetes
kubectl get pods -n scheduling
kubectl logs -f deployment/scheduling-service -n scheduling

# Health check
curl http://localhost:9025/actuator/health
```

### Event Service (Port: 9026)
```bash
# Build
cd services/event-service && mvn clean package -DskipTests

# Docker build
docker build -t gindho/event-service:latest ./services/event-service

# Run locally
cd services/event-service && mvn spring-boot:run -DskipTests

# Kubernetes
kubectl get pods -n event
kubectl logs -f deployment/event-service -n event

# Health check
curl http://localhost:9026/actuator/health
```

### Notification Service (Port: 9027)
```bash
# Build
cd services/notification-service && mvn clean package -DskipTests

# Docker build
docker build -t gindho/notification-service:latest ./services/notification-service

# Run locally
cd services/notification-service && mvn spring-boot:run -DskipTests

# Kubernetes
kubectl get pods -n notification
kubectl logs -f deployment/notification-service -n notification

# Health check
curl http://localhost:9027/actuator/health
```

### Reporting Service (Port: 9028)
```bash
# Build
cd services/reporting-service && mvn clean package -DskipTests

# Docker build
docker build -t gindho/reporting-service:latest ./services/reporting-service

# Run locally
cd services/reporting-service && mvn spring-boot:run -DskipTests

# Kubernetes
kubectl get pods -n reporting
kubectl logs -f deployment/reporting-service -n reporting

# Health check
curl http://localhost:9028/actuator/health
```

### Audit Service (Port: 9029)
```bash
# Build
cd services/audit-service && mvn clean package -DskipTests

# Docker build
docker build -t gindho/audit-service:latest ./services/audit-service

# Run locally
cd services/audit-service && mvn spring-boot:run -DskipTests

# Kubernetes
kubectl get pods -n audit
kubectl logs -f deployment/audit-service -n audit

# Health check
curl http://localhost:9029/actuator/health
```

### Authorization Service (Port: 9012)
```bash
# Build
cd services/authorization-service && mvn clean package -DskipTests

# Docker build
docker build -t gindho/authorization-service:latest ./services/authorization-service

# Run locally
cd services/authorization-service && mvn spring-boot:run -DskipTests

# Kubernetes
kubectl get pods -n authorization
kubectl logs -f deployment/authorization-service -n authorization

# Health check
curl http://localhost:9012/actuator/health
```

### Identity Service (Port: 9001)
```bash
# Build
cd services/identity-service && mvn clean package -DskipTests

# Docker build
docker build -t gindho/identity-service:latest ./services/identity-service

# Run locally
cd services/identity-service && mvn spring-boot:run -DskipTests

# Kubernetes
kubectl get pods -n identity
kubectl logs -f deployment/identity-service -n identity

# Health check
curl http://localhost:9001/actuator/health
```

### API Gateway (Port: 9002)
```bash
# Build
cd services/api-gateway && mvn clean package -DskipTests

# Docker build
docker build -t gindho/api-gateway:latest ./services/api-gateway

# Run locally
cd services/api-gateway && mvn spring-boot:run -DskipTests

# Kubernetes
kubectl get pods -n apigateway
kubectl logs -f deployment/api-gateway -n apigateway

# Health check
curl http://localhost:9002/actuator/health
```

---

## 6. KAFKA COMMANDS

### Kafka Topics Management
```bash
# List topics (from kafka container)
docker exec -it gindho-kafka kafka-topics --list --bootstrap-server localhost:99092


# Create topic
docker exec -it gindho-kafka kafka-topics --create --topic patient-events --bootstrap-server localhost:99092  --partitions 3 --replication-factor 1

# Describe topic
docker exec -it gindho-kafka kafka-topics --describe --topic patient-events --bootstrap-server localhost:99092


# Delete topic
docker exec -it gindho-kafka kafka-topics --delete --topic patient-events --bootstrap-server localhost:99092

```

### Kafka Producer/Consumer Testing
```bash
# Produce messages
docker exec -it gindho-kafka kafka-console-producer --topic patient-events --bootstrap-server localhost:99092


# Consume messages
docker exec -it gindho-kafka kafka-console-consumer --topic patient-events --from-beginning --bootstrap-server localhost:99092


# Consume with specific group
docker exec -it gindho-kafka kafka-console-consumer --topic patient-events --group test-group --bootstrap-server localhost:99092

```

---

## 7. DATABASE COMMANDS

### PostgreSQL
```bash
# Connect to PostgreSQL
docker exec -it gindho-postgres psql -U gindho -d gindho

# List databases
docker exec -it gindho-postgres psql -U gindho -c "\l"

# Connect to specific database
docker exec -it gindho-postgres psql -U gindho -d patient_service

# List tables
docker exec -it gindho-postgres psql -U gindho -d patient_service -c "\dt"

# Run query
docker exec -it gindho-postgres psql -U gindho -d patient_service -c "SELECT * FROM patients LIMIT 10;"

# Backup database
docker exec -it gindho-postgres pg_dump -U gindho patient_service > patient_service_backup.sql

# Restore database
docker exec -it gindho-postgres psql -U gindho -d patient_service < patient_service_backup.sql
```

### MongoDB
```bash
# Connect to MongoDB
docker exec -it gindho-mongodb mongosh -u gindho -p gindho

# Show databases
docker exec -it gindho-mongodb mongosh -u gindho -p gindho --eval "show dbs"

# Use database
docker exec -it gindho-mongodb mongosh -u gindho -p gindho --eval "use medical_record_db"

# Show collections
docker exec -it gindho-mongodb mongosh -u gindho -p gindho --eval "show collections"

# Run query
docker exec -it gindho-mongodb mongosh -u gindho -p gindho --eval "db.medical_records.find().limit(10).pretty()"
```

### Redis
```bash
# Connect to Redis
docker exec -it gindho-redis redis-cli

# Ping Redis
docker exec -it gindho-redis redis-cli ping

# Get all keys
docker exec -it gindho-redis redis-cli KEYS '*'

# Get value by key
docker exec -it gindho-redis redis-cli GET "key-name"

# Flush all data (CAUTION)
docker exec -it gindho-redis redis-cli FLUSHALL
```

---

## 8. MONITORING COMMANDS

### Prometheus
```bash
# Access Prometheus UI
open http://localhost:9990

# Query metrics
curl 'http://localhost:9990/api/v1/query?query=up'

# Check targets
curl 'http://localhost:9990/api/v1/targets'
```

### Grafana
```bash
# Access Grafana UI
open http://localhost:9300

# Default credentials: admin/admin (check .env for actual credentials)
```

### Jaeger (Tracing)
```bash
# Access Jaeger UI
open http://localhost:19686
```

### Loki (Logs)
```bash
# Query logs via Grafana Explore
# Or use logcli
docker exec -it gindho-loki logcli query '{service="patient-service"}'
```

---

## 9. KEYCLOAK COMMANDS

### Keycloak Administration
```bash
# Access Keycloak Admin Console
open http://localhost:9001

# Default admin credentials (check .env)
# Username: admin
# Password: (from .env KEYCLOAK_ADMIN_PASSWORD)

# Export realm
docker exec gindho-keycloak /opt/keycloak/bin/kc.sh export --dir /tmp/export --realm gindho-realm

# Import realm (already configured in docker-compose)
# Realm file: docker/keycloak/realm-export.json
```

---

## 10. KONG API GATEWAY COMMANDS

### Kong Administration
```bash
# Access Kong Admin API
curl http://localhost:9041

# List services
curl http://localhost:9041/services

# List routes
curl http://localhost:9041/routes

# List plugins
curl http://localhost:9041/plugins

# Check Kong status
curl http://localhost:9041/status
```

---

## 11. TESTING COMMANDS

### Run Tests
```bash
# Run all tests
mvn test

# Run tests for specific service
cd services/patient-service && mvn test

# Run tests with coverage
mvn test jacoco:report

# Run integration tests
mvn verify

# Run specific test class
mvn test -Dtest=PatientControllerTest

# Run specific test method
mvn test -Dtest=PatientControllerTest#testCreatePatient
```

### Test Profiles
```bash
# Run with test profile
mvn test -Ptest

# Run with integration-test profile
mvn verify -Pintegration-test
```

---

## 12. USEFUL SCRIPTS

### deploy.sh Script
```bash
# Show help
./deploy.sh help

# Clean build artifacts
./deploy.sh clean

# Build all JARs
./deploy.sh build

# Build Docker images
./deploy.sh docker

# Deploy locally (Docker Compose + host services)
./deploy.sh local

# Deploy everything in Docker
./deploy.sh docker-all

# Deploy to Kubernetes
./deploy.sh k8s

# Build and deploy locally
./deploy.sh all
```

### Batch Operations
```bash
# Build all services in parallel
for svc in services/*/; do
  name=$(basename "$svc")
  [ "$name" = "common" ] && continue
  [ "$name" = "generator-core" ] && continue
  echo "Building $name..."
  (cd "$svc" && mvn clean package -DskipTests) &
done
wait
echo "All services built"

# Start all services locally in background
for svc in services/*/; do
  name=$(basename "$svc")
  [ "$name" = "common" ] && continue
  [ "$name" = "api-gateway" ] && continue
  port=$(grep -oP 'port:\s*\K\d+' "$svc/src/main/resources/application.yml" | head -1)
  echo "Starting $name on port $port..."
  (cd "$svc" && mvn spring-boot:run -DskipTests > ../../logs/$name.log 2>&1) &
  sleep 3
done
wait
echo "All services started"
```

---

## 13. HELM CHARTS (Kubernetes Package Manager)

### Helm Commands
```bash
# List Helm releases
helm list -A

# Install Helm chart
helm install patient-service ./infrastructure/helm/patient-service

# Upgrade Helm release
helm upgrade patient-service ./infrastructure/helm/patient-service

# Uninstall Helm release
helm uninstall patient-service

# View Helm values
helm show values ./infrastructure/helm/patient-service
```

---

## 14. TROUBLESHOOTING COMMANDS

### Debug Build Issues
```bash
# Maven dependency tree
mvn dependency:tree

# Maven dependency analysis
mvn dependency:analyze

# Check for dependency conflicts
mvn dependency:tree -Dverbose

# Clean local Maven repository (last resort)
rm -rf ~/.m2/repository/com/gindho
```

### Debug Runtime Issues
```bash
# Check Java version
java -version

# Check Maven version
mvn -version

# Check Docker version
docker --version

# Check kubectl version
kubectl version

# Check Kubernetes cluster
kubectl cluster-info

# Check available nodes
kubectl get nodes

# Check node resources
kubectl describe node <node-name>
```

### Network Debugging
```bash
# Test service connectivity from within cluster
kubectl run debug --image=busybox:1.36 --rm -it --restart=Never -- sh
# Then inside pod:
wget -O- http://patient-service:9004/actuator/health

# Check DNS resolution
kubectl run debug --image=busybox:1.36 --rm -it --restart=Never -- nslookup patient-service

# Check network policies
kubectl get networkpolicies -A
```

---

## 15. CI/CD COMMANDS

### Generic CI Pipeline Commands
```bash
# Install dependencies
mvn dependency:go-offline

# Run quality checks
mvn verify -DskipTests

# Build and test
mvn clean verify

# Build Docker image in CI
docker build -t gindho/patient-service:$CI_COMMIT_SHA ./services/patient-service

# Push Docker image
docker push gindho/patient-service:$CI_COMMIT_SHA

# Deploy to Kubernetes
kubectl set image deployment/patient-service patient-service=gindho/patient-service:$CI_COMMIT_SHA -n patient
kubectl rollout status deployment/patient-service -n patient
```

---

## 16. ENVIRONMENT VARIABLES

### Common Environment Variables
```bash
# Database
POSTGRES_USER=gindho
POSTGRES_PASSWORD=your_password
POSTGRES_DB=gindho
POSTGRES_PORT=95432

# MongoDB
MONGO_USERNAME=gindho
MONGO_PASSWORD=your_password
MONGO_PORT=97017

# Redis
REDIS_PORT=96379

# Kafka
KAFKA_PORT=99092

# Keycloak
KEYCLOAK_PORT=9001
KEYCLOAK_ADMIN=admin
KEYCLOAK_ADMIN_PASSWORD=your_password
KEYCLOAK_CLIENT_SECRET=test-secret

# Kong
KONG_PROXY_PORT=9002
KONG_ADMIN_PORT=9041

# JWT
JWT_SECRET=your_jwt_secret_key

# Monitoring
PROMETHEUS_PORT=9990
GRAFANA_PORT=9300
GRAFANA_ADMIN_USER=admin
GRAFANA_ADMIN_PASSWORD=your_password
JAEGER_UI_PORT=19686
LOKI_PORT=9310

# Mail (for notifications)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password

# SMS
SMS_PROVIDER=twilio
```

---

## 17. QUICK REFERENCE - SERVICE PORTS

| Service | Port | Namespace |
|---------|------|-----------|
| API Gateway (Kong) | 9000 | apigateway |
| Identity Service | 9001 | identity |
| Patient Service | 9004 | patient |
| Appointment Service | 9005 | appointment |
| Medical Record Service | 9006 | emr |
| Admission Service | 9007 | admission |
| Emergency Service | 9008 | emergency |
| Ward Service | 9009 | ward |
| Bed Service | 9010 | bed |
| Round Service | 9011 | round |
| Surgery Service | 9012 | surgery |
| Prescription Service | 9013 | prescription |
| Pharmacy Service | 9014 | pharmacy |
| Laboratory Service | 9015 | laboratory |
| Imaging Service | 9016 | imaging |
| Billing Service | 9017 | billing |
| Insurance Service | 9018 | insurance |
| Payment Service | 9019 | payment |
| Inventory Service | 9020 | inventory |
| Procurement Service | 9021 | procurement |
| Asset Service | 9022 | asset |
| Ambulance Service | 9023 | ambulance |
| HR Service | 9024 | hr |
| Scheduling Service | 9025 | scheduling |
| Event Service | 9026 | event |
| Notification Service | 9027 | notification |
| Reporting Service | 9028 | reporting |
| Audit Service | 9029 | audit |
| Authorization Service | 9012 | authorization |

---

## 18. USEFUL ALIASES

Add these to your `.bashrc` or `.zshrc`:

```bash
# GinDHO aliases
alias gindho-build='mvn clean install -DskipTests -f /path/to/GinDHO_Hospital/pom.xml'
alias gindho-clean='find /path/to/GinDHO_Hospital -name "target" -type d -exec rm -rf {} +'
alias gindho-docker-build='cd /path/to/GinDHO_Hospital && ./deploy.sh docker'
alias gindho-local='cd /path/to/GinDHO_Hospital && ./deploy.sh local'
alias gindho-k8s='cd /path/to/GinDHO_Hospital && ./deploy.sh k8s'
alias gindho-logs='tail -f /path/to/GinDHO_Hospital/logs/*.log'
alias gindho-pods='kubectl get pods -A'
alias gindho-services='kubectl get services -A'
alias gindho-ns='kubectl get namespaces'
```

---

## Notes

- All microservices use Spring Boot 3.2.0 with Java 21
- Services communicate via Kafka events and REST APIs
- API Gateway (Kong) routes external traffic to microservices
- Keycloak handles authentication and authorization
- All services expose Actuator endpoints for health checks
- Docker Compose profiles: `services`, `legacy`, `all`
- Kubernetes namespaces isolate each microservice
- HPA configured for auto-scaling (2-10 replicas based on CPU/Memory)

---

## Support

For issues or questions:
- Check logs: `docker logs <container>` or `kubectl logs <pod> -n <namespace>`
- Review deployment: `kubectl describe deployment <service> -n <namespace>`
- Check events: `kubectl get events -n <namespace> --sort-by='.lastTimestamp'`
- Monitor metrics: http://localhost:9300 (Grafana)