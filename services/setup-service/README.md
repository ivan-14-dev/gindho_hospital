# GinDHO Setup Service

Service d'installation et de configuration initiale de l'infrastructure hospitalière GinDHO.

## Description

Ce service permet de configurer l'ensemble de l'infrastructure GinDHO depuis un navigateur web, sans avoir à modifier manuellement les fichiers de configuration.

## Fonctionnalités

- ✅ Interface web intuitive en 6 étapes
- ✅ Configuration de la base de données
- ✅ Configuration de Keycloak (authentification)
- ✅ Création du premier compte SuperAdmin
- ✅ Configuration du serveur SMTP
- ✅ Configuration des domaines et URLs
- ✅ Visualisation en temps réel de la progression
- ✅ Logs détaillés de l'installation

## Démarrage rapide

### 1. Lancer le service

```bash
cd hospital/services/setup-service
mvn spring-boot:run
```

Le service sera accessible sur `http://localhost:9000`

### 2. Accéder à l'interface

Ouvrez votre navigateur et accédez à `http://localhost:9000`

### 3. Suivre les étapes

1. **Informations de l'hôpital** - Renseignez le nom, l'adresse, les coordonnées
2. **Compte SuperAdmin** - Créez le premier compte administrateur
3. **Configuration SMTP** - Configurez le serveur de messagerie
4. **Configuration de l'application** - Définissez l'URL et les paramètres de connexion
5. **Vérification** - Vérifiez toutes les informations
6. **Installation** - Lancez l'installation et suivez la progression

## Configuration

### Variables d'environnement

| Variable | Description | Défaut |
|----------|-------------|--------|
| `DB_USERNAME` | Nom d'utilisateur PostgreSQL | `gindho` |
| `DB_PASSWORD` | Mot de passe PostgreSQL | (requis) |
| `KAFKA_BOOTSTRAP_SERVERS` | Serveurs Kafka | `localhost:9092` |
| `SERVER_PORT` | Port du service | `9000` |

### Exemple de lancement

```bash
export DB_PASSWORD=monpassword
export KAFKA_BOOTSTRAP_SERVERS=kafka:29092

mvn spring-boot:run
```

## API Endpoints

### GET /api/setup/status
Retourne le statut global de l'installation.

**Réponse:**
```json
{
  "setupCompleted": false,
  "hospitalName": null,
  "domainName": null,
  "steps": [...]
}
```

### POST /api/setup/start
Démarre le processus d'installation avec les paramètres fournis.

**Body:**
```json
{
  "hospitalName": "Hôpital Central",
  "hospitalAddress": "123 Rue de la Santé",
  "city": "Douala",
  "country": "Cameroun",
  "phone": "+237 123 456 789",
  "email": "contact@hopital-central.cm",
  "domainName": "hopital-central.cm",
  "databaseUsername": "gindho",
  "databasePassword": "MotDePasseDB123!",
  "superAdminUsername": "admin",
  "superAdminPassword": "MotDePasse123!",
  "superAdminEmail": "admin@hopital-central.cm",
  "superAdminFirstName": "Jean",
  "superAdminLastName": "Dupont",
  "smtpHost": "smtp.gmail.com",
  "smtpPort": 587,
  "smtpUsername": "user@gmail.com",
  "smtpPassword": "app_password",
  "smtpFromEmail": "noreply@hopital-central.cm",
  "applicationUrl": "https://hopital-central.cm",
  "applicationEmail": "app@hopital-central.cm",
  "applicationEmailPassword": "email_password"
}
```

### GET /api/setup/progress
Retourne la progression détaillée de l'installation.

### POST /api/setup/reset
Réinitialise le processus d'installation.

## Architecture

```
setup-service/
├── src/main/
│   ├── java/com/gindho/setup/
│   │   ├── SetupApplication.java          # Point d'entrée
│   │   ├── controller/
│   │   │   └── SetupController.java       # API REST
│   │   ├── service/
│   │   │   ├── SetupService.java          # Interface
│   │   │   └── SetupServiceImpl.java      # Implémentation
│   │   ├── dto/
│   │   │   └── SetupRequest.java          # DTO de requête
│   │   └── model/
│   │       └── SetupProgress.java         # Entité JPA
│   └── resources/
│       ├── application.properties         # Configuration
│       └── static/
│           └── index.html                 # Interface web
├── Dockerfile
└── pom.xml
```

## Intégration avec Docker Compose

Ajoutez le service au fichier `hospital/docker/docker-compose.yml`:

```yaml
setup-service:
  build:
    context: ../services/setup-service
    dockerfile: Dockerfile
  container_name: gindho-setup-service
  ports: ["9000:9000"]
  environment:
    - SERVER_PORT=9000
    - DB_USERNAME=${DB_USERNAME:-gindho}
    - DB_PASSWORD=${DB_PASSWORD}
    - KAFKA_BOOTSTRAP_SERVERS=kafka:29092
  depends_on:
    postgres:
      condition: service_healthy
    kafka:
      condition: service_started
```

## Sécurité

- Le service doit être désactivé après la première installation
- Accès restreint à l'interface de setup (IP whitelist recommandé)
- Les mots de passe sont transmis de manière sécurisée via HTTPS en production
- Le service ne stocke pas les mots de passe en clair (utilisation de variables d'environnement)

## Prochaines étapes après installation

1. Désactiver le setup-service dans docker-compose.yml
2. Configurer les paramètres dans les fichiers `.env` de chaque service
3. Démarrer tous les services avec `docker-compose up`
4. Accéder à l'application via l'URL configurée

## Support

Pour toute question ou problème, consultez la documentation principale de GinDHO.