#!/bin/bash
set -euo pipefail

BASE_DIR="/media/ivan/Ultimate/Ivan/script/common/GinDHO"
LOG_DIR="$BASE_DIR/logs/container-tests"
mkdir -p "$LOG_DIR"

run_service_container() {
  local service="$1"
  local port="$2"
	local timeout="${3:-60}"
  
  echo "=== Testing $service (port $port) ==="
  
  docker build -t "gindho/$service:test" "services/$service" > "$LOG_DIR/${service}-build.log" 2>&1 || {
    echo "  BUILD FAILED"
    tail -5 "$LOG_DIR/${service}-build.log" | sed 's/^/    /'
    return 1
  }
  
  docker rm -f "$service-test" >/dev/null 2>&1 || true
  
  local db_name="${service//-/_}"
  
  docker run -d --name "$service-test" -p "$port":8080 \
    -e "SPRING_DATASOURCE_URL=jdbc:h2:mem:${db_name}" \
    -e "DB_USERNAME=sa" \
    -e "DB_PASSWORD=" \
    -e "JWT_SECRET=test-secret" \
    -e "KAFKA_BOOTSTRAP_SERVERS=localhost:9092" \
    --health-cmd="curl -f http://localhost:8080/actuator/health || exit 1" \
    --health-interval=5s \
    --health-timeout=3s \
    --health-retries=2 \
    "gindho/$service:test" > "$LOG_DIR/${service}-run.log" 2>&1 || {
    echo "  RUN FAILED"
    cat "$LOG_DIR/${service}-run.log" | sed 's/^/    /'
    docker rm -f "$service-test" >/dev/null 2>&1 || true
    return 1
  }
  
  echo "  Container started, waiting for health..."
  
  local elapsed=0
  while [ $elapsed -lt $timeout ]; do
    local status
    status=$(docker inspect --format='{{.State.Health.Status}}' "$service-test" 2>/dev/null || echo "starting")
    if [ "$status" = "healthy" ]; then
      echo "  HEALTHY after ${elapsed}s"
      docker logs "$service-test" --tail 20 > "$LOG_DIR/${service}-healthylog.log" 2>&1 || true
      docker stop "$service-test" >/dev/null 2>&1 || true
      docker rm "$service-test" >/dev/null 2>&1 || true
      return 0
    fi
    sleep 5
    elapsed=$((elapsed + 5))
  done
  
  echo "  TIMEOUT after ${timeout}s"
  docker logs "$service-test" --tail 30 | sed 's/^/    /'
  docker stop "$service-test" >/dev/null 2>&1 || true
  docker rm "$service-test" >/dev/null 2>&1 || true
  return 1
}

SERVICES=(
  "identity-service:8081"
  "patient-service:8082"
  "appointment-service:8083"
  "billing-service:8084"
  "notification-service:8085"
)

for svc_port in "${SERVICES[@]}"; do
  IFS=':' read -r svc port <<< "$svc_port"
  run_service_container "$svc" "$port" 45 || echo "  FAILED: $svc"
	echo ""
done

echo "=== Results saved to $LOG_DIR ==="
