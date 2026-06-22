#!/bin/bash
# Generate Helm charts for all GinDHO microservices

set -e

SERVICES=(
    "medical-record:8083:emr"
    "admission:8084:patient"
    "emergency:8085:patient"
    "ward:8086:patient"
    "bed:8087:patient"
    "round:8088:patient"
    "surgery:8089:patient"
    "prescription:8090:prescription"
    "pharmacy:8091:pharmacy"
    "laboratory:8092:laboratory"
    "imaging:8093:emr"
    "billing:8094:billing"
    "insurance:8095:billing"
    "payment:8096:billing"
    "inventory:8097:inventory"
    "procurement:8098:inventory"
    "asset:8099:inventory"
    "ambulance:8100:patient"
    "hr:8101:hr"
    "scheduling:8102:hr"
    "event:8103:infrastructure"
    "notification:8104:infrastructure"
    "reporting:8105:reporting"
    "audit:8106:security"
    "authorization:8107:security"
    "identity:8100:security"
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
    SPRING_DATASOURCE_URL: "jdbc:postgresql://postgres.infrastructure.svc.cluster.local:5432/${SERVICE}_service"
    KAFKA_BOOTSTRAP_SERVERS: "kafka.infrastructure.svc.cluster.local:9092"
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