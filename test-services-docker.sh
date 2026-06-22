#!/bin/bash
set -euo pipefail

BASE_DIR="/media/ivan/Ultimate/Ivan/script/common/GinDHO"
LOG_DIR="$BASE_DIR/logs/container-tests"
mkdir -p "$LOG_DIR"

echo "=== Starting PostgreSQL ==="
docker rm -f postgres-test >/dev/null 2>&1 || true
docker run -d --name postgres-test \
  -e POSTGRES_USER=gindho \
  -e POSTGRES_PASSWORD=gindho123 \
  -e POSTGRES_DB=gindho \
  -p 5432:5432 \
  postgres:15 > "$LOG_DIR/postgres.log" 2>&1

echo "Waiting for PostgreSQL..."
for i in {1..15}; do
  if docker exec postgres-test pg_isready -U gindho >/dev/null 2>&1; then
    echo "PostgreSQL ready"
    break
  fi
  sleep 2
done

echo ""
echo "=== Testing services in containers ==="

services=(
  "identity-service:8081"
  "patient-service:8082"
  "appointment-service:8083"
  "billing-service:8084"
  "notification-service:8085"
)

for svc_port in "${services[@]}"; do
  IFS=':' read -r svc port <<< "$svc_port"
  echo ""
  echo "--- Testing $svc (port $port) ---"
  
  # Build
  echo "  Building..."
  if ! docker build -t "gindho/$svc:test" "services/$svc" > "$LOG_DIR/${svc}-build.log" 2>&1; then
    echo "  BUILD FAILED"
    tail -3 "$LOG_DIR/${svc}-build.log" | sed 's/^/    /'
    continue
  fi
  echo "  Build OK"
  
  # Clean old container
  docker rm -f "$svc-test" >/dev/null 2>&1 || true
  
  # Determine DB name
  db_name="${svc//-/_}"
  
  # Run with health check
  echo "  Starting container..."
  docker run -d --name "$svc-test" \
    --add-host=host.docker.internal:host-gateway \
    -p "$port":8080 \
    -e "SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/${db_name}" \
    -e "DB_USERNAME=gindho" \
    -e "DB_PASSWORD=gindho123" \
    -e "JWT_SECRET=test-secret" \
    -e "KAFKA_BOOTSTRAP_SERVERS=host.docker.internal:9092" \
    --health-cmd="curl -f http://localhost:8080/actuator/health || exit 1" \
    --health-interval=5s \
    --health-timeout=3s \
    --health-retries=3 \
    "gindho/$svc:test" > "$LOG_DIR/${svc}-run.log" 2>&1 || {
    echo "  RUN FAILED"
    cat "$LOG_DIR/${svc}-run.log" | sed 's/^/    /'
    docker rm -f "$svc-test" >/dev/null 2>&1 || true
    continue
  }
  
  # Wait for health
  echo "  Waiting for health (max 60s)..."
  healthy=false
  for i in {1..12}; do
    status=$(docker inspect --format='{{.State.Health.Status}}' "$svc-test" 2>/dev/null || echo "starting")
    if [ "$status" = "healthy" ]; then
      echo "  HEALTHY after $((i*5))s"
      healthy=true
      break
    fi
    sleep 5
  done
  
  if [ "$healthy" = false ]; then
    echo "  TIMEOUT or UNHEALTHY"
    echo "  Last 20 log lines:"
    docker logs "$svc-test" --tail 20 2>&1 | sed 's/^/    /'
  fi
  
  # Cleanup
  docker stop "$svc-test" >/dev/null 2>&1 || true
  docker rm "$svc-test" >/dev/null 2>&1 || true
done

echo ""
echo "=== Test Summary ==="
echo "Logs saved to: $LOG_DIR"
for f in "$LOG_DIR"/*-build.log; do
  [ -f "$f" ] && echo "  $(basename ${f%-build.log}): $(tail -1 "$f" 2>/dev/null | grep -o 'Successfully tagged\|BUILD FAILED' || echo 'unknown')"
done
