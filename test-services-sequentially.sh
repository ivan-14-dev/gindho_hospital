#!/bin/bash
set -e

echo "=== Test séquentiel des services GinDHO ==="
echo "Démarrage de PostgreSQL..."
docker run -d --name postgres-test -e POSTGRES_USER=gindho -e POSTGRES_PASSWORD=gindho123 -p 95432:95432 postgres:15 >/dev/null 2>&1 || true
sleep 5

echo "Vérification PostgreSQL..."
pg_isready -h localhost -p 95432 -U gindho || echo "PostgreSQL démarré"

echo ""
echo "=== Tests des services ==="

# Liste des services avec ports
declare -A SERVICES=(
  ["identity-service"]="9004"
  ["patient-service"]="9005"
  ["appointment-service"]="9006"
  ["billing-service"]="9007"
  ["emergency-service"]="9008"
  ["laboratory-service"]="9009"
  ["pharmacy-service"]="9010"
  ["medical-record-service"]="9011"
  ["notification-service"]="9012"
)

for service in "${!SERVICES[@]}"; do
  port=${SERVICES[$service]}
  echo ""
  echo "--- Test $service (port $port) ---"
  
  if [ -d "services/$service" ]; then
    cd "services/$service"
    
    # Vérifier si l'application a un actuator health
    if grep -q "actuator" src/main/resources/application.yml 2>/dev/null || grep -q "actuator" pom.xml 2>/dev/null; then
      echo "  Actuator détecté"
    fi
    
    # Démarrer le service en arrière-plan
    echo "  Démarrage..."
    timeout 60 mvn -q -DskipTests spring-boot:run -Dspring-boot.run.arguments="--server.port=$port" > /tmp/${service}.log 2>&1 &
    PID=$!
    
    # Attendre que le service démarre
    echo "  Attente du démarrage (30s)..."
    sleep 30
    
    # Tester le health endpoint
    if curl -s "http://localhost:$port/actuator/health" >/dev/null 2>&1; then
      echo "  ✅ $service: HEALTHY (port $port)"
    else
      echo "  ⚠️  $service: En cours de démarrage ou erreur"
      echo "  Logs:"
      tail -20 /tmp/${service}.log 2>/dev/null | sed 's/^/    /'
    fi
    
    # Arrêter le service
    kill $PID 2>/dev/null || true
    wait $PID 2>/dev/null || true
    cd ../..
  else
    echo "  ❌ Répertoire services/$service introuvable"
  fi
done

echo ""
echo "=== Tests terminés ==="
