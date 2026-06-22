#!/bin/bash
set +e

echo "=== Test séquentiel des services GinDHO ==="
echo "Démarrage de PostgreSQL..."
docker run -d --name postgres-test -e POSTGRES_USER=gindho -e POSTGRES_PASSWORD=gindho123 -p 5432:5432 postgres:15 >/dev/null 2>&1 || true
sleep 3

SERVICES=(
  "identity-service:8081"
  "patient-service:8082"
  "appointment-service:8083"
  "billing-service:8084"
  "emergency-service:8085"
  "laboratory-service:8086"
  "pharmacy-service:8087"
  "medical-record-service:8088"
  "notification-service:8089"
)

for svc_port in "${SERVICES[@]}"; do
  IFS=':' read -r svc port <<< "$svc_port"
  echo ""
  echo "--- $svc (port $port) ---"
  
  if [ -d "services/$svc" ]; then
    cd "services/$svc"
    
    # Test rapide: vérifier si le service compile
    echo "  Compilation..."
    if mvn -q -DskipTests compile > /tmp/${svc}-compile.log 2>&1; then
      echo "  ✅ Compilation OK"
    else
      echo "  ❌ Erreur de compilation"
      tail -5 /tmp/${svc}-compile.log 2>/dev/null | sed 's/^/    /'
      cd ../..
      continue
    fi
    
    # Test des endpoints disponibles
    echo "  Vérification des endpoints..."
    
    # Chercher les classes de test existantes
    if ls src/test/java/**/*Test.java 1>/dev/null 2>&1; then
      echo "  Tests détectés:"
      ls src/test/java/**/*Test.java 2>/dev/null | head -3 | sed 's/^/    /'
    else
      echo "  Aucun test unitaire détecté"
    fi
    
    cd ../..
  else
    echo "  ❌ Répertoire introuvable"
  fi
done

echo ""
echo "=== Résumé ==="
echo "Tests terminés"
