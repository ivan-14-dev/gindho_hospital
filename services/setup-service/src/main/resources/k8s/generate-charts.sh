#!/bin/bash
# Generate Helm charts for all GinDHO microservices

set -e

SERVICES=(
    "medical-record:9006:emr"
    "admission:9007:patient"
    "emergency:9008:patient"
    "ward:9009:patient"
    "bed:9010:patient"
    "round:9011:patient"
    "surgery:9012:patient"
    "prescription:9013:prescription"
    "pharmacy:9014:pharmacy"
    "laboratory:9015:laboratory"
    "imaging:9016:emr"
    "billing:9017:billing"
    "insurance:9018:billing"
    "payment:9019:billing"
    "inventory:9020:inventory"
    "procurement:9021:inventory"
    "asset:9022:inventory"
    "ambulance:9023:patient"
    "hr:9024:hr"
    "scheduling:9025:hr"
    "event:9026:infrastructure"
    "notification:9027:infrastructure"
    "reporting:9028:reporting"
    "audit:9029:security"
    "authorization:9030:security"
    "identity:9023:security"
)

for SERVICE_INFO in "${SERVICES[@]}"; do
    IFS=':' read -r SERVICE PORT NAMESPACE <<< "$SERVICE_INFO"
    
    # Create Chart.yaml
    cat > k8s/helm/${SERVICE}/Chart.yaml << EOF
apiVersion: v2
name: ${SERVICE}-service
version: 1.0.0
description: Helm chart for GinDHO ${SERVICE}-service microservice
type: application
keywords:
  - gindho
  - ${SERVICE}
  - hospital
home: https://github.com/gindho/gindho
maintainers:
  - name: GinDHO Team
    email: team@gindho.com
appVersion: "1.0.0"
EOF

    # Create values.yaml
    cat > k8s/helm/${SERVICE}/values.yaml << EOF
replicaCount: 3
image:
  repository: gindho/${SERVICE}-service
  tag: latest
  pullPolicy: IfNotPresent
service:
  port: ${PORT}
  targetPort: ${PORT}
  name: http
configMap:
  name: ${SERVICE}-service-config
  data:
    SPRING_DATASOURCE_URL: "jdbc:postgresql://postgres.infrastructure.svc.cluster.local:95432/${SERVICE}_service"
    KAFKA_BOOTSTRAP_SERVERS: "kafka.infrastructure.svc.cluster.local:99092"
    MANAGEMENT_ENDPOINTS_WEB_EXPOSE_INCLUDE: "health,info,prometheus,metrics"
secrets:
  db_username:
    name: ${SERVICE}-service-secret
    key: db_username
  db_password:
    name: ${SERVICE}-service-secret
    key: db_password
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: ${PORT}
  initialDelaySeconds: 60
  periodSeconds: 30
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: ${PORT}
  initialDelaySeconds: 30
  periodSeconds: 10
resources:
  requests:
    memory: "512Mi"
    cpu: "250m"
  limits:
    memory: "1Gi"
    cpu: "500m"
hpa:
  enabled: true
  minReplicas: 3
  maxReplicas: 10
  targetCPUUtilizationPercentage: 70
pdb:
  enabled: true
  minAvailable: 2
networkPolicy:
  enabled: true
  ingressNamespaces:
    - kong
    - istio-system
serviceMonitor:
  enabled: true
  interval: 30s
  namespace: monitoring
  path: /actuator/prometheus
podAnnotations:
  prometheus.io/scrape: "true"
  prometheus.io/port: "${PORT}"
  prometheus.io/path: "/actuator/prometheus"
namespace: ${NAMESPACE}
EOF

    echo "Generated chart for ${SERVICE}-service"
done

echo "All Helm charts generated!"