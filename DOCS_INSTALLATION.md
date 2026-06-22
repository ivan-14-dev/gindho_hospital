# 📋 Guide d'Installation GinDHO

## Prérequis
- Java 21 (JDK)
- Maven 3.8+
- PostgreSQL 15+
- Node.js 18+ (pour React)

## Installation Maven (si non installé)

### Option 1: SDKMAN (recommandé)
```bash
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install maven
sdk install java 21.0.2-tem
```

### Option 2: Installation manuelle
```bash
wget https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz
tar -xzf apache-maven-3.9.6-bin.tar.gz
sudo mv apache-maven-3.9.6 /opt/maven
export PATH=/opt/maven/bin:$PATH
```

## Configuration Base de Données

```bash
# Connectez-vous à PostgreSQL
sudo -u postgres psql

# Créer la base de données
CREATE DATABASE gindho;
CREATE USER gindho_user WITH PASSWORD 'password123';
GRANT ALL PRIVILEGES ON DATABASE gindho TO gindho_user;
\q
```

## Configuration du Projet

1. Modifier `backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/gindho
spring.datasource.username=gindho_user
spring.datasource.password=password123
jwt.secret=votre_secret_jwt_ici_64_caracteres_minimum_pour_securite
```

2. Compiler le backend:
```bash
cd backend
mvn clean compile
```

3. Lancer le backend:
```bash
mvn spring-boot:run
```

## Vérification

Le backend devrait être accessible sur:
- API: http://localhost:8080/api
- Swagger: http://localhost:8080/swagger-ui.html

## Tests

```bash
# Tests unitaires
mvn test

# Tests d'intégration
mvn verify
```

## Scripts de lancement

```bash
# Backend uniquement
./run-backend.sh

# JavaFX client (après démarrage backend)
./run-javafx.sh

# Tout ensemble
./run-all.sh
```

## Dépannage

### Erreur "mvn: command not found"
- Vérifier l'installation Maven
- Ajouter Maven au PATH

### Erreur de connexion PostgreSQL
- Vérifier que PostgreSQL tourne
- Vérifier les identifiants dans application.properties

### Port 8080 déjà utilisé
- Changer `server.port` dans application.properties
- Ou arrêter le service utilisant ce port

## Structure du Projet
```
GinDHO/
├── backend/          # Spring Boot API
├── javafx-client/    # Client Desktop
├── react-web/        # Client Web
└── Illustration_image/ # Diagrammes
```

## Prochaines Étapes
1. ✅ Backend API fonctionnel
2. ⏳ JavaFX client complet
3. ⏳ React web client
4. ⏳ Tests d'intégration
5. ⏳ Déploiement Docker
