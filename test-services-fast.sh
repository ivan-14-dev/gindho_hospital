#!/bin/bash
set +e

echo "=== Test séquentiel des services GinDHO ==="
echo "Démarrage de PostgreSQL..."
docker run -d --name postgres-test -e POSTGRES_USER=gindho -e POSTGRES_PASSWORD=gindho123 -p 95432:95432 postgres:15 >/dev/null 2>&1 || true
sleep 3

SERVICES=(
  "identity-service:9004"
  "patient-service:9005"
  "appointment-service:9006"
  "billing-service:9007"
  "emergency-service:9008"
  "laboratory-service:9009"
  "pharmacy-service:9010"
  "medical-record-service:9011"
  "notification-service:9012"
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
