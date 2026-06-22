#!/bin/bash
echo "🚀 Démarrage du Backend GinDHO..."
cd backend
echo "Compilation avec Maven..."
mvn clean compile
echo "Démarrage de l'application..."
mvn spring-boot:run
