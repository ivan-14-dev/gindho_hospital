#!/bin/bash
echo "🚀 Démarrage complet de GinDHO (Backend + JavaFX)..."
echo "=========================================="

# Start backend in background
echo "📦 Démarrage du backend Spring Boot..."
cd backend
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8000 &
BACKEND_PID=$!
cd ..

# Wait for backend to start
echo "⏳ Attente du démarrage du backend (30s)..."
sleep 30

# Check if backend is running
if curl -s http://localhost:8000/api/auth/login > /dev/null 2>&1; then
    echo "✅ Backend démarré sur http://localhost:8000"
else
    echo "⚠️  Le backend peut nécessiter plus de temps"
fi

# Start JavaFX client
echo "🖥️  Démarrage du client JavaFX..."
cd javafx-client
mvn javafx:run

# Cleanup on exit
kill $BACKEND_PID 2>/dev/null
echo "\n✅ Arrêt complet de GinDHO"
